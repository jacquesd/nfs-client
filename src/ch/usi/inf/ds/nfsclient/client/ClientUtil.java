package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.sattr;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.timeval;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;


public final class ClientUtil {
    private ClientUtil() {}

    public static final int MAX_BYTES = 8192;
    public static final int CREATE_DIR = 16895;  // = '0o0040777' dir, rwxrwxrwx
    public static final int CREATE_FILE = 33279; // = '0o0100777' file, rwxrwxrwx

    public static timeval getTime(final long millis) {
        final timeval t = new timeval();
        t.seconds = (int) (millis / 1000.0);
        t.useconds = 0;
        return t;
    }

    public static timeval now() { return ClientUtil.getTime(new Date().getTime()); }

    public static sattr getFileSAttr(final File file) throws IOException {
        return ClientUtil.setFileSAttr(ClientUtil.getSAttr(file));
    }

    public static sattr getDirSAttr(final File file) throws IOException {
        return ClientUtil.setDirSAttr(ClientUtil.getSAttr(file));
    }

    public static sattr getDirSAttr(final timeval atime, final timeval mtime) {
        return ClientUtil.setDirSAttr(ClientUtil.getSAttr(atime, mtime));
    }

    private static sattr setFileSAttr(final sattr attrs) {
        attrs.mode = ClientUtil.CREATE_FILE;
        return attrs;
    }

    private static sattr setDirSAttr(final sattr attrs) {
        attrs.mode = ClientUtil.CREATE_DIR;
        return attrs;
    }

    private static sattr getSAttr(final File file) throws IOException {
        final BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        final timeval atime = ClientUtil.getTime(attr.lastAccessTime().toMillis());
        final timeval mtime = ClientUtil.getTime(attr.lastModifiedTime().toMillis());

        return ClientUtil.getSAttr(atime, mtime);
    }

    private static sattr getSAttr(final timeval atime, final timeval mtime) {
        final sattr attrs = new sattr();
        attrs.uid = 65534;  // nfsnobody
        attrs.gid = 65534;  // nfsnobody
        attrs.atime = atime;
        attrs.mtime = mtime;
        attrs.size = -1; // 0 means truncated, -1 means ignored

        return attrs;
    }
}

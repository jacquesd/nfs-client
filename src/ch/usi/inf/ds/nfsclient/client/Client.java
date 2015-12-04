package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.errors.DirectoryCreationException;
import ch.usi.inf.ds.nfsclient.jrpcgen.mount.dirpath;
import ch.usi.inf.ds.nfsclient.jrpcgen.mount.fhstatus;
import ch.usi.inf.ds.nfsclient.jrpcgen.mount.mountprogClient;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.*;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Client {
    private final fhandle root;
    private final nfsClient nfs;
    private final String mountPoint;

    public Client(final String host, final String remotePath, final String mountPoint)
            throws OncRpcException, IOException {
        this.mountPoint = mountPoint;
        final mountprogClient mountProg = new mountprogClient(InetAddress.getByName(host), OncRpcProtocols.ONCRPC_UDP);
        final OncRpcClientAuth auth = new OncRpcClientAuthUnix("barfleur", 501, 20);
        mountProg.getClient().setAuth(auth);

        final fhstatus mountOp = mountProg.MOUNTPROC_MNT_1(new dirpath(remotePath));
        if (mountOp.status == 0) {
            this.root = new fhandle(mountOp.directory.value);
            this.nfs = new nfsClient(InetAddress.getByName(host), OncRpcProtocols.ONCRPC_UDP);
            this.nfs.getClient().setAuth(auth);
        } else {
            this.root = null;
            this.nfs = null;
        }
    }

    public List<entry> readDir() throws IOException, OncRpcException {
        return this.readDir(this.root);
    }

    public List<entry> readDir(fhandle dir) throws IOException, OncRpcException {
        final List<entry> entries = new ArrayList<>();
        final readdirargs args = new readdirargs();
        args.dir = dir;
        args.count = 8192;
        args.cookie = new nfscookie(new byte[]{0, 0, 0, 0});

        final readdirres result = this.nfs.NFSPROC_READDIR_2(args);
        if (result.status == stat.NFS_OK) {
            entry entry = result.readdirok.entries;
            while (entry != null) {
                entries.add(entry);
                entry = entry.nextentry;
            }
        }
        return entries;
    }

    public diropres lookup(final fhandle parent, final String path) throws IOException, OncRpcException {
        final String[] split = path.split(File.separator);

        try {
            final fhandle parentNode = this.getParentDir(parent, split, false);

            final filename fileName = new filename();
            fileName.value = split[split.length - 1];

            final diropargs diropargs = new diropargs();
            diropargs.dir = parentNode;
            diropargs.name = fileName;

            return this.nfs.NFSPROC_LOOKUP_2(diropargs);

        } catch (final FileNotFoundException e) {
            final diropres res = new diropres();
            res.status = 1;
            return res;
        }
    }

    public void addDirectory(final File file) throws IOException, OncRpcException {
        final String[] path = this.getClientRelativePath(file);
        final fhandle parent = this.getParentDir(this.root, path, true);

        final String folderName = path[path.length - 1];

        final diropres alreadyExists = this.lookup(parent, folderName);

        if (alreadyExists.status == 2) {
            final BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            final long lastAcc = attr.lastAccessTime().toMillis();
            final long lastMod = attr.lastModifiedTime().toMillis();
            this.makeDir(parent, folderName, this.getTime(lastAcc), this.getTime(lastMod));

        }
    }

    private fhandle makeDir(final fhandle parent, final String name, final timeval atime, final timeval mtime)
            throws IOException, OncRpcException {
        final filename fileName = new filename(name);
        final diropargs diropargs = new diropargs();
        diropargs.dir = parent;
        diropargs.name = fileName;

        final sattr sattr = new sattr();
        sattr.mode = 16895; // = '0o40777' dir, rwxrwxrwx
        sattr.uid = 65534;  // nfsnobody
        sattr.gid = 65534;  // nfsnobody
        sattr.atime = atime;
        sattr.mtime = mtime;
        sattr.size = -1; // 0 means truncated, -1 means ignored

        final createargs createargs = new createargs();
        createargs.where = diropargs;
        createargs.attributes = sattr;

        final diropres result = this.nfs.NFSPROC_MKDIR_2(createargs);

        if (result.status != 0) {
            throw new DirectoryCreationException(name, result.status);
        }

        return result.diropok.file;
    }

    private fhandle getParentDir(final fhandle from, final String[] path, final boolean createPath) throws IOException, OncRpcException {
        fhandle parent = from;

        for (int i = 0; i < path.length - 1; i++) {
            final String pathNode = path[i];

            if (pathNode.length() <= 0) { continue; }

            final diropres res = this.lookup(parent, pathNode);
            if (res.status == 0) {
                parent = res.diropok.file;
            } else if (res.status == 2 && createPath) { // Dir does not exist
                parent = this.makeDir(parent, pathNode, this.now(), this.now());
            } else if (createPath) {
                throw new DirectoryCreationException(pathNode, res.status);
            } else {
                throw new FileNotFoundException(pathNode);
            }
        }
        return parent;
    }

    private String[] getClientRelativePath(final File file) {
        return file.getAbsolutePath().replaceAll(this.mountPoint, "").split(File.separator);
    }

    private timeval getTime(final long millis) {
        final timeval t = new timeval();
        t.seconds = (int) (millis / 1000.0);
        t.useconds = 0;
        return t;
    }

    private timeval now() {
        return getTime(new Date().getTime());
    }
}

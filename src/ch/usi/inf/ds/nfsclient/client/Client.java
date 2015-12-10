package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.errors.DirectoryCreationException;
import ch.usi.inf.ds.nfsclient.errors.FileCreationException;
import ch.usi.inf.ds.nfsclient.errors.ReadException;
import ch.usi.inf.ds.nfsclient.errors.WriteException;
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
import java.util.ArrayList;
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

    public fhandle getRoot() { return this.root; }

    public List<entry> readDir() throws IOException, OncRpcException {
        return this.readDir(this.root);
    }

    public List<entry> readDir(fhandle dir) throws IOException, OncRpcException {
        final List<entry> entries = new ArrayList<>();
        final readdirargs args = new readdirargs();
        args.dir = dir;
        args.count = ClientUtil.MAX_BYTES;
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

    public byte[] readFile(final fhandle fileHandle, final String fileName) throws IOException, OncRpcException {
        final readargs readargs = new readargs();
        readargs.file = fileHandle;
        readargs.offset = 0;
        readargs.count = ClientUtil.MAX_BYTES;
        readargs.totalcount = 0; // The argument "totalcount" is unused, and is removed in the next protocol revision.

        final readres result = this.nfs.NFSPROC_READ_2(readargs);

        if(result.status != stat.NFS_OK) {
            throw new ReadException(fileName, result.status);
        }

        return result.read.data.value;
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

    public void addDirectory(final File dir) throws IOException, OncRpcException {
        final String[] path = this.getClientRelativePath(dir);
        final fhandle parent = this.getParentDir(this.root, path, true);
        final String dirName = path[path.length - 1];

        final diropres alreadyExists = this.lookup(parent, dirName);

        if (alreadyExists.status == stat.NFSERR_NOENT) {
            this.makeDir(parent, dirName, dir);
        }
    }

    public void addFile(final File file, final byte[] data) throws IOException, OncRpcException {
        final String[] path = this.getClientRelativePath(file);
        final fhandle parent = this.getParentDir(this.root, path, true);
        final String fileName = path[path.length - 1];

        final diropres alreadyExists = this.lookup(parent, fileName);

        fhandle fileHandle = null;
        if (alreadyExists.status != stat.NFSERR_NOENT) {
            fileHandle = alreadyExists.diropok.file;
        } else {
            fileHandle = this.createFile(parent, fileName, file);
        }
        this.writeToFile(fileHandle, data, fileName);

    }

    private fhandle makeDir(final fhandle parent, final String name, final File dir)
            throws IOException, OncRpcException {
        return this.makeDir(parent, name, ClientUtil.getDirSAttr(dir));
    }

    private fhandle makeDir(final fhandle parent, final String name, final timeval atime, final timeval mtime)
            throws IOException, OncRpcException {
        return this.makeDir(parent, name, ClientUtil.getDirSAttr(atime, mtime));
    }

    private fhandle makeDir(final fhandle parent, final String name, final sattr attrs)
            throws IOException, OncRpcException {
        final filename fileName = new filename(name);
        final diropargs dirOpArgs = new diropargs();
        dirOpArgs.dir = parent;
        dirOpArgs.name = fileName;

        final createargs args = new createargs();
        args.where = dirOpArgs;
        args.attributes = attrs;

        final diropres result = this.nfs.NFSPROC_MKDIR_2(args);

        if (result.status != stat.NFS_OK) {
            throw new DirectoryCreationException(name, result.status);
        }

        return result.diropok.file;
    }

    private fhandle createFile(final fhandle parent, final String fileName, final File file)
            throws IOException, OncRpcException {
        final diropargs dirOpArgs = new diropargs();
        dirOpArgs.dir = parent;
        dirOpArgs.name = new filename(fileName);

        final createargs args = new createargs();
        args.attributes = ClientUtil.getFileSAttr(file);
        args.where = dirOpArgs;

        final diropres result = this.nfs.NFSPROC_CREATE_2(args);
        if (result.status != stat.NFS_OK) {
            throw new FileCreationException(fileName, result.status);
        }
        return result.diropok.file;
    }

    private void writeToFile(fhandle fileHandle, final byte[] data, final String fileName)
            throws IOException, OncRpcException {
        final nfsdata nfsData =new nfsdata();
        nfsData.value = data;

        final writeargs args=new writeargs();

        args.file = fileHandle;
        args.data = nfsData;
        args.offset = 0;

        args.beginoffset = 0; // The arguments "beginoffset" and "totalcount" are ignored
        args.totalcount = 0;  // and are removed in the next protocol revision.

        final attrstat result = this.nfs.NFSPROC_WRITE_2(args);

        if(result.status != stat.NFS_OK) {
            throw new WriteException(fileName, result.status);
        }
    }

    private fhandle getParentDir(final fhandle from, final String[] path, final boolean createPath) throws IOException, OncRpcException {
        fhandle parent = from;

        for (int i = 0; i < path.length - 1; i++) {
            final String pathNode = path[i];

            if (pathNode.length() <= 0) { continue; }

            final diropres res = this.lookup(parent, pathNode);
            if (res.status == stat.NFS_OK) {
                parent = res.diropok.file;
            } else if (res.status == stat.NFSERR_NOENT && createPath) { // Dir does not exist
                final timeval now = ClientUtil.now();
                parent = this.makeDir(parent, pathNode, now, now);
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
}

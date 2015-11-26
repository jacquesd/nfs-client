package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.jrpcgen.mount.dirpath;
import ch.usi.inf.ds.nfsclient.jrpcgen.mount.fhstatus;
import ch.usi.inf.ds.nfsclient.jrpcgen.mount.mountprogClient;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.*;
import org.acplt.oncrpc.OncRpcClientAuth;
import org.acplt.oncrpc.OncRpcClientAuthUnix;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private final mountprogClient mountProg;
    private final fhandle root;
    private final nfsClient nfs;

    public Client(final String host, final String mountPoint)
            throws OncRpcException, IOException {
        mountProg = new mountprogClient(InetAddress.getByName(host), OncRpcProtocols.ONCRPC_UDP);
        OncRpcClientAuth auth = new OncRpcClientAuthUnix("barfleur", 501, 20);
        mountProg.getClient().setAuth(auth);

        final fhstatus mountOp = mountProg.MOUNTPROC_MNT_1(new dirpath(mountPoint));
        if (mountOp.status == 0) {
            System.out.println("Yay: " + mountOp.directory);
            System.out.println("Yay: " + mountOp.directory.value);
            root = new fhandle(mountOp.directory.value);
            nfs = new nfsClient(InetAddress.getByName(host), OncRpcProtocols.ONCRPC_UDP);
            nfs.getClient().setAuth(auth);
        } else {
            System.out.println("We are doomed for now");
            root = null;
            nfs = null;
        }
    }

    public List<entry> readDir() throws IOException, OncRpcException {
        final List<entry> entries = new ArrayList<>();
        final readdirargs args = new readdirargs();
        args.dir = root;
        args.count = 8192;

        final nfscookie cookie = new nfscookie(new byte[]{0, 0, 0, 0});
        args.cookie = cookie;

        final readdirres result = nfs.NFSPROC_READDIR_2(args);
        if (result.status == stat.NFS_OK) {
            entry entry = result.readdirok.entries;
            while (entry != null) {
                entries.add(entry);
                entry = entry.nextentry;
            }
        }
        return entries;
    }
}

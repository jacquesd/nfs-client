/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class readlinkres implements XdrAble {
    public int status;
    public path data;

    public readlinkres() {
    }

    public readlinkres(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(status);
        switch ( status ) {
        case stat.NFS_OK:
            data.xdrEncode(xdr);
            break;
        default:
            break;
        }
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        status = xdr.xdrDecodeInt();
        switch ( status ) {
        case stat.NFS_OK:
            data = new path(xdr);
            break;
        default:
            break;
        }
    }

}
// End of readlinkres.java

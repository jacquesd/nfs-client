/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class attrstat implements XdrAble {
    public int status;
    public fattr attributes;

    public attrstat() {
    }

    public attrstat(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(status);
        switch ( status ) {
        case stat.NFS_OK:
            attributes.xdrEncode(xdr);
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
            attributes = new fattr(xdr);
            break;
        default:
            break;
        }
    }

}
// End of attrstat.java

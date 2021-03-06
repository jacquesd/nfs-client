/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class linkargs implements XdrAble {
    public fhandle from;
    public diropargs to;

    public linkargs() {
    }

    public linkargs(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        from.xdrEncode(xdr);
        to.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        from = new fhandle(xdr);
        to = new diropargs(xdr);
    }

}
// End of linkargs.java

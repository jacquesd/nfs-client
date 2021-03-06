/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class createargs implements XdrAble {
    public diropargs where;
    public sattr attributes;

    public createargs() {
    }

    public createargs(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        where.xdrEncode(xdr);
        attributes.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        where = new diropargs(xdr);
        attributes = new sattr(xdr);
    }

}
// End of createargs.java

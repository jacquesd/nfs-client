/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class readresOK implements XdrAble {
    public fattr attributes;
    public nfsdata data;

    public readresOK() {
    }

    public readresOK(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        attributes.xdrEncode(xdr);
        data.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        attributes = new fattr(xdr);
        data = new nfsdata(xdr);
    }

}
// End of readresOK.java

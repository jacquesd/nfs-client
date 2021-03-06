/*
 * Automatically generated by jrpcgen 1.1.1 on 11/19/15 4:33 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.mount;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class mountlist implements XdrAble {
    public name hostname;
    public dirpath directory;
    public mountlist nextentry;

    public mountlist() {
    }

    public mountlist(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        hostname.xdrEncode(xdr);
        directory.xdrEncode(xdr);
        nextentry.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        hostname = new name(xdr);
        directory = new dirpath(xdr);
        nextentry = new mountlist(xdr);
    }

}
// End of mountlist.java

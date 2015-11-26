/*
 * Automatically generated by jrpcgen 1.1.1 on 11/26/15 8:57 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.nfs;
import org.acplt.oncrpc.*;
import java.io.IOException;

public class readdirresOK implements XdrAble {
    public entry entries;
    public boolean eof;

    public readdirresOK() {
    }

    public readdirresOK(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        if ( entries != null ) { xdr.xdrEncodeBoolean(true); entries.xdrEncode(xdr); } else { xdr.xdrEncodeBoolean(false); };
        xdr.xdrEncodeBoolean(eof);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        entries = xdr.xdrDecodeBoolean() ? new entry(xdr) : null;
        eof = xdr.xdrDecodeBoolean();
    }

}
// End of readdirresOK.java

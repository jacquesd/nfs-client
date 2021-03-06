/*
 * Automatically generated by jrpcgen 1.1.1 on 11/19/15 4:33 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package ch.usi.inf.ds.nfsclient.jrpcgen.mount;
import org.acplt.oncrpc.*;
import java.io.IOException;

import java.net.InetAddress;

/**
 * The class <code>mountprogClient</code> implements the client stub proxy
 * for the MOUNTPROG remote program. It provides method stubs
 * which, when called, in turn call the appropriate remote method (procedure).
 */
public class mountprogClient extends OncRpcClientStub {

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy dummy.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient() throws OncRpcException, IOException {
        super(null);
    }

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient(InetAddress host, int protocol)
           throws OncRpcException, IOException {
        super(host, mountprog.MOUNTPROG, 1, 0, protocol);
    }

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient(InetAddress host, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, mountprog.MOUNTPROG, 1, port, protocol);
    }

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param client ONC/RPC client connection object implementing a particular
     *   protocol.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient(OncRpcClient client)
           throws OncRpcException, IOException {
        super(client);
    }

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient(InetAddress host, int program, int version, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, 0, protocol);
    }

    /**
     * Constructs a <code>mountprogClient</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.acplt.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountprogClient(InetAddress host, int program, int version, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, port, protocol);
    }

    /**
     * Call remote procedure MOUNTPROC_NULL_1.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_NULL_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mountprog.MOUNTPROC_NULL_1, mountprog.MOUNTVERS, args$, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_MNT_1.
     * @param arg1 parameter (of type dirpath) to the remote procedure call.
     * @return Result from remote procedure call (of type fhstatus).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public fhstatus MOUNTPROC_MNT_1(dirpath arg1)
           throws OncRpcException, IOException {
        fhstatus result$ = new fhstatus();
        client.call(mountprog.MOUNTPROC_MNT_1, mountprog.MOUNTVERS, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure MOUNTPROC_DUMP_1.
     * @return Result from remote procedure call (of type mountlist).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountlist MOUNTPROC_DUMP_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        mountlist result$ = new mountlist();
        client.call(mountprog.MOUNTPROC_DUMP_1, mountprog.MOUNTVERS, args$, result$);
        return result$;
    }

    /**
     * Call remote procedure MOUNTPROC_UMNT_1.
     * @param arg1 parameter (of type dirpath) to the remote procedure call.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_UMNT_1(dirpath arg1)
           throws OncRpcException, IOException {
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mountprog.MOUNTPROC_UMNT_1, mountprog.MOUNTVERS, arg1, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_UMNTALL_1.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_UMNTALL_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mountprog.MOUNTPROC_UMNTALL_1, mountprog.MOUNTVERS, args$, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_EXPORT_1.
     * @return Result from remote procedure call (of type exportlist).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public exportlist MOUNTPROC_EXPORT_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        exportlist result$ = new exportlist();
        client.call(mountprog.MOUNTPROC_EXPORT_1, mountprog.MOUNTVERS, args$, result$);
        return result$;
    }

}
// End of mountprogClient.java

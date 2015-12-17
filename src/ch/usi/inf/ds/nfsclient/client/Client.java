package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.diropres;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.entry;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.fhandle;
import org.acplt.oncrpc.OncRpcException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by kodikodytis on 17/12/15.
 */
public interface Client {
    fhandle getRoot();

    String getMountPoint();

    String getHost();

    String getPath();

    List<entry> readDir(fhandle dir) throws IOException, OncRpcException;

    byte[] readFile(fhandle fileHandle, String fileName) throws IOException, OncRpcException;

    diropres lookup(fhandle parent, String path) throws IOException, OncRpcException;

    void addDirectory(File dir) throws IOException, OncRpcException;

    void addFile(File file, byte[] data) throws IOException, OncRpcException;
}

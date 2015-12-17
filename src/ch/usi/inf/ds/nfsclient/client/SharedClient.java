package ch.usi.inf.ds.nfsclient.client;


import belisarius.sss.SSSInt;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.diropres;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.entry;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.fhandle;
import org.acplt.oncrpc.OncRpcException;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SharedClient implements Client {
    private final Client[] clients;
    private final SSSInt sharer;
    private final int shares;
    private final int threshold;

    public SharedClient(final Client[] clients, final int threshold) {
        this.clients = clients;
        this.sharer = new SSSInt();
        this.shares = clients.length;
        this.threshold = threshold;
    }

    @Override
    public fhandle getRoot() {
        return this.clients[0].getRoot();
    }

    @Override
    public String getMountPoint() {
        return this.clients[0].getMountPoint();
    }

    @Override
    public String getHost() {
        return this.clients[0].getHost();
    }

    @Override
    public String getPath() {
        return this.clients[0].getPath();
    }

    @Override
    public List<entry> readDir(final fhandle dir) throws IOException, OncRpcException {
        return this.clients[0].readDir(dir);
    }

    @Override
    public byte[] readFile(final fhandle fileHandle, final String fileName) throws IOException, OncRpcException {
        final ByteBuffer[] sharesData = new ByteBuffer[this.threshold];

        for (int i = 0; i < sharesData.length; i++) {
            sharesData[i] = ByteBuffer.wrap(this.clients[i].readFile(fileHandle, fileName));
        }

        final ByteBuffer data = ByteBuffer.allocate(sharesData.length);
        this.sharer.join(sharesData.length, IntStream.range(0, this.clients.length).toArray(), sharesData, data);
        return data.array();
    }

    @Override
    public diropres lookup(final fhandle parent, final String path) throws IOException, OncRpcException {
        return this.clients[0].lookup(parent, path);
    }

    @Override
    public void addDirectory(final File dir) throws IOException, OncRpcException {
        for (final Client client : this.clients) { client.addDirectory(dir); }
    }

    @Override
    public void addFile(final File file, final byte[] data) throws IOException, OncRpcException {
        final ByteBuffer src = ByteBuffer.wrap(data);

        final ByteBuffer[] dest = new ByteBuffer[this.shares];
        Arrays.stream(dest).forEach(d -> d = ByteBuffer.allocate(data.length));
        this.sharer.split(data.length, src, this.threshold, IntStream.range(0, this.clients.length).toArray(), dest);
        Arrays.stream(dest).forEach(Buffer::rewind);

        for(int i = 0; i < this.clients.length; i++) {
            this.clients[i].addFile(file, dest[i].array());
        }

    }
}

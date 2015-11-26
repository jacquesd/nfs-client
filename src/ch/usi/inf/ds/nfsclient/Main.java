package ch.usi.inf.ds.nfsclient;

import ch.usi.inf.ds.nfsclient.client.Client;
import ch.usi.inf.ds.nfsclient.files.DebugFileListener;
import ch.usi.inf.ds.nfsclient.files.FileWatcher;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.entry;
import org.acplt.oncrpc.OncRpcException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class Main {

    private static final String directory = "test_dir";

    public static void main(final String[] args) throws IOException {
        final ArrayList<Thread> threads = new ArrayList<>();

        String dir = FileSystems.getDefault().getPath(directory).toAbsolutePath().normalize().toString();

        final FileWatcher watcher = new FileWatcher(dir);
        watcher.addListener(new DebugFileListener());

        threads.add(new Thread(watcher));

        threads.forEach(java.lang.Thread::start);

        try {
            final Client client = new Client("127.0.0.1", "/exports/server");
            for (entry e : client.readDir()) {
                System.out.println(e.name.value);
            }

        } catch (OncRpcException e) {
            e.printStackTrace();
        }
    }
}

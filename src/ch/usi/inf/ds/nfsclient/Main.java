package ch.usi.inf.ds.nfsclient;

import ch.usi.inf.ds.nfsclient.client.Client;
import ch.usi.inf.ds.nfsclient.client.NFSFileListener;
import ch.usi.inf.ds.nfsclient.files.DebugFileListener;
import ch.usi.inf.ds.nfsclient.files.FileWatcher;
import org.acplt.oncrpc.OncRpcException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class Main {

    private static final String mountPoint = "/exports/mountpoint";

    public static void main(final String[] args) throws IOException {
        final ArrayList<Thread> threads = new ArrayList<>();

        final String dir = FileSystems.getDefault().getPath(Main.mountPoint).toAbsolutePath().normalize().toString();

        final FileWatcher watcher = new FileWatcher(dir);
        try {
            final Client client = new Client("127.0.0.1", "/exports/server", dir);
            watcher.addListener(new DebugFileListener());
            watcher.addListener(new NFSFileListener(client));
        } catch (final OncRpcException e) {
            e.printStackTrace();
        }
        threads.add(new Thread(watcher));
        threads.forEach(java.lang.Thread::start);
    }
}

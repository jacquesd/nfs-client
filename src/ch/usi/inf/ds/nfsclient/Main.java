package ch.usi.inf.ds.nfsclient;

import ch.usi.inf.ds.nfsclient.app.BaseApp;
import ch.usi.inf.ds.nfsclient.client.BaseClient;
import ch.usi.inf.ds.nfsclient.client.Client;
import ch.usi.inf.ds.nfsclient.client.EncryptedClient;
import ch.usi.inf.ds.nfsclient.client.NFSFileListener;
import ch.usi.inf.ds.nfsclient.files.DebugFileListener;
import ch.usi.inf.ds.nfsclient.files.FileWatcher;
import org.acplt.oncrpc.OncRpcException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

public class Main {

    public static void main(final String[] args) throws IOException {
        final String[] server = args[0].split(":");
        final String host;
        final String path;
        if (server.length != 2) {
            System.err.println("Invalid remote. Expected <hostname:remote_path>");
            System.exit(1);
            return;
        } else {
            host = server[0];
            path = server[1];
        }
        final String mountPoint = args[1];
        final String keyFile;
        final boolean encrypted;
        if (args.length == 3) {
            keyFile = args[2];
            encrypted = true;
        } else {
            keyFile = null;
            encrypted = false;
        }

        final ArrayList<Thread> threads = new ArrayList<>();

        final String dir = FileSystems.getDefault().getPath(mountPoint).toAbsolutePath().normalize().toString();

        final FileWatcher watcher = new FileWatcher(dir);
        final BaseApp app;
        try {
            final Client client = encrypted ? new EncryptedClient(host, path, dir, keyFile) : new BaseClient(host, path, dir);
            watcher.addListener(new DebugFileListener());
            watcher.addListener(new NFSFileListener(client));
            app = new BaseApp(client);
        } catch (final OncRpcException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        System.out.println(String.join(" ", "connected to", host + ":" + path, "using a",
                encrypted ? "secure" : "unsecure", "client"));
        threads.add(new Thread(watcher));
        threads.add(new Thread(app));
        threads.forEach(java.lang.Thread::start);
    }
}

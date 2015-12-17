package ch.usi.inf.ds.nfsclient;

import ch.usi.inf.ds.nfsclient.app.BaseApp;
import ch.usi.inf.ds.nfsclient.client.*;
import ch.usi.inf.ds.nfsclient.files.FileWatcher;
import org.acplt.oncrpc.OncRpcException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Properties;

public class Main {

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: [...].Main <config.properties>");
            System.exit(1);
            return;
        }
        Properties prop = new Properties();
        InputStream input = null;

        final String servers[];
        final int threshold;
        final String mountPoint;
        String keyFile = null;

        try {
            input = new FileInputStream(args[0]);
            prop.load(input);

            servers = prop.getProperty("servers").split(",");
            threshold = Integer.parseInt(prop.getProperty("threshold") == null ? "0" : prop.getProperty("threshold"));
            mountPoint = prop.getProperty("mountPoint");
            keyFile = prop.getProperty("keyFile");

        } catch (final IOException e) {
            System.err.println("Properties file not found: " + args[0]);
            System.exit(1);
            return;
        } finally {
            if (input != null) {
                try { input.close();
                } catch (final IOException e) { e.printStackTrace(); }
            }
        }

        final String dir = FileSystems.getDefault().getPath(mountPoint).toAbsolutePath().normalize().toString();

        final boolean encrypted = keyFile != null;
        final Client[] clients = new Client[servers.length];
        for (int i = 0; i < servers.length; i++) {
            final String[] server = servers[i].split(":");
            if (server.length != 2) {
                System.err.println("Invalid remote. Expected <hostname:remote_path>");
                System.exit(1);
                return;
            }

            final String host = server[0];
            final String path = server[1];
            try {
                clients[i] = encrypted
                        ? new EncryptedClient(host, path, dir, keyFile)
                        : new BaseClient(host, path, dir);
            } catch (OncRpcException e) {
                e.printStackTrace();
                System.exit(1);
                return;
            }
        }

        final Client client = clients.length == 1 ? clients[0] : new SharedClient(clients, threshold);

        final ArrayList<Thread> threads = new ArrayList<>();

        final FileWatcher watcher = new FileWatcher(dir);
        final BaseApp app;
        try {
            watcher.addListener(new NFSFileListener(client));
            app = new BaseApp(client);
        } catch (final OncRpcException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        System.out.println(String.join(" ", "connected to", "" + clients.length, "server(s) using",
                encrypted ? "a secure" : "an unsecure", "client"));
        threads.add(new Thread(watcher));
        threads.add(new Thread(app));
        threads.forEach(java.lang.Thread::start);
    }
}

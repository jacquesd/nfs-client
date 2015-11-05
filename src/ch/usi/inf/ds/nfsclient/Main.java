package ch.usi.inf.ds.nfsclient;

import ch.usi.inf.ds.nfsclient.files.DebugFileListener;
import ch.usi.inf.ds.nfsclient.files.FileWatcher;

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
    }
}

package ch.usi.inf.ds.client;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    private static final String directory = "test_dir";



	public static void main(final String[] args) throws IOException {
        final ArrayList<Thread> threads = new ArrayList<>();

        String dir = FileSystems.getDefault().getPath(directory).toAbsolutePath().normalize().toString();

        final Watcher watcher = new Watcher(dir);

        threads.add(new Thread(watcher));

        threads.forEach(java.lang.Thread::start);
	}
}

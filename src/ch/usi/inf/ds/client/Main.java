package ch.usi.inf.ds.client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(final String[] args) throws IOException {
		final Watcher watcher = new Watcher();
		final Path path = Paths.get("test_dir");
		System.out.println(path.toAbsolutePath());
		try {
			watcher.watch(path);
		} catch (final InterruptedException e) {
			System.out.println("Bye");
		}

	}
}

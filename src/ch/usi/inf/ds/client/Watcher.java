package ch.usi.inf.ds.client;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.sun.nio.file.SensitivityWatchEventModifier;

public class Watcher {

	private WatchService service;
	private WatchKey key;

	public Watcher() {

	}

	private void register(final Path dir) {
		try {
			this.key = dir.register(this.service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (final IOException err) {
			System.err.println(err);
		}
	}

	public void watch(final Path dir) throws IOException, InterruptedException {
		this.service = FileSystems.getDefault().newWatchService();
		dir.register(this.service,
				new WatchEvent.Kind[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY },
				SensitivityWatchEventModifier.HIGH);
		this.register(dir);

		boolean watching = true;
		WatchKey key = null;
		do {
			key = this.service.take();

			for (final WatchEvent<?> event : key.pollEvents()) {
				final WatchEvent.Kind kind = event.kind();
				if (ENTRY_CREATE.equals(kind)) {
					System.out.println("Created " + event.context().toString());
				} else if (ENTRY_DELETE.equals(kind)) {
					System.out.println("Deleted " + event.context().toString());
				} else if (ENTRY_MODIFY.equals(kind)) {
					System.out.println("Modified " + event.context().toString());
				}
				watching = key.reset();
			}
		} while (watching);

	}

}

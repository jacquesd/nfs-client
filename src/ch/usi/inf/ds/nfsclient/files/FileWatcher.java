package ch.usi.inf.ds.nfsclient.files;

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

public class FileWatcher implements Runnable {

	private final Path dir;

	public FileWatcher(String dir) {
        this.dir = FileSystems.getDefault().getPath(dir).toAbsolutePath().normalize();
	}

	private WatchKey register(final WatchService service, final Path dir) throws IOException {
		return dir.register(service, new WatchEvent.Kind[] {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY},
                SensitivityWatchEventModifier.HIGH);
	}

	@Override
	public void run() {
        try {
            final WatchService service = FileSystems.getDefault().newWatchService();
            WatchKey key = register(service, this.dir);

            while(true) {
                try {
                    key = service.take();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                for (final WatchEvent<?> event : key.pollEvents()) {
                    final WatchEvent.Kind kind = event.kind();
                    if (ENTRY_CREATE.equals(kind)) {
                        System.out.println("Created " + event.context().toString());
                    } else if (ENTRY_DELETE.equals(kind)) {
                        System.out.println("Deleted " + event.context().toString());
                    } else if (ENTRY_MODIFY.equals(kind)) {
                        System.out.println("Modified " + event.context().toString());
                    }

                    if (!key.reset()) {
                        break;
                    }
                }


            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

	}
}

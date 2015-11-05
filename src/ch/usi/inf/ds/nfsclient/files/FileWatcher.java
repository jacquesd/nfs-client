package ch.usi.inf.ds.nfsclient.files;

import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {

    private final Path dir;
    private final ArrayList<FileWatcherListener> listeners;

    public FileWatcher(final String dir) {
        this.dir = FileSystems.getDefault().getPath(dir).toAbsolutePath().normalize();
        this.listeners = new ArrayList<>();
    }

    private WatchKey register(final WatchService service, final Path dir) throws IOException {
        return dir.register(service, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY},
                SensitivityWatchEventModifier.HIGH);
    }

    public void addListener(final FileWatcherListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void run() {
        try {
            final WatchService service = FileSystems.getDefault().newWatchService();
            @SuppressWarnings("UnusedAssignment")
            WatchKey key = this.register(service, this.dir);

            while (true) {
                try {
                    key = service.take();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                for (final WatchEvent<?> event : key.pollEvents()) {

                    final WatchEvent.Kind kind = event.kind();
                    final Path parentPath = (Path) key.watchable();
                    final Path filePath = parentPath.resolve((Path) event.context());
                    final File file = filePath.toAbsolutePath().normalize().toFile();

                    if (ENTRY_CREATE.equals(kind)) {
                        notifyFileCreated(file);
                    } else if (ENTRY_DELETE.equals(kind)) {
                        notifyFileDeleted(file);
                    } else if (ENTRY_MODIFY.equals(kind)) {
                        notifyFileModified(file);
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

    private void notifyFileModified(final File file) {
        this.listeners.forEach(listener -> listener.fileModified(file));
    }

    private void notifyFileDeleted(final File file) {
        this.listeners.forEach(listener -> listener.fileDeleted(file));
    }

    private void notifyFileCreated(final File file) {
        this.listeners.forEach(listener -> listener.fileCreated(file));
    }
}

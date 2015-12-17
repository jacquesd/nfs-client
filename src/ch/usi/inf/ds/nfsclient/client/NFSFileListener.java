package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.files.FileWatcherListener;

import java.io.File;
import java.io.FileInputStream;


public class NFSFileListener implements FileWatcherListener {

    private final BaseClient client;

    public NFSFileListener(final BaseClient client) {
        this.client = client;
    }

    @Override
    public void fileCreated(final File file) {
        try {
            if (file.isDirectory())  {
                this.client.addDirectory(file.getAbsoluteFile().getCanonicalFile());
                return;
            }

            final FileInputStream reader = new FileInputStream(file);
            final byte[] buffer = new byte[(int) file.length()];
            reader.read(buffer);
            this.client.addFile(file, buffer);

        } catch (final Exception e) { e.printStackTrace(); }
    }

    @Override
    public void fileModified(final File file) {
        this.fileCreated(file);
    }

    @Override
    public void fileDeleted(final File file) {
        System.err.println("Deletion of " + file.getName() + " not supported");
    }
}

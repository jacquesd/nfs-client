package ch.usi.inf.ds.nfsclient.files;

import java.io.File;
import java.util.logging.Logger;

public class DebugFileListener implements FileWatcherListener {

    private static final Logger LOG = Logger.getLogger(DebugFileListener.class.getName());

    @Override
    public void fileCreated(final File file) { LOG.fine("created: " + file.getAbsolutePath()); }

    @Override
    public void fileModified(final File file) { LOG.fine("modified: " + file.getAbsolutePath()); }

    @Override
    public void fileDeleted(final File file) { LOG.fine("deleted: " + file.getAbsolutePath()); }
}

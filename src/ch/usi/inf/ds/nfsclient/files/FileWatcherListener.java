package ch.usi.inf.ds.nfsclient.files;


import java.io.File;

public interface FileWatcherListener {
    void fileCreated(File file);
    void fileModified(File file);
    void fileDeleted(File file);
}

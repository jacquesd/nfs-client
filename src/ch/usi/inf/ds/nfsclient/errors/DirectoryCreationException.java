package ch.usi.inf.ds.nfsclient.errors;


import java.io.IOException;

public class DirectoryCreationException extends IOException {

    public DirectoryCreationException(final String dirName, final int status) {
        super("Error while creating directory '" + dirName + "' with error code: " + status);
    }
}

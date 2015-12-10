package ch.usi.inf.ds.nfsclient.errors;

import java.io.IOException;

public class WriteException extends IOException {
    public WriteException(final String fileName, final int status) {
        super("Error while writing to the file '" + fileName + "' with error code: " + status);
    }
}

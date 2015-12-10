package ch.usi.inf.ds.nfsclient.errors;

import java.io.IOException;

public class ReadException extends IOException {
    public ReadException(final String fileName, final int status) {
        super("Error while reading from the file '" + fileName + "' with error code: " + status);
    }
}

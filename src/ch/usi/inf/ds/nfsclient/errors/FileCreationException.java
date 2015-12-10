package ch.usi.inf.ds.nfsclient.errors;

import java.io.IOException;


public class FileCreationException extends IOException{

    public FileCreationException(final String fileName, final int status) {
        super("Error while creating file '" + fileName + "' with error code: " + status);
    }
}

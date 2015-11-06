package ch.usi.inf.ds.nfsclient.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileUtil {
    public static Stream<Path> forSubDirectory(final Path dir) throws IOException {
        return Files.walk(dir)
                .filter(Files::isDirectory);
    }

    public static Stream<Path> walk(final Path dir) throws IOException {
        return Files.walk(dir);
    }
}

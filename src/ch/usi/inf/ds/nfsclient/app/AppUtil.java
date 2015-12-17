package ch.usi.inf.ds.nfsclient.app;


public final class AppUtil {
    public static final int DIR_TYPE = 16384;
    public static final int READ = 256;
    public static final int WRITE = 128;
    public static final int EXEC = 64;

    private AppUtil() {}

    public static boolean isReadable(int mode) { return (mode & READ) != 0; }
    public static boolean isWritable(int mode) { return (mode & WRITE) != 0; }
    public static boolean isExecutable(int mode) { return (mode & EXEC) != 0; }
    public static boolean isDir(int mode) { return (mode & DIR_TYPE) != 0; }
}

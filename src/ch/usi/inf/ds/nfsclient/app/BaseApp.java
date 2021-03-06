package ch.usi.inf.ds.nfsclient.app;


import ch.usi.inf.ds.nfsclient.client.Client;
import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.*;
import org.acplt.oncrpc.OncRpcException;

import java.io.*;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseApp implements Runnable {
    private final Client nfs;
    private final String mountPoint;
    private fhandle working_dir;
    private String path;


    public BaseApp(final Client client) throws IOException, OncRpcException {
        this.nfs = client;
        this.working_dir = this.nfs.getRoot();
        this.path = this.nfs.getPath();
        this.mountPoint = this.nfs.getMountPoint();
    }


    @Override
    public void run() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line;
            while(true) {
                this.renderPrompt();
                line = reader.readLine();
                if (line == null) { break; } // ^ + D
                line = line.trim();

                if (line.startsWith("ls")) {
                    this.ls(line);
                } else if (line.startsWith("cd")) {
                    this.cd(line);
                } else if (line.startsWith("pwd")) {
                    System.out.println(this.path);
                } else if (line.startsWith("cat")) {
                    this.cat(line);
                } else if (line.startsWith("restore")) {
                    this.restore(line);
                } else if (!line.equals("")) {
                    System.out.println(String.join("", "Invalid command: ", line,
                            "\nAvailable commands are:", "\n - ls\n - pwd\n - cd\n - cat\n - restore"));
                }
            }

        } catch (IOException | OncRpcException e) {
            e.printStackTrace();
        }
    }

    private void restore(final String line) throws IOException, OncRpcException {
        final String[] args = line.split(" ");
        if(args.length != 2) {
            System.err.println("Usage restore: restore <filename>");
            return;
        }

        if (! "restore".equals(args[0])) {
            System.err.println("command not found: " + args[0]);
            return;
        }
        final String path = args[1];
        final diropres res = this.nfs.lookup(this.working_dir, path);
        if (res.status != stat.NFS_OK) {
            System.err.println("restore: no such file or directory: " + path);
            return;
        }
        final String target = this.getTargetPath(path);
        if (AppUtil.isDir(res.diropok.attributes.mode)) {
            final File dir = new File(target);
            if (! dir.mkdir()) {
                System.err.println("restore: Failed to create directory: " + target);
                return;
            }

            for (final entry e : this.nfs.readDir(res.diropok.file)) {
                final diropres res_dir = this.nfs.lookup(res.diropok.file, e.name.value);
                if (res_dir.status != stat.NFS_OK) {
                    System.err.println("restore: failed to restore" + target);
                    return;
                }
                this.restore("restore " + Paths.get(path, e.name.value));
            }
        } else {
            final FileOutputStream out = new FileOutputStream(target);
            final byte[] fileData = this.readFile(this.working_dir, path, "restore");
            if (fileData != null) {
                out.write(fileData);
                out.close();
            }
        }
    }

    private void renderPrompt() {
        System.out.print(String.join(" ", ">", this.nfs.getHost() + ":" + this.path, "$ "));
    }

    private void cat(final String line) throws IOException, OncRpcException {
        final String[] args = line.split(" ");
        if(args.length != 2) {
            System.err.println("Usage cat: cat <filename>");
            return;
        }

        if (! "cat".equals(args[0])) {
            System.err.println("command not found: " + args[0]);
            return;
        }

        final byte[] fileData = this.readFile(this.working_dir, args[1], "cat");
        if (fileData != null) {
            System.out.write(fileData);
        }
    }

    private void ls(final String line) throws IOException, OncRpcException {
        final String[] args = line.split(" ");
        if (args.length > 2) {
            System.err.println("ls: Usage ls: ls [<path>]");
            return;
        }

        final fhandle path;
        if (args.length == 1) {
            path = this.working_dir ;
        } else {
            final diropres res = this.nfs.lookup(this.working_dir, args[1]);
            if (res.status != stat.NFS_OK) {
                System.err.println("ls: no such file or directory: " + args[1]);
                return;
            }
            path = res.diropok.file;
        }

        final DateFormat format = new SimpleDateFormat("MMM dd yy");


        for (final entry e : this.nfs.readDir(path)) {
            final diropres res = this.nfs.lookup(path, e.name.value);
            if (res.status != stat.NFS_OK) {
                System.err.println("Error reading " + path);
            }

            final int mode = res.diropok.attributes.mode;
            final String read = AppUtil.isReadable(mode) ? "r" : "-";
            final String write = AppUtil.isWritable(mode) ? "w" : "-";
            final String exec = AppUtil.isExecutable(mode) ? "x" : "-";
            final String type = AppUtil.isDir(mode) ? "d" : "-";

            System.out.println(String.join(" ",
                    type + read + write + exec,
                    "" + res.diropok.attributes.nlink,
                    "" + res.diropok.attributes.uid,
                    "" + res.diropok.attributes.gid,
                    String.format("%4d", res.diropok.attributes.size),
                    format.format(new Date((long) res.diropok.attributes.mtime.seconds * 1000)),
                    e.name.value));
        }
    }

    private void cd(final String line) throws IOException, OncRpcException {
        final String[] args = line.split(" ");
        if (args.length > 2) {
            System.err.println("cd: Invalid usage: " + line);
            return;
        }

        if (args.length == 1) {
            this.working_dir = this.nfs.getRoot();
            this.path = this.nfs.getPath();
            return;
        }

        final String path = args[1];
        if (! "cd".equals(args[0])) {
            System.err.println("command not found: " + args[0]);
            return;
        }

        final diropres res = this.nfs.lookup(this.working_dir, path);
        if (res.status != stat.NFS_OK) {
            System.err.println("cd: no such file or directory: " + path);
            return;
        }
        if (res.diropok.attributes.type != ftype.NFDIR) {
            System.err.println("cd: not a directory: " + path);
            return;
        }
        this.working_dir = res.diropok.file;
        this.path = Paths.get(this.path).resolve(Paths.get(path)).normalize().toString();

    }

    private byte[] readFile(final fhandle dir, final String path, final String cmd)
            throws IOException, OncRpcException {
        final String filename = Paths.get(path).getFileName().toString();
        final diropres res = this.nfs.lookup(dir, path);

        if (res.status != stat.NFS_OK) {
            System.err.println(cmd + ": no such file or directory: " + path);
            return null;
        }
        if (res.diropok.attributes.type == ftype.NFDIR) {
            System.err.println(cmd + ": " + path  + " is a directory");
            return null;
        }
        try {
            return this.nfs.readFile(res.diropok.file, filename);
        } catch (IOException | OncRpcException e) {
            System.err.println("failed to read " + filename);
            return null;
        }
    }

    private String getTargetPath(final String path) {
        return Paths.get(this.path.replaceFirst(this.nfs.getPath(), this.mountPoint), path)
                .normalize().toAbsolutePath().toString();

    }
}

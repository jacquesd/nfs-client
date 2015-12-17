package ch.usi.inf.ds.nfsclient.client;

import ch.usi.inf.ds.nfsclient.jrpcgen.nfs.fhandle;
import org.acplt.oncrpc.OncRpcException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class EncryptedClient extends BaseClient {
    private Cipher cipher;
    private SecretKey key;

    public EncryptedClient(final String host, final String remotePath, final String mountPoint, final String keyFile)
            throws OncRpcException, IOException {
        super(host, remotePath, mountPoint);
        this.cipher = null;
        try {
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            System.err.println("AES is unavailable");
            System.exit(1);
            return;
        }
        this.generateKey(keyFile);
    }

    @Override
    public void addFile(final File file, final byte[] data) throws IOException, OncRpcException {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
        } catch (final InvalidKeyException e) {
            System.err.println("Invalid encryption key");
            System.exit(1);
            return;
        }

        try {
            final byte[] encrypted = this.cipher.doFinal(data);
            super.addFile(file, encrypted);
        } catch (IllegalBlockSizeException |  BadPaddingException e) {
            System.err.println("Unable to encrypt " + file.getName());
        }
    }

    @Override
    public byte[] readFile(final fhandle fileHandle, final String fileName) throws IOException, OncRpcException {
        final byte[] encryptedData = super.readFile(fileHandle, fileName);
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.key);
        } catch (final InvalidKeyException e) {
            System.err.println("Invalid encryption key");
            System.exit(1);
        }
        try {
            return this.cipher.doFinal(encryptedData);
        } catch (final IllegalBlockSizeException |  BadPaddingException e) {
            System.err.println("Unable to decrypt " + fileName);
            System.exit(1);
            return null;
        }
    }

    private void generateKey(final String keyFile) {
        final byte[] key;
        try {
            key = Files.readAllBytes(Paths.get(keyFile));
        } catch (final IOException e) {
            e.printStackTrace();
            System.err.println("Key file: " + keyFile + " not found" + System.getProperty("user.dir"));
            System.exit(1);
            return;
        }

        final MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (final NoSuchAlgorithmException e) {
            System.err.println("SHA-1 is unavailable.");
            System.exit(1);
            return;
        }

        this.key = new SecretKeySpec(Arrays.copyOf(sha.digest(key), 16), "AES"); // 128 bit only
    }
}

package cn.devkits.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Key;

public class FolderEncryption {

    private final Cipher cipher;

    public FolderEncryption(String password) throws Exception {
        Key key = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "AES");
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    public void encryptFolder(Path folder) throws Exception {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                byte[] bytes = Files.readAllBytes(file);
                byte[] encrypted;
                try {
                    encrypted = cipher.doFinal(bytes);
                    Path encryptedFile = folder.resolve(file.getFileName().toString() + ".enc");
                    Files.write(encryptedFile, encrypted);
                    return FileVisitResult.CONTINUE;
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void decryptFolder(Path folder) throws Exception {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().endsWith(".enc")) {
                    byte[] encrypted = Files.readAllBytes(file);
                    byte[] decrypted;
                    try {
                        decrypted = cipher.doFinal(encrypted);
                        Path decryptedFile = folder.resolve(file.getFileName().toString().substring(0, file.getFileName().toString().length() - 4));
                        Files.write(decryptedFile, decrypted);
                        Files.delete(file);
                    } catch (IllegalBlockSizeException | BadPaddingException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

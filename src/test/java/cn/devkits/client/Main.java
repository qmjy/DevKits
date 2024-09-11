package cn.devkits.client;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main{
    public static void main(String[] args) {
        try {
            FolderEncryption folderEncryption = new FolderEncryption("1234567890123456");
            Path folder = Paths.get("Z:\\test");
            folderEncryption.encryptFolder(folder);
            // 解密操作
//            folderEncryption.decryptFolder(folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
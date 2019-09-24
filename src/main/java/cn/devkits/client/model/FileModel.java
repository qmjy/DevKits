package cn.devkits.client.model;

import java.io.File;

public class FileModel {
    private File file;
    private String md5;


    public FileModel(String md5, File file) {
        this.md5 = md5;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


}

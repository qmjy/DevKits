package cn.devkits.client.tray.model;

import java.io.File;
import java.util.Vector;

public class FileSpliterModel {
    // 待分割的文件
    private File file;
    // 分割过程中需要显示的控制台消息
    private Vector<String> msg = new Vector<String>();
    // 是否分割结束
    private boolean finished;

    public FileSpliterModel(String filePath) {
        this.file = new File(filePath);
    }

    public String getOutputFolder() {
        return "";
    }

    public File getFile() {
        return file;
    }



}

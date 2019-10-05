package cn.devkits.client.tray.model;

import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;

public class LargeDuplicateActionModel {

    private LargeDuplicateFilesFrame frame;
    private ExecutorService threadPool;
    private FilenameFilter fileFilter;
    private int minFileSize;
    private int maxFileSize;


    public LargeDuplicateActionModel(LargeDuplicateFilesFrame frame, ExecutorService threadPool, FilenameFilter fileFilter, int maxFileSize, int minFileSize) {
        this.frame = frame;
        this.threadPool = threadPool;
        this.fileFilter = fileFilter;
        this.maxFileSize = maxFileSize;
        this.minFileSize = minFileSize;
    }


    public LargeDuplicateFilesFrame getFrame() {
        return frame;
    }


    public void setFrame(LargeDuplicateFilesFrame frame) {
        this.frame = frame;
    }


    public ExecutorService getThreadPool() {
        return threadPool;
    }


    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }


    public FilenameFilter getFileFilter() {
        return fileFilter;
    }


    public void setFileFilter(FilenameFilter fileFilter) {
        this.fileFilter = fileFilter;
    }


    public int getMinFileSize() {
        return minFileSize;
    }


    public void setMinFileSize(int minFileSize) {
        this.minFileSize = minFileSize;
    }


    public int getMaxFileSize() {
        return maxFileSize;
    }


    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}

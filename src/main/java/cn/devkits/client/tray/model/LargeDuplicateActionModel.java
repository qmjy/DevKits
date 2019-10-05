package cn.devkits.client.tray.model;

import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;

public class LargeDuplicateActionModel {

    private LargeDuplicateFilesFrame frame;
    private ExecutorService threadPool;
    private FilenameFilter fileFilter;
    private long minFileSize;
    private long maxFileSize;


    public LargeDuplicateActionModel(LargeDuplicateFilesFrame frame, ExecutorService threadPool, FilenameFilter fileFilter, long maxFileSize, long minFileSize) {
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


    public long getMinFileSize() {
        return minFileSize;
    }


    public void setMinFileSize(long minFileSize) {
        this.minFileSize = minFileSize;
    }


    public long getMaxFileSize() {
        return maxFileSize;
    }


    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}

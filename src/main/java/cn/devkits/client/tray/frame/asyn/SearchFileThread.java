package cn.devkits.client.tray.frame.asyn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import com.google.common.collect.Sets;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.model.LargeDuplicateActionModel;

/**
 * 
 * 遍历文件线程
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:14
 */
public class SearchFileThread extends Thread {

    private LargeDuplicateFilesFrame frame;
    private ExecutorService newFixedThreadPool;
    private int maxFileSize;
    private int minFileSize;
    private FilenameFilter filenameFilter;

    public SearchFileThread(LargeDuplicateActionModel actionModel) {
        this.frame = actionModel.getFrame();
        this.newFixedThreadPool = actionModel.getThreadPool();
        this.maxFileSize = actionModel.getMaxFileSize();
        this.minFileSize = actionModel.getMinFileSize();
        this.filenameFilter = actionModel.getFileFilter();
    }

    @Override
    public void run() {
        File[] listRoots = File.listRoots();
        for (File file : listRoots) {
            recursiveSearch(file);
        }
        newFixedThreadPool.shutdown();
        frame.finishedSearch();
    }

    private void recursiveSearch(File dirFile) {
        File[] listFiles = dirFile.listFiles(filenameFilter);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    recursiveSearch(file);
                } else {
                    if (file.length() > maxFileSize) {
                        // 窗口关闭以后，快速退出
                        if (!newFixedThreadPool.isShutdown()) {
                            frame.updateStatusLineText("Scanner File: " + file.getAbsolutePath());
                            newFixedThreadPool.submit(new FileMd5Thread(frame, file));
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }
}

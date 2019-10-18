package cn.devkits.client.tray.frame.asyn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import cn.devkits.client.tray.filter.DKFilenameFilter;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;

/**
 * 遍历文件线程
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:14
 */
public class SearchFileThread extends Thread {

    private LargeDuplicateFilesFrame frame;
    private long maxFileSize;
    private long minFileSize;
    private FilenameFilter filenameFilter;

    public SearchFileThread(LargeDuplicateFilesFrame frame, long max, long min) {
        this.frame = frame;
        this.maxFileSize = max;
        this.minFileSize = min;
        this.filenameFilter = new DKFilenameFilter(frame);
    }

    @Override
    public void run() {
        File[] listRoots = File.listRoots();
        for (File file : listRoots) {
            recursiveSearch(file);
        }
        frame.getTheadPool().shutdown();
        frame.searchComplete();
    }

    private void recursiveSearch(File dirFile) {
        ExecutorService theadPool = frame.getTheadPool(); // 窗口关闭以后，快速退出
        if (!theadPool.isShutdown()) {
            File[] listFiles = dirFile.listFiles(filenameFilter);
            if (listFiles != null) {
                for (File file : listFiles) {
                    frame.updateStatusLineText("Scanning: " + file.getAbsolutePath());
                    if (file.isDirectory()) {
                        recursiveSearch(file);
                    } else {
                        if (file.length() < maxFileSize && file.length() > minFileSize) {
                            theadPool.submit(new FileMd5Thread(frame, file));
                        }
                    }
                }
            }
        }


    }
}

package cn.devkits.client.tray.frame.asyn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.concurrent.ExecutorService;

import cn.devkits.client.tray.filter.DKFilenameFilter;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.util.DKFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * 遍历文件线程，获取文件大小相同的文件
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:14
 */
public class SearchFileThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchFileThread.class);

    private LargeDuplicateFilesFrame frame;
    private long maxFileSize;
    private long minFileSize;
    private FilenameFilter filenameFilter;

    //文件大小、文件路径集
    private Map<Long, Set<String>> fileMaps = new HashMap<>();

    public SearchFileThread(LargeDuplicateFilesFrame frame) {
        this.frame = frame;
        this.maxFileSize = getFileSizeThreshold(frame, true);
        this.minFileSize = getFileSizeThreshold(frame, false);
        this.filenameFilter = new DKFilenameFilter(frame);
    }

    @Override
    public void run() {
        String text = frame.getSearchPath().getText();
        if ("Computer".equals(text) || text.trim().isEmpty()) {
            int warning = JOptionPane.showConfirmDialog(frame, "Are you sure to scan the entire system, which it may take a long time?", "Warning", JOptionPane.OK_CANCEL_OPTION);
            if (JOptionPane.YES_OPTION == warning) {
                File[] listRoots = File.listRoots();
                for (File file : listRoots) {
                    recursiveSearch(file);
                }
            } else {
                frame.getStartCancelBtn().setText(LargeDuplicateFilesFrame.BUTTONS_TEXT[0]);
                return;
            }
        } else {
            File dirFile = new File(text);
            if (dirFile.isDirectory()) {
                recursiveSearch(dirFile);
            } else {
                JOptionPane.showMessageDialog(frame, "The input search path is a invalid directory!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
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
                        filterFileAndSubmit(theadPool, file);
                    }
                }
            }
        }
    }

    private void filterFileAndSubmit(ExecutorService theadPool, File file) {
        if (file.length() < maxFileSize && file.length() > minFileSize) {

            Set<String> fileSets = fileMaps.get(file.length());
            if (fileSets == null) {
                fileSets = new HashSet<>();
                fileMaps.put(file.length(), fileSets);
            } else if (fileSets.size() == 1) {
                theadPool.submit(new FileMd5Thread(frame, new File(fileSets.iterator().next())));
                theadPool.submit(new FileMd5Thread(frame, file));
            } else {
                theadPool.submit(new FileMd5Thread(frame, file));
            }
            fileSets.add(file.getAbsolutePath());
        }
    }


    private long getFileSizeThreshold(LargeDuplicateFilesFrame frame, boolean isMaxThreshold) {
        long val = convertUnti2Val(frame);
        String minText = frame.getMinFileSizeInput().getText();
        String maxText = frame.getMaxFileSizeInput().getText();
        if (isMaxThreshold) {
            try {
                if (maxText.trim().isEmpty()) {
                    LOGGER.info("Max value will set to default: Long.MAX_VALUE");
                    return Long.MAX_VALUE;
                }

                return (long) (Double.parseDouble(maxText) * val);
            } catch (NumberFormatException e) {
                LOGGER.error("Resolve the max value of user input failed: " + maxText);
                return Long.MAX_VALUE;
            }
        } else {
            try {
                return (long) (Double.parseDouble(minText) * val);
            } catch (NumberFormatException e) {
                LOGGER.error("Resolve the min value of user input failed: " + minText);
                return 0;
            }
        }
    }

    private long convertUnti2Val(LargeDuplicateFilesFrame frame) {
        String fileUnit = (String) frame.getFileSizeUnitComboBox().getSelectedItem();
        return DKFileUtil.convertUnti2Val(fileUnit);
    }
}

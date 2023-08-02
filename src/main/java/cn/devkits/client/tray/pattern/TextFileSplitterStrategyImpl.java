/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.pattern;

import cn.devkits.client.tray.model.FileSpliterModel;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.IoUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JRadioButton;

/**
 * 文本文件切割策略实现
 *
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月14日 下午12:08:29
 */
public class TextFileSplitterStrategyImpl extends TextFileSpliterStrategy implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileSplitterStrategyImpl.class);
    private static final int BUFFER_LINE_NUM = 1000;
    private final FileSpliterModel splitModel;
    private final String[] strategyNames;
    private final JRadioButton current;
    private final String param;

    public TextFileSplitterStrategyImpl(String[] strings, JRadioButton current, String param, FileSpliterModel splitModel) {
        this.strategyNames = strings;
        this.current = current;
        this.param = param;
        this.splitModel = splitModel;
    }

    @Override
    public void segmentSplit(int n) {
        splitModel.addMsg("Start to split file with segment: " + n);
        long length = splitModel.getFile().length();
        segmentSplitBySize(length / n);
    }

    @Override
    public void segmentSplitByFixedSize(long size) {
        splitModel.addMsg("Start to split file with fixed size: " + size + " KB");
    }


    @Override
    void segmentSplitByLines(int line) {
        splitModel.addMsg("Start to split file with fixed lines: " + line);

        printStartInfo();

        String splitFileName = splitModel.getFile().getName();

        long start = System.currentTimeMillis();
        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator(splitModel.getFile());

            int fileIndex = 1, lineIndex = 0;
            String outputFolderPath = splitModel.getOutputFolderPath();
            File segmentFile = wrapSegmentFileName(outputFolderPath, splitFileName, fileIndex);
            List<String> buffer = new ArrayList<String>();

            while (lineIterator.hasNext()) {
                String nextLine = lineIterator.nextLine();
                buffer.add(nextLine);
                lineIndex++;

                // 避免用户设置行数过大导致内存溢出
                if (buffer.size() == BUFFER_LINE_NUM) {
                    flushFileData(segmentFile, buffer);
                }

                if (lineIndex % line == 0) {
                    flushFileData(segmentFile, buffer);
                    fileIndex++;
                    segmentFile = wrapSegmentFileName(outputFolderPath, splitFileName, fileIndex);
                }
            }
            if (!buffer.isEmpty()) {
                flushFileData(segmentFile, buffer);
            }
        } catch (IOException e) {
            LOGGER.error("Split file with {} lines occurred an error!", line);
            splitModel.addMsg("Split file with " + line + " lines occurred an error!");
        } finally {
            if (lineIterator != null) {
                IoUtils.closeQuietly(lineIterator);
            }
        }
        splitModel.addMsg("Total time:" + (System.currentTimeMillis() - start));
        splitModel.finishSplit();
    }


    private void flushFileData(File segmentFile, List<String> buffer) throws IOException {
        FileUtils.writeLines(segmentFile, buffer, true);
        buffer.clear();
        splitModel.addMsg("Generating file: " + segmentFile.getAbsolutePath());
    }


    private void printStartInfo() {
        File originFile = splitModel.getFile();

        splitModel.addMsg("Original file size: " + DKFileUtil.formatBytes(originFile.length()));
        splitModel.addMsg("Original file path: " + originFile.getAbsolutePath());
        splitModel.addMsg("Split start time: " + DKDateTimeUtil.currentTimeStrWithPattern(DKDateTimeUtil.DATE_TIME_PATTERN_DEFAULT));

        String outputFolderPath = splitModel.getOutputFolderPath();
        splitModel.addMsg("The output folder: " + outputFolderPath);
    }


    private File wrapSegmentFileName(String outputFolderPath, String splitFileName, int fileIndex) {

        StringBuilder sb = new StringBuilder(outputFolderPath);
        sb.append(File.separator).append(FilenameUtils.getBaseName(splitFileName)).append("_").append(fileIndex).append(".").append(FilenameUtils.getExtension(splitFileName));

        return new File(sb.toString());
    }

    private void segmentSplitBySize(long size) {
        File originFile = splitModel.getFile();
        printStartInfo();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(originFile));
            // bufferedReader.read(cbuf, off, len);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        splitModel.addMsg("End Time: " + DKDateTimeUtil.currentTimeStrWithPattern(DKDateTimeUtil.DATE_TIME_PATTERN_DEFAULT));
        splitModel.finishSplit();
    }

    @Override
    public void run() {
        if (strategyNames[0].equals(current.getText())) {
            segmentSplit(Integer.parseInt(param));
        } else if (strategyNames[1].equals(current.getText())) {
            segmentSplitByLines(Integer.parseInt(param));
        } else {
            segmentSplitBySize(Long.parseLong(param));
        }
    }
}

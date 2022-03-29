/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.frame.DuplicateFilesFrame;
import cn.devkits.client.tray.frame.asyn.SearchFileThread;
import cn.devkits.client.util.DKSysUIUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;

/**
 * 启动、取消按钮事件监听
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年10月5日 下午3:25:08
 */
public class StartEndActionListener implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartEndActionListener.class);

    private DuplicateFilesFrame frame;

    public StartEndActionListener(DuplicateFilesFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        ExecutorService threadPool = frame.getTheadPool();

        if (DuplicateFilesFrame.BUTTONS_TEXT[0].equals(e.getActionCommand())) {
            if (threadPool.isShutdown()) {
                frame.initDataModel();
            }
            new Thread(new SearchFileThread(frame)).start();
            btn.setText(DuplicateFilesFrame.BUTTONS_TEXT[1]);
            frame.updateStatusLineText(DKSysUIUtil.getLocaleWithEllipsis("DUP_FILE_STATUS_LINE_START"));
        } else {
            threadPool.shutdownNow();
            btn.setText(DuplicateFilesFrame.BUTTONS_TEXT[0]);
            frame.updateStatusLineText(DKSysUIUtil.getLocale("DUP_FILE_STATUS_LINE_CANCEL"));
        }
    }
}
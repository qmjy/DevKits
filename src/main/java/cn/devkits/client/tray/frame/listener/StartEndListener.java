package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.frame.asyn.SearchFileThread;
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
public class StartEndListener implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartEndListener.class);

    private LargeDuplicateFilesFrame frame;

    public StartEndListener(LargeDuplicateFilesFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        ExecutorService threadPool = frame.getTheadPool();

        if (LargeDuplicateFilesFrame.BUTTONS_TEXT[0].equals(e.getActionCommand())) {
            if (threadPool.isShutdown()) {
                frame.initDataModel();
            }
            new Thread(new SearchFileThread(frame)).start();
            btn.setText(LargeDuplicateFilesFrame.BUTTONS_TEXT[1]);
            frame.updateStatusLineText("Start to scanner File...");
        } else {
            threadPool.shutdownNow();
            btn.setText(LargeDuplicateFilesFrame.BUTTONS_TEXT[0]);
            frame.updateStatusLineText("Scanner file canceled by user!");
        }
    }
}
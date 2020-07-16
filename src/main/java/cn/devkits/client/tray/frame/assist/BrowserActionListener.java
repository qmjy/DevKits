/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame.assist;

import cn.devkits.client.component.FileChoosePreviewerComponent;
import cn.devkits.client.tray.frame.DKFrameChosenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * 浏览文件选择事件监听器
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月24日 下午10:09:27
 */
public class BrowserActionListener implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserActionListener.class);
    private DKFrameChosenable frame;
    private FileFilter[] filters;
    private boolean callback;
    private String title;

    /**
     * constructor
     * @param frame frame
     * @param filters browse filter
     * @param title dialog title
     * @param callback callback or not
     */
    public BrowserActionListener(DKFrameChosenable frame, FileFilter[] filters,String title, boolean callback) {
        this.frame = frame;
        this.filters = filters;
        this.callback = callback;
        this.title=title;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle(title);
        jfc.setAccessory(new FileChoosePreviewerComponent(jfc));
        createFilter(jfc);// 添加文件支持的类型

        int retval = jfc.showDialog(frame.getObj(), "OK");

        if (retval == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile() != null) {
                frame.updateSelectFilePath(jfc.getSelectedFile().getAbsolutePath());
            }
            if (callback) {
                frame.callback();
            }
        } else if (retval == JFileChooser.CANCEL_OPTION) {
            LOGGER.info("User cancelled operation. No file was chosen.");
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(frame.getObj(), "An error occurred. No file was chosen.");
        } else {
            JOptionPane.showMessageDialog(frame.getObj(), "Unknown operation occurred.");
        }
    }

    private void createFilter(JFileChooser jfc) {
        for (FileFilter fileFilter : filters) {
            jfc.addChoosableFileFilter(fileFilter);
        }
    }
}

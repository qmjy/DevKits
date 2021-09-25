/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.listener;

import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.frame.*;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * tray item click listener
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月24日 下午9:38:36
 */
public class TrayItemWindowListener implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayItemWindowListener.class);

    private MenuItemEnum itemEnum;

    public TrayItemWindowListener(MenuItemEnum codeFormat) {
        this.itemEnum = codeFormat;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = null;
        switch (itemEnum) {
            case CODEC:
                break;
            case SERVER_PORT:
                frame = new ServerPortsFrame();
                break;
            case CODE_FORMAT:
                frame = new CodeFormatFrame();
                break;
            case LDF:
                frame = new DuplicateFilesFrame();
                break;
            case SCRCAPTURE:
                DKSystemUtil.invokeLocalApp("QQSnapShot.exe");
                return;
            case OS_INFO_MORE:
                frame = new OsInfoDetailFrame();
                break;
            case LOGONUI:
                frame = new LogonImageManageFrame();
                break;
            case FILEEXPLORER:
                frame = new FileExplorersFrame();
                break;
            case TODOS:
                frame = new TodoListFrame();
                break;
            case QR:
                frame = new QrCodeFrame();
                break;
            case FILESPLITER:
                frame = new FileSpliterFrame();
                break;
            case HOSTS:
                openHostFile();
                return;
            default:
                break;
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void openHostFile() {
        if (DKSystemUtil.isWindows()) {
            StringBuilder sb = new StringBuilder(System.getenv("WINDIR"));
            sb.append(File.separator).append("System32").append(File.separator).append("drivers").append(File.separator).append("etc").append(File.separator).append("hosts");
            DKFileUtil.openTextFile(new File(sb.toString()));
        } else {
            JOptionPane.showMessageDialog(null, "This feature just support for windows, sorry!");
        }
    }
}

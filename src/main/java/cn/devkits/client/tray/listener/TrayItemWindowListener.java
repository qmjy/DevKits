package cn.devkits.client.tray.listener;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.frame.CodeFormatFrame;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.frame.ServerPortsFrame;

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
                frame = new LargeDuplicateFilesFrame();
                break;
            case SCRCAPTURE:
                invokeScreenTool();
                return;
            default:
                break;
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void invokeScreenTool() {
        if (Desktop.isDesktopSupported()) {
            LOGGER.info("This system support desktop!");
            String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            LOGGER.info("'QQSnapShot.exe' of path: " + rootPath);
            try {
                Desktop.getDesktop().open(new File(rootPath + "QQSnapShot.exe"));
                LOGGER.info("Invoke file successed: " + rootPath + "QQSnapShot.exe");
            } catch (IOException e) {
                LOGGER.error("Invoke file failed: " + rootPath);
            }
        }
    }

}

package cn.devkits.client.tray.listener;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.App;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.frame.CodeFormatFrame;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.frame.ServerPortsFrame;
import cn.devkits.client.util.DKSystemUtil;

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
            try {
                if (DKSystemUtil.isRunWithJar()) {
                    Desktop.getDesktop().open(new File("./QQSnapShot.exe"));
                } else {
                    String filePath = TrayItemWindowListener.class.getClassLoader().getResource("").getPath() + "QQSnapShot.exe";
                    Desktop.getDesktop().open(new File(filePath));
                }
            } catch (IOException e) {
                LOGGER.error("Invoke file failed: " + e.getMessage());
            }
        }
    }

}

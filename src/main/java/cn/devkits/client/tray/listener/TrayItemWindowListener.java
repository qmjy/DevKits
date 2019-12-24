package cn.devkits.client.tray.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.MenuItemEnum;
import cn.devkits.client.tray.frame.CodeFormatFrame;
import cn.devkits.client.tray.frame.FileExplorersFrame;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.tray.frame.LogonImageManageFrame;
import cn.devkits.client.tray.frame.OsInfoDetailFrame;
import cn.devkits.client.tray.frame.QrCodeFrame;
import cn.devkits.client.tray.frame.ServerPortsFrame;
import cn.devkits.client.util.DKSystemUtil;

/**
 * 
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
                frame = new LargeDuplicateFilesFrame();
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
            case QR:
                frame = new QrCodeFrame();
                break;
            default:
                break;
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}

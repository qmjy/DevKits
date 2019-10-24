package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

/**
 * 
 * 系统登录界面背景管理
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月24日 下午9:41:50
 */
public class LogonImageManageFrame extends DKAbstractFrame {

    /** serialVersionUID */
    private static final long serialVersionUID = 950625064408939379L;
    // file path text
    private JTextField imgFilePathTextField;

    public LogonImageManageFrame() {
        super("Logon Background Manager", 0.7f, 0.3f);

        initUI(getRootPane());
        initListener();
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        centerPanel.add(new JLabel("Choose a picture:"));
        this.imgFilePathTextField = new JTextField(40);
        imgFilePathTextField.setEditable(false);
        centerPanel.add(imgFilePathTextField);

        JButton browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(new BrowserActionListener(this));
        centerPanel.add(browseBtn);

        jRootPane.add(centerPanel, BorderLayout.CENTER);

        jRootPane.add(createButtonPanel(), BorderLayout.PAGE_END);
    }

    private Component createButtonPanel() {
        JButton button = new JButton("OK");

        button.addActionListener(new LogonImgManageListener(this));

        // Center the button in a panel with some space around it.
        JPanel pane = new JPanel(); // use default FlowLayout
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.add(button);

        return pane;
    }

    @Override
    protected void initListener() {

    }

    public void close() {
        this.dispose();
    }

    public void updateSelectFilePath(String absolutePath) {
        imgFilePathTextField.setText(absolutePath);
    }

    public JTextField getImgFilePathTextField() {
        return imgFilePathTextField;
    }
}


/**
 * 
 * update windows logon background image registry
 * demo:https://www.rgagnon.com/javadetails/java-read-write-windows-registry-using-jna.html
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月24日 下午10:58:49
 */
class LogonImgManageListener implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogonImgManageListener.class);

    private String registryPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Authentication\\LogonUI\\Background";
    private String registryKey = "OEMBackground";
    private String bgImgPath = "C:\\Windows\\System32\\oobe\\info\\Backgrounds\\info\\Backgrounds";
    private String bgImgFileName = "backgroundDefault.jpg";

    private LogonImageManageFrame frame;

    public LogonImgManageListener(LogonImageManageFrame logonImageManageFrame) {
        this.frame = logonImageManageFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean registryValueExists = Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, registryPath, registryKey);
        if (registryValueExists) {
            int val = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, registryPath, registryKey);
            if (val != 1) {
                LOGGER.info("registry '{}' value '{}' is not correct!", registryPath, val);
                Advapi32Util.registrySetIntValue(WinReg.HKEY_LOCAL_MACHINE, registryPath, registryKey, 1);
            }
        } else {
            LOGGER.info("registry not exist: path is {}, key is {}.", registryPath, registryKey);
            Advapi32Util.registrySetIntValue(WinReg.HKEY_LOCAL_MACHINE, registryPath, registryKey, 1);

            System.out.println(Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, registryPath, registryKey));
        }

        File targetFolder = new File(bgImgPath + File.separator);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        File sourceFile = new File(frame.getImgFilePathTextField().getText());
        File targetFile = new File(bgImgPath + File.separator + bgImgFileName);
        if (targetFile.exists()) {
            boolean delete = targetFile.delete();
            if (!delete) {
                LOGGER.error("Delete old file failed: {}", targetFile);
            }
        }
        sourceFile.renameTo(targetFile);

        frame.close();
    }
}


/**
 * 
 * 浏览文件选择事件监听器
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年10月24日 下午10:09:27
 */
class BrowserActionListener implements ActionListener {
    private LogonImageManageFrame frame;

    public BrowserActionListener(LogonImageManageFrame logonImageManageFrame) {
        this.frame = logonImageManageFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "*.jpg;*.jpeg";
            }

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg");
            }
        });

        int showDialog = jfc.showDialog(new JLabel(), "选择");
        if (JFileChooser.APPROVE_OPTION == showDialog) {
            if (jfc.getSelectedFile() != null) {
                frame.updateSelectFilePath(jfc.getSelectedFile().getAbsolutePath());
            }
        }
    }
}

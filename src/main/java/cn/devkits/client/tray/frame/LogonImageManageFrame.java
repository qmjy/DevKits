package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKSystemUtil;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 
 * 系统登录界面背景管理<br>
 * https://blogs.technet.microsoft.com/deploymentguys/2011/08/22/windows-7-background-customization/
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

        jRootPane.add(createButtonPanel(jRootPane), BorderLayout.PAGE_END);
    }

    private Component createButtonPanel(JRootPane jRootPane) {
        JButton button = new JButton("OK");
        jRootPane.setDefaultButton(button);

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

    private final int WIN_LOGONO_BG_MAX_SIZE = 256 * 1024;
    private final String registryPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Authentication\\LogonUI\\Background";
    private final String registryKey = "OEMBackground";
    private final String bgImgPath = System.getenv("windir") + "\\System32\\oobe\\info\\Backgrounds";
    private final String bgImgFileName = "backgroundDefault.jpg";


    private LogonImageManageFrame frame;

    public LogonImgManageListener(LogonImageManageFrame logonImageManageFrame) {
        this.frame = logonImageManageFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        copyFile();
        updateRegistry();
        frame.close();
    }

    private void copyFile() {
        File targetFolder = new File(bgImgPath + File.separator);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        Optional<File> sourceFile = loadUserChoosedFile();

        if (sourceFile.isPresent()) {
            Optional<File> tempFile = doCompress(sourceFile.get());
            tempFile.ifPresent((file) -> {
                File targetFile = new File(bgImgPath + File.separator + bgImgFileName);
                if (targetFile.exists()) {
                    boolean delete = targetFile.delete();
                    if (!delete) {
                        LOGGER.error("Delete old file failed: {}", targetFile);
                    }
                }
                file.renameTo(targetFile);
            });
        } else {
            JOptionPane.showMessageDialog(frame, "The select file error!", "File Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Optional<File> loadUserChoosedFile() {
        String text = frame.getImgFilePathTextField().getText();
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new File(text));
    }

    private Optional<File> doCompress(File sourceFile) {
        String tempTimeStr = DKDateTimeUtil.currentTimeStrWithPattern(DKDateTimeUtil.DATE_TIME_PATTERN_FULL);
        File tempFile = new File(DKSystemUtil.getSystemTempDir() + tempTimeStr + "." + FileUtils.getExtension(sourceFile.getName()));

        Dimension screenSize = DKSystemUtil.getScreenSize();

        // windows logon background image size threshold 256KB
        if (sourceFile.length() > WIN_LOGONO_BG_MAX_SIZE) {
            try {
                for (int i = 9; i > 0; i--) {
                    Thumbnails.of(sourceFile).size(screenSize.width, screenSize.height).outputQuality(i * 0.1f).toFile(tempFile);
                    if (tempFile.length() <= WIN_LOGONO_BG_MAX_SIZE) {
                        return Optional.of(tempFile);
                    }
                }
                return Optional.empty();
            } catch (IOException e) {
                LOGGER.error("Thumbnails compress file error: {}", e.getMessage());
            }
        } else {
            try {
                FileUtils.copyFile(sourceFile, tempFile);
                return Optional.of(tempFile);
            } catch (IOException e) {
                LOGGER.error("Copy file '{}' to '{}' error: ", sourceFile.getAbsolutePath(), tempFile.getAbsolutePath(), e.getMessage());
            }
        }
        return Optional.empty();
    }

    private void updateRegistry() {
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

package cn.devkits.client.tray.frame;

import static javax.swing.JFileChooser.SELECTED_FILE_CHANGED_PROPERTY;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(LogonImageManageFrame.class);
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
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));

        centerPanel.add(new JLabel("Choose a picture:"));
        this.imgFilePathTextField = new JTextField(35);
        imgFilePathTextField.setEditable(false);
        centerPanel.add(imgFilePathTextField);

        JButton browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(new BrowserActionListener(this));
        centerPanel.add(browseBtn);
        JLabel note = new JLabel("Note: The feature is only valid for Windows 7!");
        note.setForeground(Color.RED);
        centerPanel.add(note);

        jRootPane.add(centerPanel, BorderLayout.CENTER);

        jRootPane.add(createButtonPanel(jRootPane), BorderLayout.PAGE_END);
    }

    private Component createButtonPanel(JRootPane jRootPane) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());

        JButton button = new JButton("Apply");
        jRootPane.setDefaultButton(button);

        button.addActionListener(new LogonImgManageListener(this));
        buttonPane.add(button);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();;
            Container parent = btn.getParent().getParent().getParent();
            if (parent instanceof LogonImageManageFrame) {
                LogonImageManageFrame root = (LogonImageManageFrame) parent;
                root.dispose();
            }
        });
        buttonPane.add(cancelBtn);

        return buttonPane;
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
        if (copyFile()) {
            updateRegistry();
            frame.close();
        }
    }

    private boolean copyFile() {
        File targetFolder = new File(bgImgPath + File.separator);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        Optional<File> sourceFile = loadUserChoosedFile();

        if (sourceFile.isPresent()) {
            Optional<File> tempFile = doCompress(sourceFile.get());
            File file = tempFile.get();
            File targetFile = new File(bgImgPath + File.separator + bgImgFileName);
            if (targetFile.exists()) {
                if (!targetFile.delete()) {
                    LOGGER.error("Delete old file failed: {}", targetFile);
                }
            }
            return file.renameTo(targetFile);
        } else {
            JOptionPane.showMessageDialog(frame, "Select file error, you have to select one file at least!", "File Error", JOptionPane.WARNING_MESSAGE);
            return false;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserActionListener.class);
    private LogonImageManageFrame frame;

    public BrowserActionListener(LogonImageManageFrame logonImageManageFrame) {
        this.frame = logonImageManageFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setAccessory(new FilePreviewer(jfc));
        createFilter(jfc);// 添加文件支持的类型

        int retval = jfc.showDialog(frame, "OK");

        if (retval == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile() != null) {
                frame.updateSelectFilePath(jfc.getSelectedFile().getAbsolutePath());
            }
        } else if (retval == JFileChooser.CANCEL_OPTION) {
            LOGGER.info("User cancelled operation. No file was chosen.");
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(frame, "An error occurred. No file was chosen.");
        } else {
            JOptionPane.showMessageDialog(frame, "Unknown operation occurred.");
        }
    }

    private void createFilter(JFileChooser jfc) {
        FileFilter jpgFilter = createFileFilter("JPEG Compge Files", true, "gif");
        FileFilter bothFilter = createFileFilter("JPEressed Image Files", true, "jpg");
        FileFilter gifFilter = createFileFilter("GIF ImaG and GIF Image Files", true, "jpg", "gif");
        jfc.addChoosableFileFilter(bothFilter);
        jfc.addChoosableFileFilter(jpgFilter);
        jfc.addChoosableFileFilter(gifFilter);
    }

    private FileFilter createFileFilter(String description, boolean showExtensionInDescription, String... extensions) {
        if (showExtensionInDescription) {
            description = createFileNameFilterDescriptionFromExtensions(description, extensions);
        }
        return new FileNameExtensionFilter(description, extensions);
    }

    private String createFileNameFilterDescriptionFromExtensions(String description, String[] extensions) {
        String fullDescription = (description == null) ? "(" : description + " (";
        // build the description from the extension list
        fullDescription += "." + extensions[0];
        for (int i = 1; i < extensions.length; i++) {
            fullDescription += ", .";
            fullDescription += extensions[i];
        }
        fullDescription += ")";
        return fullDescription;
    }
}


/**
 * 
 * 文件预览
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月11日 下午11:09:31
 */
class FilePreviewer extends JComponent implements PropertyChangeListener {

    /** serialVersionUID */
    private static final long serialVersionUID = 5051395170955469946L;
    ImageIcon thumbnail = null;

    public FilePreviewer(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    public void loadImage(File f) {
        if (f == null) {
            thumbnail = null;
        } else {
            ImageIcon tmpIcon = new ImageIcon(f.getPath());
            if (tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            if (isShowing()) {
                loadImage((File) e.getNewValue());
                repaint();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (thumbnail != null) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}

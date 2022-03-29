/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 系统登录界面背景管理<br>
 * https://blogs.technet.microsoft.com/deploymentguys/2011/08/22/windows-7-background-customization/
 *
 * @author Shaofeng Liu
 * @version 1.0.0
 * @time 2019年10月24日 下午9:41:50
 */
public class LogonImageManageFrame extends DKAbstractFrame implements DKFrameChosenable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 950625064408939379L;
    // file path text
    private JTextField imgFilePathTextField;
    private JButton applyBtn;
    private JButton browseBtn;
    private JButton cancelBtn;

    public LogonImageManageFrame() {
        super(DKSysUIUtil.getLocale("LOGON_BG_MNG"), 0.7f, 0.25f);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isResizable() {
        return false;
    }


    @Override
    protected void initUI(Container container) {
        JPanel centerPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        centerPanel.setLayout(layout);

        JLabel comp = new JLabel(DKSysUIUtil.getLocaleWithColon("LOGON_BG_CHOOSE_IMG"));
        this.imgFilePathTextField = new JTextField(38);
        imgFilePathTextField.setEditable(false);

        this.browseBtn = new JButton(DKSysUIUtil.getLocaleWithEllipsis("COMMON_BTNS_BROWSE"));

        centerPanel.add(comp);
        centerPanel.add(imgFilePathTextField);
        centerPanel.add(browseBtn);

        SpringLayout.Constraints backBtnCons = layout.getConstraints(comp);
        backBtnCons.setX(Spring.constant(15));
        backBtnCons.setY(Spring.constant(15));

        SpringLayout.Constraints homeBtnCons = layout.getConstraints(imgFilePathTextField);
        homeBtnCons.setX(Spring.sum(Spring.constant(15), backBtnCons.getConstraint(SpringLayout.EAST)));
        homeBtnCons.setY(Spring.constant(10));

        SpringLayout.Constraints textFieldCons = layout.getConstraints(browseBtn);
        textFieldCons.setX(Spring.sum(Spring.constant(15), homeBtnCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(10));

        // 设置容器的 东边坐标 为 文本框的东边坐标 + 5
        SpringLayout.Constraints centerPanelCons = layout.getConstraints(centerPanel);
        centerPanelCons.setConstraint(SpringLayout.EAST, Spring.sum(textFieldCons.getConstraint(SpringLayout.EAST), Spring.constant(15)));

        container.add(centerPanel, BorderLayout.CENTER);

        container.add(createButtonPanel(container), BorderLayout.PAGE_END);
    }


    private Component createButtonPanel(Container jRootPane) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel note = new JLabel(DKSysUIUtil.getLocaleWithExclamation("LOGON_BG_NOTE"));
        note.setForeground(Color.RED);
        buttonPane.add(note);

        buttonPane.add(Box.createHorizontalGlue());

        this.applyBtn = new JButton(DKSysUIUtil.getLocale("COMMON_BTNS_APPLY"));

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        this.cancelBtn = new JButton(DKSysUIUtil.getLocale("COMMON_BTNS_CANCEL"));
        buttonPane.add(cancelBtn);

        return buttonPane;
    }

    @Override
    protected void initListener() {
        FileFilter[] filters = new FileFilter[]{DKSysUIUtil.createFileFilter("Graphics Interchange Format", true, "gif"), DKSysUIUtil.createFileFilter("JPEG Compge Files", true, "jpg"),
                DKSysUIUtil.createFileFilter("GIF ImaG and GIF Image Files", true, "jpg", "gif")};

        browseBtn.addActionListener(new BrowserActionListener(this, filters, "Background Image", JFileChooser.FILES_ONLY, false));
        applyBtn.addActionListener(new LogonImgManageListener(this));
        cancelBtn.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();
            Container parent = btn.getRootPane().getParent();
            if (parent instanceof LogonImageManageFrame) {
                LogonImageManageFrame root = (LogonImageManageFrame) parent;
                root.dispose();
            }
        });
    }

    public void close() {
        this.dispose();
    }

    @Override
    public void updateSelectFilePath(String absolutePath) {
        imgFilePathTextField.setText(absolutePath);
    }

    public JTextField getImgFilePathTextField() {
        return imgFilePathTextField;
    }

    @Override
    public void callback() {
        // TODO Auto-generated method stub
    }

    @Override
    public Component getObj() {
        return this;
    }
}


/**
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
        if (isImg()) {
            if (copyFile()) {
                updateRegistry();
                frame.close();
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    DKSysUIUtil.getLocaleWithExclamation("LOGON_BG_INPUT_FILE_INVALID"),
                    DKSysUIUtil.getLocale("LOGON_BG_INPUT_FILE_TITLE"),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isImg() {
        Optional<File> choosedFile = loadUserChoosedFile();
        if (choosedFile.isPresent()) {
            return DKFileUtil.isRealImg(choosedFile.get());
        }
        return false;
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
            JOptionPane.showMessageDialog(frame,
                    DKSysUIUtil.getLocaleWithExclamation("LOGON_BG_INPUT_FILE_EMPTY_MSG"),
                    DKSysUIUtil.getLocale("LOGON_BG_INPUT_FILE_EMPTY_TITLE"),
                    JOptionPane.WARNING_MESSAGE);
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
        File tempFile = new File(DKSysUtil.getSystemTempDir() + tempTimeStr + "." + FileUtils.getExtension(sourceFile.getName()));

        Dimension screenSize = DKSysUtil.getScreenSize();

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

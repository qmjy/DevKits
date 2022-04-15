/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import cn.devkits.client.tray.model.FileTableCellRender;
import cn.devkits.client.util.DKSysUtil;
import com.google.common.collect.Lists;
import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * Mutil File Explorers
 * <br>
 * https://blog.csdn.net/xietansheng/article/details/72814492
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年12月1日 下午3:03:56
 */
public class FileExplorerPanel extends JPanel {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2230863950855742735L;
    private static final String HOME_PATH = DKSysUtil.getHomePath();

    private JButton backBtn;
    private JButton homeBtn;
    private JButton forwardBtn;
    private JTextField currentPathTextField;

    private FilesTableModel model = new FilesTableModel(HOME_PATH);
    private JTable filesTable = new JTable(model);
    private JLabel statusBar;
    private List<String> history = Lists.newArrayList(HOME_PATH);
    private int historyIndex = 0;


    public FileExplorerPanel() {
        super(new BorderLayout());

        initUI();
        initListener();
    }

    private void initUI() {
        Icon leftIcon = IconFontSwing.buildIcon(FontAwesome.ARROW_LEFT, 16, new Color(50, 50, 50));
        this.backBtn = new JButton(leftIcon);
        backBtn.setToolTipText("Back");
        backBtn.setEnabled(false);

        Icon homeIcon = IconFontSwing.buildIcon(FontAwesome.HOME, 16, new Color(50, 50, 50));
        this.homeBtn = new JButton(homeIcon);
        homeBtn.setToolTipText("Home");

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.ARROW_RIGHT, 16, new Color(50, 50, 50));
        this.forwardBtn = new JButton(rightIcon);
        forwardBtn.setToolTipText("Forward");

        this.currentPathTextField = new JTextField(HOME_PATH);

        JPanel jPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        jPanel.setLayout(layout);

        jPanel.add(backBtn);
        jPanel.add(homeBtn);
        jPanel.add(forwardBtn);
        jPanel.add(currentPathTextField);

        SpringLayout.Constraints backBtnCons = layout.getConstraints(backBtn);
        backBtnCons.setX(Spring.constant(5));
        backBtnCons.setY(Spring.constant(5));

        SpringLayout.Constraints homeBtnCons = layout.getConstraints(homeBtn);
        homeBtnCons.setX(Spring.sum(Spring.constant(5), backBtnCons.getConstraint(SpringLayout.EAST)));
        homeBtnCons.setY(Spring.constant(5));

        SpringLayout.Constraints forwardBtnCons = layout.getConstraints(forwardBtn);
        forwardBtnCons.setX(Spring.sum(Spring.constant(5), homeBtnCons.getConstraint(SpringLayout.EAST)));
        forwardBtnCons.setY(Spring.constant(5));

        SpringLayout.Constraints textFieldCons = layout.getConstraints(currentPathTextField);
        textFieldCons.setX(Spring.sum(Spring.constant(5), forwardBtnCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(5));

        // Adjust constraints for the content pane.
        DKSysUIUtil.setContainerSize(jPanel, 5);

        add(jPanel, BorderLayout.NORTH);

        filesTable.setDefaultRenderer(File.class, new FileTableCellRender());
        // arbitrary size adjustment to better account for icons
        filesTable.setRowHeight((int) (filesTable.getRowHeight() * 1.3));
        add(new JScrollPane(filesTable), BorderLayout.CENTER);

        this.statusBar = new JLabel(DKSysUIUtil.getLocaleWithEllipsis("COMMON_LABEL_TXT_READY"));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void initListener() {
        homeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File dir = new File(HOME_PATH);
                loadDirFiles(dir, true);
            }
        });

        currentPathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 判断按下的键是否是回车键
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadUserInput();
                }
            }
        });

        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowNum = filesTable.getSelectedRow();
                File file = (File) filesTable.getValueAt(rowNum, 0);
                if (file != null) {
                    if (e.getClickCount() == 1) {
                        DKSysUIUtil.enableRightClickSelect(e, filesTable);
                        updateStatusBar(file.getName());
                    } else if (e.getClickCount() == 2) {
                        openFileOrDir(file.getName());
                    }
                }
            }
        });

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = history.get(--historyIndex);
                loadDirFilesWithoutHistory(path);

                backBtn.setEnabled(historyIndex > 0);
            }
        });

        forwardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = history.get(++historyIndex);
                loadDirFilesWithoutHistory(path);

                backBtn.setEnabled(historyIndex < history.size());
            }
        });
    }

    private void loadUserInput() {
        String text = currentPathTextField.getText();
        if (text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(currentPathTextField, "The input file path is empty!");
        }

        File file = new File(text.trim());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(currentPathTextField, "The input file path is invalid!");
        }

        if (file.isDirectory()) {
            loadDirFiles(file, false);
        } else {
            DKFileUtil.openFile(file);
        }
    }

    private void loadDirFiles(File newFileDir, boolean needUpdateTextField) {
        String path = newFileDir.getAbsolutePath();

        history.add(path);
        historyIndex++;
        backBtn.setEnabled(!history.isEmpty());

        updateUI(path, needUpdateTextField);
    }

    private void loadDirFilesWithoutHistory(String filePath) {
        updateUI(filePath, true);
    }

    private void updateUI(String path, boolean needUpdateTextField) {
        if (needUpdateTextField) {
            currentPathTextField.setText(path);
        }

        model.updateRoot(path);
        filesTable.setModel(model);
        filesTable.repaint();
    }

    private void updateStatusBar(String fileName) {
        Optional<File> selectFile = getSelectFile(fileName);
        if (selectFile.isPresent()) {
            statusBar.setText(selectFile.get().getAbsolutePath());
        }
    }

    private Optional<File> getSelectFile(String fileName) {
        File[] fileLists = new File(currentPathTextField.getText()).listFiles();
        for (File file : fileLists) {
            if (file.getAbsolutePath().endsWith(fileName)) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    private void openFileOrDir(String fileName) {
        Optional<File> selectFile = getSelectFile(fileName);
        if (selectFile.isPresent()) {
            File file = selectFile.get();
            if (file.isDirectory()) {
                loadDirFiles(file, true);
            } else {
                DKFileUtil.openFile(file);
            }
        }
    }
}

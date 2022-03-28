/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.frame.listener.DuplicateFileTreeSelectionListener;
import cn.devkits.client.tray.frame.listener.StartEndListener;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 重复大文件检查<br>
 * Oracle Swing DEMO:https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#
 * GlassPaneDemo
 *
 * @author Shaofeng Liu
 * @version 1.0.0
 * @time 2019年9月26日 下午9:34:49
 */
public class DuplicateFilesFrame extends DKAbstractFrame {

    public static final String[] BUTTONS_TEXT = {DKSysUIUtil.getLocaleString("DUP_INPUT_LAB_START_DETECT"), DKSysUIUtil.getLocaleString("DUP_INPUT_LAB_STOP_DETECT")};

    private static final long serialVersionUID = 6081895254576694963L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateFilesFrame.class);

    private static final String[] FILE_TYPE_UNITS = {DKSysUIUtil.getLocaleString("DUP_INPUT_FILE_TYPE_ALL"),
            DKSysUIUtil.getLocaleString("DUP_INPUT_FILE_TYPE_DOCUMENT"), DKSysUIUtil.getLocaleString("DUP_INPUT_FILE_TYPE_IMAGE"),
            DKSysUIUtil.getLocaleString("DUP_INPUT_FILE_TYPE_AUDIO"), DKSysUIUtil.getLocaleString("DUP_INPUT_FILE_TYPE_VIDEO")};
    private static final String[] FILE_UNITS = {"Byte", "KB", "MB", "GB", "TB", "PB"};
    private static final String[] RIGHT_PANE_NAMES = {"LIST_PANE", "DETAIL_PANE"};

    private static final int COMPONENT_MARGIN_TOP_BASE = 5;
    private static final int COMPONENT_MARGIN_TOP_LABLE = 10;
    private static final int COMPONENT_MARGIN_RIGHT = 10;
    private static final int COMPONENT_MARGIN_RIGHT_IN = 5;

    /**
     * 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值
     */
    public static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private JTree tree = null;
    private JPopupMenu jtreeMenu = null;
    private JLabel statusLine = null;
    private JTextField searchPath;
    private JButton browseBtn;
    private JComboBox<String> fileTypeComboBox = null;
    private JTextField minFileSizeInput = null;
    private JTextField maxFileSizeInput = null;
    private JComboBox<String> fileSizeUnitComboBox = null;
    private JButton startCancelBtn = null;
    private JButton exportBtn = null;
    private JPanel rightPane;
    private CardLayout rightPaneLayout;
    private ExecutorService theadPool = null;

    private DefaultMutableTreeNode treeNode = null;
    private DefaultTreeModel treeModel = null;

    private ConcurrentHashMap<String, Set<String>> md5FilesMap = null;

    public DuplicateFilesFrame() {
        super(DKSysUIUtil.getLocaleString("DUP_WIN_TITLE"), 1.2f);
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.add(initNorthPane(), BorderLayout.NORTH);

        JSplitPane jSplitPane = new JSplitPane();

        md5FilesMap = new ConcurrentHashMap<>();
        treeNode = new DefaultMutableTreeNode(DKSysUIUtil.getLocaleString("DUP_TREE_NODE_ROOT"));
        treeModel = new DefaultTreeModel(treeNode);
        tree = new JTree(treeModel);
        initPopupMenu();

        // ToolTipManager.sharedInstance().registerComponent(tree);
        // TreeCellRenderer renderer = new LargeDuplicateFilesTreeCellRenderer();
        // tree.setCellRenderer(renderer);

        JScrollPane scrollPane = new JScrollPane(tree);
        jSplitPane.setLeftComponent(scrollPane);

        rightPane = new JPanel();
        rightPaneLayout = new CardLayout();
        rightPane.setLayout(rightPaneLayout);

        rightPane.add(new JScrollPane(), RIGHT_PANE_NAMES[0]);
        rightPane.add(new JPanel(), RIGHT_PANE_NAMES[1]);
        jSplitPane.setRightComponent(new JScrollPane(rightPane));

        jSplitPane.setResizeWeight(0.3);
        rootContainer.add(jSplitPane, BorderLayout.CENTER);

        statusLine = new JLabel(DKSysUIUtil.getLocaleString("DUP_FILE_STATUS_LINE_READY")) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Dimension d = this.getSize();
                g.setColor(Color.GRAY);
                g.drawLine(0, 0, d.width, 0);
            }
        };
        statusLine.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH, 25));
        rootContainer.add(statusLine, BorderLayout.SOUTH);

        initDataModel();
    }


    private JPanel initNorthPane() {
        JPanel northRootPane = new JPanel();
        SpringLayout mgr = new SpringLayout();
        northRootPane.setLayout(mgr);

        JLabel fileSearchPathLbl = new JLabel(DKSysUIUtil.getLocaleStringWithColon("DUP_INPUT_LAB_PATH"), JLabel.LEFT);
        this.searchPath = new JTextField();
        searchPath.setColumns(15);
        searchPath.setText("Computer");

        this.browseBtn = new JButton(DKSysUIUtil.getLocaleStringWithEllipsis("COMMON_BTNS_BROWSE"));
        browseBtn.setFocusPainted(false);

        JLabel fileTypeLabel = new JLabel(DKSysUIUtil.getLocaleStringWithColon("COMMON_LABEL_FILE_TYPE"), JLabel.RIGHT);
        fileTypeComboBox = new JComboBox<String>(FILE_TYPE_UNITS);
        fileTypeComboBox.setLightWeightPopupEnabled(false);

        JLabel minSizeLabel = new JLabel(DKSysUIUtil.getLocaleStringWithColon("DUP_INPUT_LAB_MIN"), JLabel.RIGHT);
        minFileSizeInput = new JTextField(2);
        minFileSizeInput.setText("0");

        JLabel maxSizeLabel = new JLabel(DKSysUIUtil.getLocaleStringWithColon("DUP_INPUT_LAB_MAX"), JLabel.RIGHT);
        maxFileSizeInput = new JTextField(2);

        JLabel fileSizeUnit = new JLabel(DKSysUIUtil.getLocaleStringWithColon("DUP_INPUT_LAB_SIZE_UNIT"), JLabel.RIGHT);
        fileSizeUnitComboBox = new JComboBox<String>(FILE_UNITS);
        fileSizeUnitComboBox.setLightWeightPopupEnabled(false);
        // 默认选中MB单位
        fileSizeUnitComboBox.setSelectedIndex(2);

        startCancelBtn = new JButton(BUTTONS_TEXT[0]);
        // 不显示焦点虚线边框
        startCancelBtn.setFocusPainted(false);

        exportBtn = new JButton(DKSysUIUtil.getLocaleString("COMMON_BTNS_EXPORT"));
        // 不显示焦点虚线边框
        exportBtn.setFocusPainted(false);

        northRootPane.add(fileSearchPathLbl);
        northRootPane.add(searchPath);
        northRootPane.add(browseBtn);
        northRootPane.add(fileTypeLabel);
        northRootPane.add(fileTypeComboBox);
        northRootPane.add(minSizeLabel);
        northRootPane.add(minFileSizeInput);
        northRootPane.add(maxSizeLabel);
        northRootPane.add(maxFileSizeInput);
        northRootPane.add(fileSizeUnit);
        northRootPane.add(fileSizeUnitComboBox);
        northRootPane.add(startCancelBtn);
        northRootPane.add(exportBtn);

        Constraints searchFilePathLbl = mgr.getConstraints(fileSearchPathLbl);
        searchFilePathLbl.setX(Spring.constant(COMPONENT_MARGIN_RIGHT));
        searchFilePathLbl.setY(Spring.constant(COMPONENT_MARGIN_TOP_LABLE));

        Constraints searchFilePathTextFieldCons = mgr.getConstraints(searchPath);
        searchFilePathTextFieldCons.setConstraint(SpringLayout.WEST, Spring.sum(searchFilePathLbl.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        searchFilePathTextFieldCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints browselBtnCons = mgr.getConstraints(browseBtn);
        browselBtnCons.setConstraint(SpringLayout.WEST, Spring.sum(searchFilePathTextFieldCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        browselBtnCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints fileTypeLabelCons = mgr.getConstraints(fileTypeLabel);
        fileTypeLabelCons.setConstraint(SpringLayout.WEST, Spring.sum(browselBtnCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        fileTypeLabelCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_LABLE));

        Constraints fileTypeComboCons = mgr.getConstraints(fileTypeComboBox);
        fileTypeComboCons.setConstraint(SpringLayout.WEST, Spring.sum(fileTypeLabelCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        fileTypeComboCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints minSizeLabelCons = mgr.getConstraints(minSizeLabel);
        minSizeLabelCons.setConstraint(SpringLayout.WEST, Spring.sum(fileTypeComboCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        minSizeLabelCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_LABLE));

        Constraints minFileSizeInputCons = mgr.getConstraints(minFileSizeInput);
        minFileSizeInputCons.setConstraint(SpringLayout.WEST, Spring.sum(minSizeLabelCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        minFileSizeInputCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints maxSizeLabelCons = mgr.getConstraints(maxSizeLabel);
        maxSizeLabelCons.setConstraint(SpringLayout.WEST, Spring.sum(minFileSizeInputCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        maxSizeLabelCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_LABLE));

        Constraints maxFileSizeInputCons = mgr.getConstraints(maxFileSizeInput);
        maxFileSizeInputCons.setConstraint(SpringLayout.WEST, Spring.sum(maxSizeLabelCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        maxFileSizeInputCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints fileSizeUnitCons = mgr.getConstraints(fileSizeUnit);
        fileSizeUnitCons.setConstraint(SpringLayout.WEST, Spring.sum(maxFileSizeInputCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        fileSizeUnitCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_LABLE));

        Constraints fileSizeUnitComboBoxCons = mgr.getConstraints(fileSizeUnitComboBox);
        fileSizeUnitComboBoxCons.setConstraint(SpringLayout.WEST, Spring.sum(fileSizeUnitCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT_IN)));
        fileSizeUnitComboBoxCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints startCancelBtnCons = mgr.getConstraints(startCancelBtn);
        startCancelBtnCons.setConstraint(SpringLayout.WEST, Spring.sum(fileSizeUnitComboBoxCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        startCancelBtnCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        Constraints exportBtnCons = mgr.getConstraints(exportBtn);
        exportBtnCons.setConstraint(SpringLayout.WEST, Spring.sum(startCancelBtnCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        exportBtnCons.setY(Spring.constant(COMPONENT_MARGIN_TOP_BASE));

        SpringLayout.Constraints panelCons = mgr.getConstraints(northRootPane);
        panelCons.setConstraint(SpringLayout.SOUTH, Spring.sum(exportBtnCons.getConstraint(SpringLayout.SOUTH), Spring.constant(COMPONENT_MARGIN_TOP_BASE)));
        panelCons.setConstraint(SpringLayout.WEST, Spring.sum(exportBtnCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));
        panelCons.setConstraint(SpringLayout.EAST, Spring.sum(exportBtnCons.getConstraint(SpringLayout.EAST), Spring.constant(COMPONENT_MARGIN_RIGHT)));

        return northRootPane;
    }

    private void initPopupMenu() {
        this.jtreeMenu = new JPopupMenu();

        JMenuItem copyPath2Clipboard = new JMenuItem(DKSysUIUtil.getLocaleString("DUP_FILE_MENU_COPY_PATH"));
        copyPath2Clipboard.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKSysUIUtil.setSystemClipboard(node.getUserObject().toString());
        });
        jtreeMenu.add(copyPath2Clipboard);
        JMenuItem copyParentPath2Clipboard = new JMenuItem(DKSysUIUtil.getLocaleString("DUP_FILE_MENU_COPY_PARENT_PATH"));
        copyParentPath2Clipboard.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKSysUIUtil.setSystemClipboard(new File(node.getUserObject().toString()).getParent());
        });
        jtreeMenu.add(copyParentPath2Clipboard);
        jtreeMenu.addSeparator();
        JMenuItem openFolder = new JMenuItem(DKSysUIUtil.getLocaleString("DUP_FILE_MENU_SHOW_IN_EXPLORER"));
        openFolder.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKFileUtil.openFolder(node.getUserObject().toString());
        });
        jtreeMenu.add(openFolder);
        JMenuItem openFile = new JMenuItem(DKSysUIUtil.getLocaleString("DUP_FILE_MENU_OPEN"));
        openFile.addActionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            DKFileUtil.openFile(node.getUserObject().toString());
        });
        jtreeMenu.add(openFile);
        jtreeMenu.addSeparator();
        JMenuItem delete = new JMenuItem(DKSysUIUtil.getLocaleString("DUP_FILE_MENU_DELETE"));
        delete.addActionListener(e -> {
            int deleteOption = JOptionPane.showConfirmDialog(this, DKSysUIUtil.getLocaleString(
                    "DUP_FILE_MENU_DEL_DIALOG_CONTENT"), DKSysUIUtil.getLocaleString(
                    "DUP_FILE_MENU_DEL_DIALOG_TITLE"), JOptionPane.YES_NO_OPTION);
            if (deleteOption == JOptionPane.YES_OPTION) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node.getLevel() == 2) {//第三层叶子节点
                    File file = new File(node.getUserObject().toString());
                    boolean b = FileUtils.deleteQuietly(file);
                    if (b) {
                        //TODO 文件删除收树刷新
                    } else {
                        JOptionPane.showMessageDialog(this, DKSysUIUtil.getLocaleString(
                                "DUP_FILE_MENU_DEL_FAILED_CONTENT"), DKSysUIUtil.getLocaleString(
                                "DUP_FILE_MENU_DEL_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, DKSysUIUtil.getLocaleString(
                            "DUP_FILE_MENU_DEL_DIALOG_WARNING_CONTENT"), DKSysUIUtil.getLocaleString(
                            "DUP_FILE_MENU_DEL_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        jtreeMenu.add(delete);
    }


    @Override
    protected void initListener() {
        // 窗口关闭时释放线程池资源
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (theadPool != null && !theadPool.isShutdown()) {
                    theadPool.shutdownNow();
                }
            }
        });

        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                searchPath.setText(file.getAbsolutePath());
            }
        });

        startCancelBtn.addActionListener(new StartEndListener(this));
        exportBtn.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setCurrentDirectory(DKSysUtil.getHomeDir());
            jfc.setDialogTitle(DKSysUIUtil.getLocaleString("FILE_CHOOSER_DIALOG_TITLE_EXPORT"));
            int i = jfc.showSaveDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                exportResult(file);
            }
        });

        // 左侧树单选事件
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new DuplicateFileTreeSelectionListener(rightPane, rightPaneLayout, md5FilesMap, RIGHT_PANE_NAMES));

        // 叶子节点双击打开文件所在目录
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                    return;
                }
                // 鼠标左键
                if (e.getButton() == MouseEvent.BUTTON1) {
                    JTree tree = (JTree) e.getSource();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (e.getClickCount() == 2 && node.getLevel() == 2) {
                        File file = new File(node.getUserObject().toString());
                        DKFileUtil.openFile(file.getParentFile());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                TreePath tp = tree.getClosestPathForLocation(e.getX(), e.getY());
                if (tp != null) {
                    tree.setSelectionPath(tp);
                }
                jtreeMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }


    private void exportResult(File saveFolder) {
        File exportFile = new File(saveFolder.getAbsolutePath() + File.separator + "DevkitsDuplicateFiles_" + DKDateTimeUtil.currentTimeStr() + ".csv");
        flushResult(exportFile, Lists.newArrayList(new String[]{"File Name, MD5, File Extension, File Path, File Size, Create Time"}));

        DefaultTreeModel rootModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) rootModel.getRoot();

        Enumeration<?> children = treeNode.children();
        while (children.hasMoreElements()) {
            List<String> data = new ArrayList<>();

            DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) children.nextElement();
            String md5 = childTreeNode.getUserObject().toString();
            Enumeration children1 = childTreeNode.children();
            while (children1.hasMoreElements()) {
                File f = new File(children1.nextElement().toString());
                StringBuilder sb = new StringBuilder();
                sb.append(f.getName()).append(",").append(md5).append(",").append(FilenameUtils.getExtension(f.getName())).append(",")
                        .append(f.getAbsolutePath()).append(",").append(DKFileUtil.formatBytes(f.length())).append(",")
                        .append(DKDateTimeUtil.getDatetimeStrOfLong(f.lastModified(), "yyyy-MM-dd HH:mm:ss"));
                data.add(sb.toString());
            }
            flushResult(exportFile, data);
        }
    }

    private void flushResult(File exportFile, List<String> data) {
        try {
            FileUtils.writeLines(exportFile, data, true);
        } catch (IOException e) {
            LOGGER.error("Flush result to file '{}' failed: {}", exportFile.getAbsolutePath(), e.getMessage());
        }
    }

    public void initDataModel() {
        md5FilesMap.clear();
        treeNode.removeAllChildren();
        treeModel.reload();

        theadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);
    }

    public void searchComplete() {
        theadPool.shutdown();
        while (!theadPool.isTerminated()) {
            try {
                TimeUnit.MICROSECONDS.sleep(200);
            } catch (InterruptedException e) {
                LOGGER.error("Sleep error: {0}", e.getMessage());
            }
        }
        updateStatusLineText(DKSysUIUtil.getLocaleStringWithParam("DUP_FILE_STATUS_LINE_RESULT", md5FilesMap.size()));
        startCancelBtn.setText(BUTTONS_TEXT[0]);
    }

    public void updateStatusLineText(final String text) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                statusLine.setText(text);
            }
        });
    }

    public void updateTreeData(String md5, String file) {
        Set<String> filePathSets = md5FilesMap.get(md5);
        if (filePathSets == null) {
            filePathSets = new HashSet<>();
            filePathSets.add(file);
            md5FilesMap.put(md5, filePathSets);
        } else {
            if (filePathSets.size() == 1) {
                insertTreeNode(md5, filePathSets.iterator().next());
            }
            filePathSets.add(file);
            insertTreeNode(md5, file);
        }
    }

    private void insertTreeNode(String md5, String file) {
        DefaultTreeModel rootModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) rootModel.getRoot();

        int childCount = treeNode.getChildCount();
        Enumeration<?> children = treeNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) children.nextElement();
            if (childTreeNode.getUserObject().toString().equals(md5)) {
                rootModel.insertNodeInto(new DefaultMutableTreeNode(file), childTreeNode, childTreeNode.getChildCount());
                return;
            }
        }

        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(md5);
        rootModel.insertNodeInto(newChild, treeNode, childCount);
        rootModel.insertNodeInto(new DefaultMutableTreeNode(file), newChild, newChild.getChildCount());
    }


    public JComboBox<String> getFileTypeComboBox() {
        return fileTypeComboBox;
    }

    public JTextField getMinFileSizeInput() {
        return minFileSizeInput;
    }

    public JTextField getMaxFileSizeInput() {
        return maxFileSizeInput;
    }

    public JComboBox<String> getFileSizeUnitComboBox() {
        return fileSizeUnitComboBox;
    }

    public ExecutorService getTheadPool() {
        return theadPool;
    }

    public JButton getStartCancelBtn() {
        return startCancelBtn;
    }

    public JTextField getSearchPath() {
        return searchPath;
    }
}

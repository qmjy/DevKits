package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.frame.asyn.SearchFileThread;
import cn.devkits.client.tray.model.LargeDuplicateFilesTableModel;

/**
 * 重复大文件检查<br>
 * Oracle Swing DEMO:https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#
 * GlassPaneDemo
 * 
 * @author Shaofeng Liu
 * @version 1.0.0
 * @time 2019年9月26日 下午9:34:49
 */
public class LargeDuplicateFilesFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6081895254576694963L;
    private static final Logger LOGGER = LoggerFactory.getLogger(LargeDuplicateFilesFrame.class);

    private static final String[] FILE_TYPE_UNITS = {"All", "Document", "Image", "Audio", "Video"};
    private static final String[] FILE_UNITS = {"Byte", "KB", "MB", "GB", "TB", "PB"};

    /** 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值 */
    public static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 100;

    private JTree tree = null;
    private JTable table = null;
    private JLabel statusLine = null;
    private JComboBox<String> fileTypeComboBox = null;
    private JTextField minFileSizeInput = null;
    private JTextField maxFileSizeInput = null;
    private JComboBox<String> fileSizeUnitComboBox = null;
    private JButton startCancelBtn = null;
    private ExecutorService theadPool = null;

    private DefaultMutableTreeNode treeNode = null;
    private DefaultTreeModel treeModel = null;
    private HashMap<String, List<File>> fileMd5Map = null;


    public LargeDuplicateFilesFrame() {
        super("Large Duplicate Files", 1.2f);

        initUI(getRootPane());
        initListener();
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());
        jRootPane.setBorder(new EmptyBorder(5, 5, 0, 5));

        jRootPane.add(initNorthPane(), BorderLayout.NORTH);

        JSplitPane jSplitPane = new JSplitPane();

        treeNode = new DefaultMutableTreeNode("Duplicate Files");
        treeModel = new DefaultTreeModel(treeNode);
        tree = new JTree(treeModel);

        // ToolTipManager.sharedInstance().registerComponent(tree);
        // TreeCellRenderer renderer = new LargeDuplicateFilesTreeCellRenderer();
        // tree.setCellRenderer(renderer);

        JScrollPane scrollPane = new JScrollPane(tree);
        jSplitPane.setLeftComponent(scrollPane);

        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);// 列自适应
        jSplitPane.setRightComponent(new JScrollPane(table));

        jSplitPane.setResizeWeight(0.3);
        jRootPane.add(jSplitPane, BorderLayout.CENTER);

        statusLine = new JLabel("Ready to go...");
        statusLine.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH, 25));
        jRootPane.add(statusLine, BorderLayout.SOUTH);

        initDataModel();
    }

    private JPanel initNorthPane() {
        JPanel northRootPane = new JPanel();
        northRootPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));

        northRootPane.add(new JLabel("File Type:"));
        fileTypeComboBox = new JComboBox<String>(FILE_TYPE_UNITS);
        fileTypeComboBox.setLightWeightPopupEnabled(false);
        northRootPane.add(fileTypeComboBox);

        northRootPane.add(new JLabel("File Size:"));
        minFileSizeInput = new JTextField(6);
        minFileSizeInput.setText("0");
        northRootPane.add(minFileSizeInput);

        northRootPane.add(new JLabel("-"));

        maxFileSizeInput = new JTextField(6);
        northRootPane.add(maxFileSizeInput);

        fileSizeUnitComboBox = new JComboBox<String>(FILE_UNITS);
        fileSizeUnitComboBox.setLightWeightPopupEnabled(false);
        northRootPane.add(fileSizeUnitComboBox);

        startCancelBtn = new JButton("Start");
        startCancelBtn.setFocusPainted(false);// 不显示焦点虚线边框
        northRootPane.add(startCancelBtn);

        return northRootPane;
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

        startCancelBtn.addActionListener(new StartEndListener(this));

        // 左侧树单选事件
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                List<File> fileList = null;
                if (node != null) {
                    if (node.getLevel() == 0) {
                        return;
                    } else if (node.getLevel() == 1) {
                        fileList = fileMd5Map.get(node.getUserObject().toString());
                    } else if (node.getLevel() == 2) {
                        fileList = new ArrayList<File>();
                        fileList.add(new File(node.getUserObject().toString()));
                    }
                }

                if (fileList != null) {
                    updateTableData(fileList);
                }
            }
        });

        // 叶子节点双击打开文件所在目录
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (e.getClickCount() == 2 && node.getLevel() == 2) {
                    File file = new File(node.getUserObject().toString());
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (IOException e1) {
                        LOGGER.error("Open file failed: " + file.getParentFile().getAbsolutePath());
                    }
                }
            }
        });
    }

    public void initDataModel() {
        fileMd5Map = new HashMap<String, List<File>>();

        treeNode.removeAllChildren();
        treeModel.reload();

        table.setModel(new LargeDuplicateFilesTableModel());

        theadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);
    }

    public void searchComplete() {
        updateStatusLineText("Files Search Completed!");
        startCancelBtn.setText("Start");
    }

    public void updateStatusLineText(final String text) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                statusLine.setText(text);
            }
        });
    }

    public void updateTableData(List<File> files) {
        table.setModel(new LargeDuplicateFilesTableModel(files));
    }

    public void updateTreeData(String md5, File file) {
        List<File> list = null;
        if (fileMd5Map.containsKey(md5)) {
            list = fileMd5Map.get(md5);
            if (list.size() == 1) {
                insertTreeNode(md5, list.get(0));
            }

            list.add(file);
            insertTreeNode(md5, file);
        } else {
            list = new ArrayList<File>();
            list.add(file);
            fileMd5Map.put(md5, list);
        }
    }

    private void insertTreeNode(String md5, File file) {
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



}



/**
 * 启动、取消按钮事件监听
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年10月5日 下午3:25:08
 */
class StartEndListener implements ActionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartEndListener.class);

    private LargeDuplicateFilesFrame frame;

    public StartEndListener(LargeDuplicateFilesFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        ExecutorService threadPool = frame.getTheadPool();

        if ("Start".equals(e.getActionCommand())) {
            if (threadPool.isShutdown()) {
                frame.initDataModel();
            }
            new Thread(new SearchFileThread(frame, getFileSizeThreshold(frame, true), getFileSizeThreshold(frame, false))).start();
            btn.setText("Cancel");
            frame.updateStatusLineText("Start to scanner File...");
        } else {
            threadPool.shutdownNow();
            btn.setText("Start");
            frame.updateStatusLineText("Scanner file canceled by user!");
        }
    }


    private long getFileSizeThreshold(LargeDuplicateFilesFrame frame, boolean isMaxThreshold) {
        long val = convertUnti2Val(frame);
        String minText = frame.getMinFileSizeInput().getText();
        String maxText = frame.getMaxFileSizeInput().getText();
        if (isMaxThreshold) {
            try {
                return (long) (Double.parseDouble(maxText) * val);
            } catch (NumberFormatException e) {
                LOGGER.error("Resolve the max value of user input failed: " + maxText);
                return Long.MAX_VALUE;
            }
        } else {
            try {
                return (long) (Double.parseDouble(minText) * val);
            } catch (NumberFormatException e) {
                LOGGER.error("Resolve the min value of user input failed: " + minText);
                return 0;
            }
        }
    }

    private long convertUnti2Val(LargeDuplicateFilesFrame frame) {
        String fileUnit = (String) frame.getFileSizeUnitComboBox().getSelectedItem();
        switch (fileUnit) {
            case "Byte":
                return 1L;
            case "KB":
                return 1L * 1024;
            case "MB":
                return 1L * 1024 * 1024;
            case "GB":
                return 1L * 1024 * 1024 * 1024;
            case "TB":
                return 1L * 1024 * 1024 * 1024 * 1024;
            case "PB":
                return 1L * 1024 * 1024 * 1024 * 1024 * 1024;
            default:
                return 1L;
        }
    }

}

package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.App;
import cn.devkits.client.tray.model.LargeDuplicateFilesTableModel;

/**
 * 
 * 重复大文件检查<br>
 * Oracle Swing
 * DEMO:https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#GlassPaneDemo
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:49
 */
public class LargeDuplicateFilesFrame extends DKAbstractFrame {

    private static final long serialVersionUID = 6081895254576694963L;

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    /** 50MB */
    private final int MAX_FILE_LEN = 1024 * 1024 * 50;
    /** 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值 */
    private static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 100;

    private static DefaultTreeModel rootModel = null;
    private static JTree tree = null;
    private static JTable jTable = null;
    private static JLabel statusLine = null;

    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);

    private HashMap<String, List<File>> fileMd5Map = new HashMap<String, List<File>>();

    public LargeDuplicateFilesFrame() {
        super("Large Duplicate Files");

        // 启动异步线程开始检查文件
        new Thread(new SearchFileThread(this, newFixedThreadPool, MAX_FILE_LEN)).start();
    }


    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());
        jRootPane.setBorder(new EmptyBorder(5, 5, 0, 5));

        JSplitPane jSplitPane = new JSplitPane();

        rootModel = new DefaultTreeModel(new DefaultMutableTreeNode("Duplicate Files"));
        tree = new JTree(rootModel);
        JScrollPane comp = new JScrollPane(tree);
        comp.setPreferredSize(new Dimension((int) (WINDOW_SIZE_WIDTH * 0.3), WINDOW_SIZE_HEIGHT));

        jSplitPane.setLeftComponent(comp);

        jTable = new JTable(new LargeDuplicateFilesTableModel());
        jSplitPane.setRightComponent(new JScrollPane(jTable));

        jRootPane.add(jSplitPane, BorderLayout.CENTER);

        statusLine = new JLabel("Welcome to DK Tool: Large Duplicate Files Scanner...");
        statusLine.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH, 25));
        jRootPane.add(statusLine, BorderLayout.SOUTH);
    }

    @Override
    protected void initListener() {
        // 窗口关闭时释放线程池资源
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                newFixedThreadPool.shutdownNow();
            }
        });

        // 左侧树单选事件
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                List<File> fileList = null;
                if (node.getLevel() == 0) {
                    return;
                } else if (node.getLevel() == 1) {
                    fileList = fileMd5Map.get(node.getUserObject().toString());
                } else if (node.getLevel() == 2) {
                    fileList = new ArrayList<File>();
                    fileList.add(new File(node.getUserObject().toString()));
                }
                updateTableData(fileList);
            }
        });
    }

    public void updateStatusLineText(final String text) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                statusLine.setText(text);
            }
        });
    }

    public void updateTableData(List<File> files) {
        jTable.setModel(new LargeDuplicateFilesTableModel(files));
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
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) rootModel.getRoot();

        int childCount = treeNode.getChildCount();
        if (childCount > 0) {
            Enumeration<?> children = treeNode.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) children.nextElement();
                if (childTreeNode.getUserObject().toString().equals(md5)) {
                    rootModel.insertNodeInto(new DefaultMutableTreeNode(file), childTreeNode, childTreeNode.getChildCount());
                }
            }
        } else {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(md5);
            rootModel.insertNodeInto(newChild, treeNode, childCount);
            rootModel.insertNodeInto(new DefaultMutableTreeNode(file), newChild, newChild.getChildCount());
        }
    }
}


/**
 * 
 * 遍历文件线程
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:14
 */
class SearchFileThread extends Thread {

    private LargeDuplicateFilesFrame frame;
    private ExecutorService newFixedThreadPool;
    private int maxLength;

    public SearchFileThread(LargeDuplicateFilesFrame frame, ExecutorService newFixedThreadPool, int maxLength) {
        this.frame = frame;
        this.newFixedThreadPool = newFixedThreadPool;
        this.maxLength = maxLength;
    }

    @Override
    public void run() {
        File[] listRoots = File.listRoots();
        for (File file : listRoots) {
            recursiveSearch(file);
        }
        newFixedThreadPool.shutdown();
    }

    private void recursiveSearch(File dirFile) {
        File[] listFiles = dirFile.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    recursiveSearch(file);
                } else {
                    if (file.length() > maxLength) {
                        // 窗口关闭以后，快速退出
                        if (!newFixedThreadPool.isShutdown()) {
                            if (!file.getAbsolutePath().contains(".svn") && !file.getAbsolutePath().contains(".git")) {
                                frame.updateStatusLineText("Scanner File: " + file.getAbsolutePath());
                                newFixedThreadPool.submit(new FileMd5Thread(frame, file));
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
        }
    }
}


/**
 * 文件MD5 计算线程
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:33:04
 */
class FileMd5Thread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMd5Thread.class);

    private File file;
    private LargeDuplicateFilesFrame frame;

    public FileMd5Thread(LargeDuplicateFilesFrame frame, File file) {
        this.frame = frame;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
            frame.updateTreeData(md5Hex, file);
        } catch (FileNotFoundException e) {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        }
    }
}

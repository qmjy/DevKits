package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.App;

/**
 * 重复大文件检查
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:49
 */
public class LargeDuplicateFilesFrame extends DKAbstractFrame
{

    private static final long serialVersionUID = 6081895254576694963L;

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    /** 10MB */
    private final int MAX_FILE_LEN = 1024 * 1024 * 10;
    /** 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值 */
    private static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 100;

    private static final DefaultTreeModel rootModel = new DefaultTreeModel(new DefaultMutableTreeNode("Duplicate Files"));
    private static final JTree tree = new JTree(rootModel);

    private ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);

    private HashMap<String, Set<File>> fileMd5Map = new HashMap<String, Set<File>>();

    public LargeDuplicateFilesFrame()
    {
        super("Large Duplicate Files");

        // 启动异步线程开始检查文件
        new Thread(new SearchFileThread(this, newFixedThreadPool, MAX_FILE_LEN)).start();
    }

    @Override
    protected JRootPane createRootPane()
    {
        JRootPane jRootPane = new JRootPane();
        jRootPane.setLayout(new BorderLayout());

        JSplitPane jSplitPane = new JSplitPane();

        jSplitPane.setLeftComponent(new JScrollPane(tree));

        Object[][] data =
        {
        { "zz", "sdfgd", "sdfgd", "sdfgd", "sdfgd", "" },
        { "12312", "sdfgd", "sdfgd", "3452345", "sdfgd", "" } };
        String[] names =
        { "Index", "MD5", "File Path", "File Size", "File Size", "Action" };

        JTable jTable = new JTable(data, names);
        jSplitPane.setRightComponent(new JScrollPane(jTable));
        jSplitPane.setDividerLocation(0.4);

        jRootPane.add(jSplitPane, BorderLayout.CENTER);

        return jRootPane;
    }

    @Override
    protected void initListener()
    {
        // 窗口关闭时释放线程池资源
        super.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                newFixedThreadPool.shutdownNow();
            }
        });

        // 左侧树单选事件
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                LOGGER.info(node.getUserObject().toString());
            }
        });
    }

    public void updateTreeData(String md5, File file)
    {
        updateFileMap(md5, file);

        // 存在重复的文件才显示
        if (fileMd5Map.containsKey(md5) && fileMd5Map.get(md5).size() >= 2)
        {
            insertTreeNode(md5, file);
        }
    }

    private void insertTreeNode(String md5, File file)
    {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) rootModel.getRoot();

        int childCount = treeNode.getChildCount();
        if (childCount > 0)
        {
            Enumeration<?> children = treeNode.children();
            while (children.hasMoreElements())
            {
                DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) children.nextElement();
                if (childTreeNode.getUserObject().toString().equals(md5))
                {
                    rootModel.insertNodeInto(new DefaultMutableTreeNode(file.getName()), childTreeNode, childTreeNode.getChildCount());
                }
            }
        } else
        {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(md5);
            rootModel.insertNodeInto(newChild, treeNode, childCount);
            rootModel.insertNodeInto(new DefaultMutableTreeNode(file.getName()), newChild, newChild.getChildCount());
        }
    }

    private void updateFileMap(String md5, File file)
    {
        Set<File> list = null;
        if (fileMd5Map.containsKey(md5))
        {
            list = fileMd5Map.get(md5);
            if (list.size() == 1)
            {
                insertTreeNode(md5, file);
            }
            list.add(file);
        } else
        {
            list = new HashSet<File>();
            list.add(file);
            fileMd5Map.put(md5, list);
        }
    }
}

/**
 * 遍历文件线程
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:14
 */
class SearchFileThread extends Thread
{

    private LargeDuplicateFilesFrame frame;
    private ExecutorService newFixedThreadPool;
    private int maxLength;

    public SearchFileThread(LargeDuplicateFilesFrame frame, ExecutorService newFixedThreadPool, int maxLength)
    {
        this.frame = frame;
        this.newFixedThreadPool = newFixedThreadPool;
        this.maxLength = maxLength;
    }

    @Override
    public void run()
    {
        File[] listRoots = File.listRoots();
        for (File file : listRoots)
        {
            recursiveSearch(file);
        }
        newFixedThreadPool.shutdown();
    }

    private void recursiveSearch(File dirFile)
    {
        File[] listFiles = dirFile.listFiles();
        if (listFiles != null)
        {
            for (File file : listFiles)
            {
                if (file.isDirectory())
                {
                    recursiveSearch(file);
                } else
                {
                    if (file.length() > maxLength)
                    {
                        // 窗口关闭以后，快速退出
                        if (!newFixedThreadPool.isShutdown())
                        {
                            newFixedThreadPool.submit(new FileMd5Thread(frame, file));
                        } else
                        {
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
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:33:04
 */
class FileMd5Thread extends Thread
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMd5Thread.class);

    private File file;
    private LargeDuplicateFilesFrame frame;

    public FileMd5Thread(LargeDuplicateFilesFrame frame, File file)
    {
        this.frame = frame;
        this.file = file;
    }

    @Override
    public void run()
    {
        try
        {
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
            frame.updateTreeData(md5Hex, file);
        } catch (FileNotFoundException e)
        {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        } catch (IOException e)
        {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        }
    }
}

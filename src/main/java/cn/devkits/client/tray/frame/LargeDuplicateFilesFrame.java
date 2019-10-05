package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import cn.devkits.client.DKConstant;
import cn.devkits.client.tray.frame.asyn.SearchFileThread;
import cn.devkits.client.tray.model.LargeDuplicateActionModel;
import cn.devkits.client.tray.model.LargeDuplicateFilesTableModel;

/**
 * 重复大文件检查<br>
 * Oracle Swing DEMO:https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#
 * GlassPaneDemo
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:34:49
 */
public class LargeDuplicateFilesFrame extends DKAbstractFrame {

  private static final long serialVersionUID = 6081895254576694963L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LargeDuplicateFilesFrame.class);
  private static final String[] FILE_TYPE_UNITS = {"All", "Document", "Image", "Audio", "Video"};
  private static final String[] FILE_UNITS = {"KB", "MB", "GB", "TB", "PB"};

  /** 50MB */
  private final int MAX_FILE_LEN = 1024 * 1024 * 50;

  /** 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值 */
  public static final int FIXED_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 100;

  private static DefaultTreeModel rootModel = null;
  private static JTree tree = null;
  private static JTable jTable = null;
  private static JLabel statusLine = null;
  private static JComboBox<String> fileTypeComboBox = null;
  private static JTextField minFileSizeInput = null;
  private static JTextField maxFileSizeInput = null;
  private static JComboBox<String> fileSizeUnitComboBox = null;
  private static JButton startBtn = null;

  private ExecutorService theadPool = null;
  private HashMap<String, List<File>> fileMd5Map = new HashMap<String, List<File>>();

  public LargeDuplicateFilesFrame() {
    super("Large Duplicate Files", 1.2f);
  }

  @Override
  protected void initUI(JRootPane jRootPane) {
    jRootPane.setLayout(new BorderLayout());
    jRootPane.setBorder(new EmptyBorder(5, 5, 0, 5));

    jRootPane.add(initNorthPane(), BorderLayout.NORTH);

    JSplitPane jSplitPane = new JSplitPane();

    rootModel = new DefaultTreeModel(new DefaultMutableTreeNode("Duplicate Files"));
    tree = new JTree(rootModel);
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setPreferredSize(new Dimension((int) (WINDOW_SIZE_WIDTH * 0.3), WINDOW_SIZE_HEIGHT));
    jSplitPane.setLeftComponent(scrollPane);

    jTable = new JTable(new LargeDuplicateFilesTableModel());
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);// 列自适应
    jSplitPane.setRightComponent(new JScrollPane(jTable));

    jRootPane.add(jSplitPane, BorderLayout.CENTER);

    statusLine = new JLabel("Large Duplicate Files Scanner...");
    statusLine.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH, 25));
    jRootPane.add(statusLine, BorderLayout.SOUTH);
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

    startBtn = new JButton("Start");
    theadPool = Executors.newFixedThreadPool(FIXED_THREAD_NUM);
    LargeDuplicateActionModel actionModel =
        new LargeDuplicateActionModel(this, theadPool, new DKFilenameFilter(fileTypeComboBox),
            Long.MAX_VALUE, 1024 * 1024 * 1);
    startBtn.addActionListener(new StartEndListener(actionModel));
    northRootPane.add(startBtn);

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

  public void finishedSearch() {
    updateStatusLineText("Files Search Completed!");
    startBtn.setText("Start");
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
    Enumeration<?> children = treeNode.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) children.nextElement();
      if (childTreeNode.getUserObject().toString().equals(md5)) {
        rootModel.insertNodeInto(new DefaultMutableTreeNode(file), childTreeNode,
            childTreeNode.getChildCount());
        return;
      }
    }

    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(md5);
    rootModel.insertNodeInto(newChild, treeNode, childCount);
    rootModel.insertNodeInto(new DefaultMutableTreeNode(file), newChild, newChild.getChildCount());
  }
}


class DKFilenameFilter implements FilenameFilter {

  private JComboBox<String> fileTypeComboBox;

  public DKFilenameFilter(JComboBox<String> fileTypeComboBox) {
    this.fileTypeComboBox = fileTypeComboBox;
  }

  @Override
  public boolean accept(File dir, String name) {

    File file = new File(dir, name);
    if (file.isDirectory()) {
      return true;
    }

    if (dir.getAbsolutePath().contains(".git") || dir.getAbsolutePath().contains(".svn")) {
      return false;
    }

    String fileType = (String) fileTypeComboBox.getSelectedItem();

    if ("All".equals(fileType)) {
      return true;
    } else {
      if (name.indexOf(".") > 0) {
        String suffix = name.substring(name.lastIndexOf(".")).toLowerCase(Locale.getDefault());
        if ("Document".equals(fileType)) {
          return DKConstant.FILE_TYPE_DOC.contains(suffix);
        } else if ("Image".equals(fileType)) {
          return DKConstant.FILE_TYPE_IMG.contains(suffix);
        } else if ("Audio".equals(fileType)) {
          return DKConstant.FILE_TYPE_AUDIO.contains(suffix);
        } else if ("Video".equals(fileType)) {
          return DKConstant.FILE_TYPE_VEDIO.contains(suffix);
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
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

  private LargeDuplicateActionModel actionModel;

  public StartEndListener(LargeDuplicateActionModel actionModel) {
    this.actionModel = actionModel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton btn = (JButton) e.getSource();

    ExecutorService threadPool = actionModel.getThreadPool();
    LargeDuplicateFilesFrame frame = actionModel.getFrame();

    if ("Start".equals(e.getActionCommand())) {
      if (threadPool.isShutdown()) {
        threadPool = Executors.newFixedThreadPool(LargeDuplicateFilesFrame.FIXED_THREAD_NUM);
      }
      new Thread(new SearchFileThread(actionModel)).start();
      btn.setText("Cancel");
      frame.updateStatusLineText("Start to scanner File!");
    } else {
      threadPool.shutdownNow();
      btn.setText("Start");
      frame.updateStatusLineText("Scanner file canceled by user!");
    }
  }
}

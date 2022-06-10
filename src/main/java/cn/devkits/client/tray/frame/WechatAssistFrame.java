package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.listener.FilesMouseListener;
import cn.devkits.client.tray.model.FilesTreeCellRender;
import cn.devkits.client.tray.model.FilesTreeModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;

/**
 * 微信助手窗口
 */
public class WechatAssistFrame extends DKAbstractFrame {
    private JSplitPane jSplitPane;
    private JTextField wechatDataPathInput;
    private String wechatDataPath;
    private DefaultTreeModel rootModel;
    private JTree tree;
    private JPopupMenu jTreeMenu;
    private JPanel detailsPanel;

    public WechatAssistFrame() {
        super(DKSysUIUtil.getLocale("WECHAT_ASSIST_TITLE"), 1.2f);

        String path = DKSysUIUtil.getWechatDataPath() + File.separator + "WeChat Files";
        if (new File(path).exists()) {
            wechatDataPath = path;
        }

        FilesTreeModel filesTreeModel = new FilesTreeModel(wechatDataPath);
        rootModel = new DefaultTreeModel(filesTreeModel);

        initUI(getDKPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.setLayout(new BorderLayout());
        rootContainer.add(createTopPane(), BorderLayout.NORTH);
        rootContainer.add(createCenterPane(), BorderLayout.CENTER);
    }

    private JSplitPane createCenterPane() {
        jSplitPane = new JSplitPane();
        tree = new JTree(rootModel);
        tree.setCellRenderer(new FilesTreeCellRender());

        jTreeMenu = DKSysUIUtil.createDefaultFileBaseMenu(this, tree);
        appendMenu();

        jSplitPane.setLeftComponent(new JScrollPane(tree));
        detailsPanel = new JPanel();
        jSplitPane.setRightComponent(detailsPanel);

        return jSplitPane;
    }

    private JPanel createTopPane() {
        SpringLayout layout = new SpringLayout();
        JPanel jPanel = new JPanel(layout);
        jPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JLabel label = new JLabel(DKSysUIUtil.getLocaleWithColon("WECHAT_ASSIST_USER_DATA_PATH"));
        wechatDataPathInput = new JTextField(wechatDataPath);
        JButton browse = new JButton(DKSysUIUtil.getLocaleWithEllipsis("COMMON_BTNS_BROWSE"));

        jPanel.add(label);
        jPanel.add(wechatDataPathInput);
        jPanel.add(browse);

        SpringLayout.Constraints labelCons = layout.getConstraints(label);
        labelCons.setX(Spring.constant(5));
        labelCons.setY(Spring.constant(9));

        SpringLayout.Constraints jTextFieldCons = layout.getConstraints(wechatDataPathInput);
        jTextFieldCons.setX(Spring.sum(Spring.constant(5), labelCons.getConstraint(SpringLayout.EAST)));
        jTextFieldCons.setY(Spring.constant(5));

        SpringLayout.Constraints browseCons = layout.getConstraints(browse);
        browseCons.setX(Spring.sum(Spring.constant(5), jTextFieldCons.getConstraint(SpringLayout.EAST)));
        browseCons.setY(Spring.constant(5));

        SpringLayout.Constraints panelCons = layout.getConstraints(jPanel);

        panelCons.setConstraint(
                SpringLayout.EAST,
                Spring.sum(Spring.constant(5), browseCons.getConstraint(SpringLayout.EAST))
        );

        panelCons.setConstraint(
                SpringLayout.SOUTH,
                Spring.sum(Spring.constant(5), browseCons.getConstraint(SpringLayout.SOUTH))
        );
        return jPanel;
    }

    private void appendMenu() {
        jTreeMenu.addSeparator();
        JMenuItem decodeItem = new JMenuItem(DKSysUIUtil.getLocaleWithEllipsis("POPUP_MENU_FILE_DECODE"));
        decodeItem.addActionListener(e -> {
            FilesTreeModel node = (FilesTreeModel) tree.getLastSelectedPathComponent();
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String selectPath = chooser.getSelectedFile().getPath();
                File originFile = node.getNode();
                if (originFile.isDirectory()) {
                    File[] files = originFile.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".dat");
                        }
                    });
                    for (File file : files) {
                        DKFileUtil.decodeImgOfWechat(file, selectPath);
                    }
                } else {
                    DKFileUtil.decodeImgOfWechat(originFile, selectPath);
                }
            }
        });
        jTreeMenu.add(decodeItem);
    }

    @Override
    protected void initListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                //自动设置分割框宽度
                jSplitPane.setDividerLocation(.26);
            }
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                String[] filePaths = getSelectNodeFilePath();
                System.out.println(filePaths.length);
            }
        });

        // 叶子节点双击打开文件所在目录
        tree.addMouseListener(new FilesMouseListener(tree, jTreeMenu));
    }

    private String[] getSelectNodeFilePath() {
        TreePath[] selectionPaths = tree.getSelectionModel().getSelectionPaths();

        String[] results = new String[selectionPaths.length];
        for (int i = 0; i < selectionPaths.length; i++) {
            Object[] paths = selectionPaths[i].getPath();
            String[] strings = new String[paths.length];
            for (int j = 0; j < paths.length; j++) {
                strings[j] = paths[j].toString();
            }
            FilesTreeModel root = (FilesTreeModel) rootModel.getRoot();
            results[i] = Paths.get(root.getNodeParentPath(), strings).toString();
        }
        return results;
    }
}

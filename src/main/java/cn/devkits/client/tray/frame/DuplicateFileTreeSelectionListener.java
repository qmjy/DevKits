package cn.devkits.client.tray.frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.devkits.client.asyn.AppStarter;
import cn.devkits.client.tray.model.LargeDuplicateFilesTableModel;
import cn.devkits.client.util.DKFileUtil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 重复文件树点击事件处理
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/9/25
 */
public class DuplicateFileTreeSelectionListener implements TreeSelectionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateFileTreeSelectionListener.class);
    private final JPanel rightPane;
    private final CardLayout rightPaneLayout;
    private final String[] rightPaneNames;
    private JTable table = new JTable();
    private JLabel previewLabel = new JLabel();
    private ConcurrentHashMap<String, Set<String>> md5FilesMap;

    public DuplicateFileTreeSelectionListener(JPanel rightPane, CardLayout rightPaneLayout, ConcurrentHashMap<String, Set<String>> md5FilesMap,
                                              String[] rightPaneNames) {
        this.md5FilesMap = md5FilesMap;
        this.rightPane = rightPane;
        this.rightPaneLayout = rightPaneLayout;
        this.rightPaneNames = rightPaneNames;

        Component[] components = rightPane.getComponents();
        initTablePanel((JScrollPane) components[0]);
        initDetailPanel((JScrollPane) components[1]);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node != null) {
            Component[] components = rightPane.getComponents();
            if (node.getLevel() == 0) {
                return;
            } else if (node.getLevel() == 1) {
                String md5 = node.getUserObject().toString();
                Set<String> filesSet = md5FilesMap.get(md5);
                if (filesSet != null) {
                    table.setModel(new LargeDuplicateFilesTableModel(filesSet));
                    rightPaneLayout.show(rightPane, rightPaneNames[0]);
                }
            } else if (node.getLevel() == 2) {
                rightPaneLayout.show(rightPane, rightPaneNames[1]);
                String filePath = node.getUserObject().toString();
                if (DKFileUtil.isImgFromExtension(filePath)) {
                    try {
                        BufferedImage myPicture = ImageIO.read(new File(filePath));
                        previewLabel.setIcon(new ImageIcon(myPicture));
                    } catch (IOException ioException) {
                        LOGGER.error("Read image data failed: {}", filePath);
                    }
                }
            }
        }
    }


    private void initDetailPanel(JScrollPane detailPanel) {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        view.add(createCenterPanel(), BorderLayout.CENTER);
        view.add(createSouthPanel(), BorderLayout.SOUTH);

        detailPanel.setViewportView(view);
    }

    private JPanel createCenterPanel() {
        JPanel previewPanel = new JPanel();
        previewPanel.add(previewLabel);
        return previewPanel;
    }

    private void updatePriview(File file) {
    }

    private JPanel createSouthPanel() {
        return new JPanel();
    }

    private void initTablePanel(JScrollPane tablePanel) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(new LargeDuplicateFilesTableModel());
        tablePanel.setViewportView(table);
    }
}

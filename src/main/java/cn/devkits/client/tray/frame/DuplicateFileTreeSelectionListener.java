package cn.devkits.client.tray.frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
                updatePreview(node.getUserObject().toString());
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
        JPanel previewPanelOfCenter = new JPanel();
        previewPanelOfCenter.add(previewLabel);
        return previewPanelOfCenter;
    }

    private JPanel createSouthPanel() {
        JPanel previewPanelOfSouth = new JPanel();
        previewPanelOfSouth.setBackground(Color.BLUE);
        return previewPanelOfSouth;
    }

    /**
     * 更新文件预览
     *
     * @param filePath 文件路径
     */
    private void updatePreview(String filePath) {
        //TODO 待完善其他预览信息展示
        if (DKFileUtil.isImgFromExtension(filePath)) {
            try {
                BufferedImage myPicture = ImageIO.read(new File(filePath));
                previewLabel.setIcon(new ImageIcon(myPicture));
            } catch (IOException ioException) {
                LOGGER.error("Read image data failed: {}", filePath);
            }
        }

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(filePath));
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //格式化输出[directory.getName()] - tag.getTagName() = tag.getDescription()
                    System.out.format("[%s] - %s = %s\n",
                            directory.getName(), tag.getTagName(), tag.getDescription());
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        LOGGER.error("ERROR: {0}", error);
                    }
                }
            }
        } catch (ImageProcessingException e) {
            LOGGER.error("Read image meta data failed: {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Read image meta data failed: {}", e.getMessage());
        }
    }


    private void initTablePanel(JScrollPane tablePanel) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(new LargeDuplicateFilesTableModel());
        tablePanel.setViewportView(table);
    }
}

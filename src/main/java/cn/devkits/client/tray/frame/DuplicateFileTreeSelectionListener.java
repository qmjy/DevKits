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
import javax.swing.JTabbedPane;
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
import java.awt.FlowLayout;
import java.awt.Image;
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
    private JPanel previewPanelOfCenter;
    private JPanel previewPanelOfSouth;
    private JLabel previewLabel = new JLabel();
    private JTabbedPane propertiesTabbedPane;
    private ConcurrentHashMap<String, Set<String>> md5FilesMap;

    public DuplicateFileTreeSelectionListener(JPanel rightPane, CardLayout rightPaneLayout, ConcurrentHashMap<String, Set<String>> md5FilesMap,
                                              String[] rightPaneNames) {
        this.md5FilesMap = md5FilesMap;
        this.rightPane = rightPane;
        this.rightPaneLayout = rightPaneLayout;
        this.rightPaneNames = rightPaneNames;

        Component[] components = rightPane.getComponents();
        initTablePanel((JScrollPane) components[0]);
        initDetailPanel((JPanel) components[1]);
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


    private void initDetailPanel(JPanel detailPanel) {
        detailPanel.setLayout(new BorderLayout());

        detailPanel.add(createCenterPanel(), BorderLayout.CENTER);
        detailPanel.add(createSouthPanel(), BorderLayout.SOUTH);
    }

    private JPanel createCenterPanel() {
        this.previewPanelOfCenter = new JPanel();
        previewPanelOfCenter.add(previewLabel);
        return previewPanelOfCenter;
    }

    private JPanel createSouthPanel() {
        this.previewPanelOfSouth = new JPanel();
        previewPanelOfSouth.setLayout(new BorderLayout());
        propertiesTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        // 不显示选项卡上的焦点虚线边框
        propertiesTabbedPane.setFocusable(false);
        previewPanelOfSouth.add(propertiesTabbedPane, BorderLayout.CENTER);
        return previewPanelOfSouth;
    }

    /**
     * 更新文件预览
     *
     * @param filePath 文件路径
     */
    private void updatePreview(String filePath) {
        propertiesTabbedPane.removeAll();
        Dimension parentContainerSize = previewPanelOfCenter.getParent().getSize();

        int pWidth = (int) parentContainerSize.getWidth();
        int pHeight = (int) parentContainerSize.getHeight();
        int pHeightOfDot8 = (int) (parentContainerSize.getHeight() * 0.8);

        previewPanelOfCenter.setPreferredSize(new Dimension(pWidth, pHeightOfDot8));
        previewPanelOfSouth.setPreferredSize(new Dimension(pWidth, pHeight - pHeightOfDot8));

        //TODO 待完善其他预览信息展示
        if (DKFileUtil.isImgFromExtension(filePath)) {
            try {
                BufferedImage myPicture = ImageIO.read(new File(filePath));
                ImageIcon image = new ImageIcon(myPicture);
                Dimension sizeWithAspectRatio = DKFileUtil.getSizeWithAspectRatio(pWidth, pHeightOfDot8, image.getIconWidth(), image.getIconHeight());
                Image img = image.getImage().getScaledInstance((int) sizeWithAspectRatio.getWidth(), (int) sizeWithAspectRatio.getHeight(), Image.SCALE_DEFAULT);
                image.setImage(img);
                previewLabel.setIcon(image);
            } catch (IOException ioException) {
                LOGGER.error("Read image data failed: {}", filePath);
            }
        }

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(filePath));
            for (Directory directory : metadata.getDirectories()) {
                JPanel component = new JPanel();
                component.setLayout(new FlowLayout(FlowLayout.LEFT));
                propertiesTabbedPane.addTab(directory.getName(), component);
                for (Tag tag : directory.getTags()) {
                    component.add(new JLabel(tag.getTagName() + "：" + tag.getDescription()));
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

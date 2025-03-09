package cn.devkits.client.tray.frame;

import cn.devkits.client.cmd.ui.DKJImagePopupMenu;
import cn.devkits.client.tray.frame.listener.DelFileTableActionListener;
import cn.devkits.client.tray.frame.listener.ImgTableListSelectionListener;
import cn.devkits.client.tray.frame.listener.ImgTableMenuItemActionListener;
import cn.devkits.client.tray.frame.listener.SelectFileTableActionListener;
import cn.devkits.client.tray.model.FileTableCellRender;
import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图片处理窗体
 *
 * @author Shaofeng Liu
 * @Date 2022/3/24
 */
public class ImgProcessingFrame extends DKAbstractFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImgProcessingFrame.class);

    private final FilesTableModel filesModel = new FilesTableModel();
    private final JTable table = new JTable(filesModel);
    private final DKJImagePopupMenu menu = DKSysUIUtil.createDKJPopupMenu();
    private final JLabel statusLine = new JLabel(DKSysUIUtil.getLocaleWithEllipsis("COMMON_LABEL_TXT_READY"));
    private final JLabel previewLabel = new JLabel(DKSysUIUtil.getLocaleWithEllipsis("COMMON_LABEL_TXT_PREVIEW"), JLabel.CENTER);

    public ImgProcessingFrame() {
        super(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_TITLE"), 1.2f);
        initPopupMenu();
        initUI(getDKPane());
        initListener();
    }


    @Override
    protected void initUI(Container rootContainer) {
        Box horizontalBox = Box.createHorizontalBox();
        float PANE_WIDTH_L = 0.6f;
        horizontalBox.add(createLeftPane(PANE_WIDTH_L));
        float PANE_WIDTH_R = 1 - PANE_WIDTH_L;
        horizontalBox.add(createRightPane(PANE_WIDTH_R));

        rootContainer.add(BorderLayout.CENTER, horizontalBox);
        rootContainer.add(BorderLayout.SOUTH, createBottomPane());
    }


    private Component createBottomPane() {
        JPanel bottomPane = DKSysUIUtil.createPaneWithBorder(Color.GRAY);
        bottomPane.setLayout(new BorderLayout());
        bottomPane.add(statusLine);
        return bottomPane;
    }

    public void updateSelectFile(File file) {
        statusLine.setText(file.getName());

        try {
            BufferedImage myPicture = ImageIO.read(file);
            ImageIcon image = new ImageIcon(myPicture);
            Dimension sizeWithAspectRatio = DKFileUtil.getSizeWithAspectRatio(previewLabel.getWidth(), previewLabel.getHeight(), image.getIconWidth(), image.getIconHeight());
            Image img = image.getImage().getScaledInstance((int) sizeWithAspectRatio.getWidth(), (int) sizeWithAspectRatio.getHeight(), Image.SCALE_DEFAULT);
            image.setImage(img);
            previewLabel.setIcon(image);
        } catch (IOException e) {
            LOGGER.error("Load image file failed: {0}", file.getAbsolutePath());
        }
    }

    private Component createRightPane(float rightWidth) {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT")));
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * rightWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());
        jPanel.add(createProcessPanel(), BorderLayout.CENTER);
        jPanel.add(createSavePanel(), BorderLayout.SOUTH);
        return jPanel;
    }

    private JPanel createSavePanel() {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        jPanel.add(new JButton(DKSysUIUtil.getLocale("COMMON_BTN_SAVE")));
        return jPanel;
    }


    private JPanel createProcessPanel() {
        FormLayout layout = new FormLayout(
                "right:max(40dlu;p), 4dlu, 40dlu, 7dlu, right:p, 4dlu, 40dlu, 7dlu, right:p, 4dlu, 60dlu",
                "p, 5dlu, p, 5dlu, p, 5dlu, p, 20dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 20dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_ORIGIN_PROPERTIES"), cc.xyw(1, 1, 11));
        builder.addLabel("宽度:", cc.xy(1, 3));
        builder.add(new JTextField(), cc.xy(3, 3));
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        builder.add(previewLabel, cc.xywh(7, 3, 5, 5));
        builder.addLabel("高度:", cc.xy(1, 5));
        builder.add(new JTextField(), cc.xy(3, 5));
        builder.addLabel("扩展名:", cc.xy(1, 7));
        builder.add(new JTextField(), cc.xy(3, 7));

        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT_PROPERTIES"), cc.xyw(1, 9, 11));
        builder.addLabel("宽度:", cc.xy(1, 11));
        builder.add(new JSpinner(new SpinnerNumberModel(10, 0, 100, 1)), cc.xy(3, 11));
        builder.addLabel("高度:", cc.xy(5, 11));
        builder.add(new JSpinner(new SpinnerNumberModel(10, 0, 100, 1)), cc.xy(7, 11));
        builder.addLabel("尺寸:", cc.xy(9, 11));
        builder.add(createNormalSize(), cc.xy(11, 11));
        builder.addLabel("输出质量:", cc.xy(1, 13));
        builder.add(new JSlider(JSlider.HORIZONTAL, 0, 10, 10), cc.xyw(3, 13, 9));
        builder.addLabel("旋转角度:", cc.xy(1, 15));
        builder.add(new JSpinner(new SpinnerNumberModel(0, 0, 360, 1)), cc.xy(3, 15));
        builder.addLabel("翻转:", cc.xy(5, 15));
        builder.add(createFlipRadio(), cc.xyw(7, 15, 5));
        builder.addLabel("添加水印:", cc.xy(1, 17));
        builder.add(new JTextArea(), cc.xyw(3, 17, 5));


        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT_EXTENSION"), cc.xyw(1, 19, 11));
        builder.addLabel("扩展名:", cc.xy(1, 21));
        builder.add(createExtensionComboBox(), cc.xy(3, 21));

        return builder.getPanel();
    }

    private JPanel createFlipRadio() {
        final JRadioButton radApple = new JRadioButton("不翻转", true);
        final JRadioButton radMango = new JRadioButton("水平翻转");
        final JRadioButton radPeer = new JRadioButton("垂直翻转");

        ButtonGroup group = new ButtonGroup();
        group.add(radApple);
        group.add(radMango);
        group.add(radPeer);

        JPanel jPanel = new JPanel();
        jPanel.add(radApple);
        jPanel.add(radMango);
        jPanel.add(radPeer);
        return jPanel;
    }

    private Component createNormalSize() {
        String[] strings = {"1寸", "2寸", "小2寸（护照）", "5寸", "6寸", "7寸", "8寸", "10寸", "12寸", "15寸"};
        return new JComboBox<>(strings);
    }

    private JComboBox createExtensionComboBox() {
        String[] strings = {"jpg", "gif", "png", "bmp", "raw"};
        return new JComboBox<>(strings);
    }

    private Component createLeftPane(float leftWidth) {
        JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.GRAY));
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * leftWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());

        table.setDefaultRenderer(File.class, new FileTableCellRender());
        // arbitrary size adjustment to better account for icons
        table.setRowHeight((int) (table.getRowHeight() * 1.3));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jPanel.add(table.getTableHeader(), BorderLayout.NORTH);
        jPanel.add(table, BorderLayout.CENTER);
        jPanel.add(createOriginInfoPane(), BorderLayout.SOUTH);
        return jPanel;
    }

    private JPanel createOriginInfoPane() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JPanel appendPane = new JPanel();
        appendPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        appendPane.setLayout(new FlowLayout());

        JButton addBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_ADD_FILE"));
        addBtn.addActionListener(new SelectFileTableActionListener(table, true));
        appendPane.add(addBtn);
        JButton addFolderBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_ADD_FOLDER"));
        addFolderBtn.addActionListener(new SelectFileTableActionListener(table, false));
        appendPane.add(addFolderBtn);
        JButton delSelectedBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_RMV_FILE"));
        delSelectedBtn.addActionListener(new DelFileTableActionListener(table));
        appendPane.add(delSelectedBtn);

        jPanel.add(appendPane, BorderLayout.CENTER);
        return jPanel;
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    protected void initListener() {
        table.getSelectionModel().addListSelectionListener(new ImgTableListSelectionListener(this, table));
        // 支持右键选择表格行和打开右键菜单
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                DKSysUIUtil.enableRightClickSelect(e, table);
                if (table.getSelectedRow() < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void initPopupMenu() {
        JMenuItem removeFromTable = new JMenuItem(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_TREE_MENU_RMV"));
        removeFromTable.addActionListener(new DelFileTableActionListener(table));
        menu.add(removeFromTable);

        menu.addSeparator();

        JMenuItem propertiesItem = new JMenuItem(DKSysUIUtil.getLocaleWithEllipsis("IMG_PROCESSING_FRAME_TREE_MENU_DETAIL"));
        propertiesItem.addActionListener(new ImgTableMenuItemActionListener(table));
        menu.add(propertiesItem);
    }
}

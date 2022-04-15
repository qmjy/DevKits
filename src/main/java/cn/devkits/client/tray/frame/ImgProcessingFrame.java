package cn.devkits.client.tray.frame;

import cn.devkits.client.cmd.ui.DKJImagePopupMenu;
import cn.devkits.client.tray.frame.listener.DelFileTableActionListener;
import cn.devkits.client.tray.frame.listener.ImgTableListSelectionListener;
import cn.devkits.client.tray.frame.listener.ImgTableMenuItemActionListener;
import cn.devkits.client.tray.frame.listener.SelectFileTableActionListener;
import cn.devkits.client.tray.model.FileTableCellRender;
import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKSysUIUtil;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 图片处理窗体
 *
 * @author Shaofeng Liu
 * @Date 2022/3/24
 */
public class ImgProcessingFrame extends DKAbstractFrame {
    private float PANE_WIDTH_L = 0.6f;
    private float PANE_WIDTH_R = 1 - PANE_WIDTH_L;

    private FilesTableModel filesModel = new FilesTableModel();
    private JTable table = new JTable(filesModel);
    private DKJImagePopupMenu menu = DKSysUIUtil.createDKJPopupMenu();
    private JLabel statusLine = new JLabel(DKSysUIUtil.getLocaleWithEllipsis("COMMON_LABEL_TXT_READY"));

    public ImgProcessingFrame() {
        super(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_TITLE"), 1.2f);
        initPopupMenu();
        initUI(getDKPane());
        initListener();
    }


    @Override
    protected void initUI(Container rootContainer) {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(createLeftPane(PANE_WIDTH_L));
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

    public void updateStatusLine(String text) {
        statusLine.setText(text);
    }

    private Component createRightPane(float rightWidth) {
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * rightWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());
        jPanel.add(createProcessPanel(), BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel createProcessPanel() {
        FormLayout layout = new FormLayout(
                "right:max(50dlu;p), 4dlu, 75dlu, 7dlu, right:p, 4dlu, 75dlu",
                "p, 2dlu, p, 3dlu, p, 3dlu, p, 7dlu, p, 2dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_ORIGIN_PROPERTIES"), cc.xyw(1, 1, 7));
        builder.addLabel("Identifier", cc.xy(1, 3));
        builder.add(new JTextField(), cc.xy(3, 3));
        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT_PROPERTIES"), cc.xyw(1, 5, 7));
        builder.add(new JPanel(), cc.xyw(3, 5, 5));
        builder.addLabel("len[mm]", cc.xy(1, 7));
        builder.add(new JTextField(), cc.xy(3, 7));

        builder.addSeparator(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT_EXTENSION"), cc.xyw(1, 9, 7));
        builder.addLabel("da[mm]", cc.xy(1, 11));
        builder.add(new JTextField(), cc.xy(3, 11));

        JPanel panel = builder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("IMG_PROCESSING_R_OP_PARAM_OUTPUT")));
        return panel;
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

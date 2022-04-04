package cn.devkits.client.tray.frame;

import cn.devkits.client.cmd.ui.DKJImagePopupMenu;
import cn.devkits.client.tray.frame.listener.DelFileTableActionListener;
import cn.devkits.client.tray.frame.listener.ImgTableListSelectionListener;
import cn.devkits.client.tray.frame.listener.ImgTableMenuItemActionListener;
import cn.devkits.client.tray.frame.listener.SelectFileTableActionListener;
import cn.devkits.client.tray.model.FileTableCellRender;
import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKSysUIUtil;

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

    public ImgProcessingFrame() {
        super(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_TITLE"));
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
        bottomPane.add(new JLabel("这里是状态栏区域...."));
        return bottomPane;
    }

    private Component createRightPane(float rightWidth) {
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * rightWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());
        jPanel.add(createTabPanel(), BorderLayout.CENTER);
        return jPanel;
    }

    private JTabbedPane createTabPanel() {
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.add(DKSysUIUtil.getLocale("IMG_PROCESSING_R_TAB_NAME_PROCESS"), new JLabel());
        jTabbedPane.add(DKSysUIUtil.getLocale("IMG_PROCESSING_R_TAB_NAME_FORMAT"), new JLabel());
        return jTabbedPane;
    }

    private Component createLeftPane(float leftWidth) {
        JPanel jPanel = DKSysUIUtil.createPaneWithBorder(Color.GRAY);
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * leftWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());

        table.setDefaultRenderer(Object.class, new FileTableCellRender());
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
        appendPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY));
        appendPane.setLayout(new FlowLayout());

        JButton addBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_ADD_FILE"));
        addBtn.addActionListener(new SelectFileTableActionListener(table, true));
        appendPane.add(addBtn);
        JButton addFolderBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_ADD_FOLDER"));
        addFolderBtn.addActionListener(new SelectFileTableActionListener(table, false));
        appendPane.add(addFolderBtn);
        JButton delSelectedBtn = new JButton(DKSysUIUtil.getLocale("IMG_PROCESSING_FRAME_BTNS_DEL_FILE"));
        delSelectedBtn.addActionListener(new DelFileTableActionListener(table));
        appendPane.add(delSelectedBtn);

        jPanel.add(appendPane, BorderLayout.CENTER);
        return jPanel;
    }


    @Override
    protected void initListener() {
        table.getSelectionModel().addListSelectionListener(new ImgTableListSelectionListener(this, table));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
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

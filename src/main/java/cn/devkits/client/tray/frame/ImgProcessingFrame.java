package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.listener.SelectFileListener;
import cn.devkits.client.tray.model.ImgProcessingListModel;
import cn.devkits.client.util.DKSysUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 图片处理窗体
 *
 * @author Shaofeng Liu
 * @Date 2022/3/24
 */
public class ImgProcessingFrame extends DKAbstractFrame {
    private float PANE_WIDTH_L = 0.6f;
    private float PANE_WIDTH_R = 1 - PANE_WIDTH_L;

    private ImgProcessingListModel filesModel = null;
    private JTable table = null;

    public ImgProcessingFrame() {
        super(DKSysUIUtil.getLocaleString("IMG_PROCESSING_FRAME_TITLE"));
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.setLayout(new BorderLayout());

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
        jTabbedPane.add(DKSysUIUtil.getLocaleString("IMG_PROCESSING_R_TAB_NAME_PROCESS"), new JLabel());
        jTabbedPane.add(DKSysUIUtil.getLocaleString("IMG_PROCESSING_R_TAB_NAME_FORMAT"), new JLabel());
        return jTabbedPane;
    }

    private Component createLeftPane(float leftWidth) {
        JPanel jPanel = DKSysUIUtil.createPaneWithBorder(Color.GRAY);
        jPanel.setPreferredSize(new Dimension((int) (getWidth() * leftWidth), getHeight()));
        jPanel.setLayout(new BorderLayout());

        this.filesModel = new ImgProcessingListModel();
        this.table = new JTable(filesModel);

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


        JButton addBtn = new JButton(DKSysUIUtil.getLocaleString("IMG_PROCESSING_FRAME_BTNS_ADD_FILE"));
        addBtn.addActionListener(new SelectFileListener(table, true));
        appendPane.add(addBtn);
        JButton addFolderBtn = new JButton(DKSysUIUtil.getLocaleString("IMG_PROCESSING_FRAME_BTNS_ADD_FOLDER"));
        addFolderBtn.addActionListener(new SelectFileListener(table, false));
        appendPane.add(addFolderBtn);
        JButton delSelectedBtn = new JButton(DKSysUIUtil.getLocaleString("IMG_PROCESSING_FRAME_BTNS_DEL_FILE"));
        delSelectedBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            filesModel.removeFileOfRow(selectedRow);
            table.setModel(filesModel);
        });
        appendPane.add(delSelectedBtn);

        jPanel.add(appendPane, BorderLayout.NORTH);

        JPanel imgInfoPane = new JPanel();
        imgInfoPane.setLayout(new GridLayout(1, 4, 5, 5));
        imgInfoPane.add(new JLabel("图像宽度:"));
        imgInfoPane.add(new JLabel("1024px"));
        imgInfoPane.add(new JLabel("图像高度："));
        imgInfoPane.add(new JLabel("1024px"));

        jPanel.add(imgInfoPane, BorderLayout.CENTER);
        return jPanel;
    }

    @Override
    protected void initListener() {

    }
}

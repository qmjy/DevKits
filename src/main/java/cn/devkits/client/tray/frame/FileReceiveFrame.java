package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.frame.FileReceiveServer.TransferTask;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileReceiveFrame extends DKAbstractFrame {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReceiveFrame.class);

    private static final int QR_IMG_SIZE = 260;
    private static final int REFRESH_INTERVAL_MS = 300;
    private static final int INFO_PANE_WIDTH = 330;

    private final FileReceiveServer receiveServer = new FileReceiveServer();

    private JLabel qrLabel;
    private JLabel urlLabel;
    private JLabel tipLabel;
    private JLabel pathLabel;
    private JButton openDirBtn;
    private DefaultTableModel taskModel;
    private JTable taskTable;
    private Timer refreshTimer;
    private JPopupMenu taskPopupMenu;
    private JMenuItem openFileItem;
    private JMenuItem openFolderItem;
    private JMenuItem cancelItem;
    private List<TransferTask> currentTasks = new ArrayList<>();
    private final Map<Integer, Integer> taskRowMap = new ConcurrentHashMap<>();

    public FileReceiveFrame() {
        super(DKSysUIUtil.getLocale("FILE_RECEIVE_FRAME_TITLE"), 0.55f);
        initUI(getDKPane());
        initListener();
        startServer();
        refreshTimer = new Timer(REFRESH_INTERVAL_MS, e -> refreshTasks());
        refreshTimer.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshTimer.stop();
                stopServer();
            }
        });
    }

    @Override
    protected void initUI(Container rootContainer) {
        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.add(BorderLayout.WEST, createInfoPane());
        centerWrap.add(BorderLayout.CENTER, createTaskPane());
        rootContainer.add(BorderLayout.CENTER, centerWrap);
        rootContainer.add(BorderLayout.SOUTH, createBottomPane());
    }

    @Override
    protected void initListener() {
        openDirBtn.addActionListener(e -> {
            if (receiveServer.getSaveDir() != null) {
                DKFileUtil.openFolder(receiveServer.getSaveDir().getAbsolutePath());
            }
        });
        urlLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String url = urlLabel.getText();
                if (url.startsWith("http://")) {
                    DKSysUIUtil.setSystemClipboard(url);
                    JOptionPane.showMessageDialog(FileReceiveFrame.this, DKSysUIUtil.getLocale("FILE_RECEIVE_URL_COPIED"));
                }
            }
        });
        taskPopupMenu = new JPopupMenu();
        openFileItem = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_OPEN"));
        openFileItem.addActionListener(e -> openSelectedTaskFile());
        openFolderItem = new JMenuItem(DKSysUIUtil.getLocale("POPUP_MENU_FILE_SHOW_IN_EXPLORER"));
        openFolderItem.addActionListener(e -> openSelectedTaskFolder());
        taskPopupMenu.add(openFileItem);
        taskPopupMenu.add(openFolderItem);
        taskPopupMenu.addSeparator();

        cancelItem = new JMenuItem(DKSysUIUtil.getLocale("FILE_RECEIVE_CANCEL"));
        cancelItem.addActionListener(e -> {
            TransferTask t = getSelectedTask();
            if (t != null) t.cancel();
        });
        taskPopupMenu.add(cancelItem);

        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { handleTaskMouseEvent(e); }
            @Override
            public void mouseReleased(MouseEvent e) { handleTaskMouseEvent(e); }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    openSelectedTaskFile();
                }
            }
        });
    }

    private void handleTaskMouseEvent(MouseEvent e) {
        if (e.isPopupTrigger()) {
            DKSysUIUtil.enableRightClickSelect(e, taskTable);
            TransferTask t = getSelectedTask();
            boolean received = t != null && t.getStatus() == TransferTask.Status.DONE && t.getFile() != null;
            boolean canCancel = t != null && (t.getStatus() == TransferTask.Status.TRANSFERRING || t.getStatus() == TransferTask.Status.PAUSED);
            openFileItem.setEnabled(received);
            openFolderItem.setEnabled(received);
            cancelItem.setEnabled(canCancel);
            taskPopupMenu.show(taskTable, e.getX(), e.getY());
        }
    }

    private TransferTask getSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0 || row >= taskModel.getRowCount()) {
            return null;
        }
        Object idObj = taskModel.getValueAt(row, 0);
        if (!(idObj instanceof Integer)) {
            return null;
        }
        int id = (Integer) idObj;
        for (TransferTask t : currentTasks) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    private void openSelectedTaskFile() {
        TransferTask t = getSelectedTask();
        if (t != null && t.getFile() != null) {
            DKFileUtil.openFile(t.getFile());
        }
    }

    private void openSelectedTaskFolder() {
        TransferTask t = getSelectedTask();
        if (t != null && t.getFile() != null) {
            DKFileUtil.openFolder(t.getFile().getParent());
        }
    }

    private Component createInfoPane() {
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("FILE_RECEIVE_INFO_TITLE")));
        infoPane.setPreferredSize(new Dimension(INFO_PANE_WIDTH, 0));
        infoPane.setMaximumSize(new Dimension(INFO_PANE_WIDTH, Integer.MAX_VALUE));
        qrLabel = new JLabel(DKSysUIUtil.getLocale("FILE_RECEIVE_WAITING"), SwingConstants.CENTER);
        infoPane.add(BorderLayout.CENTER, qrLabel);
        urlLabel = new JLabel("N/A", SwingConstants.CENTER);
        urlLabel.setFont(urlLabel.getFont().deriveFont(Font.BOLD, 16f));
        urlLabel.setForeground(new Color(74, 144, 217));
        urlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        urlLabel.setToolTipText(DKSysUIUtil.getLocale("FILE_RECEIVE_URL_TIP"));
        urlLabel.setPreferredSize(new Dimension(0, urlLabel.getPreferredSize().height));
        infoPane.add(BorderLayout.NORTH, urlLabel);
        tipLabel = DKSysUIUtil.createLabelWithTextColor(DKSysUIUtil.getLocale("FILE_RECEIVE_TIP"), Color.GRAY);
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipLabel.setPreferredSize(new Dimension(0, tipLabel.getPreferredSize().height));
        infoPane.add(BorderLayout.SOUTH, tipLabel);
        return infoPane;
    }

    private Component createTaskPane() {
        JPanel taskPane = new JPanel(new BorderLayout());
        taskPane.setBorder(BorderFactory.createTitledBorder(DKSysUIUtil.getLocale("FILE_RECEIVE_TASKS_TITLE")));
        taskModel = new DefaultTableModel(new Object[][]{}, new Object[]{
                "ID",
                DKSysUIUtil.getLocale("FILE_RECEIVE_TABLE_FILE"),
                DKSysUIUtil.getLocale("FILE_RECEIVE_TABLE_SIZE"),
                DKSysUIUtil.getLocale("FILE_RECEIVE_TABLE_PROGRESS"),
                DKSysUIUtil.getLocale("FILE_RECEIVE_TABLE_STATUS")}) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Float.class : String.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        taskTable = new JTable(taskModel);
        taskTable.setRowHeight(26);
        taskTable.setDefaultRenderer(Float.class, new ProgressBarRenderer());
        taskTable.removeColumn(taskTable.getColumn("ID"));
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        taskPane.add(BorderLayout.CENTER, new JScrollPane(taskTable));
        return taskPane;
    }

    private Component createBottomPane() {
        JPanel bottomPane = new JPanel(new BorderLayout());
        pathLabel = new JLabel(DKSysUIUtil.getLocaleWithColon("FILE_RECEIVE_SAVE_PATH") + FileReceiveServer.resolveSaveDir().getAbsolutePath());
        pathLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPane.add(BorderLayout.CENTER, pathLabel);
        JPanel buttonPane = new JPanel();
        openDirBtn = new JButton(DKSysUIUtil.getLocale("FILE_RECEIVE_OPEN_DIR"));
        openDirBtn.setEnabled(false);
        buttonPane.add(openDirBtn);
        bottomPane.add(BorderLayout.EAST, buttonPane);
        return bottomPane;
    }

    private void startServer() {
        new Thread(() -> {
            try {
                receiveServer.start();
                String url = buildAccessUrl();
                BufferedImage qrImg = DKSysUtil.generateQrImg(url, QR_IMG_SIZE, QR_IMG_SIZE);
                SwingUtilities.invokeLater(() -> {
                    if (receiveServer.isRunning()) {
                        if (qrImg != null) {
                            qrLabel.setIcon(new ImageIcon(qrImg));
                            qrLabel.setText(null);
                        }
                        urlLabel.setText(url);
                        tipLabel.setText(DKSysUIUtil.getLocale("FILE_RECEIVE_TIP"));
                        pathLabel.setText(DKSysUIUtil.getLocaleWithColon("FILE_RECEIVE_SAVE_PATH") + receiveServer.getSaveDir().getAbsolutePath());
                        openDirBtn.setEnabled(true);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("Start file receive server failed: {}", ex.getMessage());
                SwingUtilities.invokeLater(() -> {
                    stopServer();
                    JOptionPane.showMessageDialog(FileReceiveFrame.this,
                            DKSysUIUtil.getLocaleWithParam("FILE_RECEIVE_SERVER_START_FAILED", ex.getMessage()),
                            DKSysUIUtil.getLocale("FILE_RECEIVE_FRAME_TITLE"), JOptionPane.ERROR_MESSAGE);
                });
            }
        }, "file-receive-starter").start();
    }

    private void stopServer() {
        receiveServer.stop();
        taskRowMap.clear();
        SwingUtilities.invokeLater(() -> {
            taskModel.setRowCount(0);
            qrLabel.setIcon(null);
            qrLabel.setText(DKSysUIUtil.getLocale("FILE_RECEIVE_WAITING"));
            urlLabel.setText("N/A");
            tipLabel.setText(DKSysUIUtil.getLocale("FILE_RECEIVE_TIP"));
            openDirBtn.setEnabled(false);
        });
    }

    private String buildAccessUrl() {
        String ip = DKNetworkUtil.getIp().orElseGet(() -> {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                LOGGER.warn("Get local host address failed: {}", e.getMessage());
                return "127.0.0.1";
            }
        });
        return "http://" + ip + ":" + receiveServer.getPort() + "/";
    }

    private void refreshTasks() {
        List<TransferTask> tasks = receiveServer.getTasks();

        List<Map.Entry<Integer, Integer>> deadRows = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : taskRowMap.entrySet()) {
            boolean alive = false;
            for (TransferTask t : tasks) {
                if (t.getId() == entry.getKey()) { alive = true; break; }
            }
            if (!alive) deadRows.add(entry);
        }
        deadRows.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        for (Map.Entry<Integer, Integer> dead : deadRows) {
            int row = dead.getValue();
            if (row >= 0 && row < taskModel.getRowCount()) {
                taskModel.removeRow(row);
            }
            taskRowMap.remove(dead.getKey());
            for (Map.Entry<Integer, Integer> e : taskRowMap.entrySet()) {
                if (e.getValue() > row) e.setValue(e.getValue() - 1);
            }
        }

        for (TransferTask task : tasks) {
            Integer row = taskRowMap.get(task.getId());
            if (row == null) {
                row = taskModel.getRowCount();
                taskRowMap.put(task.getId(), row);
                taskModel.addRow(new Object[]{
                        task.getId(),
                        task.getFileName(),
                        FileUtils.byteCountToDisplaySize(Math.max(0, task.getTotalSize())),
                        task.getProgress(),
                        getStatusText(task)});
            } else {
                taskModel.setValueAt(FileUtils.byteCountToDisplaySize(Math.max(0, task.getTotalSize())), row, 2);
                taskModel.setValueAt(task.getProgress(), row, 3);
                taskModel.setValueAt(getStatusText(task), row, 4);
            }
        }
        currentTasks = tasks;
    }

    private String getStatusText(TransferTask task) {
        return switch (task.getStatus()) {
            case TRANSFERRING -> DKSysUIUtil.getLocale("FILE_RECEIVE_STATUS_TRANSFERRING");
            case PAUSED -> DKSysUIUtil.getLocale("FILE_RECEIVE_STATUS_PAUSED");
            case DONE -> DKSysUIUtil.getLocale("FILE_RECEIVE_STATUS_DONE");
            case FAILED -> DKSysUIUtil.getLocale("FILE_RECEIVE_STATUS_FAILED");
            case CANCELLED -> DKSysUIUtil.getLocale("FILE_RECEIVE_STATUS_CANCELLED");
        };
    }

    private static class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {
        private ProgressBarRenderer() { super(0, 100); setStringPainted(true); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            float progress = value instanceof Float ? (Float) value : 0f;
            int percent = Math.round(progress * 100);
            setValue(percent);
            setString(percent + "%");
            return this;
        }
    }
}

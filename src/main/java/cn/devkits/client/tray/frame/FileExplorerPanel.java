package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import cn.devkits.client.tray.model.FileTableModel;
import cn.devkits.client.util.DKFileUtil;

public class FileExplorerPanel extends JPanel {

    /** serialVersionUID */
    private static final long serialVersionUID = -2230863950855742735L;
    private JTextField currentPathTextField;
    private JTable filesTable;
    private JLabel statusBar;

    private String defaultPath = System.getProperty("user.home");
    private FileTableModel model;

    public FileExplorerPanel() {
        super(new BorderLayout());

        initUI();
        initListener();
    }

    private void initUI() {
        this.currentPathTextField = new JTextField(defaultPath);
        add(currentPathTextField, BorderLayout.NORTH);

        this.model = new FileTableModel(new File(defaultPath));
        this.filesTable = new JTable(model);
        add(new JScrollPane(filesTable), BorderLayout.CENTER);

        this.statusBar = new JLabel("Read to go...");
        add(statusBar, BorderLayout.SOUTH);
    }


    private void initListener() {
        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int rowNum = filesTable.getSelectedRow();
                    String fileName = (String) filesTable.getValueAt(rowNum, 0);
                    updateStatusBar(fileName);
                } else if (e.getClickCount() == 2) {
                    int rowNum = filesTable.getSelectedRow();
                    String fileName = (String) filesTable.getValueAt(rowNum, 0);
                    openFileOrDir(fileName);
                }
            }
        });
    }

    private void updateTable(File newFileDir) {
        defaultPath = newFileDir.getAbsolutePath();
        currentPathTextField.setText(defaultPath);

        model.updateRoot(new File(defaultPath));
        filesTable.validate();
        filesTable.updateUI();
    }

    private void updateStatusBar(String fileName) {
        Optional<File> selectFile = getSelectFile(fileName);
        if (selectFile.isPresent()) {
            statusBar.setText(selectFile.get().getAbsolutePath());
        }
    }

    private Optional<File> getSelectFile(String fileName) {
        File[] fileLists = new File(defaultPath).listFiles();
        for (File file : fileLists) {
            if (file.getAbsolutePath().endsWith(fileName)) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }


    private void openFileOrDir(String fileName) {
        Optional<File> selectFile = getSelectFile(fileName);
        if (selectFile.isPresent()) {
            File file = selectFile.get();
            if (file.isDirectory()) {
                updateTable(file);
            } else {
                DKFileUtil.openFile(file);
            }
        }
    }

}

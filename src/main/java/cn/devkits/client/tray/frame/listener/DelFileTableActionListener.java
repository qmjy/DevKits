package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.model.FilesTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DelFileTableActionListener implements ActionListener {
    private final JTable table;

    public DelFileTableActionListener(JTable table) {
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        FilesTableModel model = (FilesTableModel) table.getModel();
        model.removeFileOfRow(selectedRow);
        table.setModel(model);
        table.repaint();
    }
}

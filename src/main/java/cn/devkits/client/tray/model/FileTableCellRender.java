package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKFileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;

public class FileTableCellRender extends DefaultTableCellRenderer {
    private FileSystemView fileSystemView = DKFileUtil.getFileSysView();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component defaultComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 0) {
            File f = (File) value;
            setIcon(fileSystemView.getSystemIcon(f));
            setText(f.getName());
        }
        return defaultComponent;
    }
}

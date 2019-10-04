package cn.devkits.client.tray.model;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import cn.devkits.client.util.DKFileUtil;

public class LargeDuplicateFilesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 5068639881692339753L;

    private List<File> files;
    private String[] names = {"Index", "File Path", "File Size", "Create Time", "Last Modify Time", "Action"};


    public LargeDuplicateFilesTableModel() {
        this.files = new ArrayList<File>();
    }

    public LargeDuplicateFilesTableModel(List<File> files) {
        this.files = files;
    }

    @Override
    public String getColumnName(int column) {
        return names[column];
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        File file = files.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return file.getAbsolutePath();
            case 2:
                return file.length();
            case 3:
                return Instant.ofEpochSecond(DKFileUtil.getFileAttr(file).creationTime().toMillis());
            case 4:
                return Instant.ofEpochSecond(file.lastModified());

            default:
                return null;
        }

    }

}

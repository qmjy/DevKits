package cn.devkits.client.tray.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import oshi.util.FormatUtil;

public class LargeDuplicateFilesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 5068639881692339753L;

    private List<File> files;
    private String[] names = {"File Name", "File Path", "File Size", "Create Time", "Last Modify Time"};


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
                return file.getName();
            case 1:
                return file.getParent();
            case 2:
                return FormatUtil.formatBytes(file.length());
            case 3:
                return DKDateTimeUtil.long2Str(DKFileUtil.getFileAttr(file).creationTime().toMillis());
            case 4:
                return DKDateTimeUtil.long2Str(file.lastModified());

            default:
                return null;
        }

    }

}

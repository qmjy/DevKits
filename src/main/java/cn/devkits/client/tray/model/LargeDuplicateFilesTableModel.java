package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

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
                return DKFileUtil.formatBytes(file.length());
            case 3:
                Optional<BasicFileAttributes> fileAttr = DKFileUtil.getFileAttr(file);
                if (fileAttr.isPresent()) {
                    return DKDateTimeUtil.long2Str(fileAttr.get().creationTime().toMillis());
                } else {
                    return "";
                }
            case 4:
                return DKDateTimeUtil.long2Str(file.lastModified());
            default:
                return null;
        }

    }

}

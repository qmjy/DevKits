package cn.devkits.client.tray.model;

import java.io.File;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import cn.devkits.client.util.DKDateTimeUtil;
import oshi.util.FormatUtil;

public class FileTableModel extends AbstractTableModel {
    /** serialVersionUID */
    private static final long serialVersionUID = 1654386662658602678L;
    protected File dir;
    protected String[] filenames;

    protected String[] columnNames = new String[] {"Name", "Last modified", "Type", "Size"};


    public FileTableModel(File dir) {
        this.dir = dir;
        this.filenames = dir.list();
    }

    /**
     * 更新文件根路径
     * @param dir 文件根路径
     */
    public void updateRoot(File dir) {
        this.dir = dir;
        this.filenames = dir.list();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return filenames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    public Object getValueAt(int row, int col) {
        File f = new File(dir, filenames[row]);
        switch (col) {
            case 0:
                return filenames[row];
            case 1:
                return DKDateTimeUtil.long2Str(f.lastModified());
            case 2:
                return getFileType(f);
            case 3:
                return FormatUtil.formatBytes(f.length());
            case 4:
            default:
                return null;
        }
    }

    private String getFileType(File f) {
        if (f.isDirectory()) {
            return "Folder";
        }

        String fileName = f.getName();
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase(Locale.getDefault());
        }

        return "";
    }
}

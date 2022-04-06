/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;
import oshi.util.FormatUtil;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilesTableModel extends AbstractTableModel {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1654386662658602678L;
    protected String currentDir;
    private List<String> children = new ArrayList<>();

    protected String[] columnNames = new String[]{"COMMON_LABEL_FILE_NAME", "COMMON_LABEL_FILE_PATH",
            "COMMON_LABEL_FILE_LAST_MODIFY", "COMMON_LABEL_FILE_TYPE", "COMMON_LABEL_FILE_LENGTH"};

    public FilesTableModel() {
        File[] files = new File(DKSysUtil.getHomePath()).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (dir.isDirectory()) {
                    return false;
                }
                return DKFileUtil.isImgFromExtension(name);
            }
        });
        for (File file : files) {
            children.add(file.getAbsolutePath());
        }
    }

    public FilesTableModel(String dir) {
        this.currentDir = dir;
        String[] list = new File(dir).list();
        for (String s : list) {
            children.add(dir + File.separator + s);
        }
    }

    /**
     * 更新文件根路径
     *
     * @param dir 文件根路径
     */
    public void updateRoot(String dir) {
        this.currentDir = dir;
        //注意：list返回的不是全路径
        String[] list = new File(dir).list();
        for (String s : list) {
            children.add(s);
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return DKSysUIUtil.getLocale(columnNames[col]);
    }


    /**
     * 获取表格模型的指定行
     *
     * @param rowIndex 指定行的文件模型
     * @return 文件对象
     */
    public File getFileAt(int rowIndex) {
        return new File(children.get(rowIndex));
    }

    /**
     * 新增多个文件
     *
     * @param files 待追加的文件列表
     */
    public void addFiles(List<File> files) {
        for (File file : files) {
            if (!contains(file.getAbsolutePath())) {
                children.add(file.getAbsolutePath());
            }
        }
    }

    /**
     * 新增多个文件
     *
     * @param files 待追加的文件列表
     */
    public void addFiles(File[] files) {
        for (File file : files) {
            children.add(file.getAbsolutePath());
        }
    }

    /**
     * 新增单个文件
     *
     * @param filePath 待追加的文件
     */
    public void addFile(String filePath) {
        if (!contains(filePath)) {
            children.add(filePath);
        }
    }

    private boolean contains(String absolutePath) {
        for (String filePath : children) {
            if (filePath.equals(absolutePath)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 通过行号删除选中的数据
     *
     * @param selectedRow 选中的行号
     * @return 是否删除成功
     */
    public boolean removeFileOfRow(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < children.size()) {
            children.remove(selectedRow);
            return true;
        }
        return false;
    }


    @Override
    public int getRowCount() {
        return children.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int row, int col) {
        String filePath = children.get(row);
        File f = new File(filePath);
        switch (col) {
            case 0:
                return f;
            case 1:
                return new File(f.getAbsolutePath()).getParent();
            case 2:
                return DKDateTimeUtil.long2Str(f.lastModified());
            case 3:
                return getFileType(f);
            case 4:
                if (f.isDirectory()) {
                    return "";
                    // return FormatUtil.formatBytes(DKFileUtil.getFolderSize(f));
                }
                return FormatUtil.formatBytes(f.length());
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

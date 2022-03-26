package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUtil;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片处理，对象模型
 */
public class ImgProcessingListModel extends AbstractTableModel {

    private static final String[] tableNames = new String[]{"文件名", "文件路径", "文件大小", "最后修改时间"};
    private List<File> fileList = new ArrayList<>();

    public ImgProcessingListModel() {
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
            fileList.add(file);
        }
    }

    /**
     * 新增多个文件
     *
     * @param files 待追加的文件列表
     */
    public void addFiles(List<File> files) {
        fileList.addAll(files);
    }

    /**
     * 新增多个文件
     *
     * @param files 待追加的文件列表
     */
    public void addFiles(File[] files) {
        for (File file : files) {
            fileList.add(file);
        }
    }

    /**
     * 新增单个文件
     *
     * @param f 待追加的文件
     */
    public void addFile(File f) {
        fileList.add(f);
    }

    /**
     * 通过行号删除选中的数据
     *
     * @param selectedRow 选中的行号
     * @return 是否删除成功
     */
    public boolean removeFileOfRow(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < fileList.size()) {
            fileList.remove(selectedRow);
            return true;
        }
        return false;
    }


    @Override
    public int getRowCount() {
        return fileList.size();
    }

    @Override
    public int getColumnCount() {
        return tableNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return tableNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return fileList.get(rowIndex).getName();
            case 1:
                return fileList.get(rowIndex).getAbsolutePath();
            case 2:
                return DKFileUtil.formatBytes(fileList.get(rowIndex).length());
            case 3:
                return DKDateTimeUtil.long2Str(fileList.get(rowIndex).lastModified());
            default:
                break;
        }
        return null;
    }


}

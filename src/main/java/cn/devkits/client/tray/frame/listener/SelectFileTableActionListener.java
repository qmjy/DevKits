package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.model.ImgProcessingListModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件选择监听器
 *
 * @author Shaofeng Liu
 * @Date 2022/03/26
 */
public class SelectFileTableActionListener implements ActionListener {

    private final JTable table;
    private final ImgProcessingListModel filesModel;
    private final boolean isSelectFile;

    /**
     * 构造方法
     *
     * @param table        待刷新的表格
     * @param isSelectFile 选择文件还是目录
     */
    public SelectFileTableActionListener(JTable table, boolean isSelectFile) {
        this.table = table;
        this.filesModel = (ImgProcessingListModel) table.getModel();
        this.isSelectFile = isSelectFile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(isSelectFile ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
        jfc.setMultiSelectionEnabled(true);
        jfc.setCurrentDirectory(DKSysUtil.getHomeDir());
        jfc.setDialogTitle(DKSysUIUtil.getLocaleString("FILE_CHOOSER_DIALOG_TITLE_EXPORT"));
        int i = jfc.showSaveDialog(table.getParent());
        if (i == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = jfc.getSelectedFiles();
            if (isSelectFile) {
                filesModel.addFiles(DKFileUtil.filterImgsFromFiles(selectedFiles));
            } else {
                filesModel.addFiles(filterImgFromDir(selectedFiles));
            }
            table.setModel(filesModel);
            table.repaint();
        }
    }

    private List<File> filterImgFromDir(File[] selectedFiles) {
        List<File> receiveList = new ArrayList<>();
        for (File selectedDir : selectedFiles) {
            DKFileUtil.filterImgsFromDir(selectedDir, receiveList);
        }
        return receiveList;
    }
}

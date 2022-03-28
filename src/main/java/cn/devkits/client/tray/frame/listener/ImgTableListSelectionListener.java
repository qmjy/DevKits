package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.frame.ImgProcessingFrame;
import cn.devkits.client.tray.model.ImgProcessingListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;

/**
 * 表格行选中事件
 *
 * @author Shaofeng Liu
 * @Date 2022/03/28
 */
public class ImgTableListSelectionListener implements ListSelectionListener {
    private final JTable table;
    private final ImgProcessingFrame parent;

    public ImgTableListSelectionListener(ImgProcessingFrame imgProcessingFrame, JTable table) {
        this.parent = imgProcessingFrame;
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = table.getSelectedRow();
            ImgProcessingListModel selectionModel = (ImgProcessingListModel) table.getModel();
            File fileAt = selectionModel.getFileAt(selectedRow);
        }
    }
}

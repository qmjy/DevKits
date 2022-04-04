package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImgTableMenuItemActionListener implements ActionListener {
    private final JTable table;
    private final FilesTableModel filesModel;

    public ImgTableMenuItemActionListener(JTable table) {
        this.table = table;
        this.filesModel = (FilesTableModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        File file = filesModel.getFileAt(selectedRow);

        JDialog jDialog = new JDialog();
        jDialog.setTitle(file.getName());
        int height = 300;
        Rectangle center = DKSysUIUtil.getCenter((int) (height / DKSysUIUtil.GOLDEN_RATIO), height);
        jDialog.setBounds(center);
        wrapImgInfo(jDialog, file);

        jDialog.setVisible(true);
    }


    public void wrapImgInfo(JDialog jDialog, File file) {
        JScrollPane comp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel view = new JPanel();
        view.setLayout(new GridLayout(0, 2, 5, 5));

        Metadata metadata = DKFileUtil.getMetadataOfFile(file);
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                JLabel keyLabel = new JLabel(tag.getTagName() + ":", JLabel.RIGHT);
                keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));
                keyLabel.setToolTipText(tag.getTagName());
                view.add(keyLabel);

                JLabel valLabel = new JLabel(tag.getDescription(), JLabel.LEFT);
                valLabel.setToolTipText(tag.getDescription());
                view.add(valLabel);
            }
        }
        comp.setViewportView(view);
        jDialog.add(comp);
    }
}

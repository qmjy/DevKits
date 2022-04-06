package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.model.FilesTableModel;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Optional;

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
        Optional<File> fileAt = filesModel.getFileAt(selectedRow);
        if(fileAt.isPresent()){
            File file = fileAt.get();
            JDialog jDialog = new JDialog();
            jDialog.setTitle(file.getName());
            int height = 300;
            Rectangle center = DKSysUIUtil.getCenter((int) (height / DKSysUIUtil.GOLDEN_RATIO), height);
            jDialog.setBounds(center);
            wrapImgInfo(jDialog, file);

            jDialog.setVisible(true);
        }
    }


    public void wrapImgInfo(JDialog jDialog, File file) {
        JScrollPane comp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        comp.setBorder(null);

        FormLayout layout = new FormLayout(
                "right:max(40dlu;p), 4dlu, 150dlu", ""); // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        Metadata metadata = DKFileUtil.getMetadataOfFile(file);
        for (Directory directory : metadata.getDirectories()) {
            builder.appendSeparator(directory.getName());

            for (Tag tag : directory.getTags()) {
                JLabel keyLabel = new JLabel(tag.getTagName() + ":", JLabel.RIGHT);
                keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));

                final JLabel valLabel = new JLabel(tag.getDescription(), JLabel.LEFT);
                valLabel.setToolTipText(DKSysUIUtil.getLocale("COMMON_TOOLTIPS_CLICK_4_COPY"));
                valLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            DKSysUIUtil.setSystemClipboard(valLabel.getText());
                        }
                    }
                });

                builder.append(keyLabel, valLabel);
                builder.nextLine();
            }
        }
        comp.setViewportView(builder.getPanel());
        jDialog.add(comp);
    }
}

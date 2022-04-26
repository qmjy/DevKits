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
import java.util.*;
import java.util.List;

public class ImgTableMenuItemActionListener implements ActionListener {
    private final JTable table;
    private final FilesTableModel filesModel;

    private File file;
    private Map<String, Map<String, String>> fileMetaMapdata;
    private List<String> fileMetaListData;

    public ImgTableMenuItemActionListener(JTable table) {
        this.table = table;
        this.filesModel = (FilesTableModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        Optional<File> fileAt = filesModel.getFileAt(selectedRow);
        if (fileAt.isPresent()) {
            this.file = fileAt.get();
            this.fileMetaMapdata = new LinkedHashMap<>();
            this.fileMetaListData = new ArrayList<>();
            showDialog();
        }
    }

    private void showDialog() {
        JDialog jDialog = new JDialog();
        jDialog.setTitle(file.getName());
        int height = 350;
        Rectangle center = DKSysUIUtil.getCenter((int) (height / DKSysUIUtil.GOLDEN_RATIO), height);
        jDialog.setBounds(center);

        JPanel jPanel = new JPanel(new BorderLayout());

        jPanel.add(wrapImgInfo(file), BorderLayout.CENTER);
        jPanel.add(createButtons(), BorderLayout.SOUTH);

        jDialog.setContentPane(jPanel);
        jDialog.setVisible(true);
    }

    private JPanel createButtons() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        JButton copyAllBtn = new JButton(DKSysUIUtil.getLocale("COMMON_BTNS_COPY_ALL"));
        copyAllBtn.addActionListener(e -> {
            DKSysUIUtil.setSystemClipboard(wrapContent());
        });
        jPanel.add(copyAllBtn);
        JButton saveAsBtn = new JButton(DKSysUIUtil.getLocale("COMMON_BTNS_SAVE_AS"));
        saveAsBtn.addActionListener(e -> saveAs2File(saveAsBtn));
        jPanel.add(saveAsBtn);
        return jPanel;
    }

    private void saveAs2File(JButton parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(DKSysUtil.getDesktopPath() + File.separator + file.getName() + ".ini"));
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File selectFile = fileChooser.getSelectedFile();
            if (selectFile == null) {
                return;
            }
            if (!selectFile.getName().toLowerCase().endsWith(".ini")) {
                selectFile = new File(selectFile.getParentFile(), file.getName() + ".ini");
            }
            DKFileUtil.writeFile(selectFile, fileMetaListData);
            DKFileUtil.openFile(selectFile);
        }
    }

    private String wrapContent() {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<String, Map<String, String>>> iterator = fileMetaMapdata.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map<String, String>> next = iterator.next();
            String directory = next.getKey();
            sb.append("[").append(directory).append("]").append(System.getProperty("line.separator"));


            Map<String, String> details = next.getValue();
            Iterator<Map.Entry<String, String>> iterator1 = details.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<String, String> next1 = iterator1.next();
                String key = next1.getKey();
                String value = next1.getValue();
                sb.append(key).append("=").append(value).append(System.getProperty("line.separator"));
            }
        }
        return sb.toString();
    }


    public Container wrapImgInfo(File file) {
        JScrollPane comp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        comp.setBorder(null);

        FormLayout layout = new FormLayout(
                "right:max(40dlu;p), 4dlu, 200dlu", ""); // add rows dynamically
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setDefaultDialogBorder();

        Metadata metadata = DKFileUtil.getMetadataOfFile(file);
        for (Directory directory : metadata.getDirectories()) {
            builder.appendSeparator(directory.getName());
            fileMetaListData.add("[" + directory.getName() + "]");

            Map<String, String> dataMap = new LinkedHashMap<>();
            for (Tag tag : directory.getTags()) {
                JLabel keyLabel = new JLabel(tag.getTagName() + ":", JLabel.RIGHT);
//                keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD));

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

                dataMap.put(tag.getTagName(), tag.getDescription());
                builder.append(keyLabel, valLabel);
                builder.nextLine();
                fileMetaListData.add(tag.getTagName() + "=" + tag.getDescription());
            }
            fileMetaListData.add(""); //增加空行
            fileMetaMapdata.put(directory.getName(), dataMap);
        }
        comp.setViewportView(builder.getPanel());
        return comp;
    }
}

package cn.devkits.client.tray.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import javax.swing.JComboBox;
import cn.devkits.client.DKConstant;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;

public class DKFilenameFilter implements FilenameFilter {

    private JComboBox<String> fileTypeComboBox;

    public DKFilenameFilter(LargeDuplicateFilesFrame frame) {
        this.fileTypeComboBox = LargeDuplicateFilesFrame.getFileTypeComboBox();
    }

    @Override
    public boolean accept(File dir, String name) {

        File file = new File(dir, name);
        if (file.isDirectory()) {
            return true;
        }

        if (dir.getAbsolutePath().contains(".git") || dir.getAbsolutePath().contains(".svn")) {
            return false;
        }

        String fileType = (String) fileTypeComboBox.getSelectedItem();

        if ("All".equals(fileType)) {
            return true;
        } else {
            if (name.indexOf(".") > 0) {
                String suffix = name.substring(name.lastIndexOf(".")).toLowerCase(Locale.getDefault());
                if ("Document".equals(fileType)) {
                    return DKConstant.FILE_TYPE_DOC.contains(suffix);
                } else if ("Image".equals(fileType)) {
                    return DKConstant.FILE_TYPE_IMG.contains(suffix);
                } else if ("Audio".equals(fileType)) {
                    return DKConstant.FILE_TYPE_AUDIO.contains(suffix);
                } else if ("Video".equals(fileType)) {
                    return DKConstant.FILE_TYPE_VEDIO.contains(suffix);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}

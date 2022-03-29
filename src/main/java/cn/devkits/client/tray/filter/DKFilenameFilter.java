/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import javax.swing.JComboBox;
import cn.devkits.client.DKConstants;
import cn.devkits.client.tray.frame.DuplicateFilesFrame;
import cn.devkits.client.util.DKSysUIUtil;

public class DKFilenameFilter implements FilenameFilter {

    private JComboBox<String> fileTypeComboBox;

    public DKFilenameFilter(DuplicateFilesFrame frame) {
        this.fileTypeComboBox = frame.getFileTypeComboBox();
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
        if (DKSysUIUtil.getLocale("DUP_INPUT_FILE_TYPE_ALL").equals(fileType)) {
            return true;
        } else {
            if (name.indexOf(".") > 0) {
                String suffix = name.substring(name.lastIndexOf(".")).toLowerCase(Locale.getDefault());
                if (DKSysUIUtil.getLocale("DUP_INPUT_FILE_TYPE_DOCUMENT").equals(fileType)) {
                    return DKConstants.FILE_TYPE_DOC.contains(suffix);
                } else if (DKSysUIUtil.getLocale("DUP_INPUT_FILE_TYPE_IMAGE").equals(fileType)) {
                    return DKConstants.FILE_TYPE_IMG.contains(suffix);
                } else if (DKSysUIUtil.getLocale("DUP_INPUT_FILE_TYPE_AUDIO").equals(fileType)) {
                    return DKConstants.FILE_TYPE_AUDIO.contains(suffix);
                } else if (DKSysUIUtil.getLocale("DUP_INPUT_FILE_TYPE_VIDEO").equals(fileType)) {
                    return DKConstants.FILE_TYPE_VEDIO.contains(suffix);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}

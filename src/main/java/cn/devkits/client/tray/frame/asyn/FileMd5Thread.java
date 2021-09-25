/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame.asyn;

import cn.devkits.client.tray.frame.DuplicateFilesFrame;
import cn.devkits.client.util.DKFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 文件MD5 计算线程
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:33:04
 */
class FileMd5Thread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMd5Thread.class);
    //md5：files
    private final DuplicateFilesFrame frame;

    private File file;

    public FileMd5Thread(DuplicateFilesFrame frame, File file) {
        super(file.getName());
        this.frame = frame;
        this.file = file;
    }

    @Override
    public void run() {
        String fileMd5 = DKFileUtil.getFileMd5(file).get();
        frame.updateTreeData(fileMd5, file.getAbsolutePath());
    }
}

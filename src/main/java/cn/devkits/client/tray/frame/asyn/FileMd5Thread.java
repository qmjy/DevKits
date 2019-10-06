package cn.devkits.client.tray.frame.asyn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;

/**
 * 文件MD5 计算线程
 * 
 * @author shaofeng liu
 * @version 1.0.0
 * @datetime 2019年9月26日 下午9:33:04
 */
class FileMd5Thread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMd5Thread.class);

    private File file;
    private LargeDuplicateFilesFrame frame;

    public FileMd5Thread(LargeDuplicateFilesFrame frame, File file) {
        super(file.getName());
        this.frame = frame;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            String md5Hex = DigestUtils.md5Hex(new FileInputStream(file));
            frame.updateTreeData(md5Hex, file);
        } catch (FileNotFoundException e) {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("计算文件MD5失败：" + e.getMessage());
        }
    }
}

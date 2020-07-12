package cn.devkits.client.tray.frame.asyn;

import cn.devkits.client.tray.frame.LargeDuplicateFilesFrame;
import cn.devkits.client.util.DKFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private final LargeDuplicateFilesFrame frame;

    private File file;

    public FileMd5Thread(LargeDuplicateFilesFrame frame, File file) {
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

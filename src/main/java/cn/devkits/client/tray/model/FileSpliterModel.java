package cn.devkits.client.tray.model;

import cn.devkits.client.util.DKFileUtil;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * 文件分割模型
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月18日 下午12:04:23
 */
public class FileSpliterModel {
    // 待分割的文件
    private File file;
    private File ouputFolder;
    // 分割过程中需要显示的控制台消息
    private Queue<String> msgs = new LinkedBlockingQueue<String>();
    // 是否分割结束
    private boolean finished;

    public FileSpliterModel(String filePath) {
        this.file = new File(filePath);
        this.ouputFolder = new File(file.getPath() + File.separator + file.getName() + "_Result");
        handlerResultFolder();
    }


    private void handlerResultFolder() {
        if (ouputFolder.exists()) {
            DKFileUtil.clearFolder(ouputFolder);
        } else {
            ouputFolder.mkdirs();
        }
    }


    /**
     * 获取结果输出文件夹
     * @return 结果输出文件夹
     */
    public File getOutputFolder() {
        return ouputFolder;
    }

    /**
     * 添加消息
     * @param msg 待显示到控制台的消息
     */
    public void addMsg(String msg) {
        msgs.add(msg);
    }

    /**
     * 获取控制台消息
     */
    public String pollMsg() {
        return msgs.poll();
    }

    public boolean isMsgEmpty() {
        return msgs.isEmpty();
    }

    public File getFile() {
        return file;
    }

    public boolean isFinished() {
        return finished;
    }

    public void updateStatus(boolean b) {
        this.finished = b;
    }

}

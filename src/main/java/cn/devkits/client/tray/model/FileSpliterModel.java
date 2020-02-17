package cn.devkits.client.tray.model;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileSpliterModel {
    // 待分割的文件
    private File file;
    // 分割过程中需要显示的控制台消息
    private Queue<String> msgs = new LinkedBlockingQueue<String>();
    // 是否分割结束
    private boolean finished;

    public FileSpliterModel(String filePath) {
        this.file = new File(filePath);
        // TODO 结果文件生成策略
    }


    /**
     * 获取结果输出文件夹
     * @return 结果输出文件夹
     */
    public File getOutputFolder() {
        // TODO 结果输出文件夹
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
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

package cn.devkits.client.tray.frame;

import java.awt.Component;

/**
 * 对话框需要具备浏览文件功能
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月11日 下午8:51:09
 */
public interface DKFrameChosenable {
    /**
     * update
     * @param filePath the browse file or path
     */
    void updateSelectFilePath(String filePath);

    /**
     * call back implements
     */
    void callback();

    /**
     * implement component instance
     * @return Component instance
     */
    Component getObj();
}

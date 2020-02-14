package cn.devkits.client.tray.pattern;

import java.io.File;

/**
 * 
 * 文本文件分割策略器
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月13日 下午9:45:43
 */
public abstract class TextFileSpliterStrategy implements FileSpliterStrategy {
    /**
     * 判断文件是否是文本文件
     * @param f 待判定的文件
     * @return 是否是文本文件
     */
    protected boolean isTextFile(File f) {
        // TODO 待实现
        return true;
    }


    /**
     * 按照固定行数分割文本文件
     * @param line 指定的文件行数
     */
    abstract void segmentSplitByLines(int line);

}

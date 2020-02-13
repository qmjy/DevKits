package cn.devkits.client.tray.pattern;

/**
 * 
 * Excel文件分割策略器
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月13日 下午9:48:48
 */
public abstract class ExcelFileSpliterStrategy implements FileSpliterStrategy {

    /**
     * 按照sheet也来进行分割，一个sheet也生成一个文件
     */
    abstract void segmentSplitBySheet();

}

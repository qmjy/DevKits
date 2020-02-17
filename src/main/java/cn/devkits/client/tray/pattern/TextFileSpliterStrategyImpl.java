package cn.devkits.client.tray.pattern;

import cn.devkits.client.tray.model.FileSpliterModel;
import cn.devkits.client.util.DKDateTimeUtil;
import cn.devkits.client.util.DKFileUtil;
import javax.swing.JRadioButton;

/**
 * 文本文件切割策略实现
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月14日 下午12:08:29
 */
public class TextFileSpliterStrategyImpl extends TextFileSpliterStrategy implements Runnable {

    private FileSpliterModel splitModel;
    private String[] strategyNames;
    private JRadioButton current;
    private String param;

    public TextFileSpliterStrategyImpl(String[] strings, JRadioButton current, String param) {
        this.strategyNames = strings;
        this.current = current;
        this.param = param;
    }

    @Override
    public void execute(FileSpliterModel splitModel) {
        this.splitModel = splitModel;
        new Thread(this, "text-file-spliter-thread").start();
    }

    @Override
    public void segmentSplit(int n) {
        splitModel.addMsg("Start to split file with segment: " + n + System.lineSeparator());
        long length = splitModel.getFile().length();
        segmentSplitBySize(length / n);
    }

    @Override
    public void segmentSplitByFixedSize(float size) {
        splitModel.addMsg("Start to split file with fixed size: " + size + " KB" + System.lineSeparator());
    }

    @Override
    void segmentSplitByLines(int line) {
        splitModel.addMsg("Start to split file with fixed lines: " + line + System.lineSeparator());
        splitModel.updateStatus(true);
    }

    private void segmentSplitBySize(float size) {
        splitModel.addMsg("Orignal file size: " + DKFileUtil.formatBytes(splitModel.getFile().length()) + System.lineSeparator());
        splitModel.addMsg("Orignal file path: " + splitModel.getFile().getAbsolutePath() + System.lineSeparator());
        splitModel.addMsg("Start Time: " + DKDateTimeUtil.currentTimeStrWithPattern(DKDateTimeUtil.DATE_TIME_PATTERN_DEFAULT)+ System.lineSeparator());
        splitModel.updateStatus(true);
    }

    @Override
    public void run() {
        if (strategyNames[0].equals(current.getText())) {
            segmentSplit(Integer.parseInt(param));
        } else if (strategyNames[1].equals(current.getText())) {
            segmentSplitBySize(Float.parseFloat(param));
        } else {
            segmentSplitByLines(Integer.parseInt(param));
        }
    }
}

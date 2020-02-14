package cn.devkits.client.tray.pattern;

import cn.devkits.client.tray.model.FileSpliterModel;
import java.io.File;
import javax.swing.JRadioButton;

/**
 * 文本文件切割策略实现
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月14日 下午12:08:29
 */
public class TextFileSpliterStrategyImpl extends TextFileSpliterStrategy {
    
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

        if (strategyNames[0].equals(current.getText())) {
            segmentSplit(Integer.parseInt(param));
        } else if (strategyNames[1].equals(current.getText())) {
            segmentSplitBySize(Float.parseFloat(param));
        } else {
            segmentSplitByLines(Integer.parseInt(param));
        }
    }

    @Override
    public void segmentSplit(int n) {
        long length = splitModel.getFile().length();
        segmentSplitBySize(length / n);
    }

    @Override
    public void segmentSplitBySize(float size) {

    }


    @Override
    void segmentSplitByLines(int line) {
        // TODO Auto-generated method stub

    }
}

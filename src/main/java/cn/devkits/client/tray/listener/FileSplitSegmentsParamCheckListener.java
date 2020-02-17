package cn.devkits.client.tray.listener;

import cn.devkits.client.tray.frame.FileSpliterFrame;
import cn.devkits.client.util.DKStringUtil;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class FileSplitSegmentsParamCheckListener extends KeyAdapter {

    private FileSpliterFrame frame;
    private Class<?> inputTextType;


    public FileSplitSegmentsParamCheckListener(FileSpliterFrame fileSpliterFrame, Class<?> clazz) {
        this.frame = fileSpliterFrame;
        this.inputTextType = clazz;
    }


    @Override
    public void keyReleased(KeyEvent e) {
        JTextField sourceInput = (JTextField) e.getSource();
        String userInputText = sourceInput.getText();
        if (inputTextType.equals(Integer.class)) {
            if (userInputText.trim().length() == 0 || DKStringUtil.isPositiveInt(userInputText)) {
                sourceInput.setBorder(null);
                frame.updateApplyBtnState(true);
            } else {
                sourceInput.setBorder(BorderFactory.createLineBorder(Color.RED));
                frame.updateApplyBtnState(false);
            }
        } else if (inputTextType.equals(Float.class)) {
            if (userInputText.trim().length() == 0 || DKStringUtil.isPositiveInt(userInputText) || DKStringUtil.isPositiveFloat(userInputText)) {
                sourceInput.setBorder(null);
                frame.updateApplyBtnState(true);
            } else {
                sourceInput.setBorder(BorderFactory.createLineBorder(Color.RED));
                frame.updateApplyBtnState(false);
            }
        }
    }

}

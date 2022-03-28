/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.frame.FileSpliterFrame;
import cn.devkits.client.util.DKStringUtil;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 * 
 * 错误校验监听器
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月17日 下午10:51:29
 */
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
                sourceInput.setBackground(Color.WHITE);
                frame.updateApplyBtnState(true);
            } else {
                sourceInput.setBackground(Color.RED);
                frame.updateApplyBtnState(false);
            }
        } else if (inputTextType.equals(Float.class)) {
            if (userInputText.trim().length() == 0 || DKStringUtil.isPositiveInt(userInputText) || DKStringUtil.isPositiveFloat(userInputText)) {
                sourceInput.setBackground(Color.WHITE);
                frame.updateApplyBtnState(true);
            } else {
                sourceInput.setBackground(Color.RED);
                frame.updateApplyBtnState(false);
            }
        }
    }
}

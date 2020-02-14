package cn.devkits.client.tray.listener;

import cn.devkits.client.tray.frame.FileSpliterFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class FileSplitSegmentsParamCheckListener extends KeyAdapter {

    private FileSpliterFrame frame;
    private Class<?> inputTextType;


    public FileSplitSegmentsParamCheckListener(FileSpliterFrame fileSpliterFrame, Class<?> clazz) {
        this.frame=fileSpliterFrame;
        this.inputTextType = clazz;
    }


    @Override
    public void keyReleased(KeyEvent e) {
        Object source = e.getSource();
        if (source instanceof JTextField) {
            JTextField field = (JTextField) source;
        }
    }

}

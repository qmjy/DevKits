package cn.devkits.client.tray.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import cn.devkits.client.tray.window.ScreenCaptureWindow;

/**
 * 
 * 截屏鼠标事件
 * 
 * @author fengshao
 * @version 1.0.0
 * @datetime 2019年9月5日 下午10:35:36
 */
public class CaptureMouseListener extends MouseAdapter {

    private ScreenCaptureWindow window;

    public CaptureMouseListener(ScreenCaptureWindow screenCaptureWindow) {
        this.window = screenCaptureWindow;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        window.setOrgx(e.getX());
        window.setOrgy(e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

}

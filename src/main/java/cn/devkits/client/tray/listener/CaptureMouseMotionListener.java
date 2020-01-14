package cn.devkits.client.tray.listener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import cn.devkits.client.tray.window.ScreenCaptureWindow;

public class CaptureMouseMotionListener extends MouseMotionAdapter {

	private ScreenCaptureWindow window;

	public CaptureMouseMotionListener(ScreenCaptureWindow screenCaptureWindow) {
		this.window = screenCaptureWindow;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// 鼠标拖动时，记录坐标并重绘窗口
		window.setEndx(e.getX());
		window.setEndy(e.getY());

		int orgx = window.getOrgx();
		int orgy = window.getOrgy();

		int endx = window.getEndx();
		int endy = window.getEndy();

		// 临时图像，用于缓冲屏幕区域放置屏幕闪烁
		Image tempImage2 = window.createImage(window.getWidth(), window.getHeight());
		Graphics g = tempImage2.getGraphics();
		g.drawImage(window.getTempImage(), 0, 0, null);
		int x = Math.min(orgx, endx);
		int y = Math.min(orgy, endy);
		int width = Math.abs(endx - orgx) + 1;
		int height = Math.abs(endy - orgy) + 1;
		// 加上1防止width或height0
		g.setColor(Color.BLUE);
		g.drawRect(x - 1, y - 1, width + 1, height + 1);
		// 减1加1都了防止图片矩形框覆盖掉
		Image saveImage = window.getImage().getSubimage(x, y, width, height);
		g.drawImage(saveImage, x, y, null);

		window.getGraphics().drawImage(tempImage2, 0, 0, window);
	}

}
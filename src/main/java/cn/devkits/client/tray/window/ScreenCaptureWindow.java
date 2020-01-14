package cn.devkits.client.tray.window;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JFrame;
import javax.swing.JWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.devkits.client.tray.listener.CaptureEscKeyListener;
import cn.devkits.client.tray.listener.CaptureMouseListener;
import cn.devkits.client.tray.listener.CaptureMouseMotionListener;

public class ScreenCaptureWindow extends JWindow {

	private static final long serialVersionUID = 5062833920649545099L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCaptureWindow.class);

	private int orgx, orgy, endx, endy;

	private BufferedImage image = null;
	private BufferedImage tempImage = null;

	public ScreenCaptureWindow() {
		// 置顶
//		setAlwaysOnTop(true);

		initLocation();
		initListener();
	}

	private void initLocation() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		LOGGER.info("Capture width is {}, height is {}", d.width, d.height);
		this.setBounds(0, 0, d.width, d.height);

		try {
			Robot robot = new Robot();
			image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
		} catch (AWTException e) {
			LOGGER.error("Create Screen Capture Failed!");
		}
	}

	private void initListener() {
		this.addKeyListener(new CaptureEscKeyListener(this));
		this.addMouseListener(new CaptureMouseListener(this));
		this.addMouseMotionListener(new CaptureMouseMotionListener(this));
	}

	@Override
	public void paint(Graphics g) {
		RescaleOp ro = new RescaleOp(0.8f, 0, null);
		tempImage = ro.filter(image, null);
		g.drawImage(tempImage, 0, 0, this);
	}

	public int getOrgx() {
		return orgx;
	}

	public void setOrgx(int orgx) {
		this.orgx = orgx;
	}

	public int getOrgy() {
		return orgy;
	}

	public void setOrgy(int orgy) {
		this.orgy = orgy;
	}

	public int getEndx() {
		return endx;
	}

	public void setEndx(int endx) {
		this.endx = endx;
	}

	public int getEndy() {
		return endy;
	}

	public void setEndy(int endy) {
		this.endy = endy;
	}

	public BufferedImage getTempImage() {
		return tempImage;
	}

	public BufferedImage getImage() {
		return image;
	}

}

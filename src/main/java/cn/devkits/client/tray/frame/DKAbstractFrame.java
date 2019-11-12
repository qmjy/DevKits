package cn.devkits.client.tray.frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JRootPane;

public abstract class DKAbstractFrame extends JFrame implements DKFrameable {
    private static final long serialVersionUID = 6346125541327870409L;

    private static final int DEFAULT_LOAD_FACTOR = 1;

    protected int currentWidth = 0;
    protected int currentHeight = 0;

    protected DKAbstractFrame() {

    }

    /**
     * constructor
     * 
     * @param title window title
     */
    protected DKAbstractFrame(String title) {
        this(title, DEFAULT_LOAD_FACTOR);

        initUI(getRootPane());
    }

    /**
     * constructor
     * 
     * @param title window title
     * @param loadFactor window width and height factor
     */
    protected DKAbstractFrame(String title, float loadFactor) {
        this(title, loadFactor, loadFactor);
    }

    /**
     * frame constructor
     * 
     * @param title frame title
     * @param widthLoadFactor width set factor
     * @param heightLoadFactor height set factor
     */
    protected DKAbstractFrame(String title, float widthLoadFactor, float heightLoadFactor) {
        super(title);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        currentWidth = WINDOW_SIZE_WIDTH;
        currentHeight = WINDOW_SIZE_HEIGHT;
        if (widthLoadFactor > 0 && heightLoadFactor > 0) {

            if (currentWidth * widthLoadFactor > screenSize.getWidth()) {
                currentWidth = (int) screenSize.getWidth();
            } else {
                currentWidth *= widthLoadFactor;
            }

            if (currentHeight * heightLoadFactor > screenSize.getHeight()) {
                currentHeight = (int) screenSize.getHeight();
            } else {
                currentHeight *= heightLoadFactor;
            }
        }

        this.setBounds((int) ((screenSize.width - currentWidth) / 2), (int) ((screenSize.height - currentHeight) / 2), (int) currentWidth, (int) currentHeight);
    }

    /**
     * 创建UI
     * 
     * @param jRootPane Root Pane
     */
    protected abstract void initUI(JRootPane jRootPane);

    /**
     * 需要初始化的监听器
     */
    protected abstract void initListener();
}

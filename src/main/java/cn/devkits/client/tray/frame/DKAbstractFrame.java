package cn.devkits.client.tray.frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public abstract class DKAbstractFrame extends JFrame implements DKFrameable {
    private static final long serialVersionUID = 6346125541327870409L;

    protected DKAbstractFrame() {

    }

    protected DKAbstractFrame(String title) {
        super(title);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setBounds((screenSize.width - WINDOW_SIZE_WIDTH) / 2, (screenSize.height - WINDOW_SIZE_HEIGHT) / 2, WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT);
    }
}

package cn.devkits.client.tray.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;

public class FileExplorersFrame extends DKAbstractFrame {

    /** serialVersionUID */
    private static final long serialVersionUID = 3555313857984383501L;

    public FileExplorersFrame() {
        super("File Explorer", 1.2f);

        initUI(getRootPane());
        initListener();
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JSplitPane top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        top.setLeftComponent(getDirs());
        top.setRightComponent(getDirs());
        top.setResizeWeight(0.5);

        JSplitPane bottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottom.setLeftComponent(getDirs());
        bottom.setRightComponent(getDirs());
        bottom.setResizeWeight(0.5);

        JSplitPane comp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        comp.setResizeWeight(0.5);

        jRootPane.add(comp, BorderLayout.CENTER);
    }

    private Component getDirs() {
        return new FileExplorerPanel();
    }

    @Override
    protected void initListener() {

    }

}

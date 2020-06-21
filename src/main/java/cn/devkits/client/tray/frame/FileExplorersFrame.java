package cn.devkits.client.tray.frame;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;

public class FileExplorersFrame extends DKAbstractFrame {

    /** serialVersionUID */
    private static final long serialVersionUID = 3555313857984383501L;

    public FileExplorersFrame() {
        super("File Explorer", 1.2f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContianer) {
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

        rootContianer.add(comp);
    }

    private Component getDirs() {
        return new FileExplorerPanel();
    }

    @Override
    protected void initListener() {

    }

}

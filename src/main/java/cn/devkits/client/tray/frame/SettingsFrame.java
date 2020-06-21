package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSystemUIUtil;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends DKAbstractFrame {

    public SettingsFrame(){
        super(DKSystemUIUtil.getLocaleString("SETTINGS_TITLE"), 0.7f, 0.6f);

        initUI(getRootPane().getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        jSplitPane.setLeftComponent(new JLabel("sdfg"));
        jSplitPane.setRightComponent(new JLabel("sdfg"));

        rootContainer.add(jSplitPane);
//        JXCollapsiblePane jxCollapsiblePane = new JXCollapsiblePane(JXCollapsiblePane.Direction.RIGHT);
//        jxCollapsiblePane.add(new JLabel("sdfg"));
//        jxCollapsiblePane.add(new JLabel("sdfg"));
//        jxCollapsiblePane.add(new JLabel("sdfg"));
//        jxCollapsiblePane.add(new JLabel("sdfg"));
//        jxCollapsiblePane.add(new JLabel("sdfg"));
//        jRootPane.add(jxCollapsiblePane);
    }

    @Override
    protected void initListener() {

    }
}

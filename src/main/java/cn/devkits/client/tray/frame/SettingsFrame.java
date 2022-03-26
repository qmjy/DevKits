/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.action.*;
import cn.devkits.client.util.DKSysUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 系统设置对话框
 */
public class SettingsFrame extends DKAbstractFrame {

    private JSplitPane jSplitPane;
    private JPanel cardLayoutRootPanel;
    private CardLayout rightCardLayout;

    public SettingsFrame() {
        super(DKSysUIUtil.getLocaleString("SETTINGS_TITLE"), 0.8f);

        initUI(getRootPane().getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        this.cardLayoutRootPanel = createRightPane();

        jSplitPane.setLeftComponent(createLeftPane());
        jSplitPane.setRightComponent(cardLayoutRootPanel);

        rootContainer.add(jSplitPane);
    }

    private JPanel createRightPane() {
        JPanel jPanel = new JPanel();

        this.rightCardLayout = new CardLayout();
        jPanel.setLayout(rightCardLayout);

        return jPanel;
    }

    private Component createLeftPane() {
        // a container to put all JXTaskPane together
        JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

        // create a first taskPane with common actions
        JXTaskPane commonsPane = new JXTaskPane();
        commonsPane.setTitle(DKSysUIUtil.getLocaleString("SETTINGS_GROUP_COMMON_SETTINGS"));
        commonsPane.setSpecial(true);
        commonsPane.setIcon(IconFontSwing.buildIcon(FontAwesome.TACHOMETER, 16, new Color(50, 50, 50)));
        commonsPane.setFocusable(false);

        commonsPane.add(new ThemeSettingsAction(this, cardLayoutRootPanel)).setFocusable(false);

        taskPaneContainer.add(commonsPane);

        // create another taskPane, it will show sysPane of the selected file
        JXTaskPane sysPane = new JXTaskPane();
        sysPane.setTitle(DKSysUIUtil.getLocaleString("SETTINGS_GROUP_SYS_SETTINGS"));
        sysPane.setIcon(IconFontSwing.buildIcon(FontAwesome.COGS, 16, new Color(50, 50, 50)));
        sysPane.setFocusable(false);

        // add standard components to the sysPane taskPane
//        sysPane.add(new LanguageSettingsAction(this, cardLayoutRootPanel)).setFocusable(false);
        sysPane.add(new EmailSettingsAction(this, cardLayoutRootPanel)).setFocusable(false);
        sysPane.add(new FileServerSettingsAction(this, cardLayoutRootPanel)).setFocusable(false);
        sysPane.add(new SystemRegisterAction(this, cardLayoutRootPanel)).setFocusable(false);
        sysPane.add(new HotKeySettingsAction(this, cardLayoutRootPanel)).setFocusable(false);
        sysPane.add(new OtherSettingsAction(this, cardLayoutRootPanel)).setFocusable(false);

        taskPaneContainer.add(sysPane);

        return new JScrollPane(taskPaneContainer);
    }

    @Override
    protected void initListener() {
        /**
         * 窗口打开后设置默认分割面板比例
         */
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                jSplitPane.setDividerLocation(.26);
            }
        });
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                jSplitPane.setDividerLocation(.26);
            }
        });
    }
}

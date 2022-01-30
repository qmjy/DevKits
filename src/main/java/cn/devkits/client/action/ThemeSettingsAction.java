/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cn.devkits.client.util.DKSystemUIUtil;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * 主题设置面板
 */
public class ThemeSettingsAction extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeSettingsAction.class);
    private String currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();


    public ThemeSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);
        applyBtn.setVisible(false);
        closeBtn.setVisible(false);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.THEMEISLE, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:max(50dlu;p), 4dlu, 40dlu:grow, 4dlu, 150dlu:grow, 4dlu, 35dlu, 4dlu",
                "p, 4dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_THEME_SETTINGS"), cc.xyw(1, 1, 8));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_THEME_LAB"), cc.xy(1, 5));
        JComboBox<String> component = new JComboBox<>(listLookAndFeel());
        component.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String itemName = (String) e.getItem();
                DKSystemUIUtil.setLookAndFeel(itemName);
            }
        });
        builder.add(component, cc.xyw(3, 5, 6));

        return builder.getPanel();
    }

    private String[] listLookAndFeel() {
        UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        String[] lookAndFeels = new String[lookAndFeelInfos.length];
        for (int i = 0; i < lookAndFeelInfos.length; i++) {
            lookAndFeels[i] = lookAndFeelInfos[i].getClassName();
        }
        return lookAndFeels;
    }
}

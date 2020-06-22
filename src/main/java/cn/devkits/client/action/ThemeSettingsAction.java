package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;

public class ThemeSettingsAction extends BaseAction {
    public ThemeSettingsAction(JPanel rightPane) {
        super(rightPane);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.THEMEISLE, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_COMMON_SETTINGS_THEME_DESC"));

        registerPane();
    }

    @Override
    protected void registerPane() {
        JPanel comp = new JPanel();
        comp.add(new JLabel((String) getValue(Action.SHORT_DESCRIPTION)));
        contentPane.add((String) getValue(Action.NAME), comp);
    }
}

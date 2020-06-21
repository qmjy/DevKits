package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 设置面板设置系统默认语言<br>
 * https://www.cnblogs.com/jiangzhaowei/p/7448735.html
 */
public class LanguageSettingsAction extends AbstractAction {

    private JPanel contentPane;

    public LanguageSettingsAction(JPanel rightPane) {
        this.contentPane = rightPane;

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_GLOBAL_SETTINGS_LANG"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.GLOBE, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_GLOBAL_SETTINGS_LANG_DESC"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JLabel(this.toString()), BorderLayout.CENTER);
        contentPane.updateUI();
        //TODO 布局管理器修改
    }
}

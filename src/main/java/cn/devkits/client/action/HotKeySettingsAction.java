/*
 * Copyright (c) 2019-2021 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cn.devkits.client.App;
import cn.devkits.client.service.EmailService;
import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/2/4
 */
public class HotKeySettingsAction extends BaseAction {
    /**
     * 创建新待办默认快捷键
     */
    private static final String NEW_TASK_HOT_KEY_DEFAULT = "Ctrl + T";
    /**
     * 截屏默认快捷键
     */
    private static final String SCREEN_SHOT_HOT_KEY_DEFAULT = "Ctrl + Alt + A";

    private JTextField newTaskShortKeyTextField = new JTextField(NEW_TASK_HOT_KEY_DEFAULT);
    private JTextField screenShotShortKeyTextField = new JTextField(SCREEN_SHOT_HOT_KEY_DEFAULT);
    private JButton newTaskShortKeyClearBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CLEAR"));
    private JButton screenShotShortKeyClearBtn = new JButton(DKSystemUIUtil.getLocaleString("COMMON_BTNS_CLEAR"));

    public HotKeySettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        EmailService service = (EmailService) App.getContext().getBean("emailServiceImpl");

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_HOTKEY"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.KEYBOARD_O, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_HOTKEY_DESC"));

        registerPane();
        registerListener();
    }

    private void registerListener() {
        newTaskShortKeyClearBtn.addActionListener(e -> {
            newTaskShortKeyTextField.setText("");
        });
        screenShotShortKeyClearBtn.addActionListener(e -> {
            screenShotShortKeyTextField.setText("");
        });
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:max(50dlu;p), 4dlu, 40dlu:grow, 4dlu, 150dlu:grow, 4dlu, 35dlu, 4dlu",
                "p, 4dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_HOTKEY_SETTINGS"), cc.xyw(1, 1, 8));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_HOTKEY_NEW_TASK_LAB"), cc.xy(1, 5));
        builder.add(newTaskShortKeyTextField, cc.xyw(3, 5, 4));
        builder.add(newTaskShortKeyClearBtn, cc.xy(7, 5));

        builder.addLabel(DKSystemUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_HOTKEY_SCREENSHOT"), cc.xy(1, 7));
        builder.add(screenShotShortKeyTextField, cc.xyw(3, 7, 4));
        builder.add(screenShotShortKeyClearBtn, cc.xy(7, 7));

        return builder.getPanel();
    }
}

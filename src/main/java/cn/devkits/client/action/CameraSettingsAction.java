package cn.devkits.client.action;

import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Frame;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/11/3
 */
public class CameraSettingsAction extends BaseAction {

    public CameraSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);


        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_CAMERA"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.CAMERA_RETRO, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_CAMERA_DESC"));

        registerPane();
    }
}

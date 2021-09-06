package cn.devkits.client.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
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
 * Local File Server
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/9/6
 */
public class FileServerSettingsAction  extends BaseAction{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerSettingsAction.class);

    public FileServerSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);



        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_FILE_SERVER"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.KEY, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_FILE_SERVER_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:pref, 6dlu, 50dlu:grow, 6dlu, 30dlu"); // 5 columns; add rows later

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);

        return builder.getPanel();
    }
}

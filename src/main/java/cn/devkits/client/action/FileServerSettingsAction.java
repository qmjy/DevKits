package cn.devkits.client.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
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
public class FileServerSettingsAction extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServerSettingsAction.class);

    public FileServerSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);


        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_FILE_SERVER"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.FILES_O, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_FILE_SERVER_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:max(50dlu;p),4dlu, 75dlu, 7dlu, right:p, 4dlu, 75dlu, 4dlu, 75dlu, 4dlu, 75dlu",
                "p, 4dlu, p, 3dlu, p, 3dlu, p, 7dlu, p, 2dlu, p, 3dlu, p, 3dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_FILE_SERVER_SEG"), cc.xyw(1, 1, 11));
        builder.addLabel("Identifier", cc.xy(1, 3));
        builder.add(new JTextField(), cc.xy(3, 3));
        builder.addLabel("PTI[kW]", cc.xy(1, 5));
        builder.add(new JTextField(), cc.xy(3, 5));
        builder.addLabel("Power[kW]", cc.xy(5, 5));
        builder.add(new JTextField(), cc.xy(7, 5));
        builder.addLabel("len[mm]", cc.xy(1, 7));
        builder.add(new JTextField(), cc.xy(3, 7));

        return builder.getPanel();
    }
}

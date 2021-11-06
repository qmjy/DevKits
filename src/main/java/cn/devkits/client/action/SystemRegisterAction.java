package cn.devkits.client.action;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cn.devkits.client.util.DKSystemUIUtil;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

/**
 * <p>
 *
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/11/6ASSEDF
 */
public class SystemRegisterAction extends BaseAction {
    private ButtonGroup enableCameraGroup;

    public SystemRegisterAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.DESKTOP, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:max(15dlu;p):grow, 4dlu, 80dlu:grow, 4dlu, right:max(15dlu;p):grow, 4dlu, 45dlu:grow, 4dlu, right:max(15dlu;p):grow, 4dlu, 50dlu:grow",
                "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 10dlu, p, 4dlu, d");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER_DRIVER"), cc.xyw(1, 1, 11));


        enableCameraGroup = new ButtonGroup();
        String enableTxt = DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER_DRIVER_ENABLE_TXT");
        JRadioButton enableCamera = new JRadioButton(enableTxt);
        enableCamera.setActionCommand(enableTxt);
        builder.add(enableCamera, cc.xy(1, 3));

        String disableTxt = DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER_DRIVER_DISABLE_TXT");
        JRadioButton disableCamera = new JRadioButton(disableTxt);
        disableCamera.setActionCommand(disableTxt);
        builder.add(disableCamera, cc.xy(3, 3));

        enableCameraGroup.add(enableCamera);
        enableCameraGroup.add(disableCamera);

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_CONTEXT_MENU"), cc.xyw(1, 7, 11));

        return builder.getPanel();
    }

    @Override
    protected void doApply(ActionEvent e) {
        ButtonModel selection = enableCameraGroup.getSelection();
        if (DKSystemUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_SYS_REGISTER_DRIVER_ENABLE_TXT").equals(selection.getActionCommand())) {
            enableCamera();
        } else {
            disableCamera();
        }
    }

    private void disableCamera() {

    }

    private void enableCamera() {

    }

    @Override
    protected void doClose(ActionEvent e) {
        super.doClose(e);
    }
}

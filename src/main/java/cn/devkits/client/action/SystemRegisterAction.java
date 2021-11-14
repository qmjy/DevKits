package cn.devkits.client.action;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import cn.devkits.client.util.WinRegisterUtil;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

    private JCheckBox enableCamera;
    private JCheckBox enableQrRecognize;

    public SystemRegisterAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_REGISTER"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.DESKTOP, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSystemUIUtil.getLocaleString("SETTINGS_SYS_REGISTER_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        FormLayout layout = new FormLayout(
                "right:max(15dlu;p):grow, 4dlu, 80dlu:grow, 4dlu, right:max(15dlu;p):grow, 4dlu, 45dlu:grow, 4dlu, right:max(15dlu;p):grow, 4dlu, 50dlu:grow",
                "p, 4dlu, p, 20dlu, p, 4dlu, p, 4dlu, p, 10dlu, p, 4dlu, d");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_REGISTER_DRIVER"), cc.xyw(1, 1, 11));

        String enableTxt = DKSystemUIUtil.getLocaleString("SETTINGS_SYS_REGISTER_DRIVER_ENABLE_TXT");
        enableCamera = new JCheckBox(enableTxt);
        enableCamera.setActionCommand(enableTxt);
        builder.add(enableCamera, cc.xyw(1, 3, 11));

        builder.addSeparator(DKSystemUIUtil.getLocaleString("SETTINGS_SYS_CONTEXT_MENU"), cc.xyw(1, 5, 11));

        String enableQrRecognizeTxt = DKSystemUIUtil.getLocaleString("SETTINGS_SYS_CONTEXT_MENU_QR_4_IMG");
        enableQrRecognize = new JCheckBox(enableQrRecognizeTxt);
        enableQrRecognize.setActionCommand(enableQrRecognizeTxt);
        builder.add(enableQrRecognize, cc.xyw(1, 7, 11));

        return builder.getPanel();
    }

    @Override
    protected void doApply(ActionEvent e) {
        if (enableCamera.isSelected()) {
            installCamera();
        } else {
            uninstallCamera();
        }

        if (enableQrRecognize.isSelected()) {
            installQrRecognize();
        } else {
            unInstallQrRecognize();
        }
    }

    private void installQrRecognize() {
        unInstallQrRecognize();
        if (DKSystemUtil.isWindows()) {
            WinRegisterUtil instance = WinRegisterUtil.getInstance();
            instance.regQrCodeSubCommand();
        }
    }

    private void unInstallQrRecognize() {
    }

    private void uninstallCamera() {

    }

    private void installCamera() {

    }

    @Override
    protected void doClose(ActionEvent e) {
        super.doClose(e);
    }
}

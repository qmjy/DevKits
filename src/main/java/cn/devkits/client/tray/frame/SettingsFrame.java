package cn.devkits.client.tray.frame;

import cn.devkits.client.action.LanguageSettingsAction;
import cn.devkits.client.util.DKSystemUIUtil;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;

public class SettingsFrame extends DKAbstractFrame {

    private JSplitPane jSplitPane;
    private JPanel rightPane;

    public SettingsFrame() {
        super(DKSystemUIUtil.getLocaleString("SETTINGS_TITLE"), 0.8f, 0.7f);

        initUI(getRootPane().getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        this.rightPane = createRightPane();

        jSplitPane.setLeftComponent(createLeftPane());
        jSplitPane.setRightComponent(rightPane);

        rootContainer.add(jSplitPane);
    }

    private JPanel createRightPane() {
        return new JPanel();
    }

    private Component createLeftPane() {
        // a container to put all JXTaskPane together
        JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

        // create a first taskPane with common actions
        JXTaskPane actionPane = new JXTaskPane();
        actionPane.setTitle("Files and Folders");
        actionPane.setSpecial(true);
        actionPane.setFocusable(false);

        // actions can be added, a hyperlink will be created
        Action renameSelectedFile = new LanguageSettingsAction(rightPane);
        actionPane.add(renameSelectedFile);
        actionPane.add(new LanguageSettingsAction(rightPane));

        // add this taskPane to the taskPaneContainer
        taskPaneContainer.add(actionPane);

        // create another taskPane, it will show globalTaskPane of the selected file
        JXTaskPane globalTaskPane = new JXTaskPane();
        globalTaskPane.setTitle(DKSystemUIUtil.getLocaleString("SETTINGS_GROUP_GLOBAL_SETTINGS"));
        globalTaskPane.setFocusable(false);

        // add standard components to the globalTaskPane taskPane
        globalTaskPane.add(new LanguageSettingsAction(rightPane));

        taskPaneContainer.add(globalTaskPane);

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
    }
}

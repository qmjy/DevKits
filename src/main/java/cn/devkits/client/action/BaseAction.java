package cn.devkits.client.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class BaseAction extends AbstractAction {
    protected final JPanel contentPane;

    public BaseAction(JPanel rightPane) {
        this.contentPane = rightPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout layout = (CardLayout) contentPane.getLayout();
        layout.show(contentPane, (String) getValue(Action.NAME));
    }

    /**
     * 注册设置页面Action内容展示面板
     */
    protected abstract void registerPane();
}

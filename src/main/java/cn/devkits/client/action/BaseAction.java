/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 设置面板基础设置Action<br>
 * https://www.cnblogs.com/jiangzhaowei/p/7448735.html
 */
public abstract class BaseAction extends AbstractAction {
    protected final JPanel cardLayoutRootPanel;
    private JPanel rightPanel = new JPanel();

    public BaseAction(JPanel cardLayoutRootPanel) {
        this.cardLayoutRootPanel = cardLayoutRootPanel;
        this.rightPanel.setLayout(new BorderLayout());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout layout = (CardLayout) cardLayoutRootPanel.getLayout();
        layout.show(cardLayoutRootPanel, (String) getValue(Action.NAME));
    }

    /**
     * 注册设置页面Action内容展示面板，自定义面板内容
     */
    protected void registerPane() {
        rightPanel.add(BorderLayout.CENTER, drawCenterPanel());
        rightPanel.add(BorderLayout.PAGE_END, createBottomBar());

        cardLayoutRootPanel.add((String) getValue(Action.NAME), rightPanel);
    }

    protected Component createBottomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        buttonPane.add(Box.createHorizontalGlue());

        JButton applyBtn = new JButton("Apply");

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton cancelBtn = new JButton("Cancel");
        buttonPane.add(cancelBtn);

        return buttonPane;
    }


    protected Component drawCenterPanel() {
        return new JLabel((String) getValue(Action.SHORT_DESCRIPTION));
    }
}

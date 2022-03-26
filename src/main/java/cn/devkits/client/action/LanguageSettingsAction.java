/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.action;

import cn.devkits.client.util.DKSysUIUtil;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 设置面板设置系统默认语言
 */
public class LanguageSettingsAction extends BaseAction {
    public LanguageSettingsAction(Frame frame, JPanel cardLayoutRootPanel) {
        super(frame, cardLayoutRootPanel);

        putValue(Action.NAME, DKSysUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_LANG"));

        Icon rightIcon = IconFontSwing.buildIcon(FontAwesome.GLOBE, 16, new Color(50, 50, 50));

        putValue(Action.SMALL_ICON, rightIcon);
        putValue(Action.MNEMONIC_KEY, null);
        putValue(Action.SHORT_DESCRIPTION, DKSysUIUtil.getLocaleString("SETTINGS_SYS_SETTINGS_LANG_DESC"));

        registerPane();
    }

    @Override
    protected Component drawCenterPanel() {
        JPanel jPanel = new JPanel();
        SpringLayout mgr = new SpringLayout();
        jPanel.setLayout(mgr);

        JLabel currentLangLbl = new JLabel();
        currentLangLbl.setText(DKSysUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_LANG_LBL_CURRENT"));
        jPanel.add(currentLangLbl);
        JLabel currrentLang = new JLabel();
        jPanel.add(currrentLang);
        JLabel chooseLangLabl = new JLabel();
        chooseLangLabl.setText(DKSysUIUtil.getLocaleStringWithColon("SETTINGS_SYS_SETTINGS_LANG_LBL_CHOOSE"));
        jPanel.add(chooseLangLabl);
        JLabel chooseLangLbl = new JLabel();
        jPanel.add(chooseLangLbl);
        JComboBox<Object> langCombo = new JComboBox<>();
        langCombo.setModel(loadSupportLang());
        jPanel.add(langCombo);

        SpringLayout.Constraints currentLangLblCons = mgr.getConstraints(currentLangLbl);
        currentLangLblCons.setX(Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_8));
        currentLangLblCons.setY(Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_8));

        SpringLayout.Constraints currentLangCons = mgr.getConstraints(currrentLang);
        currentLangCons.setConstraint(SpringLayout.WEST, Spring.sum(currentLangLblCons.getConstraint(SpringLayout.EAST), Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_5)));
        currentLangCons.setY(Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_8));

        SpringLayout.Constraints chooseLangCons = mgr.getConstraints(chooseLangLabl);
        chooseLangCons.setX(Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_8));
        chooseLangCons.setConstraint(SpringLayout.NORTH, Spring.sum(currentLangLblCons.getConstraint(SpringLayout.SOUTH), Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_8)));

        SpringLayout.Constraints langsCons = mgr.getConstraints(langCombo);
        langsCons.setConstraint(SpringLayout.WEST, Spring.sum(chooseLangCons.getConstraint(SpringLayout.EAST), Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_5)));
        langsCons.setConstraint(SpringLayout.NORTH, Spring.sum(currentLangLblCons.getConstraint(SpringLayout.SOUTH), Spring.constant(DKSysUIUtil.COMPONENT_UI_PADDING_5)));
        return jPanel;
    }

    private ComboBoxModel<Object> loadSupportLang() {
        DefaultComboBoxModel<Object> objectDefaultComboBoxModel = new DefaultComboBoxModel<>();
        return objectDefaultComboBoxModel;
    }

    @Override
    protected void doApply(ActionEvent e) {
        super.doApply(e);
    }
}

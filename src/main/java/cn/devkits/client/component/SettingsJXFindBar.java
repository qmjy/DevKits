/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.component;

import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.search.Searchable;

import javax.swing.*;
import java.awt.*;

/**
 * 去除原始的label和搜索按钮
 */
public class SettingsJXFindBar extends JXFindBar {

    public SettingsJXFindBar(Searchable searchable) {
        super(searchable);
    }

    @Override
    protected void build() {
        setLayout(new BorderLayout());
        add(searchField, BorderLayout.CENTER);
    }
}

/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.component;

import java.awt.Insets;
import javax.swing.JPanel;

public class InsetPanel extends JPanel {

    /** serialVersionUID */
    private static final long serialVersionUID = 3705066528076150042L;
    private Insets i;

    public InsetPanel(Insets i) {
        this.i = i;
    }

    @Override
    public Insets getInsets() {
        return i;
    }
}

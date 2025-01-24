/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKStringUtil;
import cn.devkits.client.util.DKSysUIUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;

/**
 * 代码格式化
 *
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月25日 下午11:11:04
 */
public class CodeFormatFrame extends DKAbstractFrame {

    @Serial
    private static final long serialVersionUID = -3324482544348779089L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFormatFrame.class);

    private JSplitPane currentComponent;

    public CodeFormatFrame() {
        super("Code Format");

        initUI(getDKPane());
        initListener();
    }


    @Override
    protected void initUI(Container rootContainer) {
        JTabbedPane tabbedPane = new JTabbedPane();
        addTabContent(tabbedPane, SyntaxConstants.SYNTAX_STYLE_JSON, "Json Format");
        addTabContent(tabbedPane, SyntaxConstants.SYNTAX_STYLE_XML, "XML Format");
        Dimension screenFriendlySize = DKSysUIUtil.getScreenFriendlySize();
        tabbedPane.setPreferredSize(new Dimension((int) screenFriendlySize.getWidth(), (int) screenFriendlySize.getHeight()));

        rootContainer.add(tabbedPane);
    }


    @Override
    protected void initListener() {
        /**
         * add resize listener, change divider location when windows resized.
         */
        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentComponent.setDividerLocation(getWidth() / 2);
            }
        });

        super.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (e.getComponent() instanceof CodeFormatFrame) {
                    CodeFormatFrame frame = (CodeFormatFrame) e.getComponent();
                    // TODO
                }
            }
        });
    }


    private void addTabContent(JTabbedPane tabbedPane, String styleKey, String title) {
        currentComponent = new JSplitPane();

        RSyntaxTextArea leftTextArea = new RSyntaxTextArea(20, 60);
        leftTextArea.setSyntaxEditingStyle(styleKey);
        leftTextArea.setCodeFoldingEnabled(true);

        RSyntaxTextArea rightTextArea = new RSyntaxTextArea(20, 60);
        rightTextArea.setSyntaxEditingStyle(styleKey);
        rightTextArea.setCodeFoldingEnabled(true);

//        leftTextArea.addKeyListener(new JsonKeyListener(rightTextArea, title));

        RTextScrollPane sp = new RTextScrollPane(leftTextArea);
        currentComponent.setLeftComponent(sp);

        RTextScrollPane rightScrollPane = new RTextScrollPane(rightTextArea);
        currentComponent.setRightComponent(rightScrollPane);

        currentComponent.setDividerLocation(0.5d);

        tabbedPane.addTab(title, currentComponent);
        tabbedPane.setEnabledAt(0, true);
    }


    class JsonKeyListener extends KeyAdapter {
        private final JTextArea rightTextArea;
        private final String title;

        public JsonKeyListener(JTextArea rightTextArea, String title) {
            this.rightTextArea = rightTextArea;
            this.title = title;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            JTextArea left = (JTextArea) e.getSource();
            String text = left.getText();

            if (text == null || text.trim().isEmpty()) {
                return;
            }

            String formatStr = "";
            if ("Json Format".equals(title)) {
                formatStr = DKStringUtil.jsonFormat(text);
            } else if ("XML Format".equals(title)) {
                formatStr = DKStringUtil.xmlFormat(text);
            }
            if (formatStr.startsWith("Invalid")) {
                JOptionPane.showMessageDialog(currentComponent, formatStr);
            } else {
                rightTextArea.setText(formatStr);
            }
        }
    }

    public JSplitPane getCurrentComponent() {
        return currentComponent;
    }
}

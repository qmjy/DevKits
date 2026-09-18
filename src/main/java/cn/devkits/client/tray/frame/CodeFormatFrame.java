/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSysUIUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.Serial;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 代码格式化：JSON / XML 格式化与压缩
 * <p>
 * 双栏布局，左侧为输入区，右侧为结果区，支持格式化、压缩、复制、清空操作。
 * 已去除 org.fife.ui 第三方编辑器组件依赖，改用原生 Swing 实现。
 *
 * @author shaofeng liu
 * @version 1.1.0
 * @time 2019年11月25日 下午11:11:04
 */
public class CodeFormatFrame extends DKAbstractFrame {

    @Serial
    private static final long serialVersionUID = -3324482544348779089L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFormatFrame.class);

    private static final Font CODE_FONT = new Font("Courier New", Font.PLAIN, 14);

    private JSplitPane currentComponent;

    public CodeFormatFrame() {
        super("Code Format");

        initUI(getDKPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        JTabbedPane tabbedPane = new JTabbedPane();
        addTabContent(tabbedPane, FormatType.JSON, "Json Format");
        addTabContent(tabbedPane, FormatType.XML, "XML Format");
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
    }

    /**
     * 添加一个格式化页签：工具栏 + 左输入区 + 右结果区
     *
     * @param tabbedPane 页签容器
     * @param type       格式化类型
     * @param title      页签标题
     */
    private void addTabContent(JTabbedPane tabbedPane, FormatType type, String title) {
        JTextArea inputArea = createTextArea();
        JTextArea outputArea = createTextArea();
        outputArea.setEditable(false);

        CodeFoldingModel inputFoldModel = new CodeFoldingModel(inputArea, type == FormatType.XML);
        CodeFoldingModel outputFoldModel = new CodeFoldingModel(outputArea, type == FormatType.XML);

        JButton formatBtn = new JButton("Format");
        formatBtn.addActionListener(e -> doFormat(type, inputArea, outputArea, inputFoldModel));

        JButton compactBtn = new JButton("Compact");
        compactBtn.addActionListener(e -> doCompact(type, inputArea, outputArea, inputFoldModel));

        JButton copyBtn = new JButton("Copy");
        copyBtn.addActionListener(e -> copyToClipboard(outputArea));

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.add(formatBtn);
        toolbar.add(compactBtn);
        toolbar.add(copyBtn);
        toolbar.add(clearBtn);

        currentComponent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createCodeScrollPane(inputArea, inputFoldModel),
                createCodeScrollPane(outputArea, outputFoldModel));
        currentComponent.setDividerLocation(0.5d);
        currentComponent.setResizeWeight(0.5d);

        JPanel tabRoot = new JPanel(new BorderLayout());
        tabRoot.add(toolbar, BorderLayout.NORTH);
        tabRoot.add(currentComponent, BorderLayout.CENTER);

        tabbedPane.addTab(title, tabRoot);
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setFont(CODE_FONT);
        return textArea;
    }

    /**
     * 创建带行号栏的代码编辑区滚动面板
     *
     * @param textArea  文本编辑区
     * @param foldModel 折叠模型，可为 null（不显示折叠图标）
     * @return 带 row header 行号栏的滚动面板
     */
    private JScrollPane createCodeScrollPane(JTextArea textArea, CodeFoldingModel foldModel) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(new TextLineNumber(textArea, foldModel));
        return scrollPane;
    }

    /**
     * 执行格式化：输入区 -> 结果区
     */
    private void doFormat(FormatType type, JTextArea inputArea, JTextArea outputArea, CodeFoldingModel inputFoldModel) {
        inputFoldModel.expandAll();
        String input = inputArea.getText();
        if (input == null || input.trim().isEmpty()) {
            showWarn("Please input the content to be formatted.");
            return;
        }
        try {
            outputArea.setText(type.format(input));
            outputArea.setCaretPosition(0);
        } catch (Exception e) {
            LOGGER.error("Format {} failed", type, e);
            showWarn("Invalid " + type + " string: " + e.getMessage());
        }
    }

    /**
     * 执行压缩（去空白）：输入区 -> 结果区
     */
    private void doCompact(FormatType type, JTextArea inputArea, JTextArea outputArea, CodeFoldingModel inputFoldModel) {
        inputFoldModel.expandAll();
        String input = inputArea.getText();
        if (input == null || input.trim().isEmpty()) {
            showWarn("Please input the content to be compacted.");
            return;
        }
        try {
            outputArea.setText(type.compact(input));
            outputArea.setCaretPosition(0);
        } catch (Exception e) {
            LOGGER.error("Compact {} failed", type, e);
            showWarn("Invalid " + type + " string: " + e.getMessage());
        }
    }

    private void copyToClipboard(JTextArea outputArea) {
        String text = outputArea.getText();
        if (text == null || text.isEmpty()) {
            showWarn("Nothing to copy.");
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(currentComponent, msg, "Code Format", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * JSON / XML 格式化与压缩实现
     */
    private enum FormatType {
        /**
         * JSON，基于 Gson
         */
        JSON {
            @Override
            String format(String input) {
                JsonElement element = JsonParser.parseString(input);
                return new GsonBuilder().setPrettyPrinting().create().toJson(element);
            }

            @Override
            String compact(String input) {
                JsonElement element = JsonParser.parseString(input);
                return new Gson().toJson(element);
            }
        },
        /**
         * XML，基于 dom4j
         */
        XML {
            @Override
            String format(String input) throws DocumentException {
                OutputFormat fmt = new OutputFormat("    ", true, "utf-8");
                fmt.setTrimText(true);
                fmt.setPadText(true);
                return writeXml(input, fmt);
            }

            @Override
            String compact(String input) throws DocumentException {
                return writeXml(input, OutputFormat.createCompactFormat());
            }

            private String writeXml(String input, OutputFormat fmt) throws DocumentException {
                try {
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(new StringReader(input));
                    StringWriter out = new StringWriter();
                    XMLWriter writer = new XMLWriter(out, fmt);
                    writer.write(doc);
                    writer.close();
                    return out.toString();
                } catch (java.io.IOException e) {
                    throw new DocumentException(e.getMessage());
                }
            }
        };

        /**
         * 格式化（美化）输入串
         */
        abstract String format(String input) throws Exception;

        /**
         * 压缩（去除多余空白）输入串
         */
        abstract String compact(String input) throws Exception;
    }
}

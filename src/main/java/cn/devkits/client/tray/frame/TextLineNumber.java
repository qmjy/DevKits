/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 行号栏组件：作为 {@link JScrollPane} 的 row header 使用，为 {@link JTextArea} 绘制行号。
 * <p>
 * 自动跟随文本行数调整宽度，支持当前光标所在行高亮。
 * <p>
 * 可选支持代码折叠：传入 {@link CodeFoldingModel} 后，在行号左侧绘制折叠图标，
 * 点击图标切换折叠状态（详见 {@link CodeFoldingModel}）。
 * <p>
 * 用法：
 * <pre>
 *     JScrollPane sp = new JScrollPane(textArea);
 *     sp.setRowHeaderView(new TextLineNumber(textArea));
 *     // 或带折叠：
 *     CodeFoldingModel model = new CodeFoldingModel(textArea, true);
 *     sp.setRowHeaderView(new TextLineNumber(textArea, model));
 * </pre>
 * 注意：编辑区不要开启 lineWrap（自动换行会破坏行号与逻辑行的对应关系）。
 *
 * @author shaofeng liu
 * @version 1.1.0
 */
public class TextLineNumber extends JPanel
        implements DocumentListener, CaretListener, ComponentListener, PropertyChangeListener {

    private static final long serialVersionUID = 4354317827901623553L;

    private static final int HORIZONTAL_PADDING = 8;
    private static final int MIN_DIGITS = 3;

    /**
     * 折叠图标区域宽度
     */
    private static final int FOLD_AREA_WIDTH = 18;
    private static final int FOLD_ICON_SIZE = 10;

    private final JTextArea textArea;

    /**
     * 折叠模型，为 null 时不支持折叠
     */
    private final CodeFoldingModel foldModel;

    private final Color normalColor;
    private final Color currentColor;
    private final Color currentLineBgColor;
    private final Color separatorColor;
    private final Color iconBgColor;

    public TextLineNumber(JTextArea textArea) {
        this(textArea, null);
    }

    public TextLineNumber(JTextArea textArea, CodeFoldingModel foldModel) {
        this.textArea = textArea;
        this.foldModel = foldModel;

        Color labelDisabled = UIManager.getColor("Label.disabledForeground");
        if (labelDisabled == null) {
            labelDisabled = Color.GRAY;
        }
        normalColor = labelDisabled;

        Color labelForeground = UIManager.getColor("Label.foreground");
        if (labelForeground == null) {
            labelForeground = Color.BLACK;
        }
        currentColor = labelForeground;

        Color panelBg = UIManager.getColor("Panel.background");
        Color base = panelBg != null ? panelBg : Color.WHITE;
        currentLineBgColor = blend(base, normalColor, 0.15d);
        separatorColor = blend(currentLineBgColor, normalColor, 0.5d);
        iconBgColor = blend(currentLineBgColor, normalColor, 0.35d);

        setFont(textArea.getFont());
        setPreferredSize(new Dimension(minPreferredWidth(), 0));

        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        textArea.addComponentListener(this);
        textArea.addPropertyChangeListener("font", this);

        if (foldModel != null) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int line = lineAtY(e.getY());
                    if (line >= 0 && foldModel.isFoldStart(line)) {
                        foldModel.toggle(line);
                        repaint();
                    }
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int lineHeight = fm.getHeight();
        int ascent = fm.getAscent();
        Rectangle clip = g.getClipBounds();
        int visibleTop = clip.y;
        int visibleBottom = clip.y + clip.height;
        int currentLine = currentCaretLine();

        Element root = textArea.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();

        for (int i = 0; i < lineCount; i++) {
            Element line = root.getElement(i);
            Rectangle rect;
            try {
                rect = textArea.modelToView2D(line.getStartOffset()).getBounds();
            } catch (BadLocationException e) {
                continue;
            }
            if (rect == null) {
                continue;
            }
            int lineTop = rect.y;
            int lineBottom = rect.y + lineHeight;
            if (lineBottom < visibleTop) {
                continue;
            }
            if (lineTop > visibleBottom) {
                break;
            }

            if (i == currentLine) {
                g.setColor(currentLineBgColor);
                g.fillRect(0, lineTop, getWidth(), lineHeight);
            }

            if (foldModel != null && foldModel.isFoldStart(i)) {
                drawFoldIcon(g, i, lineTop, lineHeight);
            }

            String number = String.valueOf(i + 1);
            g.setColor(i == currentLine ? currentColor : normalColor);
            int x = getWidth() - HORIZONTAL_PADDING - fm.stringWidth(number);
            g.drawString(number, x, lineTop + ascent);
        }

        // right separator line
        g.setColor(separatorColor);
        g.drawLine(getWidth() - 1, visibleTop, getWidth() - 1, visibleBottom);
    }

    /**
     * 在行号左侧绘制折叠图标：方框内 '-' 表示可折叠，'+' 表示已折叠
     */
    private void drawFoldIcon(Graphics g, int line, int lineTop, int lineHeight) {
        boolean collapsed = foldModel.isCollapsed(line);
        int x = (FOLD_AREA_WIDTH - FOLD_ICON_SIZE) / 2;
        int y = lineTop + (lineHeight - FOLD_ICON_SIZE) / 2;

        g.setColor(iconBgColor);
        g.fillRect(x, y, FOLD_ICON_SIZE, FOLD_ICON_SIZE);
        g.setColor(separatorColor);
        g.drawRect(x, y, FOLD_ICON_SIZE, FOLD_ICON_SIZE);

        g.setColor(collapsed ? currentColor : normalColor);
        int midY = y + FOLD_ICON_SIZE / 2;
        g.drawLine(x + 2, midY, x + FOLD_ICON_SIZE - 3, midY);
        if (collapsed) {
            int midX = x + FOLD_ICON_SIZE / 2;
            g.drawLine(midX, y + 2, midX, y + FOLD_ICON_SIZE - 3);
        }
    }

    private int currentCaretLine() {
        Element root = textArea.getDocument().getDefaultRootElement();
        return root.getElementIndex(textArea.getCaretPosition());
    }

    private int lineAtY(int y) {
        try {
            int offset = textArea.viewToModel2D(new Point(0, y));
            return textArea.getDocument().getDefaultRootElement().getElementIndex(offset);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 根据当前总行数计算行号栏宽度，行数变化时自动加宽
     */
    private void updatePreferredWidth() {
        int digits = String.valueOf(Math.max(textArea.getLineCount(), 1)).length();
        digits = Math.max(digits, MIN_DIGITS);
        FontMetrics fm = getFontMetrics(getFont());
        int width = 2 * HORIZONTAL_PADDING + fm.stringWidth("9".repeat(digits)) + 1;
        if (foldModel != null) {
            width += FOLD_AREA_WIDTH;
        }
        Dimension preferred = new Dimension(width, 0);
        if (!preferred.equals(getPreferredSize())) {
            setPreferredSize(preferred);
            revalidate();
        }
        repaint();
    }

    private int minPreferredWidth() {
        FontMetrics fm = getFontMetrics(getFont());
        int width = 2 * HORIZONTAL_PADDING + fm.stringWidth("9".repeat(MIN_DIGITS)) + 1;
        if (foldModel != null) {
            width += FOLD_AREA_WIDTH;
        }
        return width;
    }

    private static Color blend(Color base, Color overlay, double ratio) {
        int r = (int) (base.getRed() * (1 - ratio) + overlay.getRed() * ratio);
        int g = (int) (base.getGreen() * (1 - ratio) + overlay.getGreen() * ratio);
        int b = (int) (base.getBlue() * (1 - ratio) + overlay.getBlue() * ratio);
        return new Color(r, g, b);
    }

    // ----- DocumentListener: text changes may add/remove lines -----

    @Override
    public void insertUpdate(DocumentEvent e) {
        updatePreferredWidth();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updatePreferredWidth();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updatePreferredWidth();
    }

    // ----- CaretListener: repaint for current-line highlight -----

    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }

    // ----- ComponentListener: editor resized/visibility changes -----

    @Override
    public void componentResized(ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // no-op
    }

    @Override
    public void componentShown(ComponentEvent e) {
        updatePreferredWidth();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // no-op
    }

    // ----- PropertyChangeListener: follow editor font changes -----

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setFont(textArea.getFont());
        updatePreferredWidth();
    }
}

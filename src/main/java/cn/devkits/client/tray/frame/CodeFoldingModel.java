/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON / XML 折叠模型。
 * <p>
 * 折叠通过"隐藏中间行"实现：折叠时把 start 行与 end 行之间的文本从文档中移除并暂存，
 * 展开时在原偏移处插回。任何外部编辑都会同步修正已折叠区域的偏移，
 * 无法修正时（编辑跨越了折叠点）则丢弃折叠状态。
 * <p>
 * 折叠区域识别：
 * <ul>
 *     <li>JSON：'{' / '[' 与匹配的 '}' / ']'，忽略字符串字面量</li>
 *     <li>XML：开始标签与匹配的结束标签，忽略注释、CDATA、处理指令与自闭合标签</li>
 * </ul>
 * 仅当区域至少含一行中间内容时才视为可折叠。
 *
 * @author shaofeng liu
 * @version 1.0.0
 */
public class CodeFoldingModel implements DocumentListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeFoldingModel.class);

    private final JTextArea textArea;
    private final boolean xml;

    /**
     * 折叠/展开自身引起的文档变更标志，用于区分外部编辑
     */
    private boolean foldingOperation;

    /**
     * 已折叠区域列表
     */
    private final List<Fold> collapsedFolds = new ArrayList<>();

    /**
     * 可见（未折叠）区域映射：起始行号 -> 结束行号
     */
    private Map<Integer, Integer> visibleFolds = Collections.emptyMap();

    private static final class Fold {
        /**
         * 隐藏文本的文档插入点（折叠时被删除文本的起始偏移）
         */
        int offset;
        String hiddenText;
    }

    private static final class Tag {
        int line;
        String name;
    }

    public CodeFoldingModel(JTextArea textArea, boolean xml) {
        this.textArea = textArea;
        this.xml = xml;
        textArea.getDocument().addDocumentListener(this);
        scan();
    }

    /**
     * 指定行是否显示折叠图标（存在可折叠区域或已是折叠点）
     */
    public boolean isFoldStart(int line) {
        return isCollapsed(line) || visibleFolds.containsKey(line);
    }

    /**
     * 指定行是否处于折叠状态（该行折叠了后面的内容）
     */
    public boolean isCollapsed(int line) {
        return collapsedStartLine(line) >= 0;
    }

    private int collapsedStartLine(int line) {
        Element root = textArea.getDocument().getDefaultRootElement();
        for (Fold f : collapsedFolds) {
            if (root.getElementIndex(f.offset) - 1 == line) {
                return line;
            }
        }
        return -1;
    }

    /**
     * 切换指定行的折叠状态
     */
    public void toggle(int line) {
        Fold target = null;
        Element root = textArea.getDocument().getDefaultRootElement();
        for (Fold f : collapsedFolds) {
            if (root.getElementIndex(f.offset) - 1 == line) {
                target = f;
                break;
            }
        }
        if (target != null) {
            expand(target);
        } else {
            Integer end = visibleFolds.get(line);
            if (end != null) {
                collapse(line, end);
            }
        }
        scan();
    }

    /**
     * 展开全部折叠区域（格式化前调用，保证读取到完整文本）
     */
    public void expandAll() {
        List<Fold> snapshot = new ArrayList<>(collapsedFolds);
        snapshot.sort((a, b) -> Integer.compare(b.offset, a.offset));
        for (Fold f : snapshot) {
            expand(f);
        }
        scan();
    }

    /**
     * 折叠 [startLine, endLine]：保留首尾行，移除中间文本
     */
    private void collapse(int startLine, int endLine) {
        try {
            // 先展开折叠点落在被折叠范围内的子折叠，保证删除范围包含其隐藏内容
            int removeStart = textArea.getLineEndOffset(startLine);
            int removeEnd = textArea.getLineStartOffset(endLine);
            for (Fold f : new ArrayList<>(collapsedFolds)) {
                if (f.offset >= removeStart && f.offset <= removeEnd) {
                    expand(f);
                }
            }
            // 子折叠展开后行号/偏移已变化，重新计算
            removeStart = textArea.getLineEndOffset(startLine);
            removeEnd = textArea.getLineStartOffset(endLine);
            if (removeEnd <= removeStart) {
                return;
            }

            String hidden = textArea.getDocument().getText(removeStart, removeEnd - removeStart);
            Fold fold = new Fold();
            fold.offset = removeStart;
            fold.hiddenText = hidden;

            foldingOperation = true;
            textArea.getDocument().remove(removeStart, hidden.length());
            foldingOperation = false;

            collapsedFolds.add(fold);
            for (Fold f : collapsedFolds) {
                if (f != fold && f.offset > removeStart) {
                    f.offset -= hidden.length();
                }
            }
        } catch (BadLocationException e) {
            foldingOperation = false;
            LOGGER.warn("Collapse fold failed", e);
        }
    }

    /**
     * 展开一个折叠区域：在原偏移处插回隐藏文本
     */
    private void expand(Fold fold) {
        try {
            foldingOperation = true;
            textArea.getDocument().insertString(fold.offset, fold.hiddenText, null);
            foldingOperation = false;
            collapsedFolds.remove(fold);
            for (Fold f : collapsedFolds) {
                if (f != fold && f.offset > fold.offset) {
                    f.offset += fold.hiddenText.length();
                }
            }
        } catch (BadLocationException e) {
            foldingOperation = false;
            LOGGER.warn("Expand fold failed", e);
        }
    }

    /**
     * 重新扫描可见文本，刷新可折叠区域
     */
    private void scan() {
        visibleFolds = xml ? scanXml() : scanJson();
    }

    private Map<Integer, Integer> scanJson() {
        Map<Integer, Integer> folds = new HashMap<>();
        try {
            Document doc = textArea.getDocument();
            String text = doc.getText(0, doc.getLength());
            Deque<Integer> stack = new ArrayDeque<>();
            boolean inString = false;
            boolean escaped = false;
            int line = 0;
            for (int i = 0, n = text.length(); i < n; i++) {
                char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    continue;
                }
                if (inString) {
                    if (escaped) {
                        escaped = false;
                    } else if (c == '\\') {
                        escaped = true;
                    } else if (c == '"') {
                        inString = false;
                    }
                } else if (c == '"') {
                    inString = true;
                } else if (c == '{' || c == '[') {
                    stack.push(line);
                } else if (c == '}' || c == ']') {
                    if (!stack.isEmpty()) {
                        int start = stack.pop();
                        if (line > start + 1) {
                            folds.put(start, line);
                        }
                    }
                }
            }
        } catch (BadLocationException ignored) {
        }
        return folds;
    }

    private Map<Integer, Integer> scanXml() {
        Map<Integer, Integer> folds = new HashMap<>();
        try {
            Document doc = textArea.getDocument();
            String text = doc.getText(0, doc.getLength());
            Deque<Tag> stack = new ArrayDeque<>();
            int line = 0;
            int n = text.length();
            int i = 0;
            while (i < n) {
                char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    i++;
                    continue;
                }
                if (c != '<') {
                    i++;
                    continue;
                }
                if (text.startsWith("<!--", i)) {
                    int end = skipTo(text, i, "-->");
                    line += countNewLines(text, i, end);
                    i = end;
                    continue;
                }
                if (text.startsWith("<![CDATA[", i)) {
                    int end = skipTo(text, i, "]]>");
                    line += countNewLines(text, i, end);
                    i = end;
                    continue;
                }
                if (text.startsWith("<!", i)) {
                    int end = skipTo(text, i, ">");
                    line += countNewLines(text, i, end);
                    i = end;
                    continue;
                }
                if (text.startsWith("<?", i)) {
                    int end = skipTo(text, i, "?>");
                    line += countNewLines(text, i, end);
                    i = end;
                    continue;
                }

                boolean isClose = i + 1 < n && text.charAt(i + 1) == '/';
                int j = isClose ? i + 2 : i + 1;
                StringBuilder nameBuf = new StringBuilder();
                while (j < n && isNameChar(text.charAt(j))) {
                    nameBuf.append(text.charAt(j));
                    j++;
                }
                String name = nameBuf.toString();

                // 扫描到 '>'，跳过引号内的内容并统计换行
                char quote = 0;
                while (j < n) {
                    char ch = text.charAt(j);
                    if (quote != 0) {
                        if (ch == quote) {
                            quote = 0;
                        }
                    } else if (ch == '"' || ch == '\'') {
                        quote = ch;
                    } else if (ch == '>') {
                        break;
                    } else if (ch == '\n') {
                        line++;
                    }
                    j++;
                }
                boolean selfClosing = j > 0 && text.charAt(j - 1) == '/';

                if (isClose) {
                    Tag open = null;
                    while (!stack.isEmpty()) {
                        Tag t = stack.pop();
                        if (t.name.equals(name)) {
                            open = t;
                            break;
                        }
                    }
                    if (open != null && line > open.line + 1) {
                        folds.put(open.line, line);
                    }
                } else if (!selfClosing && !name.isEmpty()) {
                    Tag t = new Tag();
                    t.line = line;
                    t.name = name;
                    stack.push(t);
                }
                i = j + 1;
            }
        } catch (BadLocationException ignored) {
        }
        return folds;
    }

    /**
     * 从 from 开始跳到 token 之后，返回新的下标
     */
    private static int skipTo(String text, int from, String token) {
        int idx = text.indexOf(token, from);
        return idx < 0 ? text.length() : idx + token.length();
    }

    private static int countNewLines(String text, int from, int end) {
        int count = 0;
        for (int k = from; k < end && k < text.length(); k++) {
            if (text.charAt(k) == '\n') {
                count++;
            }
        }
        return count;
    }

    private static boolean isNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.' || c == ':';
    }

    // ----- DocumentListener：外部编辑时同步折叠偏移 -----

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (foldingOperation) {
            return;
        }
        int off = e.getOffset();
        int len = e.getLength();
        for (Fold f : collapsedFolds) {
            if (f.offset >= off) {
                f.offset += len;
            }
        }
        scan();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (foldingOperation) {
            return;
        }
        int off = e.getOffset();
        int len = e.getLength();
        boolean broken = false;
        for (Fold f : collapsedFolds) {
            if (f.offset >= off + len) {
                f.offset -= len;
            } else if (f.offset > off) {
                broken = true;
            }
        }
        if (broken) {
            LOGGER.warn("Fold offset invalidated by edit, drop {} folded region(s)", collapsedFolds.size());
            collapsedFolds.clear();
        }
        scan();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (!foldingOperation) {
            scan();
        }
    }
}

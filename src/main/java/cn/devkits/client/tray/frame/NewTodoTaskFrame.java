/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import cn.devkits.client.App;
import cn.devkits.client.DKConstants;
import cn.devkits.client.service.impl.TodoTaskServiceImpl;
import cn.devkits.client.tray.model.TodoTaskModel;
import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.SpringUtilities;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;

import static com.cronutils.model.CronType.SPRING;


/**
 * <p>
 * 创建待办任务对话框
 * </p>
 *
 * @author liushaofeng
 * @since 2020/8/6
 */
public class NewTodoTaskFrame extends DKAbstractFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewTodoTaskFrame.class);
    private TodoListFrame todoListFrame;
    private JTextField nameTextField;
    private JTextField cornTextField;
    private JTextArea desTextArea;
    private JTextField emailsInput;
    private int reminder;

    public NewTodoTaskFrame(TodoListFrame todoListFrame) {
        super(DKSysUIUtil.getLocaleString("TODO_NEW_DIALOG_TITLE"), 0.6f);

        initUI(getContentPane());
        initListener();

        this.todoListFrame = todoListFrame;
        App.getEventBus().register(this);
    }

    /**
     * initUI
     *
     * @param rootContainer Root Pane
     */
    @Override
    protected void initUI(Container rootContainer) {
        JPanel panel = new JPanel(new SpringLayout());

        JLabel nameLbl = new JLabel(DKSysUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_NAME"), JLabel.TRAILING);
        panel.add(nameLbl);
        this.nameTextField = new JTextField();
        nameLbl.setLabelFor(nameTextField);
        panel.add(nameTextField);

        JLabel reminderLbl = new JLabel(DKSysUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_REMINDER"), JLabel.TRAILING);
        panel.add(reminderLbl);
        JPanel reminderPane = createReminderPane();
        reminderLbl.setLabelFor(reminderPane);
        panel.add(reminderPane);

        JLabel cornLbl = new JLabel(DKSysUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_CRON"), JLabel.TRAILING);
        panel.add(cornLbl);
        JPanel cornTextFieldPane = createCornPane();
        cornLbl.setLabelFor(cornTextFieldPane);
        panel.add(cornTextFieldPane);

        JLabel descLbl = new JLabel(DKSysUIUtil.getLocaleStringWithColon("TODO_NEW_DIALOG_DESC"), JLabel.TRAILING);
        panel.add(descLbl);
        JScrollPane descTextAreaPane = createDescTextArea();
        descLbl.setLabelFor(descTextAreaPane);
        panel.add(descTextAreaPane);

        SpringUtilities.makeCompactGrid(panel,
                4, 2,
                DKSysUIUtil.COMPONENT_UI_PADDING_5, DKSysUIUtil.COMPONENT_UI_PADDING_5,
                6, 6);

        rootContainer.add(panel, BorderLayout.CENTER);
        rootContainer.add(createBottomPane(), BorderLayout.PAGE_END);
    }

    private JPanel createReminderPane() {
        JPanel jPanel = new JPanel(new SpringLayout());
        JRadioButton reminderTypeOfTrayMsg = new JRadioButton(DKSysUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_TRAY_MSG"));
        reminderTypeOfTrayMsg.setSelected(true);
        reminderTypeOfTrayMsg.setName(String.valueOf(DKConstants.TODO_REMINDER.TRAY.ordinal()));
        jPanel.add(reminderTypeOfTrayMsg);

        JRadioButton reminderTypeOfDialog = new JRadioButton(DKSysUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_DIALOG"));
        reminderTypeOfDialog.setName(String.valueOf(DKConstants.TODO_REMINDER.DIALOG.ordinal()));
        jPanel.add(reminderTypeOfDialog);

        JRadioButton reminderTypeOfEmail = new JRadioButton(DKSysUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL"));
        reminderTypeOfEmail.setName(String.valueOf(DKConstants.TODO_REMINDER.EMAIL.ordinal()));
        jPanel.add(reminderTypeOfEmail);

        ButtonGroup group = new ButtonGroup();
        group.add(reminderTypeOfTrayMsg);
        group.add(reminderTypeOfDialog);
        group.add(reminderTypeOfEmail);

        this.emailsInput = new JTextField();
        emailsInput.setToolTipText(DKSysUIUtil.getLocaleString("TODO_NEW_DIALOG_REMINDER_EMAIL_TOOLTIPS"));
        emailsInput.setEnabled(false);
        jPanel.add(emailsInput);

        reminderTypeOfTrayMsg.addItemListener(new TodoReminderItemListener(this));
        reminderTypeOfDialog.addItemListener(new TodoReminderItemListener(this));
        reminderTypeOfEmail.addItemListener(new TodoReminderItemListener(this));

        SpringUtilities.makeCompactGrid(jPanel,
                1, 4,
                DKSysUIUtil.COMPONENT_UI_PADDING_0, DKSysUIUtil.COMPONENT_UI_PADDING_5,
                DKSysUIUtil.COMPONENT_UI_PADDING_0, 2);

        return jPanel;
    }

    private JPanel createCornPane() {
        JPanel jPanel = new JPanel(new SpringLayout());

        this.cornTextField = new JTextField();
        jPanel.add(cornTextField);
        JLabel cornHelpIconLbl = createCornHelpIcon();
        jPanel.add(cornHelpIconLbl);

        SpringUtilities.makeCompactGrid(jPanel,
                1, 2,
                DKSysUIUtil.COMPONENT_UI_PADDING_0, DKSysUIUtil.COMPONENT_UI_PADDING_5,
                2, 2);
        return jPanel;
    }

    private JLabel createCornHelpIcon() {
        Icon icon = IconFontSwing.buildIcon(FontAwesome.QUESTION, 16, new Color(50, 50, 50));
        JLabel helpIconLbl = new JLabel();
        helpIconLbl.setIcon(icon);
        helpIconLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cornUrl = "https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm";
                try {
                    DKSysUIUtil.browseURL(new URI(cornUrl));
                } catch (URISyntaxException uriSyntaxException) {
                    LOGGER.error("Can't convert string to URI: {}", cornUrl);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return helpIconLbl;
    }

    private JScrollPane createDescTextArea() {
        JScrollPane jScrollPane = new JScrollPane();

        this.desTextArea = new JTextArea();
        desTextArea.setRows(10);
        desTextArea.setColumns(40);
        jScrollPane.setViewportView(desTextArea);

        return jScrollPane;
    }

    private JPanel createBottomPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        buttonPane.add(Box.createHorizontalGlue());

        JButton applyBtn = new JButton(DKSysUIUtil.getLocaleString("COMMON_BTNS_CREATE"));
        applyBtn.addActionListener(e -> {
            TodoTaskServiceImpl service = (TodoTaskServiceImpl) App.getContext().getBean("todoTaskServiceImpl");
            //TODO input check
            TodoTaskModel todoTaskModel = new TodoTaskModel(nameTextField.getText(), cornTextField.getText(), desTextArea.getText());
            todoTaskModel.setReminder(reminder);
            todoTaskModel.setEmail(emailsInput.getText());

            service.newTodoTask(todoTaskModel);
            App.getEventBus().post(todoTaskModel);
            this.setVisible(false);

            // 快捷键方式创建待办不用刷新
            if (todoListFrame != null) {
                todoListFrame.refresh();
            }
        });

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton closeBtn = new JButton(DKSysUIUtil.getLocaleString("COMMON_BTNS_CLOSE"));
        closeBtn.addActionListener(e -> {
            this.setVisible(false);
        });
        buttonPane.add(closeBtn);

        return buttonPane;
    }

    @Override
    protected void initListener() {
        cornTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTooltips();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTooltips();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTooltips();
            }

            private void updateTooltips() {
                CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(SPRING);
                CronParser parser = new CronParser(cronDefinition);
                CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINA);
                try {
                    Cron parse = parser.parse(cornTextField.getText().trim());
                    String description = descriptor.describe(parse);
                    cornTextField.setToolTipText(description);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("The input corn expression is invalid!");
                }
            }
        });
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    /**
     * 提醒类型监听器
     */
    class TodoReminderItemListener implements ItemListener {

        private final NewTodoTaskFrame newTodoTaskFrame;

        public TodoReminderItemListener(NewTodoTaskFrame newTodoTaskFrame) {
            this.newTodoTaskFrame = newTodoTaskFrame;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            JRadioButton item = (JRadioButton) e.getItem();
            emailsInput.setEnabled(String.valueOf(DKConstants.TODO_REMINDER.EMAIL.ordinal()).equals(item.getName()));
            newTodoTaskFrame.setReminder(Integer.parseInt(item.getName()));
        }
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }
}

/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.component.InsetPanel;
import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.tray.listener.FileSplitSegmentsParamCheckListener;
import cn.devkits.client.tray.model.FileSpliterModel;
import cn.devkits.client.tray.pattern.ExcelFileSpliterStrategyImpl;
import cn.devkits.client.tray.pattern.FileSpliterStrategy;
import cn.devkits.client.tray.pattern.TextFileSpliterStrategyImpl;
import cn.devkits.client.util.DKConfigUtil;
import cn.devkits.client.util.DKFileUtil;
import cn.devkits.client.util.DKSystemUIUtil;
import cn.devkits.client.util.DKSystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * 文件分割<br>
 * 1. Text file: fixed file size, line number, average file size<br>
 * 2. Excel File: sheet, line number
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月10日 下午11:17:59
 */
public class FileSpliterFrame extends DKAbstractFrame implements DKFrameChosenable {

    /** serialVersionUID */
    private static final long serialVersionUID = -6345009512566288941L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSpliterFrame.class);
    private final String[] fileTypeItems = new String[] {"TXT File", "Excel File", "More Type..."};
    private final String[] textFileSpliterParamTypes = new String[] {"Segments(Recommend)", "Fixed Lines", "Fixed Size (KB)"};
    private final static Dimension hpad10 = new Dimension(10, 1);
    private final static Dimension vpad20 = new Dimension(1, 20);
    private final static Dimension vpad4 = new Dimension(1, 4);
    private final static Insets insets = new Insets(5, 10, 0, 10);

    private JComboBox<String> jComboBox;
    private JTextField chosenFilePath;
    private JPanel sgmtParamPane;
    private CardLayout sgmtParamPaneLayout;
    private JTextArea consoleTextArea;

    private Map<JRadioButton, JTextField> mapping = new HashMap<JRadioButton, JTextField>();

    /** text split start */
    private JRadioButton averageSizeBtn;
    private JRadioButton fixedLinesBtn;
    private JRadioButton fixedSizeBtn;
    /** text split end */

    private String currentFileType = fileTypeItems[0];
    private JRadioButton current;
    private FileSpliterModel splitModel;

    private JButton openResultBtn;
    private JButton applyBtn;
    private JButton browseBtn;
    private JButton closeBtn;

    private ThreadPoolExecutor executor;

    public FileSpliterFrame() {
        super("File Spliter", 0.7f, 0.55f);

        initUI(getContentPane());

        initListener();

        executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    protected void initUI(Container rootContianer) {
        JPanel centerPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        centerPanel.setLayout(layout);

        JLabel fileTypeLbl = new JLabel("File Type:");

        jComboBox = new JComboBox<String>(fileTypeItems);
        jComboBox.setLightWeightPopupEnabled(false);

        JLabel filePathLbl = new JLabel("File Path:");
        this.chosenFilePath = new JTextField(38);
        chosenFilePath.setEditable(false);

        this.browseBtn = new JButton("Browse...");

        JPanel detailPane = createOrUpdateSegmentPane();

        this.consoleTextArea = new JTextArea();

        JScrollPane console = new JScrollPane();
        console.setViewportView(consoleTextArea);

        centerPanel.add(fileTypeLbl);
        centerPanel.add(jComboBox);
        centerPanel.add(filePathLbl);
        centerPanel.add(chosenFilePath);
        centerPanel.add(browseBtn);
        centerPanel.add(detailPane);
        centerPanel.add(console);

        SpringLayout.Constraints fileTypeLblCons = layout.getConstraints(fileTypeLbl);
        fileTypeLblCons.setX(Spring.constant(15));
        fileTypeLblCons.setY(Spring.constant(15));

        SpringLayout.Constraints fileTypeComboCons = layout.getConstraints(jComboBox);
        fileTypeComboCons.setX(Spring.sum(Spring.constant(15), fileTypeLblCons.getConstraint(SpringLayout.EAST)));
        fileTypeComboCons.setY(Spring.constant(10));

        SpringLayout.Constraints filePathLblCons = layout.getConstraints(filePathLbl);
        filePathLblCons.setX(Spring.sum(Spring.constant(15), fileTypeComboCons.getConstraint(SpringLayout.EAST)));
        filePathLblCons.setY(Spring.constant(15));

        SpringLayout.Constraints filePathFieldCons = layout.getConstraints(chosenFilePath);
        filePathFieldCons.setX(Spring.sum(Spring.constant(15), filePathLblCons.getConstraint(SpringLayout.EAST)));
        filePathFieldCons.setY(Spring.constant(10));

        SpringLayout.Constraints browseBtnCons = layout.getConstraints(browseBtn);
        browseBtnCons.setX(Spring.sum(Spring.constant(15), filePathFieldCons.getConstraint(SpringLayout.EAST)));
        browseBtnCons.setY(Spring.constant(10));

        SpringLayout.Constraints detailPaneCons = layout.getConstraints(detailPane);
        detailPaneCons.setX(Spring.constant(15));
        detailPaneCons.setY(Spring.sum(Spring.constant(15), fileTypeComboCons.getConstraint(SpringLayout.SOUTH)));

        SpringLayout.Constraints consoleCons = layout.getConstraints(console);
        consoleCons.setX(Spring.sum(Spring.constant(15), detailPaneCons.getConstraint(SpringLayout.EAST)));
        consoleCons.setY(Spring.sum(Spring.constant(15), fileTypeComboCons.getConstraint(SpringLayout.SOUTH)));
        consoleCons.setConstraint(SpringLayout.SOUTH, detailPaneCons.getConstraint(SpringLayout.SOUTH));
        consoleCons.setConstraint(SpringLayout.EAST, browseBtnCons.getConstraint(SpringLayout.EAST));

        // 设置容器的 东边坐标 为 文本框的东边坐标 + 5
        SpringLayout.Constraints centerPanelCons = layout.getConstraints(centerPanel);
        centerPanelCons.setConstraint(SpringLayout.EAST, Spring.sum(browseBtnCons.getConstraint(SpringLayout.EAST), Spring.constant(15)));

        rootContianer.add(centerPanel, BorderLayout.CENTER);

        rootContianer.add(createButtonPanel(rootContianer), BorderLayout.PAGE_END);
    }

    private JPanel createOrUpdateSegmentPane() {
        sgmtParamPane = new JPanel();
        sgmtParamPaneLayout = new CardLayout();
        sgmtParamPane.setLayout(sgmtParamPaneLayout);

        JPanel detailSgmtPane = createTxtSplitSegmentPane();
        JPanel excelSgmtPane = createExcelSplitSegmentPane();
        sgmtParamPane.add(detailSgmtPane, fileTypeItems[0]);
        sgmtParamPane.add(excelSgmtPane, fileTypeItems[1]);

        return sgmtParamPane;
    }

    private JPanel createExcelSplitSegmentPane() {
        JLabel jLabel = new JLabel("It will be come soon...");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(jLabel);
        return jPanel;
    }

    private JPanel createTxtSplitSegmentPane() {
        JPanel detailPane = new InsetPanel(insets);

        detailPane.setBorder(BorderFactory.createTitledBorder("Segment Parameter"));
        detailPane.setLayout(new BoxLayout(detailPane, BoxLayout.Y_AXIS));
        detailPane.add(Box.createRigidArea(vpad20));
        averageSizeBtn = new JRadioButton(textFileSpliterParamTypes[0]);
        averageSizeBtn.setSelected(true);
        this.current = averageSizeBtn;
        detailPane.add(averageSizeBtn);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper(averageSizeBtn, Integer.class));
        fixedLinesBtn = new JRadioButton(textFileSpliterParamTypes[1]);
        detailPane.add(fixedLinesBtn);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper(fixedLinesBtn, Integer.class));
        fixedSizeBtn = new JRadioButton(textFileSpliterParamTypes[2]);
        detailPane.add(fixedSizeBtn);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper(fixedSizeBtn, Float.class));
        detailPane.add(Box.createRigidArea(vpad20));
        detailPane.add(Box.createGlue());

        ButtonGroup group1 = new ButtonGroup();
        group1.add(fixedSizeBtn);
        group1.add(fixedLinesBtn);
        group1.add(averageSizeBtn);
        return detailPane;
    }

    private JPanel initFieldWrapper(JRadioButton jRadioButton, Class<?> clazz) {
        JPanel fieldWrapper = new JPanel();
        fieldWrapper.setLayout(new BoxLayout(fieldWrapper, BoxLayout.X_AXIS));
        fieldWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldWrapper.add(Box.createRigidArea(hpad10));
        fieldWrapper.add(Box.createRigidArea(hpad10));
        JTextField comp = new JTextField(10);
        comp.setEnabled(averageSizeBtn == jRadioButton);// 默认选中平均分配时，输入框可用，其他情况不可用
        comp.addKeyListener(new FileSplitSegmentsParamCheckListener(this, clazz));
        fieldWrapper.add(comp);

        mapping.put(jRadioButton, comp);

        return fieldWrapper;
    }

    private Component createButtonPanel(Container jRootPane) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        openResultBtn = new JButton("Open Result");
        openResultBtn.setEnabled(false);
        buttonPane.add(openResultBtn);

        buttonPane.add(Box.createHorizontalGlue());

        this.applyBtn = new JButton("Apply");

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        this.closeBtn = new JButton("Close");
        buttonPane.add(closeBtn);

        return buttonPane;
    }

    private void updateSegmentPane(String paneName) {
        sgmtParamPaneLayout.show(sgmtParamPane, paneName);
    }

    @Override
    protected void initListener() {
        OptionListener optionListener = new OptionListener(this);
        averageSizeBtn.addActionListener(optionListener);
        fixedLinesBtn.addActionListener(optionListener);
        fixedSizeBtn.addActionListener(optionListener);

        jComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String itemText = (String) e.getItem();
                    int fileTypeIndex = Arrays.binarySearch(fileTypeItems, itemText);
                    if (fileTypeIndex == fileTypeItems.length - 1) {
                        String issueUri = DKConfigUtil.getInstance().getIssueUri();
                        try {
                            URI uri = new URI(issueUri);
                            DKSystemUIUtil.browseURL(uri);
                        } catch (URISyntaxException e1) {
                            LOGGER.error("URI convert failed: {}", issueUri);
                        }
                    } else {
                        updateSegmentPane(itemText);
                    }
                }
            }
        });

        browseBtn.addActionListener(new BrowserActionListener(this, new FileFilter[0], "Split File",false));
        openResultBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DKFileUtil.openFile(splitModel.getOutputFolder());
            }
        });
        applyBtn.addActionListener(new ApplyActionListener(this));
        closeBtn.addActionListener(e -> {
            executor.shutdownNow();

            if (executor.isTerminated()) {
                JButton btn = (JButton) e.getSource();;
                Container parent = btn.getParent().getParent().getParent();
                if (parent instanceof FileSpliterFrame) {
                    FileSpliterFrame root = (FileSpliterFrame) parent;
                    root.dispose();
                }
            }
        });
    }

    @Override
    public void updateSelectFilePath(String absolutePath) {
        chosenFilePath.setText(absolutePath);
    }

    @Override
    public Component getObj() {
        return this;
    }

    public JTextField getChosenFilePath() {
        return chosenFilePath;
    }

    @Override
    public void callback() {
        // TODO Auto-generated method stub
    }

    public Map<JRadioButton, JTextField> getMapping() {
        return mapping;
    }

    public void updateCurrentJRadioBtn(JRadioButton c) {
        this.current = c;
    }

    public String getCurrentFileType() {
        return currentFileType;
    }

    public JRadioButton getCurrent() {
        return current;
    }

    public String[] getFileTypeItems() {
        return fileTypeItems;
    }

    public String[] getTextFileSpliterParamTypes() {
        return textFileSpliterParamTypes;
    }

    public void updateApplyBtnState(boolean b) {
        this.applyBtn.setEnabled(b);
    }

    public void updateConsole(String text) {
        consoleTextArea.append(text);
        consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
    }

    public void clearConsole() {
        consoleTextArea.setText("");
    }

    public void enableOpenResult(FileSpliterModel splitModel) {
        this.splitModel = splitModel;
        openResultBtn.setEnabled(true);
    }

    public void startExecute(Runnable strategy, UiUpdateThread uiUpdateThread) {
        executor.execute(strategy);
        executor.execute(uiUpdateThread);
    }
}


/**
 * 
 * ApplyActionListener
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月11日 上午12:01:33
 */
class ApplyActionListener implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyActionListener.class);
    private FileSpliterFrame frame;

    public ApplyActionListener(FileSpliterFrame fileSpliterFrame) {
        this.frame = fileSpliterFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JRadioButton current = frame.getCurrent();
        String param = frame.getMapping().get(current).getText();
        FileSpliterModel splitModel = new FileSpliterModel(frame.getChosenFilePath().getText());

        FileSpliterStrategy strategy = null;
        switch (DKSystemUtil.arraysSearch(frame.getFileTypeItems(), frame.getCurrentFileType())) {
            case 0:
                strategy = new TextFileSpliterStrategyImpl(frame.getTextFileSpliterParamTypes(), current, param, splitModel);
                break;
            case 1:
                strategy = new ExcelFileSpliterStrategyImpl();
                break;
            default:
                break;
        }
        frame.clearConsole();
        frame.startExecute((Runnable) strategy, new UiUpdateThread(frame, splitModel));
    }
}


/**
 * 事件监听器
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月13日 下午11:27:57
 */
class OptionListener implements ActionListener {

    private FileSpliterFrame fileSpliterFrame;

    public OptionListener(FileSpliterFrame fileSpliterFrame) {
        this.fileSpliterFrame = fileSpliterFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent c = (JComponent) e.getSource();
        boolean selected = false;
        if (c instanceof JToggleButton) {
            selected = ((JToggleButton) c).isSelected();
        }

        fileSpliterFrame.getMapping().values().stream().forEach(jTextField -> {
            jTextField.setEnabled(false);
        });

        JTextField jTextField = fileSpliterFrame.getMapping().get(c);
        jTextField.setEnabled(true);

        fileSpliterFrame.updateCurrentJRadioBtn((JRadioButton) c);
    }
}


class UiUpdateThread implements Runnable {
    private FileSpliterFrame frame;
    private FileSpliterModel splitModel;

    public UiUpdateThread(FileSpliterFrame frame, FileSpliterModel splitModel) {
        this.frame = frame;
        this.splitModel = splitModel;
    }

    @Override
    public void run() {
        while (!(splitModel.isFinished() && splitModel.isMsgEmpty())) {
            String text = splitModel.pollMsg();
            frame.updateConsole(text);
            DKSystemUtil.sleep(50);
        }
        frame.enableOpenResult(splitModel);
    }
}

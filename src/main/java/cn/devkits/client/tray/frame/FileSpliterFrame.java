package cn.devkits.client.tray.frame;

import cn.devkits.client.component.InsetPanel;
import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.util.DKSystemUIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    private final static Dimension hpad10 = new Dimension(10, 1);
    private final static Dimension vpad20 = new Dimension(1, 20);
    private final static Dimension vpad7 = new Dimension(1, 7);
    private final static Dimension vpad4 = new Dimension(1, 4);
    private final static Insets insets = new Insets(5, 10, 0, 10);
    // file path text
    private JTextField chosenFilePath;
    private JButton applyBtn;
    private JButton browseBtn;
    private JButton cancelBtn;

    public FileSpliterFrame() {
        super("File Spliter", 0.7f, 0.55f);

        initUI(getRootPane());

        initListener();
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        centerPanel.setLayout(layout);

        JLabel fileTypeLbl = new JLabel("File Type:");
        JComboBox<String> jComboBox = new JComboBox<String>(new String[] {"TXT File"});
        jComboBox.setLightWeightPopupEnabled(false);

        JLabel filePathLbl = new JLabel("File Path:");
        this.chosenFilePath = new JTextField(38);
        chosenFilePath.setEditable(false);

        this.browseBtn = new JButton("Browse...");

        JPanel detailPane = createOrUpdateSettingsPane();

        JTextArea consoleTextArea = new JTextArea();

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

        jRootPane.add(centerPanel, BorderLayout.CENTER);

        jRootPane.add(createButtonPanel(jRootPane), BorderLayout.PAGE_END);
    }

    private JPanel createOrUpdateSettingsPane() {
        JPanel detailPane = new InsetPanel(insets);
        detailPane.setBorder(BorderFactory.createTitledBorder("Segment parameter"));

        detailPane.setLayout(new BoxLayout(detailPane, BoxLayout.Y_AXIS));
        detailPane.add(Box.createRigidArea(vpad20));
        JRadioButton fixedSize = new JRadioButton("Fixed Size (KB)");
        detailPane.add(fixedSize);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper());
        JRadioButton fixedLines = new JRadioButton("Fixed Lines");
        detailPane.add(fixedLines);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper());
        JRadioButton averageSize = new JRadioButton("Segments");
        detailPane.add(averageSize);
        detailPane.add(Box.createRigidArea(vpad4));
        detailPane.add(initFieldWrapper());
        detailPane.add(Box.createRigidArea(vpad20));
        detailPane.add(Box.createGlue());

        ButtonGroup group1 = new ButtonGroup();
        group1.add(fixedSize);
        group1.add(fixedLines);
        group1.add(averageSize);

        return detailPane;
    }

    private JPanel initFieldWrapper() {
        JPanel fieldWrapper = new JPanel();
        fieldWrapper.setLayout(new BoxLayout(fieldWrapper, BoxLayout.X_AXIS));
        fieldWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldWrapper.add(Box.createRigidArea(hpad10));
        fieldWrapper.add(Box.createRigidArea(hpad10));
        fieldWrapper.add(new JTextField(9));
        return fieldWrapper;
    }

    private Component createButtonPanel(JRootPane jRootPane) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton note = new JButton("Open Result");
        note.setEnabled(false);
        buttonPane.add(note);

        buttonPane.add(Box.createHorizontalGlue());

        this.applyBtn = new JButton("Apply");
        jRootPane.setDefaultButton(applyBtn);

        buttonPane.add(applyBtn);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

        this.cancelBtn = new JButton("Cancel");
        buttonPane.add(cancelBtn);

        return buttonPane;
    }

    @Override
    protected void initListener() {
        browseBtn.addActionListener(new BrowserActionListener(this, new FileFilter[0], false));
        applyBtn.addActionListener(new ApplyActionListener(this));
        cancelBtn.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();;
            Container parent = btn.getParent().getParent().getParent();
            if (parent instanceof FileSpliterFrame) {
                FileSpliterFrame root = (FileSpliterFrame) parent;
                root.dispose();
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
        String filePath = frame.getChosenFilePath().getText();

        File file = new File(filePath);
        String name = file.getName();

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));

            char[] charArray = new char[1024 * 1024 * 5];
            int size = 0;
            while ((size = reader.read(charArray, 0, charArray.length)) != -1) {
                writer.write(charArray, 0, size);
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}

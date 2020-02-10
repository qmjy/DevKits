package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.frame.assist.BrowserActionListener;
import cn.devkits.client.util.DKSystemUIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * 文件分割<br>
 * 1. Text file: fixed file size, line number, average file size 2. Excel File: sheet, line number
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月10日 下午11:17:59
 */
public class FileSpliterFrame extends DKAbstractFrame {

    /** serialVersionUID */
    private static final long serialVersionUID = -6345009512566288941L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSpliterFrame.class);
    // file path text
    private JTextField chosenFilePath;
    private JButton applyBtn;
    private JButton browseBtn;
    private JButton cancelBtn;

    public FileSpliterFrame() {
        super("File Spliter", 0.7f, 0.25f);

        initUI(getRootPane());

        initListener();
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        jRootPane.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        SpringLayout layout = new SpringLayout();
        centerPanel.setLayout(layout);

        JLabel comp = new JLabel("Choose a file:");
        this.chosenFilePath = new JTextField(38);
        chosenFilePath.setEditable(false);

        this.browseBtn = new JButton("Browse...");

        centerPanel.add(comp);
        centerPanel.add(chosenFilePath);
        centerPanel.add(browseBtn);

        SpringLayout.Constraints backBtnCons = layout.getConstraints(comp);
        backBtnCons.setX(Spring.constant(15));
        backBtnCons.setY(Spring.constant(15));

        SpringLayout.Constraints homeBtnCons = layout.getConstraints(chosenFilePath);
        homeBtnCons.setX(Spring.sum(Spring.constant(15), backBtnCons.getConstraint(SpringLayout.EAST)));
        homeBtnCons.setY(Spring.constant(10));

        SpringLayout.Constraints textFieldCons = layout.getConstraints(browseBtn);
        textFieldCons.setX(Spring.sum(Spring.constant(15), homeBtnCons.getConstraint(SpringLayout.EAST)));
        textFieldCons.setY(Spring.constant(10));

        // 设置容器的 东边坐标 为 文本框的东边坐标 + 5
        SpringLayout.Constraints centerPanelCons = layout.getConstraints(centerPanel);
        centerPanelCons.setConstraint(SpringLayout.EAST, Spring.sum(textFieldCons.getConstraint(SpringLayout.EAST), Spring.constant(15)));

        jRootPane.add(centerPanel, BorderLayout.CENTER);

        jRootPane.add(createButtonPanel(jRootPane), BorderLayout.PAGE_END);
    }

    private Component createButtonPanel(JRootPane jRootPane) {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel note = new JLabel("Note: The input file will be splitted by file size of 5MB!");
        note.setForeground(Color.RED);
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
        FileFilter[] filters = new FileFilter[] {DKSystemUIUtil.createFileFilter("Text file format", true, "txt"), DKSystemUIUtil.createFileFilter("Log files", true, "log")};

        browseBtn.addActionListener(new BrowserActionListener(this, filters, false));
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


    public JTextField getChosenFilePath() {
        return chosenFilePath;
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

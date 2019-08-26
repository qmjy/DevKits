package cn.devkits.client.tray.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.xml.bind.Marshaller.Listener;

import cn.devkits.client.util.DKStringUtil;

public class CodeFormatFrame extends JFrame implements DKWindowable
{

    private static final long serialVersionUID = -3324482544348779089L;

    private JSplitPane currentComponent;

    public CodeFormatFrame()
    {
        super("Code Format");

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();

        super.setLocation((screenSize.width - WINDOW_SIZE_WIDTH) / 2, (screenSize.height - WINDOW_SIZE_HEIGHT) / 2);

        initPane();

        initListener();
    }

    private void initPane()
    {
        JTabbedPane tabbedPane = new JTabbedPane();

        addTabContent(tabbedPane, "Json Format");

        add(tabbedPane, "Center");

        tabbedPane.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT));
    }

    private void addTabContent(JTabbedPane tabbedPane, String title)
    {
        currentComponent = new JSplitPane();

        JTextArea leftTextArea = new JTextArea("Ugly Json");
        JTextArea rightTextArea = new JTextArea("Format Json");

        leftTextArea.addKeyListener(new JsonKeyListener(rightTextArea));

        currentComponent.setLeftComponent(leftTextArea);
        currentComponent.setRightComponent(rightTextArea);

        currentComponent.setDividerLocation(WINDOW_SIZE_WIDTH / 2);
        currentComponent.setOneTouchExpandable(true);

        tabbedPane.addTab(title, currentComponent);
        tabbedPane.setEnabledAt(0, true);
    }

    private void initListener()
    {
        super.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                currentComponent.setDividerLocation(getWidth() / 2);
            }
        });
    }

    class JsonKeyListener extends KeyAdapter
    {
        private JTextArea rightTextArea;

        public JsonKeyListener(JTextArea rightTextArea)
        {
            this.rightTextArea = rightTextArea;
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            JTextArea left = (JTextArea) e.getSource();
            String text = left.getText();

            if (text == null || text.trim().isEmpty())
            {
                return;
            }

            String jsonFormat = DKStringUtil.jsonFormat(text);
            if (jsonFormat.startsWith("Iinvalid"))
            {
                JOptionPane.showMessageDialog(currentComponent, jsonFormat);
            } else
            {
                rightTextArea.setText(jsonFormat);
            }

        }
    }
}

package cn.devkits.client.tray.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.devkits.client.model.SocketReachableModel;
import cn.devkits.client.util.DKNetworkUtil;

/**
 * 端口检查
 * @author www.yudeshui.club
 * @datetime 2019年8月26日 下午9:23:46
 */
public class ServerPortsFrame extends JFrame implements DKWindowable
{
    private static final long serialVersionUID = -6406148296636175804L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPortsFrame.class);

    private static final int SERVER_MAX_PORT = 65535;
    // 端口检查线程
    private static final int MAX_THREAD = 4000;

    private ArrayBlockingQueue<SocketReachableModel> msgQuene = new ArrayBlockingQueue<SocketReachableModel>(64);

    private JTextField addressInputField;

    private JButton searchBtn;

    private JTextArea userConsole;

    private JScrollPane scrollPane;

    public ServerPortsFrame()
    {
        super("Server Ports Detection");
        super.setSize(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();

        super.setLocation((screenSize.width - WINDOW_SIZE_WIDTH) / 2, (screenSize.height - WINDOW_SIZE_HEIGHT) / 2);
    }

    @Override
    protected JRootPane createRootPane()
    {
        JRootPane jRootPane = new JRootPane();

        jRootPane.setLayout(new BorderLayout());

        JPanel northPane = new JPanel();
        northPane.setLayout(new BorderLayout());

        createInputTextField(northPane);
        createSearchBtn(northPane);

        this.userConsole = new JTextArea(20, 50);
        userConsole.setAutoscrolls(true);
        userConsole.setLineWrap(true);

        this.scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(userConsole);

        jRootPane.add(northPane, BorderLayout.NORTH);
        jRootPane.add(scrollPane, BorderLayout.CENTER);

        return jRootPane;
    }

    private void createSearchBtn(final JPanel northPanel)
    {
        this.searchBtn = new JButton("查询");
        searchBtn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                userConsole.append("start to check port on server " + addressInputField.getText() + " ..." + System.getProperty("line.separator"));
                startCheck(northPanel, addressInputField.getText());
            }
        });
        northPanel.add(searchBtn, BorderLayout.EAST);
    }

    private void startCheck(JPanel northPanel, final String address)
    {
        long start = System.currentTimeMillis();

        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD);

        for (int port = 1; port <= SERVER_MAX_PORT; port++)
        {
            pool.submit(new CheckPortThread(address, port));
        }

        pool.shutdown();
        new UpdateConsoleThread(pool, northPanel, address, start).start();
    }

    private void createInputTextField(final JPanel northPanel)
    {
        final String defaultText = "input domain or ip address please...";

        this.addressInputField = new JTextField(20);
        addressInputField.setText(defaultText);
        addressInputField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                JTextField textField = (JTextField) e.getSource();
                if (defaultText.equals(textField.getText()))
                {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                JTextField textField = (JTextField) e.getSource();
                if (textField.getText().trim().isEmpty())
                {
                    textField.setText(defaultText);
                }
            }
        });
        addressInputField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                JTextField textField = (JTextField) e.getSource();
                // 判断按下的键是否是回车键
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    userConsole.append("start to check port on server " + textField.getText() + " ..." + System.getProperty("line.separator"));
                    startCheck(northPanel, textField.getText());
                }
            }
        });

        northPanel.add(addressInputField, BorderLayout.CENTER);
    }

    @Override
    public boolean isResizable()
    {
        return false;
    }

    class UpdateConsoleThread extends Thread
    {
        private List<String> ports = new ArrayList<String>();
        private long start;
        private JPanel northPanel;
        private String address;
        private ExecutorService pool;

        public UpdateConsoleThread(ExecutorService pool, JPanel northPanel, String address, long startTime)
        {
            this.pool = pool;
            this.northPanel = northPanel;
            this.address = address;
            this.start = startTime;
        }

        @Override
        public void run()
        {
            while (true)
            {
                if (pool.isTerminated() && msgQuene.isEmpty())
                {
                    String duration = String.valueOf(System.currentTimeMillis() - start - 1 * 1000);
                    userConsole.insert("This detection took a total of " + duration + " milliseconds" + System.getProperty("line.separator"), 0);
                    String portsStr = String.join(",", ports);
                    userConsole.insert("These ports are listening on server " + address + ": " + portsStr + System.getProperty("line.separator"), 0);

                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable tText = new StringSelection(portsStr);
                    clip.setContents(tText, null);

                    JOptionPane.showMessageDialog(northPanel, "The ports are listening has been copied to the clipboard!");
                    return;
                }

                try
                {
                    SocketReachableModel take = msgQuene.poll(1, TimeUnit.SECONDS);
                    if (take != null)
                    {
                        userConsole.insert(take.getMsg(), 0);
                        ports.add(take.getPort());

                        userConsole.update(userConsole.getGraphics());
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    class CheckPortThread extends Thread
    {

        private String address;
        private int port;

        public CheckPortThread(String address, int port)
        {
            this.address = address;
            this.port = port;
        }

        @Override
        public void run()
        {
            try
            {
                if (DKNetworkUtil.socketReachable(address, port))
                {
                    msgQuene.put(new SocketReachableModel(port, true, "The port " + port + " is listening..." + System.getProperty("line.separator")));
                }
                // else
                // {
                // msgQuene.put(new SocketReachableModel(port, false, "The address " + address + " with port " + port + " can not be reached!" + System.getProperty("line.separator")));
                // }
            } catch (InterruptedException e)
            {
                LOGGER.error("put check message error to 'msgQuene'!");
            }
        }
    }
}

/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import java.awt.*;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.devkits.client.util.DKSystemUIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.devkits.client.tray.model.SocketReachableModel;
import cn.devkits.client.util.DKNetworkUtil;
import cn.devkits.client.util.DKStringUtil;

/**
 * 端口检查
 *
 * @author www.devkits.cn
 * @datetime 2019年8月26日 下午9:23:46
 */
public class ServerPortsFrame extends DKAbstractFrame {
    private static final long serialVersionUID = -6406148296636175804L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPortsFrame.class);

    private static final int SERVER_MAX_PORT = 65535;
    // 端口检查线程，充分利用CPU，尽量让IO吞吐率达到最大阈值
    private static final int MAX_THREAD = Runtime.getRuntime().availableProcessors() * 250;

    private ArrayBlockingQueue<SocketReachableModel> msgQuene = new ArrayBlockingQueue<SocketReachableModel>(64);

    private JTextField addressInputField;

    private JButton searchBtn;

    private JTextArea userConsole;

    private JScrollPane scrollPane;

    public ServerPortsFrame() {
        super(DKSystemUIUtil.getLocaleString("SERVER_PORTS_DETECTION"), 0.6f);

        initUI(getContentPane());
        initListener();
    }

    @Override
    protected void initUI(Container jRootPane) {
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

    }

    private void createSearchBtn(final JPanel northPanel) {
        this.searchBtn = new JButton(DKSystemUIUtil.getLocaleString("DETECT"));
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String inputText = addressInputField.getText();
                if (DKStringUtil.isIP(inputText) || DKStringUtil.isDomain(inputText)) {
                    userConsole.append(DKSystemUIUtil.getLocaleStringWithParam("START_MSG", inputText) + System.getProperty("line.separator"));
                    startCheck(northPanel, inputText);
                } else {
                    JOptionPane.showMessageDialog(scrollPane.getParent(), DKSystemUIUtil.getLocaleStringWithParam("INVALID_ADDR_MSG", inputText));
                }
            }
        });
        northPanel.add(searchBtn, BorderLayout.EAST);
    }

    private void startCheck(JPanel northPanel, final String address) {
        long start = System.currentTimeMillis();

        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD);

        for (int port = 1; port <= SERVER_MAX_PORT; port++) {
            pool.submit(new CheckPortThread(address, port));
        }

        pool.shutdown();
        new UpdateConsoleThread(pool, northPanel, address, start).start();
    }

    private void createInputTextField(final JPanel northPanel) {
        final String defaultText = DKSystemUIUtil.getLocaleStringWithEllipsis("INPUT_MSG");

        this.addressInputField = new JTextField(20);
        addressInputField.setText(defaultText);
        addressInputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (defaultText.equals(textField.getText())) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(defaultText);
                }
            }
        });
        addressInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                // 判断按下的键是否是回车键
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String inputStr = textField.getText();
                    if (DKStringUtil.isIP(inputStr) || DKStringUtil.isDomain(inputStr)) {
                        userConsole.append(DKSystemUIUtil.getLocaleStringWithParam("START_MSG", inputStr) + System.getProperty("line.separator"));
                        startCheck(northPanel, inputStr);
                    } else {
                        JOptionPane.showMessageDialog(scrollPane.getParent(), DKSystemUIUtil.getLocaleStringWithParam("INVALID_ADDR_MSG", inputStr));
                    }
                }
            }
        });

        northPanel.add(addressInputField, BorderLayout.CENTER);
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    protected void initListener() {

    }

    class UpdateConsoleThread extends Thread {
        private List<String> ports = new ArrayList<String>();
        private long start;
        private JPanel northPanel;
        private String address;
        private ExecutorService pool;

        public UpdateConsoleThread(ExecutorService pool, JPanel northPanel, String address, long startTime) {
            this.pool = pool;
            this.northPanel = northPanel;
            this.address = address;
            this.start = startTime;
        }

        @Override
        public void run() {
            while (true) {
                if (pool.isTerminated() && msgQuene.isEmpty()) {
                    String duration = String.valueOf(System.currentTimeMillis() - start - 1 * 1000);
                    userConsole.insert(DKSystemUIUtil.getLocaleStringWithParam("TAKE_TIME", duration) + System.getProperty("line.separator"), 0);
                    String portsStr = String.join(",", ports);

                    userConsole.insert(DKSystemUIUtil.getLocaleStringWithParam("PORT_LISTENING_MSG", address, portsStr) + System.getProperty("line.separator"), 0);

                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable tText = new StringSelection(portsStr);
                    clip.setContents(tText, null);

                    JOptionPane.showMessageDialog(northPanel, DKSystemUIUtil.getLocaleString("PORT_COPIED_MSG"));
                    return;
                }

                try {
                    SocketReachableModel take = msgQuene.poll(1, TimeUnit.SECONDS);
                    if (take != null) {
                        userConsole.insert(take.getMsg(), 0);
                        ports.add(take.getPort());

                        userConsole.update(userConsole.getGraphics());
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Take port error: " + e.toString());
                }
            }
        }

    }

    class CheckPortThread extends Thread {

        private String address;
        private int port;

        public CheckPortThread(String address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                if (DKNetworkUtil.socketReachable(address, port)) {

                    msgQuene.put(new SocketReachableModel(port, true, DKSystemUIUtil.getLocaleStringWithParam("PORT_LISTENING", port) + System.getProperty("line.separator")));
                }
                // else
                // {
                // msgQuene.put(new SocketReachableModel(port, false, "The address " + address + " with port " +
                // port + " can not be reached!" + System.getProperty("line.separator")));
                // }
            } catch (InterruptedException e) {
                LOGGER.error("put check message error to 'msgQuene'!");
            }
        }
    }
}

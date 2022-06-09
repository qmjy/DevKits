package cn.devkits.client.tray.frame;

import cn.devkits.client.tray.model.FilesTreeCellRender;
import cn.devkits.client.tray.model.FilesTreeModel;
import cn.devkits.client.util.DKSysUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * 微信助手窗口
 */
public class WechatAssistFrame extends DKAbstractFrame {
    private JSplitPane jSplitPane;
    private JTextField wechatDataPathInput;
    private String wechatDataPath;
    private FilesTreeModel rootModel;

    public WechatAssistFrame() {
        super(DKSysUIUtil.getLocale("WECHAT_ASSIST_TITLE"), 1.2f);

        String path = DKSysUIUtil.getWechatDataPath() + File.separator + "WeChat Files";
        if (new File(path).exists()) {
            wechatDataPath = path;
        }

        rootModel = new FilesTreeModel(wechatDataPath);

        initUI(getDKPane());
        initListener();
    }

    @Override
    protected void initUI(Container rootContainer) {
        rootContainer.setLayout(new BorderLayout());
        rootContainer.add(createTopPane(), BorderLayout.NORTH);
        rootContainer.add(createCenterPane(), BorderLayout.CENTER);
    }

    private JSplitPane createCenterPane() {
        jSplitPane = new JSplitPane();
        JTree tree = new JTree(rootModel);
        tree.setCellRenderer(new FilesTreeCellRender());

        jSplitPane.setLeftComponent(new JScrollPane(tree));
        jSplitPane.setRightComponent(new JPanel());

        return jSplitPane;
    }

    private JPanel createTopPane() {
        SpringLayout layout = new SpringLayout();
        JPanel jPanel = new JPanel(layout);
        jPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JLabel label = new JLabel(DKSysUIUtil.getLocaleWithColon("WECHAT_ASSIST_USER_DATA_PATH"));
        wechatDataPathInput = new JTextField(wechatDataPath);
        JButton browse = new JButton(DKSysUIUtil.getLocaleWithEllipsis("COMMON_BTNS_BROWSE"));

        jPanel.add(label);
        jPanel.add(wechatDataPathInput);
        jPanel.add(browse);

        SpringLayout.Constraints labelCons = layout.getConstraints(label);
        labelCons.setX(Spring.constant(5));
        labelCons.setY(Spring.constant(9));

        SpringLayout.Constraints jTextFieldCons = layout.getConstraints(wechatDataPathInput);
        jTextFieldCons.setX(Spring.sum(Spring.constant(5), labelCons.getConstraint(SpringLayout.EAST)));
        jTextFieldCons.setY(Spring.constant(5));

        SpringLayout.Constraints browseCons = layout.getConstraints(browse);
        browseCons.setX(Spring.sum(Spring.constant(5), jTextFieldCons.getConstraint(SpringLayout.EAST)));
        browseCons.setY(Spring.constant(5));

        SpringLayout.Constraints panelCons = layout.getConstraints(jPanel);

        panelCons.setConstraint(
                SpringLayout.EAST,
                Spring.sum(Spring.constant(5), browseCons.getConstraint(SpringLayout.EAST))
        );

        panelCons.setConstraint(
                SpringLayout.SOUTH,
                Spring.sum(Spring.constant(5), browseCons.getConstraint(SpringLayout.SOUTH))
        );
        return jPanel;
    }

    @Override
    protected void initListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                //自动设置分割框宽度
                jSplitPane.setDividerLocation(.26);
            }
        });
    }
}

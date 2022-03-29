package cn.devkits.client.tray.frame.listener;

import cn.devkits.client.tray.frame.WifiManagementFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * SSID 选中事件
 *
 * @author Shaofeng Liu
 * @Date 2022/03/29
 */
public class SsidListViewSelectionListener implements ListSelectionListener {
    private final WifiManagementFrame parent;

    public SsidListViewSelectionListener(WifiManagementFrame wifiManagementFrame) {
        this.parent = wifiManagementFrame;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println();
        JList listView = (JList) e.getSource();
        if (!listView.getValueIsAdjusting()) {
            String selectedValue = (String) listView.getSelectedValue();
            parent.updateSsidDetail(selectedValue);
        }
    }
}

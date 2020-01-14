package cn.devkits.client.tray.listener;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrayItemClipboardListener implements ActionListener
{
    private String contentText;

    public TrayItemClipboardListener(String content)
    {
        this.contentText = content;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(contentText);
        clip.setContents(tText, null);
    }
}

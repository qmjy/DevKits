package cn.devkits.client.task;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Calendar;
import java.util.TimerTask;

import cn.devkits.client.util.Lunar;

public class WinNoticeTask extends TimerTask
{
    private TrayIcon trayIcon;
    private String content;

    public WinNoticeTask(TrayIcon trayIcon)
    {
        this.trayIcon = trayIcon;
        this.content = "让我们健康、高效、快乐的工作...";
    }

    private String getLunar()
    {
        Lunar lunar = new Lunar(Calendar.getInstance());
        return "农历" + lunar.toString();
    }

    @Override
    public void run()
    {
        trayIcon.displayMessage("今天是" + getLunar(), content, MessageType.INFO);
    }
}

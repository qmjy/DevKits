package cn.devkits.client.task;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.util.Calendar;
import java.util.TimerTask;
import cn.devkits.client.util.LunarUtil;

public class WinNoticeTask extends TimerTask {
    private TrayIcon trayIcon;
    private String content;

    public WinNoticeTask(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
        this.content = "让我们健康、高效、快乐的工作...";
    }

    private String getLunar() {
        Calendar calendar = Calendar.getInstance();
        int[] solarToLunar = LunarUtil.solarToLunar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH + 1), calendar.get(Calendar.DATE));

        // Lunar lunar = new Lunar(Calendar.getInstance());
        StringBuilder sb = new StringBuilder("农历");
        sb.append(solarToLunar[0]).append("年").append(solarToLunar[1]).append("月").append(solarToLunar[2]).append("日");
        return sb.toString();
    }

    @Override
    public void run() {
        trayIcon.displayMessage("今天是" + getLunar(), content, MessageType.INFO);
    }
}

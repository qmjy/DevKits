package cn.devkits.client.asyn;

import cn.devkits.client.App;
import cn.devkits.client.mapper.BaseMapper;
import cn.devkits.client.dto.SysConfig;
import cn.devkits.client.service.SysConfigService;
import cn.devkits.client.util.DKSysUIUtil;

import com.sun.management.OperatingSystemMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oshi.hardware.CentralProcessor;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 程序启动后执行
 *
 * @author sfliue
 */
@Service
public class InitializingBeanImpl implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitializingBeanImpl.class);
    @Autowired
    private BaseMapper baseDao;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public void afterPropertiesSet() {
        initDb();
        initUuid();

        updateTrayIcon();
    }

    private void updateTrayIcon() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new TrayIconUpdateThread()).start();
            }
        }, 1500);

    }

    private void initUuid() {
        Optional<SysConfig> cfgOptional = sysConfigService.findByKey(SysConfigService.SYS_CFG_UUID);
        if (cfgOptional.isPresent()) {
            SysConfig sysConfig = cfgOptional.get();
        } else {
            UUID uuid = UUID.randomUUID();
            sysConfigService.addSysConfig(new SysConfig(SysConfigService.SYS_CFG_UUID, uuid.toString()));
        }
    }

    public void initDb() {
        baseDao.createClipboardTable();
        baseDao.createSystemConfigTable();
        baseDao.createTodoTaskTable();
        baseDao.createEmailTable();
    }

    /**
     * Tray Icon更新线程
     */
    class TrayIconUpdateThread extends Thread {
        private final Logger LOGGER = LoggerFactory.getLogger(TrayIconUpdateThread.class);

        private long[] oldTicks = new long[CentralProcessor.TickType.values().length];
        private Map<String, Image> icoMap = new HashMap<>();

        public TrayIconUpdateThread() {
            super("DK-Tray-Icon-Update-Thread");
        }

        @Override
        public void run() {
            TrayIcon trayIcon = App.getTrayIcon();

            int index = 0;
            while (true) {
                String imgUrl = "assets/trayicon/dark_cat_" + index + ".png";

                Image image = icoMap.get(imgUrl);
                if (image == null) {
                    URL resource = TrayIconUpdateThread.class.getClassLoader().getResource(imgUrl);
                    image = new ImageIcon(resource).getImage();
                    icoMap.put(imgUrl, image);
                }

                int percentCpuLoad = getCpuLoad();
                if (trayIcon != null) {
                    trayIcon.setToolTip(DKSysUIUtil.getLocaleWithParam("TRAY_ICON_MSG", percentCpuLoad));
                    trayIcon.setImage(image);
                }
                try {
                    index++;
                    if (index % 4 == 0) {
                        index = 0;
                    }
                    TimeUnit.MILLISECONDS.sleep(100 - percentCpuLoad);
                } catch (InterruptedException e) {
                    LOGGER.error("DK-Tray-Icon-Update-Thread sleep failed!");
                }
            }
        }

        private int getCpuLoad() {
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double systemCpuLoad = operatingSystemMXBean.getSystemCpuLoad();
            return (int) (systemCpuLoad * 100);
        }
    }
}
package cn.devkits.client.asyn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.devkits.client.App;
import cn.devkits.client.mapper.BaseMapper;
import cn.devkits.client.model.SysConfig;
import cn.devkits.client.service.SysConfigService;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.TrayIcon;
import java.net.URL;
import java.util.Optional;
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
        new Thread(new TrayIconUpdateThread()).start();
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

    class TrayIconUpdateThread extends Thread {
        private final Logger LOGGER = LoggerFactory.getLogger(TrayIconUpdateThread.class);

        public TrayIconUpdateThread() {
            super("DK-Tray-Icon-Update-Thread");
        }

        @Override
        public void run() {
            while (true) {
                URL resource = App.class.getClassLoader().getResource("assets\\trayico\\dark_cat_0.png");
                Image image = new ImageIcon(resource).getImage();
                TrayIcon trayIcon = App.getTrayIcon();
                if (trayIcon != null) {
                    trayIcon.setImage(image);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error("DK-Tray-Icon-Update-Thread Sleep failed!");
                }
            }
        }
    }
}
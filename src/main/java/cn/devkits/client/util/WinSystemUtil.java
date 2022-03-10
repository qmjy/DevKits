package cn.devkits.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows相关实现
 *
 * @author Shaofeng Liu
 * @Date 2022/03/10
 */
public class WinSystemUtil extends DKSystemUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinSystemUtil.class);

    public WinSystemUtil() {
        super();
    }

    /**
     * 获取本地连结过的wifi列表
     *
     * @return 成功连接过的wifi列表
     */
    public List<String> getWifiNamesOfConnected() {
        ArrayList<String> objects = new ArrayList<>();

        List<String> wifiDetails = executeWinCmd("netsh wlan show profile");
        for (String wifiDetail : wifiDetails) {
            if (wifiDetail.indexOf(":") > 0) {
                objects.add(wifiDetail.split(":")[1]);
            }
        }
        return objects;
    }

    /**
     * 获取指定wifi的密码
     *
     * @param wifiName WIFI name
     * @return the password of the input wifi
     */
    public String getPwdOfWifi(String wifiName) {
        List<String> wifiDetails = executeWinCmd("netsh wlan show profile " + wifiName + " key=clear");
        for (String wifiDetail : wifiDetails) {
            if (wifiDetail.indexOf(":") > 0) {
                String[] split = wifiDetail.split(":");
                if ("关键内容".equals(split[0].trim())) {
                    return split[1].trim();
                }
            }
        }
        return null;
    }

    /**
     * 执行windows 命令
     *
     * @param cmd 待执行的命令
     * @return 执行命令后的返回结果
     */
    public List<String> executeWinCmd(String cmd) {
        List<String> dataList = new ArrayList<>();
        BufferedReader br = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(cmd);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (IOException e) {
            LOGGER.info("Can't execute runtime command: {0}！", cmd);
        } finally {
            if (br != null) {
                IoUtils.closeQuietly(br);
            }
        }
        return dataList;
    }
}

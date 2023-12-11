package cn.devkits.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Windows相关实现
 *
 * @author Shaofeng Liu
 * @datetime 2022/03/10
 */
public class WinSystemUtil extends DKSysUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinSystemUtil.class);

    public WinSystemUtil() {
        super();
    }

    @Override
    public Map<String, Map<String, String>> getAvailableSsids() {
        Map<String, Map<String, String>> stringMapMap = new HashMap<>();
        List<String> wifiDetails = executeWinCmd("netsh wlan show network mode=Bssid");
        String key = null;
        Map<String, String> values = new HashMap<>();
        //跳过前面4行无用数据
        for (int i = 4; i < wifiDetails.size(); i++) {
            String line = wifiDetails.get(i);
            if (line.trim().isEmpty()) {
                stringMapMap.put(key, values);
                values = new HashMap<>();
                continue;
            }
            String[] split = line.split(": ");
            if (line.trim().startsWith("SSID ")) {
                key = split.length > 1 ? split[1].trim() : "隐藏的网络";
            } else {
                values.put(split[0].trim(), split[1].trim());
            }
        }
        return stringMapMap;
    }

    /**
     * 获取当前连接的Wifi信息
     *
     * @return 当前连接中的wifi信息
     */
    @Override
    public Map<String, String> getCurrentSsid() {
        Map<String, String> dataMap = new HashMap<>();
        List<String> wifiDetails = executeWinCmd("netsh wlan show interface");
        for (String wifiDetail : wifiDetails) {
            //加空格是防止MAC地址被截断
            String[] split = wifiDetail.split(": ");
            if (split.length > 1) {
                if ("状态".equals(split[0].trim()) && "已断开连接".equals(split[1].trim())) {
                    return new HashMap<>();
                }
                dataMap.put(split[0].trim(), split[1].trim());
            }
        }
        return dataMap;
    }


    /**
     * 获取本地连结过的wifi列表
     *
     * @return 成功连接过的wifi列表
     */
    @Override
    public List<String> getSsidNamesOfConnected() {
        ArrayList<String> objects = new ArrayList<>();

        List<String> wifiDetails = executeWinCmd("netsh wlan show profile");
        for (String wifiDetail : wifiDetails) {
            if (wifiDetail.indexOf(":") > 0) {
                String[] split = wifiDetail.split(":");
                if (split.length == 2) {
                    String wifiName = split[1];
                    if (!wifiName.trim().isEmpty()) {
                        objects.add(wifiName.trim());
                    }
                }
            }
        }
        return objects;
    }

    /**
     * 获取指定wifi的密码
     *
     * @param wifiName WIFI name
     * @return the password of the input Wi-Fi
     */
    @Override
    public String getPwdOfSsid(String wifiName) {
        List<String> wifiDetails = executeWinCmd("netsh wlan show profile \"" + wifiName + "\" key=clear");
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
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line;
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (IOException e) {
            LOGGER.info("Can't execute runtime command: {}！", cmd);
        } finally {
            if (br != null) {
                IoUtils.closeQuietly(br);
            }
        }
        return dataList;
    }
}

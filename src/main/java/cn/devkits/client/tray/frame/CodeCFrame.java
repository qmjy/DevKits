package cn.devkits.client.tray.frame;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.swing.JRootPane;

public class CodeCFrame extends DKAbstractFrame {

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("http://www.baidu.com/刘少锋/test 34/", "UTF-8"));
    }

    @Override
    protected void initUI(JRootPane jRootPane) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initListener() {
        // TODO Auto-generated method stub

    }

}

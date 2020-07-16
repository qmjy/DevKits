/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.swing.JRootPane;

public class CodeCFrame extends DKAbstractFrame {

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("http://www.baidu.com/刘少锋/test 34/", "UTF-8"));
    }

    public CodeCFrame(){
        initUI(getContentPane());
    }

    @Override
    protected void initUI(Container rootContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initListener() {
        // TODO Auto-generated method stub

    }

}

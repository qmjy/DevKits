/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSysUIUtil;
import cn.devkits.client.util.DKSysUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 基础Frame,为所有Frame提供基础公共能力
 *
 * @author Shaofeng Liu
 */
public abstract class DKAbstractFrame extends JFrame implements DKFrameable {
    private static final long serialVersionUID = 6346125541327870409L;

    private static final int DEFAULT_LOAD_FACTOR = 1;

    protected int currentWidth = WINDOW_SIZE_WIDTH;
    protected int currentHeight = WINDOW_SIZE_HEIGHT;

    protected DKSysUtil currentSystemUtil = DKSysUtil.getCurrentSystemUtil();

    protected DKAbstractFrame() {

    }

    /**
     * constructor
     *
     * @param title window title
     */
    protected DKAbstractFrame(String title) {
        this(title, DEFAULT_LOAD_FACTOR);
    }

    /**
     * constructor
     *
     * @param title      window title
     * @param loadFactor window width and height factor
     */
    protected DKAbstractFrame(String title, float loadFactor) {
        this(title, loadFactor, loadFactor);
    }

    /**
     * frame constructor
     *
     * @param title            frame title
     * @param widthLoadFactor  width set factor
     * @param heightLoadFactor height set factor
     */
    protected DKAbstractFrame(String title, float widthLoadFactor, float heightLoadFactor) {
        super(title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (widthLoadFactor > 0 && heightLoadFactor > 0) {

            if (currentWidth * widthLoadFactor > screenSize.getWidth()) {
                currentWidth = (int) screenSize.getWidth();
            } else {
                currentWidth *= widthLoadFactor;
            }

            if (currentHeight * heightLoadFactor > screenSize.getHeight()) {
                currentHeight = (int) screenSize.getHeight();
            } else {
                currentHeight *= heightLoadFactor;
            }
        }

        this.setBounds(DKSysUIUtil.getCenter(currentWidth, currentHeight));

        initData();
        JRootPane rootPane = getRootPane();
        rootPane.setLayout(new BorderLayout());
        initUI(rootPane);
        initListener();
    }

    /**
     * frame constructor, getRootPane()不会被设置成BorderLayout，设置后将导致菜单不显示
     *
     * @param title  frame title
     * @param width  width
     * @param height height
     */
    protected DKAbstractFrame(String title, int width, int height) {
        super(title);
        this.setBounds(DKSysUIUtil.getCenter(width, height));

        initData();

        initUI(getRootPane());
        initListener();
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    /**
     * 创建UI
     *
     * @param rootContainer Root Pane
     */
    protected abstract void initUI(Container rootContainer);

    /**
     * 需要初始化的监听器
     */
    protected abstract void initListener();

}

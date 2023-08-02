/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.tray.pattern;

import cn.devkits.client.tray.model.FileSpliterModel;

/**
 * 
 * 文件分割策略
 * @author Shaofeng Liu
 * @version 1.0.1
 * @time 2020年2月13日 下午9:30:05
 */
public interface FileSpliterStrategy {
    /**
     * 平均分割为几段
     * @param n 文件会被平均分割为n段
     */
    void segmentSplit(int n);

    /**
     * 分割为固定大小段的文件
     * @param size 固定大小,单位KB
     */
    void segmentSplitByFixedSize(long size);
}

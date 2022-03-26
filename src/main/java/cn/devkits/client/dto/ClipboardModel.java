/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.dto;

public class ClipboardModel {

    public static final int CLIPBOARD_CONTENT_TYPE_STR = 1;
    public static final int CLIPBOARD_CONTENT_TYPE_FILES = 2;

    private int id;
    private String content;
    private int type;
    private String createTime;

    public ClipboardModel(String ret, int i, String string) {
        this.content = ret;
        this.type = i;
        this.createTime = string;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClipboardModel other = (ClipboardModel) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}

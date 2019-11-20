package cn.devkits.client.model;

public class ClipboardModel {

    public static final int CLIPBOARD_CONTENT_TYPE_STR = 1;
    public static final int CLIPBOARD_CONTENT_TYPE_FILES = 2;

    private String content;
    private int type;
    private String createTime;

    public ClipboardModel(String ret, int i, String string) {
        this.content = ret;
        this.type = i;
        this.createTime = string;
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

}

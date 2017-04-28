package cn.shagle.learning.activemq.message;

import java.io.Serializable;

/**
 * 目标消息对象
 * Created by Danlu on 2017/4/28.
 */
public class MessagePayload implements Serializable {

    private static final long serialVersionUID = -295422703255886286L;

    private String to;
    private String title;
    private String content;

    public MessagePayload(){}

    public MessagePayload(String to, String title, String content) {
        this.to = to;
        this.title = title;
        this.content = content;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

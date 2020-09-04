package com.sgrain.boot.common.mail.model;

import java.util.Date;

/**
 * @program: spring-parent
 * @description: 简单邮件属性实体类
 * @create: 2020/08/14
 */
public class MailMessage {
    //发件人
    private String from;
    //快速回复人
    private String replyTo;
    //收件人
    private String[] to;
    //抄送
    private String[] cc;
    //密送
    private String[] bcc;
    //发送时间
    private Date sentDate;
    //发送主题
    private String subject;
    //发送正文
    private String text;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

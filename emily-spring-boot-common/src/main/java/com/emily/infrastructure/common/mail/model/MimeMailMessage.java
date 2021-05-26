package com.emily.infrastructure.common.mail.model;

import java.io.File;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 复杂邮件实体类
 * @create: 2020/08/14
 */
public class MimeMailMessage extends MailMessage {
    //附件
    private File[] attachments;
    //正文inline元素，key为ID关联正文规则如cid:myId
    private Map<String, File> inlines;
    //是否是HTML
    private boolean html;

    public File[] getAttachments() {
        return attachments;
    }

    public void setAttachments(File[] attachments) {
        this.attachments = attachments;
    }

    public Map<String, File> getInlines() {
        return inlines;
    }

    public void setInlines(Map<String, File> inlines) {
        this.inlines = inlines;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}

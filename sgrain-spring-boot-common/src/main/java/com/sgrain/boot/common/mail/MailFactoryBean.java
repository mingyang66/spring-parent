package com.sgrain.boot.common.mail;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.mail.model.MailMessage;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: 邮件发送公用方法
 * @create: 2020/08/14
 */
public class MailFactoryBean {
    /**
     * 发送简单文本邮件
     *
     * @param javaMailSender 邮件发送实体对象
     * @param mailMessage    发送邮件实体信息
     * @return
     */
    public static boolean sendSimpleMail(JavaMailSender javaMailSender, MailMessage mailMessage) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //发件人
            simpleMailMessage.setFrom(mailMessage.getFrom());
            //收件人，可以传多个邮件地址
            simpleMailMessage.setTo(mailMessage.getTo());
            //抄送
            simpleMailMessage.setCc(mailMessage.getCc());
            //密送
            simpleMailMessage.setBcc(mailMessage.getBcc());
            //发送时间
            simpleMailMessage.setSentDate(mailMessage.getSentDate());
            //快速回复邮件地址
            simpleMailMessage.setReplyTo(mailMessage.getReplyTo());
            //邮件主题
            simpleMailMessage.setSubject(mailMessage.getSubject());
            //邮件正文
            simpleMailMessage.setText(mailMessage.getText());

            javaMailSender.send(simpleMailMessage);//发送
        } catch (Exception e) {
            LoggerUtils.error(MailFactoryBean.class, "简单邮件发送失败：" + e.getMessage());
            throw new BusinessException(AppHttpStatus.MAILE_SEND_EXCEPTION.getStatus(), "简单邮件发送失败，" + e.getMessage());
        }
        return true;
    }

    /**
     * 发送MIME类型邮件
     * @param javaMailSender 邮件发送实体对象
     * @param message        发送邮件实体信息
     * @return
     */
    public static boolean sendMimeMail(JavaMailSender javaMailSender, com.sgrain.boot.common.mail.model.MimeMailMessage message) {
        try {
            MimeMailMessage mimeMailMessage = new MimeMailMessage(javaMailSender.createMimeMessage());
            MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage.getMimeMessage(), true);
            //发件人
            helper.setFrom(message.getFrom());
            //收件人，可以传多个邮件地址
            helper.setTo(message.getTo());
            //抄送
            helper.setCc(message.getCc());
            //密送
            helper.setBcc(message.getBcc());
            //发送时间
            helper.setSentDate(message.getSentDate());
            //快速回复邮件地址
            helper.setReplyTo(message.getReplyTo());
            //邮件主题
            helper.setSubject(message.getSubject());
            //邮件正文
            helper.setText(message.getText(), message.isHtml());
            //附件
            if (ArrayUtils.isNotEmpty(message.getAttachments())) {
                for (int i = 0; i < message.getAttachments().length; i++) {
                    File file = message.getAttachments()[i];
                    helper.addAttachment(file.getName(), new FileSystemResource(file));
                }
            }
            //向正文添加内inline元素
            if (Objects.nonNull(message.getInlines())) {
                for (Iterator<String> it = message.getInlines().keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    helper.addInline(key, new FileSystemResource(message.getInlines().get(key)));
                }
            }
            javaMailSender.send(mimeMailMessage.getMimeMessage());
        } catch (Exception e) {
            LoggerUtils.error(MailFactoryBean.class, "MIME邮件发送失败：" + e.getMessage());
            throw new BusinessException(AppHttpStatus.MAILE_SEND_EXCEPTION.getStatus(), "MIME邮件发送失败，" + e.getMessage());
        }
        return true;
    }
}

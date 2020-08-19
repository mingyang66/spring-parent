package com.yaomy.control.test.api.rabbit;

import com.google.common.collect.Maps;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.mail.model.MailMessage;
import com.sgrain.boot.mail.model.MimeMailMessage;
import com.sgrain.boot.mail.utils.MailMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 发送邮件控制器测试类
 * @author: 姚明洋
 * @create: 2020/08/17
 */
@RestController
@RequestMapping("mail")
public class MailController {
    @Autowired
    @Lazy
    private JavaMailSender javaMailSender;

    @GetMapping(value = {"mail/sendSimpleMail"})
    public String sendSimpleMail() {
        try {
            MailMessage mailMessage = new MailMessage();
            //发件人
            mailMessage.setFrom("1393619868@qq.com");
            //收件人，可以传多个邮件地址
            mailMessage.setTo(new String[]{"yaomysky@foxmail.com"});
            //抄送
            //mailMessage.setCc();
            //密送
            //mailMessage.setBcc();
            //发送时间
            mailMessage.setSentDate(new Date());
            //快速回复邮件地址
            mailMessage.setReplyTo("yaomysky@foxmail.com");
            //邮件主题
            mailMessage.setSubject("主题：小米粒");
            //邮件正文
            mailMessage.setText("爸爸妈妈爱你");

            MailMessageUtils.sendSimpleMail(javaMailSender, mailMessage);
        } catch (Exception e) {
            LoggerUtils.error(VoidController.class, "发送失败" + e.getMessage());
        }
        return "邮件发送成功...";
    }

    /**
     * 发送复杂邮件
     * @return
     */
    @GetMapping(value = {"mail/sendMimeMail"})
    public String sendMimeMail() {
        try {
            com.sgrain.boot.mail.model.MimeMailMessage mimeMailMessage = new MimeMailMessage();
            //发件人
            mimeMailMessage.setFrom("1393619868@qq.com");
            //收件人，可以传多个邮件地址
            mimeMailMessage.setTo(new String[]{"yaomysky@foxmail.com"});
            //抄送
            mimeMailMessage.setCc(new String[]{"yaomysky@foxmail.com"});
            //密送
            mimeMailMessage.setBcc(new String[]{"yaomysky@foxmail.com"});
            //发送时间
            mimeMailMessage.setSentDate(new Date());
            //快速回复邮件地址
            mimeMailMessage.setReplyTo("yaomysky@foxmail.com");
            //邮件主题
            mimeMailMessage.setSubject("主题：小米粒");
            //正文是否是html
            mimeMailMessage.setHtml(true);
            //邮件正文
            mimeMailMessage.setText("<html><body><p>正文标题说明</p><img src='cid:resourceId'></body></html>");
            mimeMailMessage.setAttachments(new File[]{new File("/Users/yaomingyang/Downloads/WechatIMG211.jpeg"), new File("/Users/yaomingyang/Downloads/型号规格图文说明.pdf")});
            Map<String, File> inlinesMap = Maps.newHashMap();
            inlinesMap.put("resourceId", new File("/Users/yaomingyang/Downloads/WechatIMG211.jpeg"));
            mimeMailMessage.setInlines(inlinesMap);

            MailMessageUtils.sendMimeMail(javaMailSender, mimeMailMessage);
        } catch (Exception e) {
            LoggerUtils.error(VoidController.class, "发送失败" + e.getMessage());
        }
        return "邮件发送成功...";
    }
}

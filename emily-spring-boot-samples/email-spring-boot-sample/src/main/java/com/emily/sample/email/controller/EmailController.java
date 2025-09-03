package com.emily.sample.email.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  姚明洋
 * @since :  2025/8/12 下午2:42
 */
@RestController
public class EmailController {
    private final JavaMailSender mailSender;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @GetMapping("api/v1/email/sendSimple")
    public void sendSimpleMail(String from, String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    @GetMapping("api/v1/email/sendHtml")
    public void sendHtmlMail(String from, String to, String subject, String content)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        //发件人
        helper.setFrom(from);
        //收件人
        helper.setTo(to);
        //抄送收件人
        helper.setCc("CC@QQ.COM");
        //密件抄送收件人（其它收件人无法看到BCC中的邮件列表）
        helper.setBcc("xx@eastmoney.com");
        //回复收件人
        helper.setReplyTo("dd@qq.com");
        helper.setSubject(subject);
        helper.setText(content, true); //true标识html格式
        helper.addInline("logo", new ClassPathResource("image/12.png"));
        helper.addAttachment("document.pdf", new ClassPathResource("image/file.pdf"));

        mailSender.send(message);
    }
}

package com.emily.sample.email.config;

import jakarta.mail.internet.MimeMessage;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author :  Emily
 * @since :  2025/8/28 下午3:02
 */
public class PooledJavaMailSender extends JavaMailSenderImpl {
    private final GenericObjectPool<JavaMailSender> pool;

    public PooledJavaMailSender(GenericObjectPool<JavaMailSender> pool) {
        this.pool = pool;
    }

    @Override
    public void send(@NonNull MimeMessage mimeMessage) throws MailException {
        JavaMailSender sender = null;
        try {
            sender = pool.borrowObject();
            sender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sender != null) {
                pool.returnObject(sender);
            }
        }

    }

    @Override
    public void send(@NonNull SimpleMailMessage simpleMessage) throws MailException {
        JavaMailSender sender = null;
        try {
            sender = pool.borrowObject();
            sender.send(simpleMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sender != null) {
                pool.returnObject(sender);
            }
        }
    }
}

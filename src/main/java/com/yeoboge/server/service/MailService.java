package com.yeoboge.server.service;

import com.yeoboge.server.domain.entity.User;
import org.springframework.mail.javamail.JavaMailSender;

public interface MailService {
    void sendEmail(User receiver, JavaMailSender javaMailSender);
    String makeHtml(User receiver);
    void makePassword(String password);
}

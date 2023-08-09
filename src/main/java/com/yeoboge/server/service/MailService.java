package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.RegisterRequest;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.domain.vo.auth.RegisterResponse;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 임시 비밀번호 메일 전송 관련 메서드를 제공하는 인터페이스
 */
public interface MailService {

    /**
     * 임시 비밀번호 발급 관련 이메일을 전송함
     *
     * @param receiver 이메일을 받는 대상의 이메일 주소
     */
    void sendEmail(User receiver);

    /**
     * 임시 비밀번호 발급 관련 이메일의 html을 생성함
     *
     * @param receiver 이메일을 받는 대상의 이메일 주소
     * @return html을 String 형으로 변환시켜 리턴함
     */
    String makeHtml(User receiver);

    /**
     * 임시 비밀번호를 포함한 메일의 본문을 생성함
     *
     * @param password 임시 비밀번호
     */
    void makePassword(String password);
}

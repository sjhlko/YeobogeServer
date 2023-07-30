package com.yeoboge.server.service;

import com.yeoboge.server.domain.dto.auth.SocialRegisterRequest;
import com.yeoboge.server.domain.vo.auth.Tokens;

public interface OAuthService {
    Tokens socialRegister(SocialRegisterRequest request);
    Tokens socialLogin(String email);
}

package com.xoso.captcha.service;

import com.xoso.captcha.wsdto.CaptchaDataWsDTO;

public interface CaptchaService {
    public CaptchaDataWsDTO generateCaptcha();
    public CaptchaDataWsDTO generateCaptchaSample();

    public boolean validateCaptcha(String id, String value);
}

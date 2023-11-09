package com.xoso.api.controller;

import com.xoso.bank.data.BankData;
import com.xoso.bank.service.BankService;
import com.xoso.bank.service.ClientBankAccountReadService;
import com.xoso.captcha.service.CaptchaService;
import com.xoso.telegram.service.TelegramService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/captcha")
public class CaptchaController extends BaseController {
    @Autowired
    TelegramService telegramService;
    @Autowired
    private CaptchaService captchaService;

    @GetMapping("")
    @Operation(summary = "Create captcha")
    public ResponseEntity<?> getCaptcha(){
        return response(captchaService.generateCaptcha());
    }

    @GetMapping("test")
    @Operation(summary = "Create captcha")
    public ResponseEntity<?> getCaptchaTest(){
        return telegramService.sendMessageToTelegramBot("Test message");
    }

}

package com.xoso.api.controller;

import com.xoso.captcha.service.CaptchaService;
import com.xoso.giftcode.service.GiftCodeService;
import com.xoso.giftcode.wsdto.GiftCodeWsDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/giftcode")
public class GiftCodeController extends BaseController {

    @Autowired
    private GiftCodeService giftCodeService;

    @PostMapping("")
    @Operation(summary = "use giftcode")
    public ResponseEntity<?> useGiftCode(GiftCodeWsDto giftCode){
        giftCodeService.useGiftCode(giftCode.getCode());
        return response("success");
    }

//    @GetMapping("test")
//    @Operation(summary = "Create captcha")
//    public ResponseEntity<?> getCaptchaTest(){
//        return response(captchaService.generateCaptcha());
//    }

}

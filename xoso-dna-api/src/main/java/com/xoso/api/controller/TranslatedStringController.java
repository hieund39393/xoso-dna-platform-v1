package com.xoso.api.controller;

import com.xoso.infrastructure.core.data.ResultBuilder;
import com.xoso.language.service.TranslatedStringService;
import com.xoso.user.service.UserWriteService;
import com.xoso.user.wsdto.UserCreateRequestWsDTO;
import com.xoso.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/translated")
public class TranslatedStringController extends BaseController {

    private final TranslatedStringService translatedStringService;

    @Autowired
    public TranslatedStringController(TranslatedStringService translatedStringService) {
        this.translatedStringService = translatedStringService;
    }

    @GetMapping("string")
    public ResponseEntity<?> listAll() {
        return response(translatedStringService.retreiveAll());
    }
}

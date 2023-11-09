package com.xoso.telegram.service;

import org.springframework.http.ResponseEntity;

public interface TelegramService {
    ResponseEntity<String> sendMessageToTelegramBot(String message);
}

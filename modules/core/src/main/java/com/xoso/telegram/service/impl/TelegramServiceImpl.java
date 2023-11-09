package com.xoso.telegram.service.impl;

import com.xoso.telegram.service.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class TelegramServiceImpl implements TelegramService {
    Logger logger = LoggerFactory.getLogger(TelegramService.class);
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;
    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<String> sendMessageToTelegramBot(String message) {
        try {
            String apiUrl = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s", botToken, chatId, message);
            logger.info(apiUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>("", headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("send Telegram error:", response.getBody());
            }
            return response;
        }catch (Exception e){
            logger.error("sendMessageToTelegramBot error:"+e.getMessage());
        }
        return null;
    }
}

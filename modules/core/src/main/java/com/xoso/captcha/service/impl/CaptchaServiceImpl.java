package com.xoso.captcha.service.impl;
import com.github.cage.Cage;
import com.github.cage.GCage;
import com.xoso.captcha.service.CaptchaService;
import com.xoso.captcha.wsdto.CaptchaDataWsDTO;
import com.xoso.infrastructure.core.exception.BadRequestException;
import com.xoso.infrastructure.core.exception.ExceptionCode;
import com.xoso.job.RandomNumberStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    private String captchaSample =  "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";
    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;
    private static final int CODE_LENGTH = 4;
    private final Cage cage = new GCage(); // Sử dụng JCaptcha

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public CaptchaDataWsDTO generateCaptcha() {
        String captcha = RandomNumberStringGenerator.generate(4);
       // BufferedImage image = generateCaptchaImage(captcha);
        BufferedImage image = cage.drawImage(captcha);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        String uuid = UUID.randomUUID().toString();
        //luu vao cache
        redisTemplate.opsForValue().set(uuid, captcha, 60, TimeUnit.SECONDS);
        System.out.println("save cache: "+uuid+ " "+captcha);
        return CaptchaDataWsDTO.builder()
                .image(base64Image)
                .id(uuid)
                .build();
    }

    @Override
    public CaptchaDataWsDTO generateCaptchaSample() {
        String uuid = UUID.randomUUID().toString();
        return CaptchaDataWsDTO.builder()
                .image(captchaSample)
                .id(uuid)
                .build();
    }

    @Override
    public boolean validateCaptcha(String id, String value) {
        String captchaValue = redisTemplate.opsForValue().get(id);
        if(captchaValue == null)
            throw new BadRequestException(ExceptionCode.CAPTCHA_INCORRECT);
        if(captchaValue.equals(value))
            return true;
        System.out.println("captcha: "+value +" ref: "+captchaValue );
        throw new BadRequestException(ExceptionCode.CAPTCHA_INCORRECT);
    }

    private BufferedImage generateCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // Set background color
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw text
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 30));
        graphics.drawString(captchaText, 50, 35);

        // Add noise
        Random random = new Random();
        int noiseLevel = 150; // Increase this value for more noise
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (random.nextInt(1000) < noiseLevel) {
                    int noiseColor = random.nextInt(255);
                    image.setRGB(i, j, new Color(noiseColor, noiseColor, noiseColor).getRGB());
                }
            }
        }

        graphics.dispose();
        return image;
    }
}

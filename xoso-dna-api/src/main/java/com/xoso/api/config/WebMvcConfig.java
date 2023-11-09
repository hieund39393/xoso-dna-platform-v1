package com.xoso.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO: move config into db
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("https://lot77.org")
                .allowedOrigins("https://localhost:3000")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

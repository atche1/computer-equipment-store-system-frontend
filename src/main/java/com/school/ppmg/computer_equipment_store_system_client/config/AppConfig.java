package com.school.ppmg.computer_equipment_store_system_client.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class AppConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;

            HttpServletRequest request = attrs.getRequest();
            var session = request.getSession(false);
            if (session != null) {
                String token = (String) session.getAttribute("ACCESS_TOKEN");
                if (token != null && !token.isBlank()) {
                    template.header("Authorization", "Bearer " + token);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> new RuntimeException("Feign error: " + response.status());
    }
}
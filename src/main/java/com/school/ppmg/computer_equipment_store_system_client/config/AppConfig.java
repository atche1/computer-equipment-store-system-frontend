package com.school.ppmg.computer_equipment_store_system_client.config;

import com.school.ppmg.computer_equipment_store_system_client.exceptions.ApiConflictException;
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
        return requestTemplate -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs == null) {
                return;
            }

            HttpServletRequest request = attrs.getRequest();

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                requestTemplate.header("Authorization", authHeader);
            }
            String cookie = request.getHeader("Cookie");
            if (cookie != null) {
                requestTemplate.header("Cookie", cookie);
            }
        };
    }
    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {

            String body = "";

            try {
                if (response.body() != null) {
                    body = new String(
                            response.body().asInputStream().readAllBytes(),
                            java.nio.charset.StandardCharsets.UTF_8
                    );
                }
            } catch (Exception ignored) {
            }

            return switch (response.status()) {
                case 400 -> new RuntimeException(
                        body.isBlank() ? "Bad request" : body
                );
                case 401 -> new RuntimeException("Unauthorized");
                case 403 -> new RuntimeException("Forbidden");
                case 404 -> new RuntimeException(
                        body.isBlank() ? "Not found" : body
                );
                case 409 -> new ApiConflictException(
                        body.isBlank() ? "Conflict" : body
                );
                case 500 -> new RuntimeException("Backend error");
                default -> new RuntimeException("Feign error: " + response.status());
            };
        };
    }
}

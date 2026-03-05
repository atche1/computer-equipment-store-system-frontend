package com.school.ppmg.computer_equipment_store_system_client.config;

import com.school.ppmg.computer_equipment_store_system_client.dtos.common.ApiError;
import com.school.ppmg.computer_equipment_store_system_client.exceptions.BackendException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

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
    public ErrorDecoder feignErrorDecoder(ObjectMapper objectMapper) {
        return (methodKey, response) -> toBackendException(objectMapper, response);
    }

    private RuntimeException toBackendException(ObjectMapper objectMapper, Response response) {

        try {
            if (response.body() != null) {
                try (InputStream is = response.body().asInputStream()) {

                    ApiError err = objectMapper.readValue(is, ApiError.class);

                    String msg = (err.getMessage() != null && !err.getMessage().isBlank())
                            ? err.getMessage()
                            : "Something went wrong. Please try again.";

                    return new BackendException(msg, err.getFieldErrors());
                }
            }

        } catch (Exception ignored) {}

        return new BackendException("Something went wrong. Please try again.", null);
    }
}
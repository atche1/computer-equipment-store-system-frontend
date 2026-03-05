package com.school.ppmg.computer_equipment_store_system_client.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class FeignMultipartConfig {

    @Bean
    public FeignHttpMessageConverters feignHttpMessageConverters(
            ObjectProvider<HttpMessageConverter<?>> converters,
            ObjectProvider<HttpMessageConverterCustomizer> customizers
    ) {
        return new FeignHttpMessageConverters(converters, customizers);
    }

    @Bean
    public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}
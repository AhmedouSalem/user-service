package com.aryan.userservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${ecom.token}")
    private String ecomToken;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + ecomToken);
    }
}




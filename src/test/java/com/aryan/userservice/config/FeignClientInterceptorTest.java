package com.aryan.userservice.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class FeignClientInterceptorTest {

    private FeignClientInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new FeignClientInterceptor();
        // Injecter la valeur de ecom.token dans le champ privé
        ReflectionTestUtils.setField(interceptor, "ecomToken", "my-system-token");
    }

    @Test
    void apply_shouldAddAuthorizationHeader() {
        // Given
        RequestTemplate requestTemplate = new RequestTemplate();

        // When
        interceptor.apply(requestTemplate);

        // Then
        assertThat(requestTemplate.headers())
                .containsKey("Authorization");

        assertThat(requestTemplate.headers().get("Authorization"))
                .contains("Bearer my-system-token");
    }
}

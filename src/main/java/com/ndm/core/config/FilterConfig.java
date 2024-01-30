package com.ndm.core.config;

import com.ndm.core.filter.AttributeFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class FilterConfig {

    @Value("${client.location}")
    private String clientLocation;

    private final String[] ALLOWED_HEADER = {
            // 기본 헤더
            "Origin", "X-Requested-with", "Accept", "Authorization", "Content-Type", "Credential-Token", "Access-Token", "Refresh-Token",
            "Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers",
            // 커스텀 헤더
            "X-Credential-Token"
    };

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin(clientLocation);
        Arrays.stream(ALLOWED_HEADER).forEach(config::addAllowedHeader);
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<AttributeFilter> attributeFilter() {
        FilterRegistrationBean<AttributeFilter> filterRegistrationBean = new FilterRegistrationBean<>(new AttributeFilter());
        filterRegistrationBean.setOrder(Integer.MIN_VALUE + 1);

        return filterRegistrationBean;
    }
}

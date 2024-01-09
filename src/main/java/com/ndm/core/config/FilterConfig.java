package com.ndm.core.config;

import com.ndm.core.filter.AttributeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(6000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<AttributeFilter> attributeFilter() {
        FilterRegistrationBean<AttributeFilter> filterRegistrationBean = new FilterRegistrationBean<>(new AttributeFilter());
        filterRegistrationBean.setOrder(Integer.MIN_VALUE + 1);

        return filterRegistrationBean;
    }
}

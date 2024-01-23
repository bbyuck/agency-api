package com.ndm.core.config;

import com.ndm.core.cache.Cache;
import com.ndm.core.common.util.JWKManager;
import com.ndm.core.common.util.OIDCHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final Cache cache;

    @Bean
    public JWKManager jwkManager() {return new JWKManager(cache);}

    @Bean
    public OIDCHelper oidcHelper() {return new OIDCHelper(jwkManager());}

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser();
    }

}

package com.ndm.core.config;

import com.ndm.core.cache.ApplicationEmbeddedCache;
import com.ndm.core.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {

    /**
     * 직접 구현한 캐시
     */
    @Bean
    public Cache cache() {
        return new ApplicationEmbeddedCache();
    }

    @Bean
    public CacheManager cacheManager() {
        log.debug("User Authentication Info cache manager registered");
        return new ConcurrentMapCacheManager("userAuthentication");
    }

}

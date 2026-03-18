package com.sagax.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// CASE 14: @ConditionalOnProperty without matchIfMissing.
// The property "cache.enabled" is NEVER defined in any properties file,
// so this entire configuration is silently skipped.
// The application runs without caching, with no error or warning.
// Fix: @ConditionalOnProperty(name = "cache.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
@Slf4j
public class CacheConfig {

    @Bean
    public Map<String, Object> simpleCache() {
        log.info("Cache initialized");
        return new ConcurrentHashMap<>();
    }
}

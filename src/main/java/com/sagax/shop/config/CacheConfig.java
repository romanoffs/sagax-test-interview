package com.sagax.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

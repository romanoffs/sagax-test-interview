package com.sagax.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration // @Component
public class TestConfig {

    @Bean
    FirstBean firstBean() {
        return new FirstBean();
    }

    @Bean
    SecondBean secondBean() {
        return new SecondBean(firstBean());
    }


    class FirstBean {}
    class SecondBean {
        private final FirstBean firstBean;

        public SecondBean(FirstBean firstBean) {
            this.firstBean = firstBean;
        }

        public FirstBean getFirstBean() {
            return firstBean;
        }
    }
}

package com.sagax.shop.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TestConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Test
    void shouldInjectSameFirstBeanInstance() {
        contextRunner.run(context -> {
            // Retrieve beans from the context
            TestConfig.FirstBean firstBean = context.getBean(TestConfig.FirstBean.class);
            TestConfig.SecondBean secondBean = context.getBean(TestConfig.SecondBean.class);

            assertNotNull(firstBean, "FirstBean should be present in the context");
            assertNotNull(secondBean, "SecondBean should be present in the context");

            // Verify that the bean in the context is the same as the one injected into SecondBean
            assertSame(firstBean, secondBean.getFirstBean(),
                    "SecondBean should have the same FirstBean instance as the one in the context");
        });
    }
}

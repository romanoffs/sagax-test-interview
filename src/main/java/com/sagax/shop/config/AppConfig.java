package com.sagax.shop.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public class Config {
        public final String host;
        public final int port;

        public Config(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    public class Server {
        private volatile Config config;

        public void updateConfig(String host, int port) {
            config = new Config(host, port);
        }

        public Config getConfig() { return config; }
    }
}

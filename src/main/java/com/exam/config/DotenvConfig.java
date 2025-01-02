package com.exam.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:application.properties")
public class DotenvConfig {

    private final Environment environment;

    public DotenvConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory(".") // Points to the parent of `src`
                .filename("properties.env.dev")
                .load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}

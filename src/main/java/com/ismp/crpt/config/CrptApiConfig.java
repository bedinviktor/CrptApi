package com.ismp.crpt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crptapi")
@Getter
@Setter
public class CrptApiConfig {
    private String url;
}

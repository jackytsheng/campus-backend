package com.rmr101.campus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(ignoreResourceNotFound = true, value = {
  "classpath:jwtBackup.properties",
  "classpath:jwt.properties"})
@ConfigurationProperties(prefix="jwt")
@Data
@Component
public class JWTConfig {
  private String privateKey;
}

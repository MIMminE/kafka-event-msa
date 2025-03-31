package dev.nuts.configdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "stream-to-kafka-service")
public class StreamToKafkaServiceConfig {
    private int minStreamIntervalMs;
    private int maxStreamIntervalMs;
    private String welcomeMessage;
    private SupportRunnerType activeModel;
    private Integer fixedCount; // activeModel이 fixed 일때만 사용

    public Integer getFixedCountIfActiveModelIsFixed() {
        if (SupportRunnerType.FIXED == activeModel) {
            return fixedCount;
        }
        return null;
    }
}

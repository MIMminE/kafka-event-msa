package dev.nuts.configdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String bootstrapServers;
    private String schemaRegistryUrl;
    private String schemaRegistryUrlKey;
    private String topicName;
    private Integer numOfPartitions;
    private Short replicationFactor; // 카프카 클라이언트 설정 API에서 Short으로 받고 있음
    private List<String> topicNamesToCreate;
}

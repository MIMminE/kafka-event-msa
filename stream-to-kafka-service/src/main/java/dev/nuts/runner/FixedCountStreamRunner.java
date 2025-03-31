package dev.nuts.runner;

import dev.nuts.config.StreamToKafkaServiceConfigData;
import dev.nuts.spec.StreamListener;
import dev.nuts.spec.StreamRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name="stream-to-kafka-service.active-model", havingValue = "fixed")
public class FixedCountStreamRunner implements StreamRunner {

    private final StreamToKafkaServiceConfigData configData;
    private final StreamListener listener;
    private final StreamHelper streamHelper;

    @Override
    public void start() {

    }
}

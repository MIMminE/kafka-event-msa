package dev.nuts.runner.impl;

import dev.nuts.listener.TestStreamListener;
import dev.nuts.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@ConditionalOnExpression("${stream-to-kafka-service.enable-v1-streamer}")
public class DemoStreamRunner implements StreamRunner {

    private final TestStreamListener listener;

    @Override
    public void start() {
        IntStream.range(1, 100).mapToObj(Integer::toString).forEach(listener::onMessage);
    }
}

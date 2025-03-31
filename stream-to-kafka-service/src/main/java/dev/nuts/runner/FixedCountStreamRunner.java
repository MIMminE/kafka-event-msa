package dev.nuts.runner;

import dev.nuts.configdata.StreamToKafkaServiceConfig;
import dev.nuts.spec.StreamListener;
import dev.nuts.spec.StreamRunner;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "stream-to-kafka-service.active-model", havingValue = "fixed")
@Slf4j
public class FixedCountStreamRunner implements StreamRunner {

    private final StreamToKafkaServiceConfig configData;
    private final StreamListener listener;
    private final StreamHelper streamHelper;
    private final Random random = new Random();

    @Override
    public void start() {
        Integer fixedCount = configData.getFixedCount();
        if (fixedCount == null) {
            throw new IllegalArgumentException("The FixedCount value is not set.");
        }

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("stream-to-kafka-service");
            return thread;
        });

        for (int i = 0; i < fixedCount; i++) {
            int randomMs = random.nextInt(configData.getMinStreamIntervalMs(), configData.getMaxStreamIntervalMs() + 1);
            ScheduledFuture<Boolean> scheduledFuture = executorService.schedule(() -> {
                listener.onStatus(streamHelper.getStatus());
                return true;
            }, randomMs, TimeUnit.MILLISECONDS);
            try {
                scheduledFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
        log.info("End the stream");
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying stream-to-kafka-service");
    }
}

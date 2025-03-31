package dev.nuts.runner;

import dev.nuts.configdata.StreamToKafkaServiceConfig;
import dev.nuts.spec.StreamListener;
import dev.nuts.spec.StreamRunner;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "stream-to-kafka-service.active-model", havingValue = "infinite")
public class InfiniteStreamRunner implements StreamRunner {

    private static final Logger log = LoggerFactory.getLogger(InfiniteStreamRunner.class);
    private final StreamToKafkaServiceConfig configData;
    private final StreamListener listener;
    private final StreamHelper streamHelper;
    private final Random random = new Random();

    @Override
    public void start() {
        startProcessing();
    }

    private void startProcessing() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("stream-to-kafka-service");
            return thread;
        });

        runNextTask(executorService);
    }

    private void runNextTask(ScheduledExecutorService executorService) {
        int randomMs = random.nextInt(configData.getMinStreamIntervalMs(), configData.getMaxStreamIntervalMs() + 1);

        executorService.schedule(() -> {
            try {
                listener.onStatus(streamHelper.getStatus());
                runNextTask(executorService);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                executorService.shutdown();
            }

        }, randomMs, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying stream-to-kafka-service");
    }
}

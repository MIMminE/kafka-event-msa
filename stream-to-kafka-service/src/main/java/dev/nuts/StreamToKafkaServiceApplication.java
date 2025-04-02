package dev.nuts;

import dev.nuts.spec.StreamInitializer;
import dev.nuts.spec.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class StreamToKafkaServiceApplication implements CommandLineRunner {

    private final StreamRunner runner;
    private final ApplicationContext applicationContext;
    private final StreamInitializer streamInitializer;

    public static void main(String[] args) {
        SpringApplication.run(StreamToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        log.info("Active Runner Model is [{}]", runner.getClass());
        streamInitializer.init();
        runner.start();
        log.info("App stops...");
        SpringApplication.exit(applicationContext);
    }
}




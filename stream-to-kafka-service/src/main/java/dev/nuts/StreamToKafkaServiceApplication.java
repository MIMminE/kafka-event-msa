package dev.nuts;

import dev.nuts.config.StreamToKafkaServiceConfigData;
import dev.nuts.spec.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class StreamToKafkaServiceApplication implements CommandLineRunner {

    private final StreamToKafkaServiceConfigData configData;
    private final StreamRunner runner;

    public static void main(String[] args) {
        SpringApplication.run(StreamToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        log.info("Active Runner Model is [{}]", runner.getClass());
        log.info(configData.getWelcomeMessage());
        runner.start();
    }
}




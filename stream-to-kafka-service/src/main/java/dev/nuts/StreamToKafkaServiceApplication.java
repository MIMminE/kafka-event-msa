package dev.nuts;

import dev.nuts.configdata.StreamToKafkaServiceConfig;
import dev.nuts.spec.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class StreamToKafkaServiceApplication implements CommandLineRunner {

    private final StreamToKafkaServiceConfig configData;
    private final StreamRunner runner;
    private final ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(StreamToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        log.info("Active Runner Model is [{}]", runner.getClass());
        log.info(configData.getWelcomeMessage());
        runner.start();
        log.info("App stops...");
        SpringApplication.exit(applicationContext);
    }
}




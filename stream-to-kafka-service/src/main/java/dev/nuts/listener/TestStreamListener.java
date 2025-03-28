package dev.nuts.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestStreamListener {
    public void onMessage(String message) {
        log.info("On Message {}", message);
    }

}

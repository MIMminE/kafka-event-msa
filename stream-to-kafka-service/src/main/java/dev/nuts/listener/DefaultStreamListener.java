package dev.nuts.listener;

import dev.nuts.spec.Status;
import dev.nuts.spec.StreamListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultStreamListener implements StreamListener {
    @Override
    public void onStatus(Status status) {
        log.info("[{}] On Status {}", status.getCreatedAt(), status.getText());
    }
}

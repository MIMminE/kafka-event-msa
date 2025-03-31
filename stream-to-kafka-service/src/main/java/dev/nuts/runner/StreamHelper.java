package dev.nuts.runner;


import dev.nuts.configdata.StreamHelperConfigData;
import dev.nuts.spec.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

/**
 * 스트림 Status 생성을 도와주는 헬퍼 클래스
 */
@Component
@RequiredArgsConstructor
public class StreamHelper {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final StreamHelperConfigData configData;
    private Long serialNumber = 0L;

    public Status getStatus() {
        return new Status() {

            @Override
            public Date getCreatedAt() {
                return new Date();
            }

            @Override
            public Long getId() {
                return serialNumber++;
            }

            @Override
            public String getText() {
                if (serialNumber < configData.getStreamContentSources().size()) {
                    String text = configData.getStreamContentSources().get(serialNumber.intValue());
                    serialNumber++;
                    return text;
                }
                Random random = new Random();
                int length = random.nextInt(configData.getMaxTextLength() - configData.getMinTextLength() + 1) + configData.getMinTextLength(); // N ~ M 사이의 길이 결정
                StringBuilder builder = new StringBuilder(length);

                for (int i = 0; i < length; i++) {
                    int randomIndex = random.nextInt(CHARACTERS.length());
                    builder.append(CHARACTERS.charAt(randomIndex));
                }

                return builder.toString();
            }
        };
    }
}

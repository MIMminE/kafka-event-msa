package dev.nuts.configdata;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "stream-to-kafka-service.helper")
public class StreamHelperConfigData {

    @NotNull(message = "minTextLength cannot be null")
    @Min(value = 1, message = "minTextLength must be at least 1")
    private Integer minTextLength;

    @NotNull(message = "maxTextLength cannot be null")
    @Max(value = 1000, message = "maxTextLength must be at most 1000")
    private Integer maxTextLength;

    private List<String> streamContentSources;

    @AssertTrue(message = "minTextLength must not be greater than maxTextLength")
    public boolean isMinLessThanOrEqualToMax() {
        return minTextLength == null || maxTextLength == null || minTextLength <= maxTextLength;
    }
}

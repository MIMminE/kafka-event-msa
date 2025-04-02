package dev.nuts.client;

import dev.nuts.configdata.KafkaConfigData;
import dev.nuts.configdata.RetryConfigData;
import dev.nuts.exception.KafkaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;        // Spring Webflux에서 사용하는 웹 클라이언트 ( 비동기 통신 기반 )

    public void createTopics() {
        CreateTopicsResult createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        log.info("Create topics result: {}", createTopicsResult.values().values());
        checkTopicsCreated();
    }

    /**
     * 카프카 토픽을 생성하는 메서드
     * 설정 정보에 있는 파티션 개수와 리플리케이션 팩터 설정을 사용한다.
     * 파티션 - 데이터 분산 , 병렬 처리를 위해 토픽을 나누는 물리적 저장 단위
     * 리플리케이션 팩터 - 데이터 안정성 및 내결합성을 위해 각 파티션를 복제하는 개수
     * <p>
     * RetryContext를 받아 재시도 횟수에 대한 로그를 출력하는 부분을 포함한다.
     */
    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        try {
            List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
            log.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
            List<NewTopic> kafkaTopics = topicNames.stream().map(topicName -> new NewTopic(
                    topicName.trim(),
                    kafkaConfigData.getNumOfPartitions(),
                    kafkaConfigData.getReplicationFactor()
            )).toList();
            return adminClient.createTopics(kafkaTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!", t);
            // 재시도 횟수만큼 재시도를 했지만 그럼에도 카프카 토픽 생성에 실패할 경우 발생하는 예외
        }
    }

    public void checkTopicsCreated() {
        List<TopicListing> topics = getTopics();

        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatusCode getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .retrieve().toBodilessEntity()
                    .map(ResponseEntity::getStatusCode)
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }


    private List<TopicListing> getTopics() {
        try {
            retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!", t);
        }
        return null;
    }

    private List<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        log.info("Reading kafka topic {}, attempt {}", kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> log.debug("Topic with name {}", topic.name()));
        }
        if (topics == null) return List.of();
        return topics.stream().toList();
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topics!!");
        }
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }
}

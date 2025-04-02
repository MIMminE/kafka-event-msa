package dev.nuts.service.impl;

import dev.nuts.kafka.avro.model.StreamAvroModel;
import dev.nuts.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Slf4j
@RequiredArgsConstructor
@Component
public class StreamKafkaProducer implements KafkaProducer<Long, StreamAvroModel> {

    private final KafkaTemplate<Long, StreamAvroModel> kafkaTemplate;

    @Override
    public void send(String topicName, Long key, StreamAvroModel message) {
        log.info("Sending message='{}' to topic='{}'", message, topicName);
        CompletableFuture<SendResult<Long, StreamAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
        addCallback(topicName, message, kafkaResultFuture);
    }

    private void addCallback(String topicName, StreamAvroModel message, CompletableFuture<SendResult<Long, StreamAvroModel>> kafkaResultFuture) {

        /**
         *  비동기 처리를 위한 CompletableFuture 를 이용해 작업이 성공했을때와 실패했을때의 로직을 구현해둔다.
         */
        kafkaResultFuture.thenAccept(result -> {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            System.nanoTime());
                })
                .exceptionally(throwable -> {
                    log.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
                    return null;
                });

    }
}

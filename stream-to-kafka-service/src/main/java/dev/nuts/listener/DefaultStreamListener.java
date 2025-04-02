package dev.nuts.listener;

import dev.nuts.configdata.KafkaConfigData;
import dev.nuts.kafka.avro.model.StreamAvroModel;
import dev.nuts.service.KafkaProducer;
import dev.nuts.spec.Status;
import dev.nuts.spec.StreamListener;
import dev.nuts.transformer.StreamStatusToAvroTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultStreamListener implements StreamListener {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, StreamAvroModel> kafkaProducer;
    private final StreamStatusToAvroTransformer transformer;

    @Override
    public void onStatus(Status status) {
        log.info("[{}] On Status {}", status.getCreatedAt(), status.getText());
        StreamAvroModel avroModel = transformer.getStreamAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), avroModel.getUserId(), avroModel);
    }
}

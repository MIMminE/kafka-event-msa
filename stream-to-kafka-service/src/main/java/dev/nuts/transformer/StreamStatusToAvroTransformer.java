package dev.nuts.transformer;

import dev.nuts.kafka.avro.model.StreamAvroModel;
import dev.nuts.spec.Status;
import org.springframework.stereotype.Component;

@Component
public class StreamStatusToAvroTransformer {

    public StreamAvroModel getStreamAvroModelFromStatus(Status status) {
        return StreamAvroModel.newBuilder()
                .setId(status.getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }
}

package dev.nuts.exception;

public class StreamToKafkaServiceException extends RuntimeException {
    public StreamToKafkaServiceException() {
    }

    public StreamToKafkaServiceException(String message) {
        super(message);
    }

    public StreamToKafkaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

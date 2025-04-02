package dev.nuts.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

/**
 * Kafka 메시지는 네트워크를 통해 전송되기 때문에, 메시지의 키는 반드시 직렬화 가능해야 합니다.
 * 명시적으로 시리얼라이저를 선언함으로써 컴파일 타임에 직렬화 가능하지 않은 타입의 경우 오류를 발생시키게 한다.
 *
 * 본문에 사용되는 SpecificRecordBase 는 Avro 라이브러리에 의해 생성된 자바 클래스의 부모 클래스이다.
 * 즉 Avro 스키마로 생성된 자바 클래스라면 모두 상속받은 클래스이다.
 */
public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
    void send(String topicName, K key, V message);
}

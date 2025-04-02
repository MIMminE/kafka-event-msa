package dev.nuts.config;

import dev.nuts.configdata.KafkaConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;


/**
 * @EnableRetry는 Spring Retry에서 사용되는 어노테이션으로 특정 작업이 실패했을 때
 * 자동으로 재시도할 수 있는 기능을 제공한다. 주로 카프카와 같은 외부 시스템과의 통신에서
 * 일시적인 장애 또는 네트워크 오류 등에 대응하기 위한 재시도 로직을 간단하게 구현할 수 있게 한다.
 * @Retryable를 사용하여 구성하거나 RetryTemplate 를 써서 수동으로 작업해주어야 한다.
 */
@EnableRetry // 한번 선언을 통해 전체 스프링 프로젝트에 전역적으로 적용된다.
@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    private final KafkaConfigData kafkaConfigData;


    /**
     * 카프카와 상호작용할 수 있는 클라이언트 객체, 토픽 생성, 삭제 및 조회 같은 작업 수행
     * create 정적 메서드를 통해 Kafka와 연결할 수 있는 클라이언트 객체 생성
     * BOOTSTRAP_SERVERS_CONFIG 는 카프카 클라이언트 설정에서 가장 기본이 되는 설정으로
     * 카프카 브로커의 주소를 지정하는 역할을 수행한다.
     * localhost:9002 와 같은 형식으로 브로커 주소를 설정한다.
     *
     * 부트 스트랩
     * :: 컴퓨터 과학에서 시스템을 초기화하거나 시작하는 과정을 의미한다.
     * 클라이언트가 카프카 클러스터에 연결하기 위해 사용하는 초기 브로커 주소를 말하며
     * 의미적으로 카프카 클라이언트가 부트스트랩(초기화 및 실행 준비)을 위해 필요한 브로커 주소
     */
    @Bean
    public AdminClient adminClient() {

        return AdminClient.create(Map.of(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers()
        ));
    }

}

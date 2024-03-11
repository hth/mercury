package com.github.hth.dataconsumer.config;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class KafkaReceiverConfig {

    @Value("${external.transaction.topic}")
    private String externalTransactionTopic;

    @Bean
    public ReceiverOptions<String, CreditTransactionDTO> receiverOptions(KafkaProperties kafkaProperties) {
        kafkaProperties.getProperties().put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, UUID.randomUUID().toString());
        log.info("Consumer Properties {}", kafkaProperties.getProperties());
        return ReceiverOptions.<String, CreditTransactionDTO>create(kafkaProperties.buildConsumerProperties())
            .subscription(List.of(externalTransactionTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> consumerTemplate(ReceiverOptions<String, CreditTransactionDTO> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}

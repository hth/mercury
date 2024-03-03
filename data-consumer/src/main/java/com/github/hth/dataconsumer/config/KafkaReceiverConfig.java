package com.github.hth.dataconsumer.config;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

/**
 * hitender
 * 1/28/24 8:07 PM
 */
@Configuration
public class KafkaReceiverConfig {

    @Value("${external.transaction.topic}")
    private String externalTransactionTopic;

    @Bean
    public ReceiverOptions<String, CreditTransactionDTO> receiverOptions(KafkaProperties kafkaProperties) {
        return ReceiverOptions.<String, CreditTransactionDTO>create(kafkaProperties.buildConsumerProperties())
            .subscription(List.of(externalTransactionTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> consumerTemplate(ReceiverOptions<String, CreditTransactionDTO> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}

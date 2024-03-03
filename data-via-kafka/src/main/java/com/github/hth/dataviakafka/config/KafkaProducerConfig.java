package com.github.hth.dataviakafka.config;

import com.github.hth.dataviakafka.dto.CreditTransactionDTO;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public SenderOptions<String, CreditTransactionDTO> senderOptions(KafkaProperties kafkaProperties) {
        return SenderOptions.create(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, CreditTransactionDTO> producerTemplate(SenderOptions<String, CreditTransactionDTO> senderOptions) {
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}

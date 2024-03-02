package com.github.hth.dataconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

/**
 * hitender
 * 1/28/24 8:40 PM
 */
@Service
@Slf4j
public class ThirdPartyTransactionReceiverRunner implements CommandLineRunner {

    private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;

    public ThirdPartyTransactionReceiverRunner(ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        reactiveKafkaConsumerTemplate.receive()
            .doOnNext(r -> log.info("Transaction received at topic={} key={} value={}",r.topic(), r.key(), r.value()))
            .doOnNext(r -> r.receiverOffset().acknowledge())
            .subscribe();
    }
}

/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.service;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import com.github.hth.dataconsumer.util.EntityToDtoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ThirdPartyTransactionReceiverRunner implements CommandLineRunner {

    private final ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> reactiveKafkaConsumerTemplate;
    private final CreditTransactionRepository creditTransactionRepository;
    private final Sinks.Many<CreditTransactionDTO> sinkOfCredit;
    private final Environment environment;

    @Value("${data.consumption.delay}")
    private int delayConsumptionFromKafkaStream;

    public ThirdPartyTransactionReceiverRunner(
            ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> reactiveKafkaConsumerTemplate,
            CreditTransactionRepository creditTransactionRepository,
            Sinks.Many<CreditTransactionDTO> sinkOfCredit,
            Environment environment
    ) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.creditTransactionRepository = creditTransactionRepository;
        this.sinkOfCredit = sinkOfCredit;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Consume data every {} seconds profile={}", delayConsumptionFromKafkaStream, environment.getActiveProfiles());
        reactiveKafkaConsumerTemplate.receive()
                /* Slow down consumption of data from Kafka Stream. */
                .delayElements(Duration.ofSeconds(delayConsumptionFromKafkaStream))
                .doOnNext(r -> log.info("Transaction received at topic={} key={} value={}", r.topic(), r.key(), r.value()))
                .doOnNext(r -> r.headers().forEach(header -> log.info("header key={} value={}", header.key(), new String(header.value()))))
                .publishOn(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()))
                .flatMap(r -> persistTransactionFromKafka(r).thenReturn(r))
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<CreditTransactionDTO> persistTransactionFromKafka(ReceiverRecord<String, CreditTransactionDTO> r) {
        return creditTransactionRepository.save(EntityToDtoUtil.convertDTOToEntity(r.value()))
                .map(EntityToDtoUtil::convertEntityToDTO)
                .doOnNext(sinkOfCredit::tryEmitNext)
                .doOnNext(c -> log.info("Saved... {} {} {} {}", c.getReceiverTagEnum(), c.getTransactionStatus(), c.getTransactionId(), Thread.currentThread().getName()));
    }

}

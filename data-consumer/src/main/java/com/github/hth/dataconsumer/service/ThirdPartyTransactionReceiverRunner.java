package com.github.hth.dataconsumer.service;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import com.github.hth.dataconsumer.util.EntityToDtoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

    @Value("${data.consumption.delay:10}")
    private int delayConsumptionFromKafkaStream;

    public ThirdPartyTransactionReceiverRunner(
            ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> reactiveKafkaConsumerTemplate,
            CreditTransactionRepository creditTransactionRepository,
            Sinks.Many<CreditTransactionDTO> sinkOfCredit
    ) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.creditTransactionRepository = creditTransactionRepository;
        this.sinkOfCredit = sinkOfCredit;
    }

    @Override
    public void run(String... args) throws Exception {
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

package com.github.hth.dataconsumer.service;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.entity.CreditTransactionEntity;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import com.github.hth.dataconsumer.util.EntityToDtoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@Slf4j
public class ThirdPartyTransactionReceiverRunner implements CommandLineRunner {

    private final ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> reactiveKafkaConsumerTemplate;
    private final CreditTransactionRepository creditTransactionRepository;

    public ThirdPartyTransactionReceiverRunner(
            ReactiveKafkaConsumerTemplate<String, CreditTransactionDTO> reactiveKafkaConsumerTemplate,
            CreditTransactionRepository creditTransactionRepository
    ) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.creditTransactionRepository = creditTransactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        reactiveKafkaConsumerTemplate.receive()
                .doOnNext(r -> log.info("Transaction received at topic={} key={} value={}", r.topic(), r.key(), r.value()))
                .doOnNext(r -> r.headers().forEach(header -> log.info("header key={} value={}", header.key(), new String(header.value()))))
                .flatMap(r -> persistTransactionFromKafka(r).thenReturn(r))
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<CreditTransactionEntity> persistTransactionFromKafka(ReceiverRecord<String, CreditTransactionDTO> r) {
        return creditTransactionRepository.save(EntityToDtoUtil.convertDTOToEntity(r.value()))
                .doOnNext(c -> log.info("Committed transaction {} {}", c.getReceiverTagEnum(), c.getTransactionId()));
    }

}

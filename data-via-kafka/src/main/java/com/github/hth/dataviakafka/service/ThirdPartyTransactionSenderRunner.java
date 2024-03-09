package com.github.hth.dataviakafka.service;

import com.github.hth.dataviakafka.dto.CreditTransactionDTO;
import com.github.hth.dataviakafka.enums.ReceiverTagEnum;
import com.github.hth.dataviakafka.enums.TransactionStatusEnum;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class ThirdPartyTransactionSenderRunner implements CommandLineRunner {

    @Value("${external.transaction.topic}")
    private String externalTransactionTopic;

    @Value("${data.generate.duration:10}")
    private int dataGenerateDurationInSeconds;
    private final Faker faker;
    private final ReactiveKafkaProducerTemplate<String, CreditTransactionDTO> reactiveKafkaProducerTemplate;

    public ThirdPartyTransactionSenderRunner(ReactiveKafkaProducerTemplate<String, CreditTransactionDTO> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        faker = new Faker();
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<SenderRecord<String, CreditTransactionDTO, String>> senderRecordFlux = createTransactions()
                .map(tx -> new ProducerRecord<>(externalTransactionTopic, tx.getTransactionId(), tx))
                .doOnNext(r -> log.info("Transaction sent from topic={} key={} value={}", r.topic(), r.key(), r.value()))
                .map(producerRecord -> SenderRecord.create(producerRecord, producerRecord.key()));

        Flux<SenderResult<String>> send = reactiveKafkaProducerTemplate.send(senderRecordFlux)
                .doOnNext(result -> log.info("offset={} partition={}", result.recordMetadata().offset(), result.recordMetadata().partition()));

        send.doOnComplete(reactiveKafkaProducerTemplate::close).subscribe();
    }

    private Flux<CreditTransactionDTO> createTransactions() {
        return  Flux.interval(Duration.ofSeconds(dataGenerateDurationInSeconds))
                .map(i -> generateRandom());
    }

    private CreditTransactionDTO generateRandom() {
        return CreditTransactionDTO.create(
                ReceiverTagEnum.KAFKA,
                UUID.randomUUID().toString(),
                faker.name().fullName(),
                faker.address().fullAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.country().name(),
                faker.country().countryCode2(),
                ThreadLocalRandom.current().nextInt(10, 100),
                LocalDateTime.now(),
                /* 90% transaction success rate. */
                ThreadLocalRandom.current().nextInt(0, 10) > 0 ? TransactionStatusEnum.SUCCESS : TransactionStatusEnum.FAILURE
        );
    }
}

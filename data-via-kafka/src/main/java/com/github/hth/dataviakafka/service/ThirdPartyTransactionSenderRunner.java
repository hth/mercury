package com.github.hth.dataviakafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.Duration;

@Service
@Slf4j
public class ThirdPartyTransactionSenderRunner implements CommandLineRunner {

    @Value("${external.transaction.topic}")
    private String externalTransactionTopic;

    @Value("${data.generate.duration:10}")
    private int dataGenerateDurationInSeconds;

    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;

    public ThirdPartyTransactionSenderRunner(ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<SenderRecord<String, String, String>> senderRecordFlux = Flux.interval(Duration.ofSeconds(dataGenerateDurationInSeconds))
                .map(i -> new ProducerRecord<>(externalTransactionTopic, String.valueOf(i), "transaction-" + i))
                .doOnNext(r -> log.info("Transaction sent from topic={} key={} value={}",r.topic(), r.key(), r.value()))
                .map(producerRecord -> SenderRecord.create(producerRecord, producerRecord.key()));

        Flux<SenderResult<String>> send = reactiveKafkaProducerTemplate.send(senderRecordFlux);
        send.doOnComplete(reactiveKafkaProducerTemplate::close).subscribe();
    }
}

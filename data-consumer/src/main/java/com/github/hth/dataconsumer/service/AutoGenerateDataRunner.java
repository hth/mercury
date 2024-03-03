package com.github.hth.dataconsumer.service;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.enums.ReceiverTagEnum;
import com.github.hth.dataconsumer.enums.TransactionStatusEnum;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import com.github.hth.dataconsumer.util.EntityToDtoUtil;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.providers.base.Country;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class AutoGenerateDataRunner implements CommandLineRunner {

    private final CreditTransactionRepository creditTransactionRepository;
    private final Faker faker;

    @Value("${data.generate.duration:10}")
    private int dataGenerateDurationInSeconds;

    public AutoGenerateDataRunner(CreditTransactionRepository creditTransactionRepository) {
        this.creditTransactionRepository = creditTransactionRepository;
        faker = new Faker();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Clean database & start populating data every {} seconds", dataGenerateDurationInSeconds);
        Mono<Void> voidMono = creditTransactionRepository.deleteAll();
        Flux<CreditTransactionDTO> creditTransactionEntityFlux = Flux.interval(Duration.ofSeconds(dataGenerateDurationInSeconds))
                .map(i -> generateRandom())
                .map(EntityToDtoUtil::convertDTOToEntity)
                .publishOn(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()))
                .flatMap(creditTransactionRepository::save)
                .map(EntityToDtoUtil::convertEntityToDTO)
                .doOnNext(credit -> log.info("Saved... {} {}", credit, Thread.currentThread().getName()));

        Flux.concat(voidMono.and(creditTransactionEntityFlux)).subscribe();
    }

    private CreditTransactionDTO generateRandom() {
        Country country = faker.country();
        return CreditTransactionDTO.create(
                ReceiverTagEnum.OWN,
                UUID.randomUUID(),
                faker.name().fullName(),
                faker.address().fullAddress(),
                faker.phoneNumber().phoneNumber(),
                country.name(),
                country.countryCode2(),
                ThreadLocalRandom.current().nextInt(10, 100),
                LocalDateTime.now(),
                /* 90% transaction success rate. */
                ThreadLocalRandom.current().nextInt(0, 10) > 0 ? TransactionStatusEnum.SUCCESS : TransactionStatusEnum.FAILURE
        );
    }
}
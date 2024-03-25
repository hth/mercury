/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.datacsv.scheduler;

import com.github.hth.datacsv.dto.CreditTransactionDTO;
import com.github.hth.datacsv.enums.ReceiverTagEnum;
import com.github.hth.datacsv.enums.TransactionStatusEnum;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class ThirdPartyTransactionSenderScheduler {
    @Value("${data.generate.limitRecords:10}")
    private int limitRecords;

    @Value("${spring.aws.bucket}")
    private String bucketName;

    @Value("${data.file.prefix}")
    private String filePrefix;

    @Value("${data.csv.delimiter}")
    private String delimiterCSV;

    private final Faker faker;
    private final S3Client s3Client;

    @Autowired
    public ThirdPartyTransactionSenderScheduler(S3Client s3Client) {
        this.s3Client = s3Client;
        this.faker = new Faker();
    }

    @Scheduled(fixedRateString = "${schedule.upload.csv.in.milliseconds}", initialDelay = 60000)
    public void uploadFile() {
        log.info("Started file upload scheduled process");
        String stringBuilder = IntStream.range(0, limitRecords)
                .mapToObj(i -> generateRandom().asRecordForCSV(delimiterCSV) + System.lineSeparator())
                .collect(Collectors.joining("", "TAG" + delimiterCSV + "TX" + delimiterCSV + "NAME" + delimiterCSV + "ADDRESS" + delimiterCSV + "PHONE_NUMBER" + delimiterCSV + "COUNTRY" + delimiterCSV + "COUNTRY_CODE" + delimiterCSV + "AMOUNT" + delimiterCSV + "DATE" + delimiterCSV + "STATUS" + System.lineSeparator(), ""));

        log.debug(stringBuilder);
        try {
            File tmpFile = File.createTempFile(filePrefix, ".csv", new File(System.getProperty("java.io.tmpdir")));
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(Objects.requireNonNull(stringBuilder));
            writer.close();

            putS3Object(tmpFile);
        } catch (IOException e) {
            log.error("Error {}", e.getMessage(), e);
        }
    }

    private void putS3Object(File tmpFile) {
        try {
            LocalDateTime l = LocalDateTime.now();
            String fileName = l.getYear() + "-" + l.getMonthValue() + "-" + l.getDayOfMonth() + "-" + l.getHour() + "-" + l.getMinute() + "-" + l.getSecond();
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-credit-bank", "CSV");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .contentType("text/csv")
                    .bucket(bucketName)
                    .key(filePrefix + "-" + fileName + ".csv")
                    .metadata(metadata)
                    .build();

            s3Client.putObject(putOb, RequestBody.fromFile(tmpFile));
            log.info("Uploaded {} {} into bucket {}", tmpFile.getName(), putOb.key(), bucketName);
        } catch (S3Exception e) {
            log.error("S3 upload message={}", e.getMessage(), e);
        }
    }

    private CreditTransactionDTO generateRandom() {
        return CreditTransactionDTO.create(
                ReceiverTagEnum.CSV,
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

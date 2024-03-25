/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.scheduler;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.enums.ReceiverTagEnum;
import com.github.hth.dataconsumer.enums.TransactionStatusEnum;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import com.github.hth.dataconsumer.util.EntityToDtoUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class CSVFromS3Scheduler {

    @Value("${spring.aws.bucket}")
    private String bucketName;

    @Value("${data.file.prefix}")
    private String filePrefix;

    private final S3Client s3Client;
    private final CreditTransactionRepository creditTransactionRepository;
    private final Sinks.Many<CreditTransactionDTO> sinkOfCredit;
    private final Sinks.Many<CreditTransactionDTO> successCreditSink;
    private final Sinks.Many<CreditTransactionDTO> failureCreditSink;

    @Autowired
    public CSVFromS3Scheduler(
            S3Client s3Client,
            CreditTransactionRepository creditTransactionRepository,
            @Qualifier("sinkOfCredit")
            Sinks.Many<CreditTransactionDTO> sinkOfCredit,
            @Qualifier("successSinkOfCredit")
            Sinks.Many<CreditTransactionDTO> successCreditSink,
            @Qualifier("failureCreditSink")
            Sinks.Many<CreditTransactionDTO> failureCreditSink
    ) {
        this.s3Client = s3Client;
        this.creditTransactionRepository = creditTransactionRepository;
        this.sinkOfCredit = sinkOfCredit;
        this.successCreditSink = successCreditSink;
        this.failureCreditSink = failureCreditSink;
    }

    @Scheduled(fixedRateString = "${schedule.download.csv.in.milliseconds}", initialDelay = 6000)
    public void downloadFile() {
        log.info("Started scheduled download process");
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

        List<S3Object> contents = listObjectsV2Response.contents();
        log.info("Number of objects in the bucket: {}", contents.size());
        contents.stream()
                .map(s3Object -> processCSV(s3Object.key()))
                .skip(1) //Keeping Just one file in S3 and deleting all other files
                .forEach(this::delete);
    }

    public String processCSV(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> responseResponseBytes = s3Client.getObjectAsBytes(objectRequest);
        byte[] data = responseResponseBytes.asByteArray();

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(filePrefix, ".csv", new File(System.getProperty("java.io.tmpdir")));
            OutputStream os = new FileOutputStream(tmpFile);
            os.write(data);
            log.info("Successfully obtained bytes from an S3 object {} {}", key, tmpFile.getName());
            os.close();
        } catch (IOException e) {
            log.error("Error {}", e.getMessage(), e);
        }

        List<CreditTransactionDTO> creditTransactions = parseData(tmpFile);
        Flux<CreditTransactionDTO> creditTransactionEntityFlux = Flux.fromIterable(creditTransactions)
                .map(EntityToDtoUtil::convertDTOToEntity)
                .publishOn(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()))
                .flatMap(creditTransactionRepository::save)
                .map(EntityToDtoUtil::convertEntityToDTO)
                .doOnNext(sinkOfCredit::tryEmitNext)
                .doOnNext(c -> log.info("Saved... {} {} {} {}", c.getReceiverTagEnum(), c.getTransactionStatus(), c.getTransactionId(), Thread.currentThread().getName()));

        Flux<Sinks.EmitResult> filterResultToSpecificSink = creditTransactionEntityFlux.map(creditTransactionDTO -> switch (creditTransactionDTO.getTransactionStatus()) {
            case SUCCESS -> successCreditSink.tryEmitNext(creditTransactionDTO);
            case FAILURE -> failureCreditSink.tryEmitNext(creditTransactionDTO);
        });

        Flux.concat(creditTransactionEntityFlux, filterResultToSpecificSink).subscribe();
        return key;
    }

    public List<CreditTransactionDTO> parseData(File file) {
        List<CreditTransactionDTO> results = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            for (String[] row : allData) {
                for (String cell : row) {
                    String[] data = cell.split(";");
                    CreditTransactionDTO creditTransactionDTO = CreditTransactionDTO.create(
                            ReceiverTagEnum.valueOf(data[0]),
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            Integer.parseInt(data[7]),
                            LocalDateTime.parse(data[8]),
                            TransactionStatusEnum.valueOf(data[9])
                    );
                    results.add(creditTransactionDTO);
                    log.info("{}", creditTransactionDTO);
                }
            }

            log.info("{}", results);
        } catch (Exception e) {
            log.error("Error reading csv file {} {}", file.getName(), e.getMessage(), e);
        }

        return results;
    }

    public void delete(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("Delete file from {} {}", bucketName, key);
    }
}

package com.github.hth.dataconsumer.scheduler;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import com.github.hth.dataconsumer.repository.CreditTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Sinks;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CSVFromS3SchedulerTest {

    private CSVFromS3Scheduler csvFromS3Scheduler;
    @MockBean S3Client s3Client;
    @MockBean CreditTransactionRepository creditTransactionRepository;
    @MockBean Sinks.Many<CreditTransactionDTO> sinkOfCredit;
    @MockBean Sinks.Many<CreditTransactionDTO> successCreditSink;
    @MockBean Sinks.Many<CreditTransactionDTO> failureCreditSink;

    @BeforeEach
    void setUp() {
        csvFromS3Scheduler = new CSVFromS3Scheduler(
                s3Client,
                creditTransactionRepository,
                sinkOfCredit,
                successCreditSink,
                failureCreditSink
        );
    }

    @Test
    void parseData() throws FileNotFoundException {
        final File file = ResourceUtils.getFile("classpath:csv/Crd-Bnk-2024-3-12-18-42-33.csv");
        List<CreditTransactionDTO> results = csvFromS3Scheduler.parseData(file);
        assertEquals(10, results.size());
    }
}
/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.config;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class StreamConfig {

    private final Sinks.Many<CreditTransactionDTO> creditSink = Sinks.many().multicast().onBackpressureBuffer(2);
    private final Sinks.Many<CreditTransactionDTO> successCreditSink = Sinks.many().multicast().onBackpressureBuffer(2);
    private final Sinks.Many<CreditTransactionDTO> failureCreditSink = Sinks.many().multicast().onBackpressureBuffer(2);

    @Bean
    public Sinks.Many<CreditTransactionDTO> sinkOfCredit() {
        return creditSink;
    }

    @Bean
    public Sinks.Many<CreditTransactionDTO> successSinkOfCredit() {
        return successCreditSink;
    }

    @Bean
    public Sinks.Many<CreditTransactionDTO> failureCreditSink() {
        return failureCreditSink;
    }
}

package com.github.hth.dataconsumer.controller;

import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/")
public class StreamCreditController {

    private final Sinks.Many<CreditTransactionDTO> sinkOfCredit;
    private final Sinks.Many<CreditTransactionDTO> successSinkOfCredit;
    private final Sinks.Many<CreditTransactionDTO> failureCreditSink;

    public StreamCreditController(
            @Qualifier("sinkOfCredit")
            Sinks.Many<CreditTransactionDTO> sinkOfCredit,

            @Qualifier("successSinkOfCredit")
            Sinks.Many<CreditTransactionDTO> successSinkOfCredit,

            @Qualifier("failureCreditSink")
            Sinks.Many<CreditTransactionDTO> failureCreditSink
    ) {
        this.sinkOfCredit = sinkOfCredit;
        this.successSinkOfCredit = successSinkOfCredit;
        this.failureCreditSink= failureCreditSink;
    }

    @GetMapping(value = "credit/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CreditTransactionDTO> streamBookedOrders() {
        return sinkOfCredit.asFlux();
    }

    @GetMapping(value = "successCredit/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CreditTransactionDTO> streamSuccessBookedOrder() {
        return successSinkOfCredit.asFlux();
    }

    @GetMapping(value = "failureCredit/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CreditTransactionDTO> streamFailureBookedOrder() {
        return failureCreditSink.asFlux();
    }
}

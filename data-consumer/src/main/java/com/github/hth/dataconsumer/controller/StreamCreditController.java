package com.github.hth.dataconsumer.controller;


import com.github.hth.dataconsumer.dto.CreditTransactionDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/")
public class StreamCreditController {

    private final Sinks.Many<CreditTransactionDTO> creditSink;

    public StreamCreditController(Sinks.Many<CreditTransactionDTO> creditSink) {
        this.creditSink = creditSink;
    }

    @GetMapping(value = "credit/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CreditTransactionDTO> streamBook() {
        return creditSink.asFlux();
    }
}

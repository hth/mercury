package com.github.hth.dataai.controller;

import com.github.hth.dataai.dto.LlamaResponse;
import com.github.hth.dataai.service.LlamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class LlamaController {
    private final Logger log = LoggerFactory.getLogger(LlamaController.class);
    private final LlamaService llamaService;

    public LlamaController(LlamaService llamaService) {
        this.llamaService = llamaService;
    }

    /**
     * Json Response.
     * @param message - Joke on message
     * @return ResponseEntity<Map<String, String>>
     */
    @GetMapping("ll/generateJoke")
    public ResponseEntity<Map<String, String>> generateJoke(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        LlamaResponse llamaResponse = llamaService.generateJoke(message);
        log.info("Generate Joke {} {}", message, llamaResponse);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("generation", llamaResponse.getMessage()));
    }

    @GetMapping("ll/joke/{topic}")
    public ResponseEntity<LlamaResponse> joke(@PathVariable("topic") String topic) {
        LlamaResponse llamaResponse = llamaService.generateJoke(topic);
        log.info("Message Joke {} {}", topic, llamaResponse);
        return ResponseEntity.status(HttpStatus.OK).body(llamaResponse);
    }

    @GetMapping(value = "ll/joke/{topic}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> jokeStream(@PathVariable("topic") String topic) {
        return llamaService.generateJokeStream(topic);
    }

    @GetMapping("ll/prompt")
    public ResponseEntity<LlamaResponse> prompt(@RequestParam(value = "promptMessage", defaultValue = "Why is the sky blue?") String promptMessage) {
        final LlamaResponse llamaResponse = llamaService.generatePrompt(promptMessage);
        return ResponseEntity.status(HttpStatus.OK).body(llamaResponse);
    }

    @GetMapping(value = "ll/promptStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> promptStream(@RequestParam(value = "promptMessage", defaultValue = "Tell me a joke") String message) {
        return llamaService.generatePromptStream(message);
    }
}
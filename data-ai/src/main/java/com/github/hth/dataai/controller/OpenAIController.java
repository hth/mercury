package com.github.hth.dataai.controller;

import com.github.hth.dataai.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OpenAIController {
    private static final Logger log = LoggerFactory.getLogger(OpenAIController.class);
    private final OpenAIService openAiService;

    public OpenAIController(OpenAIService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping(value = {"ai/joke/{topic}", "/"})
    public String getJoke(@PathVariable(required = false) Optional<String> topic) {
        log.info("Generate joke on topic {}", topic);
        return openAiService.getJoke(topic.orElse("cat"));
    }
}

package com.github.hth.dataai.controller;

import com.github.hth.dataai.service.AIJokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AIJokeController {
    private static final Logger log = LoggerFactory.getLogger(AIJokeController.class);
    private final AIJokeService aiJokeService;

    public AIJokeController(AIJokeService aiJokeService) {
        this.aiJokeService = aiJokeService;
    }

    @GetMapping(value = {"joke/{topic}", "/"})
    public String getJoke(@PathVariable(required = false) Optional<String> topic) {
        log.info("Generate joke on topic {}", topic);
        return aiJokeService.getJoke(topic.orElse("cat"));
    }
}

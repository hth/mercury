package com.github.hth.dataai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LlamaResponse {
    private String message;
    private LocalDateTime start = LocalDateTime.now();
    private String duration;

    public LlamaResponse computeDuration() {
        duration = Duration.between(start, LocalDateTime.now()).toSeconds() + " seconds";
        return this;
    }
}

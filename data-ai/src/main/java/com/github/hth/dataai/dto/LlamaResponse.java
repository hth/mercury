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
    private LocalDateTime end = LocalDateTime.now();
    private long duration = Duration.between(start, end).toSeconds();
}

package com.github.hth.dataai.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorMessage {
    private int statusCode;
    private LocalDateTime localDateTime;
    private String message;
    private String description;
}

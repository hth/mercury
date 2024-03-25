package com.github.hth.dataai.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@RestControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionTranslator.class);

    public static final String OPEN_AI_CLIENT_RAISED_EXCEPTION = "Open AI client raised exception";

    @ExceptionHandler(OpenAiApiClientErrorException.class)
    ProblemDetail handleOpenAiClientErrorException(OpenAiApiClientErrorException ex) {
        log.error("Error clientError message {}", ex.getMessage());
        HttpStatus status = Optional
                .ofNullable(HttpStatus.resolve(Integer.parseInt(ex.getMessage().substring(0, 3))))
                .orElse(HttpStatus.BAD_REQUEST);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(OPEN_AI_CLIENT_RAISED_EXCEPTION);
        return problemDetail;
    }

    @ExceptionHandler(NonTransientAiException.class)
    ResponseEntity<String> handleOpenAiNonTransientAiException(NonTransientAiException ex) {
        log.error("Error nonTransient message {}", ex.getMessage());
        HttpStatus httpStatus = Optional
                .ofNullable(HttpStatus.resolve(Integer.parseInt(ex.getMessage().substring(0, 3))))
                .orElse(HttpStatus.BAD_REQUEST);

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getMessage());
    }
}

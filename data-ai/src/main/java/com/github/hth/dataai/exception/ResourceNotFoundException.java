package com.github.hth.dataai.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1025634527710536056L;

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}

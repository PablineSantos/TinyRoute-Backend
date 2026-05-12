package com.url.tinyroute.exception;

import org.springframework.http.HttpStatus;

public class DataConflictException extends BusinessException {
    public DataConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
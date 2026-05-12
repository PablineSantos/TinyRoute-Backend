package com.url.tinyroute.exception;

import org.springframework.http.HttpStatus;

public class ExpiredUrlException extends BusinessException {
    public ExpiredUrlException(String message) {
        super(message, HttpStatus.GONE);
    }
}
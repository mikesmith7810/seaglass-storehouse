package com.mike.stagerhub.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(final String message) {
        super(message);
    }
}

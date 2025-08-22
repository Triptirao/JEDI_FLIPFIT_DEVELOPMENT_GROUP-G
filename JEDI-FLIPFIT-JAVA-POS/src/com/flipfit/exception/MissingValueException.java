package com.flipfit.exception;

/**
 * Thrown when an expected value is null or missing.
 */
public class MissingValueException extends RuntimeException {

    public MissingValueException(String message) {
        super(message);
    }
}

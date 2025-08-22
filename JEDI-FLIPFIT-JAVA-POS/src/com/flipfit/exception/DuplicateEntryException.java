package com.flipfit.exception;

/**
 * Custom exception to be thrown when an attempt is made to create a duplicate entry.
 */
public class DuplicateEntryException extends RuntimeException {

    /**
     * Constructs a new DuplicateEntryException with a default detail message.
     */
    public DuplicateEntryException() {
        super("Duplicate entry found. The record already exists.");
    }

    /**
     * Constructs a new DuplicateEntryException with the specified detail message.
     *
     * @param message The detail message.
     */
    public DuplicateEntryException(String message) {
        super(message);
    }
}

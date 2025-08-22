package com.flipfit.exception;

/**
 * Custom exception to be thrown when an index is out of bounds.
 */
public class IndexOutOfBoundsException extends RuntimeException {

    /**
     * Constructs a new IndexOutOfBoundsException with a default detail message.
     */
    public IndexOutOfBoundsException() {
        super("Index is out of bounds.");
    }

    /**
     * Constructs a new IndexOutOfBoundsException with the specified detail message.
     *
     * @param message The detail message.
     */
    public IndexOutOfBoundsException(String message) {
        super(message);
    }
}
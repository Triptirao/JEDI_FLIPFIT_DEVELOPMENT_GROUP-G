package com.flipfit.exception;

/**
 * Thrown when an attempt to delete a user and its related data fails.
 * This is useful for complex deletion operations that involve multiple
 * database tables, providing a clear and specific error message.
 */
public class UnableToDeleteUserException extends RuntimeException {

    public UnableToDeleteUserException(String message) {
        super(message);
    }

    public UnableToDeleteUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
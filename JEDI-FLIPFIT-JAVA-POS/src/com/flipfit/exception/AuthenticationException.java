package com.flipfit.exception;

/**
 * Custom exception to be thrown when a user's authentication credentials
 * (e.g., username, password) are invalid or incorrect.
 * This is an unchecked exception, meaning it does not need to be declared
 * in a method's `throws` clause.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructs an AuthenticationException with no detail message.
     */
    public AuthenticationException() {
        super();
    }

    /**
     * Constructs an AuthenticationException with the specified detail message.
     *
     * @param message The detail message, providing more information about the authentication failure.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs an AuthenticationException with the specified detail message and cause.
     * This is useful for wrapping another exception that led to the authentication failure.
     *
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
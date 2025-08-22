package com.flipfit.exception;

/**
 * Custom exception to be thrown when a user attempts to access a resource or
 * perform an action for which they do not have the necessary permissions.
 * This is an unchecked exception, meaning it does not need to be declared in a method's
 * `throws` clause.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs an AccessDeniedException with no detail message.
     */
    public AccessDeniedException() {
        super();
    }

    /**
     * Constructs an AccessDeniedException with the specified detail message.
     *
     * @param message The detail message, providing more information about the access failure.
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructs an AccessDeniedException with the specified detail message and cause.
     * This is useful for wrapping another exception that led to the access denial.
     *
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.flipfit.exception;

/**
 * Thrown when user input does not conform to the expected format or type.
 * This can be used as a custom wrapper for exceptions like InputMismatchException.
 */
public class MismatchinputException extends RuntimeException {

    /**
     * Constructs an InvalidInputException with a detailed message.
     * @param message The detail message.
     */
    public MismatchinputException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidInputException with a detailed message and the original cause.
     * @param message The detail message.
     * @param cause The cause of the exception (e.g., InputMismatchException).
     */
    public MismatchinputException(String message, Throwable cause) {
        super(message, cause);
    }
}

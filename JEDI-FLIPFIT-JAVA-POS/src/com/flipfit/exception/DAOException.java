package com.flipfit.exception;

/**
 * Custom exception to be thrown for all database-related errors in the DAO layer.
 * This is an unchecked exception, which allows for cleaner code by not
 * requiring a `throws` clause in every method signature.
 */
public class DAOException extends RuntimeException {

    /**
     * Constructs a new DAOException with the specified detail message.
     * @param message The detail message, providing more information about the database failure.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructs a new DAOException with the specified detail message and cause.
     * This is useful for wrapping a lower-level exception (like SQLException)
     * and providing more context for debugging.
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.flipfit.exception;

public class HandleException {

    /**
     * This method provides a general way to handle a Throwable, which is the superclass of all errors and exceptions.
     * It prints the stack trace and a custom error message to the console.
     * @param e The Throwable object to handle.
     * @param customMessage A custom message to print along with the stack trace.
     */
    public static void handle(Throwable e, String customMessage) {
        System.err.println("An error occurred: " + customMessage);
        e.printStackTrace();
    }

    /**
     * Overloaded method to handle an exception without a custom message.
     * It prints a default message and the stack trace.
     * @param e The Exception object to handle.
     */
    public static void handle(Exception e) {
        System.err.println("An unexpected exception occurred.");
        e.printStackTrace();
    }
}
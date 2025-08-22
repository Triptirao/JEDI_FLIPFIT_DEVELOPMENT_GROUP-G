package com.flipfit.bean;

/**
 * Represents a notification sent to a user within the FlipFit application.
 * This class encapsulates the message content, recipient's email, and read status
 * of a notification.
 */
public class Notification {

    /**
     * The main content or body of the notification message.
     */
    private String message;

    /**
     * The email address of the user who is the intended recipient of the notification.
     */
    private String recipientEmail;

    /**
     * The read status of the notification. True if the notification has been read,
     * false otherwise.
     */
    private boolean isRead;

    /**
     * Constructs a new Notification object with a default unread status.
     */
    public Notification() {
        this.isRead = false; // A sensible default
    }

    /**
     * Constructs a new Notification object with a specific message and recipient,
     * defaulting to an unread status.
     *
     * @param message The content of the notification message.
     * @param recipientEmail The email of the recipient user.
     */
    public Notification(String message, String recipientEmail) {
        this.message = message;
        this.recipientEmail = recipientEmail;
        this.isRead = false;
    }

    /**
     * Gets the message content of the notification.
     *
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the email address of the notification's recipient.
     *
     * @return The recipient's email string.
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * Checks if the notification has been marked as read.
     *
     * @return True if the notification is read, false otherwise.
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the message content of the notification.
     *
     * @param message The new message string to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the email address of the notification's recipient.
     *
     * @param recipientEmail The new recipient email string to set.
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    /**
     * Sets the read status of the notification.
     *
     * @param read The boolean value to set the read status to.
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Marks the notification as read. This is a business logic method that
     * sets the internal `isRead` flag to true.
     */
    public void markAsRead() {
        this.isRead = true;
    }
}
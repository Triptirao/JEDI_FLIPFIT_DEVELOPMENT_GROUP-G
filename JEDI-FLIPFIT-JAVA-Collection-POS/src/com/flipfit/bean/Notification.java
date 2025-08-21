package com.flipfit.bean;

public class Notification {
    private String message;
    private String recipientEmail;
    private boolean isRead;

    // Default constructor
    public Notification() {
        this.isRead = false; // A sensible default
    }

    // Parameterized constructor
    public Notification(String message, String recipientEmail) {
        this.message = message;
        this.recipientEmail = recipientEmail;
        this.isRead = false;
    }

    // Getter methods
    public String getMessage() {
        return message;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public boolean isRead() {
        return isRead;
    }

    // Setter methods
    public void setMessage(String message) {
        this.message = message;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    // Setter for read status
    public void setRead(boolean read) {
        isRead = read;
    }

    // Business logic method
    public void markAsRead() {
        this.isRead = true;
    }
}
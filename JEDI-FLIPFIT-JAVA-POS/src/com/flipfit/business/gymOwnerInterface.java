package com.flipfit.business;

public interface gymOwnerInterface {
    public void addCentre(String[] gymData);
    public void viewGymDetails(String ownerId);
    public void viewCustomers();
    public void viewPayments();
    public void editGymOwnerDetails(String ownerId, int choice, String newValue);
    public void displayGymOwnerMenu(String loggedInOwnerId);
}

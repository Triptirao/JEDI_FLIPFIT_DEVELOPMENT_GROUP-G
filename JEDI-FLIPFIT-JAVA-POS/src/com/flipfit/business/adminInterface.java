package com.flipfit.business;

public interface adminInterface{
    public void approveGymRequest(String gymId);
    public void approveGymOwnerRequest(String email);
    public void viewPendingGyms();
    public void viewPendingGymOwners();
    public void viewAllGyms();
    public void viewAllGymOwners();
    public void viewAllCustomers();
    public void deleteUserById(int userId);
    public void deleteGymById(int gymId);
}

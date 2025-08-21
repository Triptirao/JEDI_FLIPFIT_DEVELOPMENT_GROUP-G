package com.flipfit.business;

public interface authenticationInterface {
    public String[] login(String email, String password);
    public void registerCustomer(String name, String email, String password, String phone);
    public void registerGymOwner(String name, String email, String password, String phone, String aadhaar, String pan, String gst);

}

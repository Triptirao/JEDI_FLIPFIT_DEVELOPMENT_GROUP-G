package com.flipfit.business;

public interface authenticationInterface {
    public String[] login(String email, String password);
    public void registerCustomer(String name, String email, String password, long phone, String city, int pincode, int paymentType, String paymentInfo);
    public void registerGymOwner(String name, String email, String password, long phone, String aadhaar, String pan, String gst);

}

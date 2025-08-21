package com.flipfit.business;

import com.flipfit.bean.Booking;

import java.util.List;

public interface customerInterface {
    public List<Booking> viewBookedSlots(String customerId);
    public List<String[]> viewCenters();
    public void bookSlot(int bookingId, int customerId, int slotId, int centreId);
    public void makePayments(int paymentType, String paymentInfo);
    public void editCustomerDetails(String customerId, int choice, String newValue);
}

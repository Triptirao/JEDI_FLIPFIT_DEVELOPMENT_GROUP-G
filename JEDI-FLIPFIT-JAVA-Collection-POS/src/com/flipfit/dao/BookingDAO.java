package com.flipfit.dao;

import com.flipfit.bean.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final List<Booking> bookings = new ArrayList<>();

    public static void bookSlot(Booking booking) {
        bookings.add(booking);
        System.out.println("Booking with ID " + booking.getBookingId() + " successfully saved.");
    }

    public static Booking getBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    public static List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    // New method to fetch bookings by customer ID
    public static List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomerId() == customerId) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }
}
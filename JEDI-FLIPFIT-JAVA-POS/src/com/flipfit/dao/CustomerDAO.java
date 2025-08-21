package com.flipfit.dao;

import com.flipfit.bean.Booking;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // This DAO now handles booking data directly.
    private static final List<Booking> bookings = new ArrayList<>();

    // Static initializer block to populate some initial data for demonstration.
    static {
        // Sample bookings
        bookings.add(new Booking(101, 1, 1, 1, "BOOKED", null, null));
        bookings.add(new Booking(102, 1, 2, 3, "BOOKED", null, null));
        bookings.add(new Booking(103, 2, 3, 2, "BOOKED", null, null));
    }

    /**
     * Saves a new booking to the list.
     * @param booking The Booking object to be saved.
     */
    public void bookSlot(Booking booking) {
        bookings.add(booking);
        System.out.println("Booking with ID " + booking.getBookingId() + " successfully saved.");
    }

    /**
     * Retrieves a list of bookings for a specific customer.
     * @param customerId The ID of the customer.
     * @return A list of Booking objects associated with the customer.
     */
    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomerId() == customerId) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }

    /**
     * Retrieves a booking by its ID.
     * @param bookingId The ID of the booking.
     * @return The Booking object if found, otherwise null.
     */
    public Booking getBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    /**
     * Retrieves all bookings.
     * @return A list of all Booking objects.
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
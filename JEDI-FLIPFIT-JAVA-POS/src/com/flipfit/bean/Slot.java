package com.flipfit.bean;

import java.time.LocalTime;

/**
 * The {@code Slot} class represents a specific time slot available at a gym for booking.
 * It's a **Plain Old Java Object (POJO)** that holds details about a slot,
 * including its ID, associated gym, start and end times, total capacity,
 * and the current number of bookings.
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-22
 */
public class Slot {

    /**
     * The unique identifier for the slot.
     */
    private int slotId;

    /**
     * The unique identifier of the gym to which this slot belongs.
     */
    private int gymId;

    /**
     * The start time of the slot.
     */
    private LocalTime startTime;

    /**
     * The end time of the slot.
     */
    private LocalTime endTime;

    /**
     * The maximum number of customers that can book this slot.
     */
    private int capacity;

    /**
     * The current number of customers who have booked this slot.
     */
    private int bookedCount;

    /**
     * Constructs a new, empty {@code Slot} object.
     * This constructor is useful for creating an instance before setting its properties.
     */
    public Slot() {
    }

    /**
     * Constructs a new {@code Slot} object with all its properties initialized.
     *
     * @param gymId      The ID of the gym.
     * @param capacity   The maximum capacity for the slot.
     */
    public Slot(int gymId, int capacity) {
        this.gymId = gymId;
        this.capacity = capacity;
    }

    /**
     * Constructs a new {@code Slot} object with all its properties initialized.
     *
     * @param slotId      The unique identifier for the slot.
     * @param gymId       The ID of the gym.
     * @param startTime   The start time of the slot.
     * @param endTime     The end time of the slot.
     * @param capacity    The maximum capacity for the slot.
     * @param bookedCount The current number of booked slots.
     */
    public Slot(int slotId, int gymId, LocalTime startTime, LocalTime endTime, int capacity, int bookedCount) {
        this.slotId = slotId;
        this.gymId = gymId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.bookedCount = bookedCount;
    }

    /**
     * Retrieves the unique identifier of the slot.
     *
     * @return The slot ID.
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Sets the unique identifier of the slot.
     *
     * @param slotId The slot ID to set.
     */
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    /**
     * Retrieves the unique identifier of the gym associated with this slot.
     *
     * @return The gym ID.
     */
    public int getGymId() {
        return gymId;
    }

    /**
     * Sets the unique identifier of the gym.
     *
     * @param gymId The gym ID to set.
     */
    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    /**
     * Retrieves the start time of the slot.
     *
     * @return The start time.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the slot.
     *
     * @param startTime The start time to set.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the end time of the slot.
     *
     * @return The end time.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the slot.
     *
     * @param endTime The end time to set.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Retrieves the total capacity of the slot.
     *
     * @return The maximum number of bookings for this slot.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the total capacity of the slot.
     *
     * @param capacity The capacity to set.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Retrieves the current number of booked slots.
     *
     * @return The number of bookings.
     */
    public int getBookedCount() {
        return bookedCount;
    }

    /**
     * Sets the current number of booked slots.
     *
     * @param bookedCount The booked count to set.
     */
    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }
}
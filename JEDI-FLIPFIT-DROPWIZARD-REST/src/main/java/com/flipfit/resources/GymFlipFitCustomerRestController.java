package com.flipfit.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;
import com.flipfit.business.CustomerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.AccessDeniedException;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.MissingValueException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST controller for customer-related operations.
 * It provides endpoints for viewing slots, booking, making payments, and editing details.
 */
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymFlipFitCustomerRestController {

    // Instantiate CustomerService here to handle all business logic.
    private final CustomerService customerService = new CustomerService(
            new UserDAO(),
            new CustomerDAO(),
            new GymOwnerDAO()
    );

    @OPTIONS
    @Path("{path: .*}")
    public Response handleOptions() {
        return Response.ok().build();
    }
    /**
     * Endpoint to view a customer's booked slots.
     * @param customerId The ID of the customer, passed as a path parameter.
     * @return A list of Booking objects or an error response.
     */
    @GET
    @Path("/bookings/{customerId}")
    public Response viewBookedSlots(@PathParam("customerId") int customerId) {
        try {
            List<Booking> bookings = customerService.viewBookedSlots(customerId);
            if (bookings.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).entity("No booked slots found.").build();
            }
            return Response.ok(bookings).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to view all available gym centers.
     * @return A list of GymCentre objects or an error response.
     */
    @GET
    @Path("/centers")
    public Response viewCenters() {
        try {
            List<GymCentre> centers = customerService.viewCenters();
            if (centers.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).entity("No gym centers found.").build();
            }
            return Response.ok(centers).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to retrieve a customer's wallet balance.
     * @param customerId The ID of the customer.
     * @return A Response containing the balance.
     */
    @GET
    @Path("/balance/{customerId}")
    public Response retrieveBalance(@PathParam("customerId") int customerId) {
        try {
            int balance = customerService.retrieveBalance(customerId);
            return Response.ok(balance).build();
        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to add money to a customer's wallet.
     * @param balanceRequest A JSON payload with customer ID and the amount to add.
     * @return A success or error response.
     */
    @POST
    @Path("/wallet/add")
    public Response addToWallet(BalanceRequest balanceRequest) {
        try {
            customerService.makePayments(balanceRequest.getCustomerId(), balanceRequest.getAmount());
            return Response.ok("Amount added to wallet successfully.").build();
        } catch (MissingValueException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to book a slot.
     * @param bookingRequest A JSON payload with booking details.
     * @return A success or error response.
     */
    @POST
    @Path("/slots/book")
    public Response bookSlot(BookingRequest bookingRequest) {
        try {
            LocalDate bookingDate = LocalDate.parse(bookingRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            customerService.bookSlot(
                    bookingRequest.getCustomerId(),
                    bookingRequest.getGymId(),
                    bookingRequest.getSlotId(),
                    bookingDate
            );
            return Response.ok("Slot booked successfully.").build();
        } catch (DateTimeParseException | DuplicateEntryException | AccessDeniedException | MissingValueException e) { // Fixed here
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to edit customer details.
     * @param customerId The ID of the customer.
     * @param updateData A JSON payload with updated User and Customer data.
     * @return A success or error response.
     */
    @PUT
    @Path("/details/{customerId}")
    public Response editDetails(@PathParam("customerId") int customerId, UserAndCustomerData updateData) {
        try {
            System.out.println(customerId);
            if (updateData.getUser() == null || updateData.getCustomer() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Missing user or customer data in payload.").build();
            }

            customerService.editCustomerDetails(
                    customerId,
                    updateData.getUser(),
                    updateData.getCustomer()
            );
            return Response.ok("Details updated successfully.").build();
        } catch (MismatchinputException | MissingValueException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint to edit payment details specifically.
     * @param customerId The ID of the customer.
     * @param paymentDetails A JSON payload with updated payment details.
     * @return A success or error response.
     */
    @PUT
    @Path("/payments/edit/{customerId}")
    public Response editPaymentDetails(@PathParam("customerId") int customerId, PaymentDetailsRequest paymentDetails) {
        try {
            customerService.editPaymentDetails(customerId, paymentDetails.getPaymentType(), paymentDetails.getPaymentInfo());
            return Response.ok("Payment details updated successfully.").build();
        } catch (MissingValueException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    // --- Private Inner Classes (DTOs) for Request Payloads ---

    private static class BookingRequest {
        private int customerId;
        private int gymId;
        private int slotId;
        private String date;

        @JsonProperty
        public int getCustomerId() { return customerId; }
        @JsonProperty
        public void setCustomerId(int customerId) { this.customerId = customerId; }
        @JsonProperty
        public int getGymId() { return gymId; }
        @JsonProperty
        public void setGymId(int gymId) { this.gymId = gymId; }
        @JsonProperty
        public int getSlotId() { return slotId; }
        @JsonProperty
        public void setSlotId(int slotId) { this.slotId = slotId; }
        @JsonProperty
        public String getDate() { return date; }
        @JsonProperty
        public void setDate(String date) { this.date = date; }
    }

    private static class BalanceRequest {
        private int customerId;
        private int amount;

        @JsonProperty
        public int getCustomerId() { return customerId; }
        @JsonProperty
        public void setCustomerId(int customerId) { this.customerId = customerId; }
        @JsonProperty
        public int getAmount() { return amount; }
        @JsonProperty
        public void setAmount(int amount) { this.amount = amount; }
    }

    private static class PaymentDetailsRequest {
        private int paymentType;
        private String paymentInfo;

        @JsonProperty
        public int getPaymentType() { return paymentType; }
        @JsonProperty
        public void setPaymentType(int paymentType) { this.paymentType = paymentType; }
        @JsonProperty
        public String getPaymentInfo() { return paymentInfo; }
        @JsonProperty
        public void setPaymentInfo(String paymentInfo) { this.paymentInfo = paymentInfo; }
    }

    private static class UserAndCustomerData {
        private User user;
        private Customer customer;

        @JsonProperty
        public User getUser() { return user; }
        @JsonProperty
        public void setUser(User user) { this.user = user; }
        @JsonProperty
        public Customer getCustomer() { return customer; }
        @JsonProperty
        public void setCustomer(Customer customer) { this.customer = customer; }
    }
}

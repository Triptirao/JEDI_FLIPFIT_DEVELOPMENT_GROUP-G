package com.flipfit.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.Payment;
import com.flipfit.bean.User;
import com.flipfit.business.GymOwnerService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.AccessDeniedException;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.UnableToDeleteUserException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST controller for gym owner-related operations.
 * It provides endpoints for adding gym centers, viewing details, and editing profiles.
 */
@Path("/gymowner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymFlipFitGymOwnerRestController {

    private final GymOwnerService gymOwnerService = new GymOwnerService(
            new UserDAO(),
            new CustomerDAO(),
            new GymOwnerDAO()
    );

    @POST
    @Path("/centres/add")
    public Response addCentre(GymCentre gymCentre) {
        try {
            gymOwnerService.addCentre(gymCentre);
            return Response.ok("Gym Centre " + gymCentre.getCentreName() + " added successfully.").build();
        } catch (AccessDeniedException | AuthenticationException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/centres/{ownerId}")
    public Response viewGymDetails(@PathParam("ownerId") int ownerId) {
        try {
            List<GymCentre> centres = gymOwnerService.viewGymDetails(ownerId);
            if (centres.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).entity("No gym centres found for this owner.").build();
            }
            return Response.ok(centres).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
        }
    }

    /**
     * New endpoint to view bookings for a specific gym owned by the owner.
     * @param ownerId The ID of the gym owner.
     * @param gymId The ID of the gym to view bookings for.
     * @return A list of Booking objects or an error response.
     */
    @GET
    @Path("/bookings/{ownerId}/{gymId}")
    public Response viewBookings(@PathParam("ownerId") int ownerId, @PathParam("gymId") int gymId) {
        try {
            List<Booking> bookings = gymOwnerService.viewBookings(ownerId, gymId);
            if (bookings.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).entity("No bookings found for gym " + gymId + ".").build();
            }
            return Response.ok(bookings).build();
        } catch (MismatchinputException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/details/{ownerId}")
    public Response editDetails(@PathParam("ownerId") int ownerId, UserAndGymOwnerData updateData) {
        try {
            if (updateData.getUser() == null || updateData.getGymOwner() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Missing user or gym owner data in payload.").build();
            }
            GymOwner updatedResult = gymOwnerService.editGymOwnerDetails(ownerId, updateData.getUser(), updateData.getGymOwner());
            return Response.ok(updatedResult).build();
        } catch (MismatchinputException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    // New DTO to combine User and GymOwner data for the REST request
    private static class UserAndGymOwnerData {
        private User user;
        private GymOwner gymOwner;

        public UserAndGymOwnerData(@JsonProperty("user") User user, @JsonProperty("gymOwner") GymOwner gymOwner) {
            this.user = user;
            this.gymOwner = gymOwner;
        }

        @JsonProperty
        public User getUser() { return user; }
        @JsonProperty
        public void setUser(User user) { this.user = user; }
        @JsonProperty
        public GymOwner getGymOwner() { return gymOwner; }
        @JsonProperty
        public void setGymOwner(GymOwner gymOwner) { this.gymOwner = gymOwner; }
    }
}

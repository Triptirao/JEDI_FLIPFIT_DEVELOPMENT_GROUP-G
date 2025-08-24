package com.flipfit.resources;

import com.flipfit.bean.Admin;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre; // Import GymCentre
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.business.AdminService;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.UnableToDeleteUserException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymFlipFitAdminRestController {


    private UserDAO userDao = new UserDAO();
    private CustomerDAO customerDao = new CustomerDAO();
    private GymOwnerDAO gymOwnerDao = new GymOwnerDAO();
    private final AdminService adminService = new AdminService(
            new AdminDAO(userDao,customerDao,gymOwnerDao), userDao,customerDao,gymOwnerDao
    );

    @GET
    @Path("/gyms")
    public Response viewAllGyms() {
        // Change the type from List<Admin> to List<GymCentre>
        List<GymCentre> gyms = adminService.viewAllGyms();
        return Response.ok(gyms).build();
    }

    @GET
    @Path("/gyms/pending")
    public Response viewPendingGyms() {
        // Change the type from List<Admin> to List<GymCentre>
        List<GymCentre> pendingGyms = adminService.viewPendingGyms();
        return Response.ok(pendingGyms).build();
    }

    @GET
    @Path("/gymowners")
    public Response viewAllGymOwners() {
        List<User> owners = adminService.viewAllGymOwners();
        return Response.ok(owners).build();
    }

    @GET
    @Path("/gymowners/pending")
    public Response viewPendingGymOwners() {
        List<User> pendingOwners = adminService.viewPendingGymOwners();
        return Response.ok(pendingOwners).build();
    }

    @GET
    @Path("/customers")
    public Response viewAllCustomers() {
        List<User> customers = adminService.viewAllCustomers();
        return Response.ok(customers).build();
    }

    @POST
    @Path("/gymowners/approve/{email}")
    public Response approveGymOwner(@PathParam("email") String email) {
        try {
            adminService.approveGymOwnerRequest(email);
            return Response.ok("Gym owner approved successfully.").build();
        } catch (MismatchinputException | AuthenticationException | DuplicateEntryException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/gyms/approve/{gymId}")
    public Response approveGym(@PathParam("gymId") int gymId) {
        try {
            adminService.approveGymRequest(gymId);
            return Response.ok("Gym centre approved successfully.").build();
        } catch (MismatchinputException | AuthenticationException | DuplicateEntryException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") int userId) {
        try {
            adminService.deleteUserById(userId);
            return Response.ok("User deleted successfully.").build();
        } catch (MismatchinputException | UnableToDeleteUserException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/gyms/{gymId}")
    public Response deleteGym(@PathParam("gymId") int gymId) {
        try {
            adminService.deleteGymById(gymId);
            return Response.ok("Gym deleted successfully.").build();
        } catch (MismatchinputException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

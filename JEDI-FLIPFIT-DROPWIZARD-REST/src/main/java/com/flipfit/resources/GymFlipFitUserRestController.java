package com.flipfit.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.business.AuthenticationService;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.AuthenticationException;
import com.flipfit.exception.DuplicateEntryException;
import com.flipfit.exception.MismatchinputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GymFlipFitUserRestController {

    private final AuthenticationService authenticationService = new AuthenticationService(
            new UserDAO(), new CustomerDAO(), new GymOwnerDAO()
    );

    @OPTIONS
    @Path("{path: .*}")
    public Response handleOptions() {
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    public Response login(User user) {
        try {
            Optional<User> authenticatedUser = Optional.ofNullable(
                    authenticationService.login(user.getEmail(), user.getPassword())
            );

            if (authenticatedUser.isPresent()) {
                return Response.ok(authenticatedUser.get()).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Login failed. Invalid credentials.").build();
            }
        } catch (AuthenticationException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Login failed: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint for new customer registration.
     * @param registrationRequest A DTO containing user and customer details.
     * @return A success or error response.
     */
    @POST
    @Path("/register/customer")
    public Response registerCustomer(CustomerRegistrationRequest registrationRequest) {
        try {
            authenticationService.registerCustomer(
                    registrationRequest.getFullName(),
                    registrationRequest.getEmail(),
                    registrationRequest.getPassword(),
                    registrationRequest.getUserPhone(),
                    registrationRequest.getCity(),
                    registrationRequest.getPinCode(),
                    registrationRequest.getPaymentType(),
                    registrationRequest.getPaymentInfo()
            );
            return Response.ok("Customer registered successfully.").build();
        } catch (DuplicateEntryException | MismatchinputException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Registration failed: " + e.getMessage()).build();
        }
    }

    /**
     * Endpoint for new gym owner registration.
     * @param registrationRequest A DTO containing user and gym owner details.
     * @return A success or error response.
     */
    @POST
    @Path("/register/owner")
    public Response registerGymOwner(GymOwnerRegistrationRequest registrationRequest) {
        try {
            authenticationService.registerGymOwner(
                    registrationRequest.getFullName(),
                    registrationRequest.getEmail(),
                    registrationRequest.getPassword(),
                    registrationRequest.getUserPhone(),
                    registrationRequest.getCity(),
                    registrationRequest.getPinCode(),
                    registrationRequest.getAadhaar(),
                    registrationRequest.getPan(),
                    registrationRequest.getGst()
            );
            return Response.ok("Gym owner registered successfully, awaiting approval.").build();
        } catch (DuplicateEntryException | MismatchinputException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Registration failed: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return Response.ok("Logout successful.").build();
    }

    // --- Private Inner Classes (DTOs) for Request Payloads ---

    private static class CustomerRegistrationRequest {
        private String fullName;
        private String email;
        private String password;
        private long userPhone;
        private String city;
        private int pinCode;
        private int paymentType;
        private String paymentInfo;

        @JsonProperty
        public String getFullName() { return fullName; }
        @JsonProperty
        public void setFullName(String fullName) { this.fullName = fullName; }
        @JsonProperty
        public String getEmail() { return email; }
        @JsonProperty
        public void setEmail(String email) { this.email = email; }
        @JsonProperty
        public String getPassword() { return password; }
        @JsonProperty
        public void setPassword(String password) { this.password = password; }
        @JsonProperty
        public long getUserPhone() { return userPhone; }
        @JsonProperty
        public void setUserPhone(long userPhone) { this.userPhone = userPhone; }
        @JsonProperty
        public String getCity() { return city; }
        @JsonProperty
        public void setCity(String city) { this.city = city; }
        @JsonProperty
        public int getPinCode() { return pinCode; }
        @JsonProperty
        public void setPinCode(int pinCode) { this.pinCode = pinCode; }
        @JsonProperty
        public int getPaymentType() { return paymentType; }
        @JsonProperty
        public void setPaymentType(int paymentType) { this.paymentType = paymentType; }
        @JsonProperty
        public String getPaymentInfo() { return paymentInfo; }
        @JsonProperty
        public void setPaymentInfo(String paymentInfo) { this.paymentInfo = paymentInfo; }
    }

    private static class GymOwnerRegistrationRequest {
        private String fullName;
        private String email;
        private String password;
        private long userPhone;
        private String city;
        private int pinCode;
        private String aadhaar;
        private String pan;
        private String gst;

        @JsonProperty
        public String getFullName() { return fullName; }
        @JsonProperty
        public void setFullName(String fullName) { this.fullName = fullName; }
        @JsonProperty
        public String getEmail() { return email; }
        @JsonProperty
        public void setEmail(String email) { this.email = email; }
        @JsonProperty
        public String getPassword() { return password; }
        @JsonProperty
        public void setPassword(String password) { this.password = password; }
        @JsonProperty
        public long getUserPhone() { return userPhone; }
        @JsonProperty
        public void setUserPhone(long userPhone) { this.userPhone = userPhone; }
        @JsonProperty
        public String getCity() { return city; }
        @JsonProperty
        public void setCity(String city) { this.city = city; }
        @JsonProperty
        public int getPinCode() { return pinCode; }
        @JsonProperty
        public void setPinCode(int pinCode) { this.pinCode = pinCode; }
        @JsonProperty
        public String getAadhaar() { return aadhaar; }
        @JsonProperty
        public void setAadhaar(String aadhaar) { this.aadhaar = aadhaar; }
        @JsonProperty
        public String getPan() { return pan; }
        @JsonProperty
        public void setPan(String pan) { this.pan = pan; }
        @JsonProperty
        public String getGst() { return gst; }
        @JsonProperty
        public void setGst(String gst) { this.gst = gst; }
    }
}

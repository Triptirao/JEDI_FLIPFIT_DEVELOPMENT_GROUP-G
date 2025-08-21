package com.flipfit.client;

import com.flipfit.business.AdminService;
import com.flipfit.business.AuthenticationService;
import com.flipfit.business.CustomerService;
import com.flipfit.business.GymOwnerService;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymCentreDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.Scanner;
import java.util.Optional;

public class ApplicationClient {

    private final Scanner scanner = new Scanner(System.in);
    private final AuthenticationService authenticationService;
    private final AdminService adminService;
    private final GymOwnerService gymOwnerService;
    private final CustomerService customerService;
    private final UserDAO userDao;
    private final AdminDAO adminDao;
    private final GymOwnerDAO gymOwnerDao;
    private final CustomerDAO customerDao;
    private final GymCentreDAO gymCentreDao;

    public ApplicationClient(UserDAO userDao, AdminDAO adminDao, GymOwnerDAO gymOwnerDao, CustomerDAO customerDao, GymCentreDAO gymCentreDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.gymOwnerDao = gymOwnerDao;
        this.customerDao = customerDao;
        this.gymCentreDao = gymCentreDao;

        this.authenticationService = new AuthenticationService(this.userDao);
        this.adminService = new AdminService(this.adminDao, this.customerDao, this.userDao,this.gymOwnerDao, this.gymCentreDao);
        this.gymOwnerService = new GymOwnerService(this.gymOwnerDao, this.userDao, this.customerDao,this.gymCentreDao);
        this.customerService = new CustomerService(this.customerDao, this.userDao, this.gymOwnerDao,this.gymCentreDao);
    }

    public void login() {
        System.out.println("--- Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Optional<String[]> user = Optional.ofNullable(authenticationService.login(email, password));

        if (user.isPresent()) {
            String[] userData = user.get();
            System.out.println("Login successful! Welcome, " + userData[2] + ".");
            String role = userData[0];

            if (role.equalsIgnoreCase("CUSTOMER")) {
                CustomerClient customerClient = new CustomerClient(customerDao, userDao, userData[1],gymCentreDao);
                customerClient.customerPage();
            } else if (role.equalsIgnoreCase("OWNER")) {
                GymOwnerClient gymOwnerClient = new GymOwnerClient(gymOwnerDao, userDao, customerDao, userData[1],gymCentreDao);
                gymOwnerClient.gymOwnerPage();
            } else if (role.equalsIgnoreCase("ADMIN")) {
                AdminClient adminClient = new AdminClient(adminDao, customerDao, userDao, gymOwnerDao,gymCentreDao);
                adminClient.adminPage();
            } else {
                System.out.println("Invalid Role for user.");
            }
        } else {
            System.out.println("Login failed. Invalid credentials.");
        }
    }

    public void registerCustomer() {
        System.out.println("--- Customer Registration ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();

        System.out.println("Registration received for customer: " + name);
        authenticationService.registerCustomer(name, email, password, phone);
        System.out.println("Registration Successful");
    }

    public void registerOwner() {
        System.out.println("--- Gym Owner Registration ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter your aadhaar number: ");
        String aadhaar = scanner.nextLine();

        System.out.print("Enter your PAN number: ");
        String pan = scanner.nextLine();

        System.out.print("Enter your GST number: ");
        String gst = scanner.nextLine();

        System.out.println("Registration received for Owner: " + name);
        authenticationService.registerGymOwner(name, email, password, phone, aadhaar, pan,gst);
        System.out.println("Registration Successful");
    }
}

package com.flipfit.client;

import com.flipfit.business.*;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationClient {

    private final Scanner scanner = new Scanner(System.in);
    private final authenticationInterface authenticationService;
    private final AdminService adminService;
    private final GymOwnerService gymOwnerService;
    private final CustomerService customerService;
    private final UserDAO userDao;
    private final AdminDAO adminDao;
    private final GymOwnerDAO gymOwnerDao;
    private final CustomerDAO customerDao;

    // Regex for email validation
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Regex for phone number validation (10 digits)
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public ApplicationClient(UserDAO userDao, AdminDAO adminDao, GymOwnerDAO gymOwnerDao, CustomerDAO customerDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.gymOwnerDao = gymOwnerDao;
        this.customerDao = customerDao;

        this.authenticationService = new AuthenticationService(this.userDao);
        this.adminService = new AdminService(this.adminDao, this.customerDao, this.userDao, this.gymOwnerDao);
        this.gymOwnerService = new GymOwnerService(this.gymOwnerDao, this.userDao, this.customerDao);
        this.customerService = new CustomerService(this.customerDao, this.userDao, this.gymOwnerDao);
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
                // Corrected call: passing all 4 arguments
                CustomerClient customerClient = new CustomerClient(customerDao, userDao, gymOwnerDao, userData[1]);
                customerClient.customerPage();
            } else if (role.equalsIgnoreCase("OWNER")) {
                GymOwnerClient gymOwnerClient = new GymOwnerClient(gymOwnerDao, userDao, customerDao, userData[1]);
                gymOwnerClient.gymOwnerPage();
            } else if (role.equalsIgnoreCase("ADMIN")) {
                AdminClient adminClient = new AdminClient(adminDao, customerDao, userDao, gymOwnerDao);
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

        String email = getValidEmailInput(); // Call validation for email
        String password = getPasswordInput(); // Reusing method for password input
        long phone = getValidPhoneInput(); // Call validation for phone number

        System.out.print("Enter your city: ");
        String city = scanner.nextLine();

        int pincode = getValidPincodeInput(); // Call validation for pin code
        int paymentType = getValidPaymentType(); // Call validation for payment type

        System.out.print("Enter your paymentInfo (UPI Id or Card Number): ");
        String paymentInfo = scanner.nextLine();


        System.out.println("Registration received for customer: " + name);
        authenticationService.registerCustomer(name, email, password, phone, city, pincode, paymentType, paymentInfo);
    }

    public void registerOwner() {
        System.out.println("--- Gym Owner Registration ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        String email = getValidEmailInput(); // Call validation for email
        String password = getPasswordInput(); // Reusing method for password input

        long phone = getValidPhoneInput(); // Call validation for phone number

        System.out.print("Enter your aadhaar number: ");
        String aadhaar = scanner.nextLine();

        System.out.print("Enter your PAN number: ");
        String pan = scanner.nextLine();

        System.out.print("Enter your GST number: ");
        String gst = scanner.nextLine();

        System.out.println("Registration received for Owner: " + name);
        authenticationService.registerGymOwner(name, email, password, phone, aadhaar, pan, gst);
        System.out.println("Registration Successful (Pending Approval)");
    }

    /**
     * Prompts for and validates email input until a valid format is entered.
     * @return A valid email string.
     */
    private String getValidEmailInput() {
        String email;
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
            if (isValidEmail(email)) {
                return email;
            } else {
                System.out.println("Invalid email format. Please try again (e.g., user@example.com).");
            }
        }
    }

    /**
     * Prompts for and validates 10-digit phone number input.
     * @return A valid 10-digit phone number string.
     */
    private long getValidPhoneInput() {
        String phone;
        while (true) {
            System.out.print("Enter your phone number (10 digits): ");
            phone = scanner.nextLine();
            if (isValidPhoneNumber(phone)) {
                return Long.parseLong(phone);
            } else {
                System.out.println("Invalid phone number format. Please enter exactly 10 digits.");
            }
        }
    }

    private int getValidPincodeInput() {
        String pincode;
        while (true) {
            System.out.print("Enter your pin code (6 digits): ");
            pincode = scanner.nextLine();
            if (pincode.matches("\\d{6}")) {
                return Integer.parseInt(pincode);
            } else {
                System.out.println("Invalid pin code format. Please enter exactly 6 digits.");
            }
        }
    }

    private int getValidPaymentType() {
        String paymentType;
        while (true) {
            System.out.print("Enter your paymentType (1 for UPI or 2 for Card Payment) : ");
            paymentType = scanner.nextLine();
            if (!Objects.equals(paymentType, "1") && !Objects.equals(paymentType, "2")) {
                System.out.println("Invalid, please enter valid payment type");
            } else {
                return Integer.parseInt(paymentType);
            }
        }
    }

    /**
     * Simple method to get password input (can be extended for password policy checks).
     * @return The password string.
     */
    private String getPasswordInput() {
        System.out.print("Enter your password: ");
        return scanner.nextLine();
    }

    /**
     * Validates an email address using a regular expression.
     * @param email The email string to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Validates a phone number using a regular expression (expects 10 digits).
     * @param phone The phone number string to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }
}
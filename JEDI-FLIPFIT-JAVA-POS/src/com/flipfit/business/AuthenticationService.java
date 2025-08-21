package com.flipfit.business;

import com.flipfit.bean.Customer;
import com.flipfit.bean.User;
import com.flipfit.dao.UserDAO;

public class AuthenticationService implements authenticationInterface {

    private UserDAO userDao;

    public AuthenticationService() {
        this.userDao = this.userDao;
    }

    public AuthenticationService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public String[] login(String email, String password) {
        // Authenticate user by checking against all users in the DAO
        for (String[] user : userDao.getAllUsers()) {
            if (user[3].equals(email) && user[4].equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void registerCustomer(String name, String email, String password, long phone, String city, int pincode, int paymentType, String paymentInfo) {
        // Here you would generate a unique ID for the new customer
        User user = new User(1, "customer", 0, name, email, password, phone, city, pincode);
        int userId = userDao.addUser(user);
        if(userId == -1) {
            System.out.println("Error in registering customer");
        }
        else {
            Customer customer = new Customer(1, "customer", userId, name, email, password, phone, city, pincode, paymentType, paymentInfo);
            if(userDao.addCustomer(customer) == true){
                System.out.println("Customer added successfully");
            }
            else {
                System.out.println("Error in registering customer");
            }
        }
    }

    public void registerGymOwner(String name, String email, String password, long phone, String aadhaar, String pan, String gst) {
        // Here you would generate a unique ID for the new gym owner
//        String newId = String.valueOf(userDao.getAllUsers().size() + 1);
//        String[] newOwner = {"OWNER", newId, name, email, password, phone, "N/A", "N/A", pan, aadhaar, gst, "false"};
//        userDao.getAllUsers().add(newOwner);
//        System.out.println("Gym owner registration received for " + name);
    }
}

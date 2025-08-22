package com.flipfit.io;

import com.flipfit.client.ApplicationClient;
import com.flipfit.dao.AdminDAO;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FlipFitScanner {
    public static void main(String[] args) {
        // Instantiate the DAOs first
        UserDAO userDao = new UserDAO();
        GymOwnerDAO gymOwnerDao = new GymOwnerDAO();
        CustomerDAO customerDao = new CustomerDAO();

        // AdminDAO constructor requires other DAOs
        AdminDAO adminDao = new AdminDAO(userDao, customerDao, gymOwnerDao);


        // Pass the DAOs to the ApplicationClient's constructor
        ApplicationClient applicationClient = new ApplicationClient(userDao, adminDao, gymOwnerDao, customerDao);

        final Scanner in = new Scanner(System.in);
        System.out.println("Welcome to flip fit application\n");

        while(true){
            System.out.println("Menu for operation:");
            System.out.println("1. Login");
            System.out.println("2. Register as Customer");
            System.out.println("3. Register as Owner");
            System.out.println("4. Exit");
            int op ;
            try {
                op = in.nextInt();
                in.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                in.next();
                continue;
            }

            switch(op){
                case 1: applicationClient.login();
                    break;
                case 2: applicationClient.registerCustomer();
                    break;
                case 3: applicationClient.registerOwner();
                    break;
                case 4: System.out.println("Application exited");
                    System.exit(0);
                default: System.out.println("Invalid option");
                    break;
            }
        }
    }
}

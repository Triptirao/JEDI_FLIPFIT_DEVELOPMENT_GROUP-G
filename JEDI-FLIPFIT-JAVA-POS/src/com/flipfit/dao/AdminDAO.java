package com.flipfit.dao;

import com.sun.tools.jconsole.JConsoleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDAO {

    // Injecting the new DAOs
    private UserDAO userDao;
    private GymOwnerDAO gymOwnerDAO;

    public AdminDAO() {
        this.userDao = new UserDAO();
        this.gymOwnerDAO = new GymOwnerDAO();
    }

    public List<String[]> getPendingGymRequests() {
        return gymOwnerDAO.getPendingGymRequests();
    }

    public void approveGymRequest(String gymId) {
        gymOwnerDAO.approveGymRequest(gymId);
    }

    public List<String[]> getPendingGymOwnerRequests() {
        return userDao.getAllUsers().stream()
                .filter(user -> user[0].equals("OWNER") && user[11].equals("false")) // Assuming Approved field is at index 8 for GymOwner in UserDAO
                .collect(Collectors.toList());
    }

    public void approveGymOwnerRequest(String email) {
        userDao.getAllUsers().stream()
                .filter(user -> user[3].equals(email))
                .findFirst()
                .ifPresent(user -> user[11] = "true"); // Assuming Approved field is at index 8
    }

    public List<String[]> getAllGyms() {
        return GymOwnerDAO.getAllGyms();
    }

    // Corrected method to get all gym owners by filtering the users
    public List<String[]> getAllGymOwners() {
        return userDao.getAllUsers().stream()
                .filter(user -> user[0].equals("OWNER") && user[11].equalsIgnoreCase("true"))
                .collect(Collectors.toList());
    }

    public List<String[]> getAllUsers() {
        return userDao.getAllUsers();
    }

    public List<String[]> getAllCustomers() {
        return userDao.getAllUsers().stream()
                .filter(user -> user[0].equals("CUSTOMER"))
                .collect(Collectors.toList());
    }

    public void deleteUser(String userId) {
        userDao.deleteUser(userId);
    }

    public void deleteGym(String gymId) {
        gymOwnerDAO.deleteGym(gymId);
    }
}

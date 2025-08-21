package com.flipfit.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GymCentreDAO {

    private static List<String[]> gymCentres = new ArrayList<>();

    static {
        // Dummy data for Gym Centres: {ID, OwnerID, Name, Slots, Capacity, Approved, City, State}
        gymCentres.add(new String[]{"1", "101", "Fitness Hub", "slot1,slot2,slot3", "50", "false", "Bengaluru", "Karnataka"});
        gymCentres.add(new String[]{"2", "102", "Zenith Fitness", "slot1,slot2,slot3", "75", "true", "Bengaluru", "Karnataka"});
        gymCentres.add(new String[]{"3", "103", "Flex Gym", "slot1,slot2", "45", "false", "Mumbai", "Maharashtra"});
    }

    public List<String[]> getAllGyms() {
        return gymCentres;
    }

    public List<String[]> getPendingGymRequests() {
        return gymCentres.stream()
                .filter(gym -> gym[4].equals("false"))
                .collect(Collectors.toList());
    }

    public void approveGymRequest(String gymId) {
        gymCentres.stream()
                .filter(gym -> gym[0].equals(gymId))
                .findFirst()
                .ifPresent(gym -> gym[4] = "true");
    }

    public void deleteGym(String gymId) {
        gymCentres.removeIf(gym -> gym[0].equals(gymId));
    }

    public List<String[]> getApprovedGyms() {
        return gymCentres.stream()
                .filter(gym -> gym[4].equals("true"))
                .collect(Collectors.toList());
    }
}

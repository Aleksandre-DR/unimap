package com.example.unimap.service;

import com.example.unimap.entity.StaffRoom;
import com.example.unimap.exception.ResourceNotFoundException;
import com.example.unimap.repository.StaffRoomRepo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static com.example.unimap.service.Translator.georgianToLatin;

@Service
public class StaffRoomService {
    private StaffRoomRepo staffRepo;
    private ArrayList<String> staffNames;

    private StaffRoomService(StaffRoomRepo staffRepo) {
        this.staffRepo = staffRepo;
        fillStaffRoomTable();

        staffNames = new ArrayList<>();
        staffRepo.findAll().forEach(sr -> staffNames.add(sr.getStaffFullName()));
    }

    private String getStaffRoom(String staffFullName) {         // from Optional to String
        Optional<StaffRoom> staffRoom = staffRepo.findById(staffFullName);
        return staffRoom.isPresent() ? staffRoom.get().getStaffRoom() : null;
    }

    public String findStaffRoom(String staffFullName) {
        if (staffFullName == null || staffFullName.length() == 0) {
            throw new ResourceNotFoundException("staff name must not be null or empty");
        }

        String ourStaffFullName = staffFullName;
        if (georgianToLatin.keySet().contains(staffFullName.charAt(0))) {
            ourStaffFullName = Translator.translator(staffFullName);     // converts into latin
        }

        ourStaffFullName = findClosestName(ourStaffFullName.toLowerCase());
        String staffRoom = getStaffRoom(ourStaffFullName);

        if (staffRoom == null) {
            ourStaffFullName = inverseStaffName(ourStaffFullName);
            ourStaffFullName = findClosestName(ourStaffFullName.toLowerCase());
            staffRoom = getStaffRoom(ourStaffFullName);
        }

        if (staffRoom == null) {
            throw new ResourceNotFoundException("No staff member found with name: " + staffFullName);
        }
        return staffRoom;
    }

    private static String inverseStaffName(String ourStaffFullName) {
        String[] nameAndSurname = ourStaffFullName.split(" ");
        if (nameAndSurname.length == 1) {       // no har has found
            return ourStaffFullName;
        }
        return nameAndSurname[1] + " " + nameAndSurname[0];
    }


    private void fillStaffRoomTable() {
        if (staffRepo.count() > 0) return;

        try (InputStream inputStream = getClass().getResourceAsStream("/staffRooms.txt")) {
            if (inputStream == null) {
                System.err.println("staffRooms.txt not found in resources.");
                return;
            }

            try (Scanner sc = new Scanner(inputStream)) {
                while (sc.hasNextLine()) {
                    String[] fromFile = sc.nextLine().split(", ");
                    staffRepo.save(new StaffRoom(fromFile[0], fromFile[1])); // 0-staff, 1-room
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int levenshteinDistance(String inputName, String realName) {
        int n = inputName.length();
        int m = realName.length();
        int[][] dp = new int[n][m];

        for (int i = 0; i < n; i++) dp[i][0] = i;
        for (int j = 0; j < m; j++) dp[0][j] = j;

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                int cost = (inputName.charAt(i - 1) == realName.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[n - 1][m - 1];
    }

    private String findClosestName(String inputName) {
        int minDistance = Integer.MAX_VALUE;
        int distance;
        String closestName = "";

        for (String staffName : staffNames) {
            distance = levenshteinDistance(inputName, staffName);
            if (distance == 0) return staffName;
            if (distance < minDistance) {
                minDistance = distance;
                closestName = staffName;
            }
        }

        return (minDistance <= 5) ? closestName : inputName;
    }
}

package com.guiyomi;

import javafx.scene.control.Alert;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final NetworkService networkService = new NetworkService();

    public boolean registerUser(String firstName, String lastName, String password, String profilePicturePath) {
        try {
            byte[] profilePictureBytes = Files.readAllBytes(Paths.get(profilePicturePath));
            String profilePictureBase64 = java.util.Base64.getEncoder().encodeToString(profilePictureBytes);

            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"password\":\"%s\",\"profilePicture\":\"%s\"}",
                firstName, lastName, password, profilePictureBase64
            );

            String response = networkService.sendPostRequest("http://localhost:3001/register", requestBody);
            if (response.startsWith("Error")) {
                showAlert("Registration failed: " + response);
                return false;
            }

            return true;

        } catch (Exception e) {
            showAlert("Failed to register user: " + e.getMessage());
            return false;
        }
    }

    public boolean loginUser(String firstName, String lastName, String password) {
        try {
            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"password\":\"%s\"}",
                firstName, lastName, password
            );

            String response = networkService.sendPostRequest("http://localhost:3001/login", requestBody);
            if (response.startsWith("Error")) {
                showAlert("Login failed: " + response);
                return false;
            }

            return true;

        } catch (Exception e) {
            showAlert("Failed to log in: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public List<String> getAllUsers() {
        return new ArrayList<>();
    }
}

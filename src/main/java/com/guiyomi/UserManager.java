package com.guiyomi;

import javafx.scene.control.Alert;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserManager {

    private final NetworkService networkService = new NetworkService();

    // Method to register a user
    public boolean registerUser(String firstName, String lastName, String password, String profilePicturePath) {
        try {
            // Read the profile picture file into a byte array
            byte[] profilePictureBytes = Files.readAllBytes(Paths.get(profilePicturePath));
            String profilePictureBase64 = java.util.Base64.getEncoder().encodeToString(profilePictureBytes);

            // Construct JSON request body
            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"password\":\"%s\",\"profilePicture\":\"%s\"}",
                firstName, lastName, password, profilePictureBase64
            );

            // Send POST request to the server
            String response = networkService.sendPostRequest("http://localhost:3000/register", requestBody);
            if (response.startsWith("Error")) {
                showAlert("Registration failed: " + response);
                return false;
            }

            return true; // Registration was successful

        } catch (Exception e) {
            showAlert("Failed to register user: " + e.getMessage());
            return false;
        }
    }

    // Method to log in a user
    public boolean loginUser(String firstName, String lastName, String password) {
        try {
            // Construct JSON request body
            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"password\":\"%s\"}",
                firstName, lastName, password
            );

            // Send POST request to the server
            String response = networkService.sendPostRequest("http://localhost:3000/login", requestBody);
            if (response.startsWith("Error")) {
                showAlert("Login failed: " + response);
                return false;
            }

            return true; // Login was successful

        } catch (Exception e) {
            showAlert("Failed to log in: " + e.getMessage());
            return false;
        }
    }

    // Helper method to show an alert dialog
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void getAllUsers() {
        
    }
}

package com.guiyomi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SignUpController {
    @FXML
    private TextField firstNameField;
    
    @FXML 
    private TextField lastNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button choosePfBtn;

    @FXML
    private Button signupBtn;

    @FXML
    private Button loginBtn;

    // Declare the profile picture file
    private File profilePictureFile;
    private String profilePictureFileName;
    private String currentUser; // Keeping this in case it's needed in the future

    @FXML
    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        profilePictureFile = fileChooser.showOpenDialog(null);
    }

    @FXML 
    public void registerUser() {
    // Validate fields
    if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
        showAlert("Please fill in all fields.");
        return;
    }

    if (profilePictureFile == null) {
        showAlert("Please select a profile picture.");
        return;
    }

    profilePictureFileName = profilePictureFile.getName().toString();

    try {
        URL url = URI.create("http://localhost:3001/register").toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Construct JSON request body
        String requestBody = String.format(
            "{\n" +
            "  \"firstName\": \"%s\",\n" +
            "  \"lastName\": \"%s\",\n" +
            "  \"password\": \"%s\",\n" +
            "  \"profilePicture\": \"%s\"\n" +
            "}",
            escapeJson(firstNameField.getText()), 
            escapeJson(lastNameField.getText()), 
            escapeJson(passwordField.getText()), 
            escapeJson(profilePictureFileName)
        );


        System.out.println("Request JSON: " + requestBody);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 201) {
            showAlert("Registration successful.");
        } else {
            showAlert("Registration failed: " + conn.getResponseMessage() + " " + responseCode);
        }
    } catch (IOException e) {
        showAlert("Failed to connect to the server.");
    }
}

// Helper method to escape JSON strings to prevent syntax errors
private String escapeJson(String value) {
    return value.replace("\"", "\\\"");
}

    @FXML
    public void loginUser() {
        try {
            URL url = URI.create("http://localhost:3001/login").toURL(); // Updated to use URI
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String requestBody = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"password\":\"%s\"}",
                firstNameField.getText(), lastNameField.getText(), passwordField.getText()
            );

            System.out.println("Login JSON: " + requestBody);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                currentUser = firstNameField.getText() + " " + lastNameField.getText();
                showAlert("Login successful.");
                openChatWindow();
            } else {
                showAlert("Login failed: " + conn.getResponseMessage() + " " + responseCode);
            }
        } catch (IOException e) {
            showAlert("Failed to connect to the server.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openChatWindow() {
        try {
            // Load the ChatMainTemplate.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/guiyomi/ChatMainTemplate.fxml")); // Adjusted the path if needed
            Parent chatRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene
            Scene chatScene = new Scene(chatRoot);
            stage.setScene(chatScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

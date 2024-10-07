package com.guiyomi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ChatMainTemplateController {

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label userNameLabel;

    @FXML
    private ListView<String> userListView; // List to display users

    @FXML
    private TextField messageTextField; // Input field for new messages

    @FXML
    private VBox messageArea; // VBox to display messages

    @FXML
    private Button sendButton;

    @FXML
    private Button logoutButton;

    private NetworkService networkService; // For handling network communications
    private UserManager userManager; // For user-related functionalities
    private String currentUser;

    // Method to initialize networkService and userManager instances
    public void setServices(NetworkService networkService, UserManager userManager) {
        this.networkService = networkService;
        this.userManager = userManager;
    }

    public void initialize() {
        // Called automatically after the FXML file is loaded
        setupEventHandlers();
        loadUsers();
    }

    public void setUserData(String userName, byte[] profilePicture) {
        currentUser = userName;

        // Set profile picture
        if (profilePicture != null) {
            Image image = new Image(new ByteArrayInputStream(profilePicture));
            profileImageView.setImage(image);
        }

        // Set username label
        userNameLabel.setText(userName);
    }

    private void setupEventHandlers() {
        // Handle send message button click
        sendButton.setOnAction(event -> sendMessage());

        // Handle logout button click
        logoutButton.setOnAction(event -> logout());

        // Handle user selection change
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatHistory(newValue);
            }
        });
    }

    private void loadUsers() {
        // Assuming `userManager.getAllUsers()` fetches a list of user names
        List<String> users = userManager.getAllUsers();
        Platform.runLater(() -> userListView.getItems().setAll(users));
    }

    private void loadChatHistory(String selectedUser) {
        // Load chat history with the selected user
        List<String> chatHistory = networkService.getChatHistory(currentUser, selectedUser);
        Platform.runLater(() -> {
            messageArea.getChildren().clear(); // Clear previous messages

            for (String message : chatHistory) {
                Label messageLabel = new Label(message);
                messageArea.getChildren().add(messageLabel);
            }
        });
    }

    private void sendMessage() {
        String message = messageTextField.getText();
        if (message.isEmpty()) {
            return; // Don't send empty messages
        }

        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            // Display an error: No user selected
            showAlert("Please select a user to send a message.");
            return;
        }

        // Send the message using networkService
        networkService.sendMessage(currentUser, selectedUser, message);

        // Display the message in the chat area
        Label messageLabel = new Label("You: " + message);
        Platform.runLater(() -> messageArea.getChildren().add(messageLabel));

        // Clear the input field
        messageTextField.clear();
    }

    private void logout() {
        // Notify the server about the logout
        networkService.logoutUser(currentUser);

        // Close the current window and redirect to the login window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/guiyomi/SignUp.fxml"));
            Parent loginRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            // Set the new scene
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

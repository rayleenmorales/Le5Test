package com.guiyomi;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    public void setServices(NetworkService networkService, UserManager userManager) {
        this.networkService = networkService;
        this.userManager = userManager;
    }

    public void initialize() {
        setupEventHandlers();
        loadUsers();
    }

    public void setUserData(String userName, byte[] profilePicture) {
        currentUser = userName;

        if (profilePicture != null) {
            Image image = new Image(new ByteArrayInputStream(profilePicture));
            profileImageView.setImage(image);
        }
        userNameLabel.setText(userName);
    }

    private void setupEventHandlers() {
        sendButton.setOnAction(event -> sendMessage());
        logoutButton.setOnAction(event -> logout());

        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatHistory(newValue);
            }
        });
    }

    private void loadUsers() {
        new Thread(() -> {
            List<String> users = userManager.getAllUsers();
            Platform.runLater(() -> userListView.getItems().setAll(users));
        }).start();
    }

    private void loadChatHistory(String selectedUser) {
        new Thread(() -> {
            List<String> chatHistory = networkService.getChatHistory(currentUser, selectedUser);
            Platform.runLater(() -> {
                messageArea.getChildren().clear();
                for (String message : chatHistory) {
                    Label messageLabel = new Label(message);
                    messageArea.getChildren().add(messageLabel);
                }
            });
        }).start();
    }

    private void sendMessage() {
        String message = messageTextField.getText();
        if (message.isEmpty()) return;

        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Please select a user to send a message.");
            return;
        }

        new Thread(() -> {
            networkService.sendMessage(currentUser, selectedUser, message);
            Platform.runLater(() -> {
                Label messageLabel = new Label("You: " + message);
                messageArea.getChildren().add(messageLabel);
                messageTextField.clear();
            });
        }).start();
    }

    private void logout() {
        new Thread(() -> {
            networkService.logoutUser(currentUser);
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/guiyomi/SignUp.fxml"));
                    Parent loginRoot = loader.load();
                    Stage stage = (Stage) logoutButton.getScene().getWindow();
                    Scene loginScene = new Scene(loginRoot);
                    stage.setScene(loginScene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

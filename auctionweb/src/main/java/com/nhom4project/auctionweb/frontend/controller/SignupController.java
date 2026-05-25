package com.nhom4project.auctionweb.controller.frontend;

import com.nhom4project.auctionweb.client.utils.BackendClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class SignupController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField userNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signUpButton;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        updateButtonState();
        fullNameField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        emailField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        userNameField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        passwordField.textProperty().addListener((o, old, newVal) -> updateButtonState());
        confirmPasswordField.textProperty().addListener((o, old, newVal) -> updateButtonState());
    }

    private void updateButtonState() {
        String fullname = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = userNameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        boolean isValid = !fullname.isEmpty() && email.contains("@") && username.length() >= 4 
                          && password.length() >= 8 && password.equals(confirm);
        
        signUpButton.setDisable(!isValid);
        if (!isValid && !fullname.isEmpty()) {
            if (!email.contains("@")) statusLabel.setText("Invalid email");
            else if (username.length() < 4) statusLabel.setText("Username must be at least 4 characters");
            else if (password.length() < 8) statusLabel.setText("Password must be at least 8 characters");
            else if (!password.equals(confirm)) statusLabel.setText("Passwords do not match");
        } else {
            statusLabel.setText("");
        }
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        signUpButton.setDisable(true);
        statusLabel.setText("Signing up...");

<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/SignupController.java
        String selectedRole = roleComboBox.getValue().toUpperCase();
=======
        String rawRole = roleComboBox.getValue();
        String tempRole = "BIDDER";
        if ("Người bán".equals(rawRole) || "Seller".equalsIgnoreCase(rawRole)) {
            tempRole = "SELLER";
        }
        final String selectedRole = tempRole;
        final String selectedRoleLabel = "SELLER".equals(selectedRole) ? "Seller" : "Bidder";
>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/SignupController.java

        new Thread(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("fullname", fullNameField.getText().trim());
                payload.put("email", emailField.getText().trim());
                payload.put("username", userNameField.getText().trim());
                payload.put("password", passwordField.getText());
                payload.put("role", selectedRole);

                HttpResponse<String> response = BackendClient.getInstance().post("/auth/signup", payload.toString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registration successful! You registered as " + selectedRoleLabel + ".");
                        alert.showAndWait();
                        onGoToSignIn(event);
                    } else {
                        statusLabel.setText("Lỗi: " + response.body());
                        signUpButton.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    signUpButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onGoToSignIn(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/fxml/signin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
<<<<<<< Updated upstream:auctionweb/src/main/java/com/nhom4project/auctionweb/controller/frontend/SignupController.java
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
=======
            SceneUtils.changeScene(stage, "/fxml/signin.fxml", "Sign In", "/style/signin.css");
>>>>>>> Stashed changes:auctionweb/src/main/java/com/nhom4project/auctionweb/frontend/controller/SignupController.java
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void onCancel(ActionEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); }
}

package frontend.signin;

import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Signin extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Đăng nhập người dùng");
        dialog.setHeaderText("Sign in");

        ButtonType signinButtonType = new ButtonType("Sign in", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(signinButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style/signin.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("signin-dialog-pane");

        // Tạo layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #f4f7fb");

        TextField userName = new TextField();
        userName.setPromptText("Username");
        userName.setPrefWidth(280);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setPrefWidth(280);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #d00000; -fx-font-weight: bold;");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userName, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(statusLabel, 1, 3);

        // QUAN TRỌNG: Đưa layout grid vào trong Dialog
        dialog.getDialogPane().setContent(grid);

        Node signinButton = dialog.getDialogPane().lookupButton(signinButtonType);
        signinButton.setDisable(true);

        // Lắng nghe thay đổi của CẢ userName VÀ password để mở khoá nút Sign in
        userName.textProperty().addListener((observable, oldValue, newValue) -> {
            signinButton.setDisable(newValue.trim().isEmpty() || password.getText().isEmpty());
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            signinButton.setDisable(newValue.isEmpty() || userName.getText().trim().isEmpty());
        });

        // Request focus vào ô username mặc định khi mở app lên
        Platform.runLater(() -> userName.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signinButtonType) {
                return new Pair<>(userName.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(userNamepassword -> {
            System.out.println("UserName = " + userNamepassword.getKey() + ", Password = " + userNamepassword.getValue());
        });
    }
}
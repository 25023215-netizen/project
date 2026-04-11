package frontend.dashboard;

import backend.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUser;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colRole;

    @FXML
    public void initialize() {
        // Kết nối cột Table với thuộc tính trong class User
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Tạo dữ liệu giả để test giao diện
        ObservableList<User> list = FXCollections.observableArrayList(
            new User(1, "admin", "Nguyễn Văn A", "Quản trị"),
            new User(2, "nhanvien01", "Trần Thị B", "Bán hàng"),
            new User(3, "kt01", "Lê Văn C", "Kế toán")
        );

        userTable.setItems(list);
    }
}
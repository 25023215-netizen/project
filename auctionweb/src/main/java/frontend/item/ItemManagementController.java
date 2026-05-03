package frontend.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import frontend.utils.BackendClient;
import frontend.utils.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.http.HttpResponse;

/**
 * Controller quản lý sản phẩm đấu giá (dành cho Seller).
 * Cho phép thêm, sửa, xóa sản phẩm và tạo phiên đấu giá từ sản phẩm.
 */
public class ItemManagementController {

    @FXML private TableView<ItemRow> itemTable;
    @FXML private TableColumn<ItemRow, String> nameColumn;
    @FXML private TableColumn<ItemRow, String> typeColumn;
    @FXML private TableColumn<ItemRow, String> priceColumn;
    @FXML private TableColumn<ItemRow, String> descColumn;

    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField extra1Field;
    @FXML private TextField extra2Field;
    @FXML private Label extra1Label;
    @FXML private Label extra2Label;
    @FXML private Label statusLabel;

    @FXML private TextField auctionTitleField;
    @FXML private TextField auctionDaysField;

    private final ObservableList<ItemRow> items = FXCollections.observableArrayList();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(d -> d.getValue().nameProperty());
        typeColumn.setCellValueFactory(d -> d.getValue().typeProperty());
        priceColumn.setCellValueFactory(d -> d.getValue().priceProperty());
        descColumn.setCellValueFactory(d -> d.getValue().descProperty());

        itemTable.setItems(items);

        typeCombo.setItems(FXCollections.observableArrayList("ELECTRONICS", "ART", "VEHICLE"));
        typeCombo.setValue("ELECTRONICS");
        typeCombo.setOnAction(e -> updateExtraFieldLabels());
        updateExtraFieldLabels();

        loadItems();
    }

    private void updateExtraFieldLabels() {
        String type = typeCombo.getValue();
        switch (type) {
            case "ELECTRONICS" -> { extra1Label.setText("Brand:"); extra2Label.setText("Model:"); }
            case "ART" -> { extra1Label.setText("Artist:"); extra2Label.setText("Medium:"); }
            case "VEHICLE" -> { extra1Label.setText("Manufacturer:"); extra2Label.setText("Year:"); }
        }
    }

    private void loadItems() {
        Long sellerId = SessionManager.getInstance().getUserId();
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/items/seller/" + sellerId);
                if (response.statusCode() == 200) {
                    JsonNode root = mapper.readTree(response.body());
                    Platform.runLater(() -> {
                        items.clear();
                        for (JsonNode node : root) {
                            items.add(new ItemRow(
                                    node.path("id").asText(),
                                    node.path("name").asText(),
                                    node.path("item_type").asText(guessType(node)),
                                    String.format("%,.0f", node.path("startingPrice").asDouble()),
                                    node.path("description").asText("")
                            ));
                        }
                        statusLabel.setText("Da tai " + items.size() + " san pham");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Loi: " + e.getMessage()));
            }
        }).start();
    }

    private String guessType(JsonNode node) {
        if (node.has("brand")) return "ELECTRONICS";
        if (node.has("artist")) return "ART";
        if (node.has("manufacturer")) return "VEHICLE";
        return "UNKNOWN";
    }

    @FXML
    private void onAddItem() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String priceText = priceField.getText().trim();
        String type = typeCombo.getValue();

        if (name.isEmpty() || priceText.isEmpty()) {
            statusLabel.setText("Vui long nhap ten va gia!");
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("type", type);
                body.put("name", name);
                body.put("description", desc);
                body.put("startingPrice", Double.parseDouble(priceText));
                body.put("sellerId", SessionManager.getInstance().getUserId());
                body.put("extraField1", extra1Field.getText().trim());
                body.put("extraField2", extra2Field.getText().trim());

                HttpResponse<String> response = BackendClient.getInstance().post("/items", body.toString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setText("Them san pham thanh cong!");
                        clearForm();
                        loadItems();
                    } else {
                        statusLabel.setText("Loi: " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Loi: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onDeleteItem() {
        ItemRow selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Vui long chon san pham can xoa!");
            return;
        }

        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance()
                        .delete("/items/" + selected.getId());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setText("Da xoa san pham!");
                        loadItems();
                    } else {
                        statusLabel.setText("Loi: " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Loi: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Tạo phiên đấu giá từ sản phẩm đã chọn.
     */
    @FXML
    private void onCreateAuction() {
        ItemRow selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Vui long chon san pham de tao phien dau gia!");
            return;
        }

        String title = auctionTitleField.getText().trim();
        if (title.isEmpty()) title = selected.nameProperty().get();

        int days = 3;
        try {
            days = Integer.parseInt(auctionDaysField.getText().trim());
        } catch (Exception ignored) {}

        String finalTitle = title;
        int finalDays = days;

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("title", finalTitle);
                body.put("category", selected.typeProperty().get());
                body.put("description", selected.descProperty().get());
                body.put("startingPrice", Double.parseDouble(selected.priceProperty().get().replace(",", "")));
                body.put("sellerId", SessionManager.getInstance().getUserId());
                body.put("endTime", java.time.LocalDateTime.now().plusDays(finalDays).toString());

                HttpResponse<String> response = BackendClient.getInstance().post("/auctions", body.toString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setText("Tao phien dau gia thanh cong!");
                    } else {
                        statusLabel.setText("Loi: " + response.body());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Loi: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = (Stage) itemTable.getScene().getWindow();
            Scene scene = new Scene(root, 1180, 760);
            scene.getStylesheets().add(getClass().getResource("/style/dashboard.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.clear();
        descField.clear();
        priceField.clear();
        extra1Field.clear();
        extra2Field.clear();
    }

    // ==================== Inner Row Class ====================

    public static class ItemRow {
        private final String id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty type;
        private final SimpleStringProperty price;
        private final SimpleStringProperty desc;

        public ItemRow(String id, String name, String type, String price, String desc) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
            this.price = new SimpleStringProperty(price);
            this.desc = new SimpleStringProperty(desc);
        }

        public String getId() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty typeProperty() { return type; }
        public SimpleStringProperty priceProperty() { return price; }
        public SimpleStringProperty descProperty() { return desc; }
    }
}

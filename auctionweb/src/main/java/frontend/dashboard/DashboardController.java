package frontend.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import frontend.utils.BackendClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardController {
    @FXML
    private Label totalAuctionsLabel;

    @FXML
    private Label runningAuctionsLabel;

    @FXML
    private Label totalBidLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<AuctionRow> auctionTable;

    @FXML
    private TableColumn<AuctionRow, String> titleColumn;

    @FXML
    private TableColumn<AuctionRow, String> categoryColumn;

    @FXML
    private TableColumn<AuctionRow, String> priceColumn;

    @FXML
    private TableColumn<AuctionRow, String> bidsColumn;

    @FXML
    private TableColumn<AuctionRow, String> statusColumn;

    @FXML
    private TableColumn<AuctionRow, String> endTimeColumn;

    private final ObservableList<AuctionRow> auctions = FXCollections.observableArrayList();
    private final ObservableList<AuctionRow> filteredAuctions = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty());
        bidsColumn.setCellValueFactory(data -> data.getValue().bidsProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        endTimeColumn.setCellValueFactory(data -> data.getValue().endTimeProperty());

        auctionTable.setItems(filteredAuctions);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        loadAuctions();
    }

    @FXML
    private void onRefresh() {
        loadAuctions();
    }

    private void loadAuctions() {
        try {
            HttpResponse<String> response = BackendClient.getInstance().get("/auctions");
            if (response.statusCode() == 200) {
                auctions.setAll(parseAuctions(response.body()));
                statusLabel.setText("Da tai danh sach dau gia tu server.");
            } else {
                loadFallbackAuctions("Server tra ve loi: " + response.statusCode());
            }
        } catch (Exception e) {
            loadFallbackAuctions("Dang hien thi du lieu mau vi chua ket noi duoc server.");
        }
        applyFilter();
        updateStats();
    }

    private ObservableList<AuctionRow> parseAuctions(String body) throws Exception {
        ObservableList<AuctionRow> rows = FXCollections.observableArrayList();
        JsonNode root = new ObjectMapper().readTree(body);
        for (JsonNode node : root) {
            String title = node.path("title").asText();
            String category = node.path("category").asText();
            BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
            String bidCount = String.valueOf(node.path("bidCount").asInt());
            String status = node.path("status").asText();
            String endTime = formatEndTime(node.path("endTime").asText());
            rows.add(new AuctionRow(title, category, currencyFormat.format(price), bidCount, status, endTime));
        }
        return rows;
    }

    private void loadFallbackAuctions(String message) {
        auctions.setAll(
                new AuctionRow("iPhone 15 Pro Max 256GB", "Electronics", "25.000.000 VND", "18", "RUNNING", "Con 2 ngay"),
                new AuctionRow("Tranh Son Dau - Ho Guom", "Art", "5.200.000 VND", "9", "RUNNING", "Con 3 ngay"),
                new AuctionRow("Honda Wave Alpha 2023", "Vehicle", "15.000.000 VND", "4", "OPEN", "Con 4 ngay")
        );
        statusLabel.setText(message);
    }

    private void applyFilter() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        filteredAuctions.setAll(auctions.filtered(row ->
                keyword.isEmpty()
                        || row.titleProperty().get().toLowerCase().contains(keyword)
                        || row.categoryProperty().get().toLowerCase().contains(keyword)
                        || row.statusProperty().get().toLowerCase().contains(keyword)
        ));
    }

    private void updateStats() {
        totalAuctionsLabel.setText(String.valueOf(auctions.size()));
        long runningCount = auctions.stream()
                .filter(row -> "RUNNING".equalsIgnoreCase(row.statusProperty().get()))
                .count();
        runningAuctionsLabel.setText(String.valueOf(runningCount));
        int totalBids = auctions.stream()
                .mapToInt(row -> Integer.parseInt(row.bidsProperty().get()))
                .sum();
        totalBidLabel.setText(String.valueOf(totalBids));
    }

    private String formatEndTime(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        try {
            LocalDateTime time = LocalDateTime.parse(value);
            return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception ignored) {
            return value;
        }
    }

    public static class AuctionRow {
        private final SimpleStringProperty title;
        private final SimpleStringProperty category;
        private final SimpleStringProperty price;
        private final SimpleStringProperty bids;
        private final SimpleStringProperty status;
        private final SimpleStringProperty endTime;

        public AuctionRow(String title, String category, String price, String bids, String status, String endTime) {
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.price = new SimpleStringProperty(price);
            this.bids = new SimpleStringProperty(bids);
            this.status = new SimpleStringProperty(status);
            this.endTime = new SimpleStringProperty(endTime);
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public SimpleStringProperty categoryProperty() {
            return category;
        }

        public SimpleStringProperty priceProperty() {
            return price;
        }

        public SimpleStringProperty bidsProperty() {
            return bids;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }

        public SimpleStringProperty endTimeProperty() {
            return endTime;
        }
    }
}

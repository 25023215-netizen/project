package frontend.bidding;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller cho trang đặt giá đấu.
 */
public class BiddingController {

    @FXML private TextField txtAuctionId;
    @FXML private TextField txtAmount;
    @FXML private Label lblStatus;

    // Bảng lịch sử
    @FXML private TableView<String[]> bidHistoryTable;
    @FXML private TableColumn<String[], String> colBidAuctionId;
    @FXML private TableColumn<String[], String> colBidProduct;
    @FXML private TableColumn<String[], String> colBidAmount;
    @FXML private TableColumn<String[], String> colBidTime;
    @FXML private TableColumn<String[], String> colBidStatus;

    private String currentUser = "admin"; // giả lập login

    private ObservableList<String[]> bidHistory;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Cấu hình cột lịch sử
        colBidAuctionId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colBidProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colBidAmount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        colBidTime.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
        colBidStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));

        // Style cột kết quả
        colBidStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Đang đấu" -> setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                        case "Thắng" -> setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                        case "Thua" -> setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Dữ liệu mẫu lịch sử
        bidHistory = FXCollections.observableArrayList();
        bidHistory.addAll(
                new String[]{"1001", "iPhone 15 Pro Max", "30,000,000 VNĐ", "20/04/2026 08:15", "Đang đấu"},
                new String[]{"1002", "MacBook Pro M3", "40,000,000 VNĐ", "19/04/2026 14:30", "Đang đấu"},
                new String[]{"1005", "iPad Air M2", "18,500,000 VNĐ", "18/04/2026 10:00", "Thua"},
                new String[]{"1006", "Apple Watch Ultra 2", "23,000,000 VNĐ", "17/04/2026 20:45", "Thắng"}
        );
        bidHistoryTable.setItems(bidHistory);
    }

    @FXML
    private void handleBid() {
        String auctionIdText = txtAuctionId.getText().trim();
        String amountText = txtAmount.getText().trim();

        if (auctionIdText.isEmpty() || amountText.isEmpty()) {
            lblStatus.setText("⚠️ Vui lòng nhập đầy đủ thông tin!");
            lblStatus.setStyle("-fx-text-fill: #f59e0b;");
            return;
        }

        try {
            int auctionId = Integer.parseInt(auctionIdText);
            double amount = Double.parseDouble(amountText.replace(",", ""));

            if (amount <= 0) {
                lblStatus.setText("❌ Số tiền phải lớn hơn 0!");
                lblStatus.setStyle("-fx-text-fill: #dc2626;");
                return;
            }

            // Thêm vào lịch sử
            String timeNow = LocalDateTime.now().format(FORMATTER);
            bidHistory.add(0, new String[]{
                    String.valueOf(auctionId),
                    "Sản phẩm #" + auctionId,
                    String.format("%,.0f VNĐ", amount),
                    timeNow,
                    "Đang đấu"
            });

            lblStatus.setText("✅ Đặt giá thành công! " + String.format("%,.0f VNĐ", amount));
            lblStatus.setStyle("-fx-text-fill: #16a34a;");

            // Xóa form
            txtAuctionId.clear();
            txtAmount.clear();

        } catch (NumberFormatException e) {
            lblStatus.setText("❌ Vui lòng nhập số hợp lệ!");
            lblStatus.setStyle("-fx-text-fill: #dc2626;");
        }
    }

    @FXML
    private void handleClear() {
        txtAuctionId.clear();
        txtAmount.clear();
        lblStatus.setText("");
    }
}
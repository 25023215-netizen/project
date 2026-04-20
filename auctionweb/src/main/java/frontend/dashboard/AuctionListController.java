package frontend.dashboard;

import backend.model.AuctionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller cho trang danh sách phiên đấu giá.
 */
public class AuctionListController {

    @FXML private TableView<AuctionItem> auctionTable;
    @FXML private TableColumn<AuctionItem, Integer> colId;
    @FXML private TableColumn<AuctionItem, String> colName;
    @FXML private TableColumn<AuctionItem, Double> colStartPrice;
    @FXML private TableColumn<AuctionItem, Double> colCurrentPrice;
    @FXML private TableColumn<AuctionItem, String> colSeller;
    @FXML private TableColumn<AuctionItem, String> colStatus;
    @FXML private TableColumn<AuctionItem, String> colEndTime;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatus;
    @FXML private Label lblTotal;

    private ObservableList<AuctionItem> allItems;

    @FXML
    public void initialize() {
        // Cấu hình cột
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStartPrice.setCellValueFactory(new PropertyValueFactory<>("startPrice"));
        colCurrentPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        colSeller.setCellValueFactory(new PropertyValueFactory<>("seller"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        // Format cột giá
        colStartPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? "" : String.format("%,.0f VNĐ", price));
            }
        });
        colCurrentPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? "" : String.format("%,.0f VNĐ", price));
                if (!empty && price != null) {
                    setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                }
            }
        });

        // Style cột trạng thái
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Đang diễn ra" -> setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                        case "Sắp diễn ra" -> setStyle("-fx-text-fill: #ca8a04; -fx-font-weight: bold;");
                        case "Đã kết thúc" -> setStyle("-fx-text-fill: #9ca3af;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // Bộ lọc trạng thái
        filterStatus.setItems(FXCollections.observableArrayList(
                "Tất cả", "Đang diễn ra", "Sắp diễn ra", "Đã kết thúc"));
        filterStatus.setValue("Tất cả");

        // Load dữ liệu mẫu
        loadSampleData();

        // Listener tìm kiếm
        searchField.textProperty().addListener((obs, oldV, newV) -> filterData());
        filterStatus.valueProperty().addListener((obs, oldV, newV) -> filterData());
    }

    private void loadSampleData() {
        allItems = FXCollections.observableArrayList(
                new AuctionItem(1001, "iPhone 15 Pro Max 256GB", "Máy mới nguyên seal",
                        25000000, 32500000, "Nguyễn Văn A", "Đang diễn ra", "20/04/2026 18:00"),
                new AuctionItem(1002, "MacBook Pro M3 14 inch", "RAM 16GB, SSD 512GB",
                        35000000, 42000000, "Trần Thị B", "Đang diễn ra", "21/04/2026 20:00"),
                new AuctionItem(1003, "Samsung Galaxy S24 Ultra", "Titanium Black 512GB",
                        20000000, 20000000, "Lê Văn C", "Sắp diễn ra", "22/04/2026 10:00"),
                new AuctionItem(1004, "Sony WH-1000XM5", "Tai nghe chống ồn cao cấp",
                        5000000, 7200000, "Phạm Thị D", "Đang diễn ra", "20/04/2026 15:00"),
                new AuctionItem(1005, "iPad Air M2", "64GB WiFi + Cellular",
                        15000000, 19500000, "Hoàng Văn E", "Đã kết thúc", "19/04/2026 12:00"),
                new AuctionItem(1006, "Apple Watch Ultra 2", "GPS + Cellular 49mm",
                        18000000, 23000000, "Ngô Thị F", "Đã kết thúc", "18/04/2026 22:00"),
                new AuctionItem(1007, "Canon EOS R6 Mark II", "Body Only",
                        40000000, 40000000, "Đỗ Văn G", "Sắp diễn ra", "25/04/2026 14:00"),
                new AuctionItem(1008, "Nintendo Switch OLED", "Neon Blue/Red",
                        7000000, 9800000, "Vũ Thị H", "Đang diễn ra", "20/04/2026 21:00"),
                new AuctionItem(1009, "Dell XPS 15", "Core i9, 32GB RAM, RTX 4060",
                        45000000, 52000000, "Bùi Văn I", "Đang diễn ra", "23/04/2026 16:00"),
                new AuctionItem(1010, "AirPods Pro 2", "USB-C, MagSafe",
                        4500000, 5800000, "Lý Thị K", "Đã kết thúc", "17/04/2026 09:00")
        );

        auctionTable.setItems(allItems);
        updateTotal(allItems.size());
    }

    private void filterData() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = filterStatus.getValue();

        ObservableList<AuctionItem> filtered = allItems.filtered(item -> {
            boolean matchSearch = search.isEmpty() || item.getName().toLowerCase().contains(search);
            boolean matchStatus = "Tất cả".equals(status) || item.getStatus().equals(status);
            return matchSearch && matchStatus;
        });

        auctionTable.setItems(filtered);
        updateTotal(filtered.size());
    }

    private void updateTotal(int count) {
        lblTotal.setText("Tổng: " + count + " phiên đấu giá");
    }

    @FXML
    private void onCreateAuction() {
        // TODO: Mở dialog tạo phiên đấu giá mới
        System.out.println("Tạo phiên đấu giá mới...");
    }

    @FXML
    private void onRefresh() {
        searchField.clear();
        filterStatus.setValue("Tất cả");
        auctionTable.setItems(allItems);
        updateTotal(allItems.size());
    }
}

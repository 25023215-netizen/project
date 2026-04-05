package frontend.auction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class AuctionListController {

    @FXML private TableView<AuctionItem> auctionTable;
    @FXML private TableColumn<AuctionItem, Integer> colId;
    @FXML private TableColumn<AuctionItem, String> colName;
    @FXML private TableColumn<AuctionItem, Double> colPrice;
    @FXML private TableColumn<AuctionItem, String> colEndTime;
    @FXML private TableColumn<AuctionItem, String> colStatus;
    @FXML private TableColumn<AuctionItem, Void> colAction;

    @FXML
    public void initialize() {
        // 1. Liên kết các cột với các thuộc tính trong class AuctionItem
        // Tên trong ngoặc kép phải CHUẨN XÁC với tên biến trong file AuctionItem.java
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Thêm nút "Tham gia" vào cột cuối cùng
        addButtonToTable();

        // 3. Tạo dữ liệu giả (Mock data) để hiển thị thử
        ObservableList<AuctionItem> mockData = FXCollections.observableArrayList(
            new AuctionItem(1, "Laptop Dell XPS 15", 25000000, "15/05/2026 20:00", "Đang diễn ra"),
            new AuctionItem(2, "Điện thoại iPhone 15 Pro Max", 28000000, "16/05/2026 15:30", "Sắp diễn ra"),
            new AuctionItem(3, "Đồng hồ Rolex Submariner", 150000000, "20/05/2026 09:00", "Đang diễn ra")
        );

        // Nạp dữ liệu vào bảng
        auctionTable.setItems(mockData);
    }

    // Hàm hỗ trợ tạo Nút bấm vào trong Cell của TableView
    private void addButtonToTable() {
        Callback<TableColumn<AuctionItem, Void>, TableCell<AuctionItem, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<AuctionItem, Void> call(final TableColumn<AuctionItem, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Tham gia");

                    {
                        // Cài đặt hành động khi bấm nút
                        btn.setOnAction((event) -> {
                            AuctionItem data = getTableView().getItems().get(getIndex());
                            System.out.println("Đang mở phòng đấu giá cho sản phẩm: " + data.getName());
                            // TODO: Thêm code chuyển màn hình sang Phòng Đấu Giá chi tiết ở đây
                        });
                        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    // Xử lý sự kiện khi click button Đóng
    @FXML
    private void onClose() {
        // Lấy Stage hiện tại và đóng cửa sổ
        Stage stage = (Stage) auctionTable.getScene().getWindow();
        stage.close();
    }
}
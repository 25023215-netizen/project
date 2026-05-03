package frontend.dashboard;

import frontend.auction.AuctionDetailController;
import frontend.model.AuctionRow;
import frontend.service.DashboardService;
import frontend.utils.NavigationManager;
import frontend.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller cho Dashboard chính.
 * Hiển thị danh sách phiên đấu giá, thống kê, tìm kiếm.
 * Click vào row để mở chi tiết phiên đấu giá.
 */
public class DashboardController {
    @FXML private Label totalAuctionsLabel;
    @FXML private Label runningAuctionsLabel;
    @FXML private Label totalBidLabel;
    @FXML private Label statusLabel;
    @FXML private Label userInfoLabel;
    @FXML private TextField searchField;

    @FXML private TableView<AuctionRow> auctionTable;
    @FXML private TableColumn<AuctionRow, String> titleColumn;
    @FXML private TableColumn<AuctionRow, String> categoryColumn;
    @FXML private TableColumn<AuctionRow, String> priceColumn;
    @FXML private TableColumn<AuctionRow, String> bidsColumn;
    @FXML private TableColumn<AuctionRow, String> statusColumn;
    @FXML private TableColumn<AuctionRow, String> endTimeColumn;

    @FXML private Button manageItemsButton;

    private final ObservableList<AuctionRow> auctions = FXCollections.observableArrayList();
    private final ObservableList<AuctionRow> filteredAuctions = FXCollections.observableArrayList();
    private final DashboardService dashboardService = new DashboardService();

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

        // Double-click vào row để mở chi tiết
        auctionTable.setOnMouseClicked(this::onTableClick);

        // Hiển thị thông tin user
        if (SessionManager.getInstance().isLoggedIn()) {
            if (userInfoLabel != null) {
                userInfoLabel.setText("Xin chao, " + SessionManager.getInstance().getFullname()
                        + " (" + SessionManager.getInstance().getRole() + ")");
            }
        }

        // Chỉ hiện nút quản lý sản phẩm cho Seller
        if (manageItemsButton != null) {
            manageItemsButton.setVisible(SessionManager.getInstance().isSeller());
            manageItemsButton.setManaged(SessionManager.getInstance().isSeller());
        }

        loadAuctions();
    }

    @FXML
    private void onRefresh() {
        loadAuctions();
    }

    /**
     * Mở màn hình quản lý sản phẩm (dành cho Seller).
     */
    @FXML
    private void onManageItems() {
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            NavigationManager.switchScene(stage, "/fxml/item_management.fxml", "/style/item_management.css", "Quan ly san pham", 1180, 760);
        } catch (Exception e) {
            statusLabel.setText("Loi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đăng xuất và quay lại màn hình đăng nhập.
     */
    @FXML
    private void onLogout() {
        SessionManager.getInstance().clear();
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            NavigationManager.switchScene(stage, "/fxml/signin.fxml", "/style/signin.css", "Online Auction System - Sign In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Double-click vào row -> mở chi tiết phiên đấu giá.
     */
    private void onTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            AuctionRow selected = auctionTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openAuctionDetail(selected.getId());
            }
        }
    }

    private void openAuctionDetail(Long auctionId) {
        try {
            Stage stage = (Stage) auctionTable.getScene().getWindow();
            AuctionDetailController controller = NavigationManager.switchScene(stage, "/fxml/auction_detail.fxml", "/style/auction_detail.css", "Chi tiet phien dau gia", 1180, 760);
            controller.setAuctionId(auctionId);
        } catch (Exception e) {
            statusLabel.setText("Loi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAuctions() {
        DashboardService.DashboardResult result = dashboardService.fetchAuctions();
        auctions.setAll(result.auctions);
        statusLabel.setText(result.message);
        if (!result.success) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setStyle("-fx-text-fill: black;");
        }
        applyFilter();
        updateStats();
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
                .mapToInt(row -> {
                    try { return Integer.parseInt(row.bidsProperty().get()); }
                    catch (NumberFormatException e) { return 0; }
                })
                .sum();
        totalBidLabel.setText(String.valueOf(totalBids));
    }
}

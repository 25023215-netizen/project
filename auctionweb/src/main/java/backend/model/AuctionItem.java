package backend.model;

import javafx.beans.property.*;

/**
 * Model cho sản phẩm đấu giá - dùng trong JavaFX TableView.
 */
public class AuctionItem {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty startPrice;
    private final DoubleProperty currentPrice;
    private final StringProperty seller;
    private final StringProperty status; // "Đang diễn ra", "Sắp diễn ra", "Đã kết thúc"
    private final StringProperty endTime;

    public AuctionItem(int id, String name, String description, double startPrice,
                       double currentPrice, String seller, String status, String endTime) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.startPrice = new SimpleDoubleProperty(startPrice);
        this.currentPrice = new SimpleDoubleProperty(currentPrice);
        this.seller = new SimpleStringProperty(seller);
        this.status = new SimpleStringProperty(status);
        this.endTime = new SimpleStringProperty(endTime);
    }

    // Property accessors
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty startPriceProperty() { return startPrice; }
    public DoubleProperty currentPriceProperty() { return currentPrice; }
    public StringProperty sellerProperty() { return seller; }
    public StringProperty statusProperty() { return status; }
    public StringProperty endTimeProperty() { return endTime; }

    // Getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public double getStartPrice() { return startPrice.get(); }
    public double getCurrentPrice() { return currentPrice.get(); }
    public String getSeller() { return seller.get(); }
    public String getStatus() { return status.get(); }
    public String getEndTime() { return endTime.get(); }

    // Setters
    public void setCurrentPrice(double price) { this.currentPrice.set(price); }
    public void setStatus(String status) { this.status.set(status); }
}

package frontend.model;

import javafx.beans.property.SimpleStringProperty;

public class AuctionRow {
    private final Long id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty category;
    private final SimpleStringProperty price;
    private final SimpleStringProperty bids;
    private final SimpleStringProperty status;
    private final SimpleStringProperty endTime;

    public AuctionRow(Long id, String title, String category, String price,
                      String bids, String status, String endTime) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleStringProperty(price);
        this.bids = new SimpleStringProperty(bids);
        this.status = new SimpleStringProperty(status);
        this.endTime = new SimpleStringProperty(endTime);
    }

    public Long getId() { return id; }
    public SimpleStringProperty titleProperty() { return title; }
    public SimpleStringProperty categoryProperty() { return category; }
    public SimpleStringProperty priceProperty() { return price; }
    public SimpleStringProperty bidsProperty() { return bids; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty endTimeProperty() { return endTime; }
}

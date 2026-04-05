package frontend.auction;

public class AuctionItem {
    private int id;
    private String name;
    private double currentPrice;
    private String endTime;
    private String status;

    public AuctionItem(int id, String name, double currentPrice, String endTime, String status) {
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.endTime = endTime;
        this.status = status;
    }

    // Các hàm Getter bắt buộc phải có để TableView có thể đọc được dữ liệu
    public int getId() { return id; }
    public String getName() { return name; }
    public double getCurrentPrice() { return currentPrice; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
}
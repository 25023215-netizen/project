package frontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import frontend.model.AuctionRow;
import frontend.utils.BackendClient;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardService {
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public static class DashboardResult {
        public boolean success;
        public List<AuctionRow> auctions;
        public String message;

        public DashboardResult(boolean success, List<AuctionRow> auctions, String message) {
            this.success = success;
            this.auctions = auctions;
            this.message = message;
        }
    }

    public DashboardResult fetchAuctions() {
        try {
            HttpResponse<String> response = BackendClient.getInstance().get("/auctions");
            if (response.statusCode() == 200) {
                List<AuctionRow> list = parseAuctions(response.body());
                return new DashboardResult(true, list, "Da tai danh sach dau gia tu server.");
            } else {
                return new DashboardResult(false, getFallbackAuctions(), "Server tra ve loi: " + response.statusCode());
            }
        } catch (Exception e) {
            return new DashboardResult(false, getFallbackAuctions(), "Dang hien thi du lieu mau vi chua ket noi duoc server.");
        }
    }

    private List<AuctionRow> parseAuctions(String body) throws Exception {
        List<AuctionRow> rows = new ArrayList<>();
        JsonNode root = new ObjectMapper().readTree(body);
        for (JsonNode node : root) {
            Long id = node.path("id").asLong();
            String title = node.path("title").asText();
            String category = node.path("category").asText();
            BigDecimal price = new BigDecimal(node.path("currentPrice").asText("0"));
            String bidCount = String.valueOf(node.path("bidCount").asInt());
            String status = node.path("status").asText();
            String endTime = formatEndTime(node.path("endTime").asText());
            rows.add(new AuctionRow(id, title, category, currencyFormat.format(price), bidCount, status, endTime));
        }
        return rows;
    }

    public List<AuctionRow> getFallbackAuctions() {
        List<AuctionRow> fallback = new ArrayList<>();
        fallback.add(new AuctionRow(0L, "iPhone 15 Pro Max 256GB", "Electronics", "25.000.000 VND", "18", "RUNNING", "Con 2 ngay"));
        fallback.add(new AuctionRow(0L, "Tranh Son Dau - Ho Guom", "Art", "5.200.000 VND", "9", "RUNNING", "Con 3 ngay"));
        fallback.add(new AuctionRow(0L, "Honda Wave Alpha 2023", "Vehicle", "15.000.000 VND", "4", "OPEN", "Con 4 ngay"));
        return fallback;
    }

    private String formatEndTime(String value) {
        if (value == null || value.isBlank()) return "-";
        try {
            LocalDateTime time = LocalDateTime.parse(value);
            return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception ignored) {
            return value;
        }
    }
}

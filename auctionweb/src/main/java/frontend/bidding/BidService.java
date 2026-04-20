package frontend.bidding;

import backend.model.Bid;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BidService {

    public static void placeBid(Bid bid) {
        try {
            URL url = new URL("http://localhost:8080/api/bid");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                "{\"auctionId\":%d,\"username\":\"%s\",\"amount\":%f}",
                bid.getAuctionId(),
                bid.getUsername(),
                bid.getAmount()
            );

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("Response: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
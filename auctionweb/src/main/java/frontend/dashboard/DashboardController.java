package frontend.dashboard;

import frontend.utils.BackendClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardController {

    @FXML private TableView<JSONObject> auctionTable;
    @FXML private TableColumn<JSONObject, String> idColumn;
    @FXML private TableColumn<JSONObject, String> nameColumn;
    @FXML private TableColumn<JSONObject, String> priceColumn;
    @FXML private TableColumn<JSONObject, String> statusColumn;
    @FXML private TableColumn<JSONObject, String> endTimeColumn;

    private ObservableList<JSONObject> auctionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadAuctions();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().optLong("id"))));
        
        nameColumn.setCellValueFactory(data -> {
            JSONObject item = data.getValue().optJSONObject("item");
            return new SimpleStringProperty(item != null ? item.optString("name") : "N/A");
        });

        priceColumn.setCellValueFactory(data -> {
            JSONObject item = data.getValue().optJSONObject("item");
            return new SimpleStringProperty(item != null ? "$" + item.optDouble("currentPrice") : "0.0");
        });

        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().optString("status")));
        endTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().optString("endTime")));
    }

    private void loadAuctions() {
        new Thread(() -> {
            try {
                HttpResponse<String> response = BackendClient.getInstance().get("/auctions");
                if (response.statusCode() == 200) {
                    JSONArray jsonArray = new JSONArray(response.body());
                    Platform.runLater(() -> {
                        auctionList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            auctionList.add(jsonArray.getJSONObject(i));
                        }
                        auctionTable.setItems(auctionList);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Simple close for now
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }
}

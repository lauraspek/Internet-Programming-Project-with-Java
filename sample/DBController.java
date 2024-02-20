package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import dbObject.*;

public class DBController {

    @FXML
    private TextArea dbArea;

    HandlerDb gestore = new HandlerDb();

    public DBController()  {
        gestore.openDb();
    }

    public void onCollectClick(ActionEvent actionEvent) {
        dbArea.setText(gestore.newsCollector());
    }

}


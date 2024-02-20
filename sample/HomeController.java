package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    private Main mainApp;



    private Stage modWindow; // Variabile per tenere traccia dello stato di apertura della finestra onModClick
    private Stage rssWindow; // Variabile per tenere traccia dello stato di apertura della finestra onRSSClick
    private Stage dbWindow; // Riferimento alla finestra di onDBClick
    private Stage usersWindow; // Riferimento alla finestra di onUsersClick
    private Stage newsWindow; // Riferimento alla finestra di onNewsClick

    public void setMain(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
    }

    @FXML
    public void onRSSClick(ActionEvent actionEvent) throws IOException{
        if (rssWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rssPane.fxml"));
            Parent root = loader.load();

            rssWindow = new Stage();
            rssWindow.setScene(new Scene(root));
            rssWindow.setOnCloseRequest(event -> rssWindow = null); // reimposta rssWindow a null quando la finestra viene chiusa
            rssWindow.show();
        }
    }

    @FXML
    public void onDBClick(ActionEvent actionEvent) throws IOException {
        if (dbWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dbPane.fxml"));
            Parent root = loader.load();

            dbWindow = new Stage();
            dbWindow.setScene(new Scene(root));
            dbWindow.setOnCloseRequest(event -> dbWindow = null);
            dbWindow.show();
        }
    }

    @FXML
    public void onUsersClick(ActionEvent actionEvent) throws IOException {
        if (usersWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userPane.fxml"));
            Parent root = loader.load();

            usersWindow = new Stage();
            usersWindow.setScene(new Scene(root));
            usersWindow.setOnCloseRequest(event -> usersWindow = null);
            usersWindow.show();
        }
    }

    @FXML
    public void onModClick(ActionEvent actionEvent) throws IOException{
        if(modWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("feedbackPane.fxml"));
            Parent root = loader.load();

            modWindow = new Stage();
            modWindow.setScene(new Scene(root));
            modWindow.setOnCloseRequest(event -> modWindow = null);
            modWindow.show();
        }
    }


    @FXML
    public void onNewsClick(ActionEvent actionEvent) throws IOException{
        if (newsWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newsPane.fxml"));
            Parent root = loader.load();

            newsWindow = new Stage();
            newsWindow.setScene(new Scene(root));
            newsWindow.setOnCloseRequest(event -> newsWindow = null);
            newsWindow.show();
        }
    }


    public void onRatingClick(ActionEvent actionEvent) throws IOException {
        if (newsWindow == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ratingPane.fxml"));
            Parent root = loader.load();

            newsWindow = new Stage();
            newsWindow.setScene(new Scene(root));
            newsWindow.setOnCloseRequest(event -> newsWindow = null);
            newsWindow.show();
        }
    }
}

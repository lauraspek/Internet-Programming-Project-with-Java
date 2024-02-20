package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import dbObject.*;

import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class RssController {

    @FXML
    private TextField addRssUrl;
    @FXML
    private TextField addRssDescription;
    @FXML
    private TextField listRssDescription;
    @FXML
    private TextField listRssUrl;
    @FXML
    private TextField listRssApproved;

    //inizializzo iteratore per scorrere
    public int iterator = -1;
    public ArrayList<Rss> allRss = new ArrayList<Rss>();
    HandlerDb gestore = new HandlerDb();

    public RssController() throws SQLException, ClassNotFoundException {
        gestore.openDb();
        allRss = gestore.getAllRss();
    }


    public void setRssArray(int i, ArrayList<Rss> allRss){
        listRssDescription.setText(allRss.get(i).getDescription());
        listRssUrl.setText(allRss.get(i).getUrl());
        listRssApproved.setText(String.valueOf(allRss.get(i).getApproved()));
    }



    public void RssNext(ActionEvent actionEvent) {
        if (allRss.size() == 0) {
            // mostra un messaggio di errore all'utente tramite un popup
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");

            alert.showAndWait();
        } else {
            if (iterator == allRss.size() - 1){
                iterator = 0;
            } else{
                iterator = iterator + 1;
            }
            setRssArray(iterator, allRss);
        }
    }

    public void RssBack(ActionEvent actionEvent) {
        if (allRss.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");
            alert.showAndWait();
        } else if (iterator == 0){
            iterator = allRss.size() - 1;
        } else if (iterator == -1){
            iterator = 0;
        }else{
            iterator = iterator - 1;
        }
        setRssArray(iterator, allRss);

    }


    public void onRemoveClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        gestore.deleteRss(allRss.get(iterator));
        allRss = gestore.getAllRss();
        if (allRss.isEmpty()) {
            iterator = 1;
        }else if(iterator >= allRss.size()){
            iterator = 0;
            setRssArray(iterator, allRss);
        }
    }

    public void onApproveRss(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String input = listRssApproved.getText();
        if (input.equals("false")){
            gestore.updateRssApproved(allRss.get(iterator));
            allRss = gestore.getAllRss();
            setRssArray(iterator, allRss);
        } else  {
            //gestore.markComment(allRss.get(iterator));
           // allRss = gestore.getAllRss();
            //setRssArray(iterator, allRss);
        }
    }

    public void onAddClick(ActionEvent actionEvent) {
        String url = addRssUrl.getText();
        String description = addRssDescription.getText();
        // Verifica che entrambi i campi siano riempiti
        if (url.isEmpty() || description.isEmpty() || gestore.checkRss(url)) {   //check e' campo per vedere se gli sto inserendo un url gia preinserito
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("There is an error during the upload");
            alert.setContentText("Enter a valid URL and description for the RSS.");
            alert.showAndWait();
            addRssDescription.clear();
            addRssUrl.clear();
        } else{
            if (gestore.addRssFromAdmin(description, url) == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "RSS added successfully!");
                alert.showAndWait();
                // Pulisco i campi di input
                addRssDescription.clear();
                addRssUrl.clear();
                allRss = gestore.getAllRss();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding RSS to database!");
                alert.showAndWait();
                // Pulisco i campi di input
                addRssDescription.clear();
                addRssUrl.clear();
            }
        }
    }



}




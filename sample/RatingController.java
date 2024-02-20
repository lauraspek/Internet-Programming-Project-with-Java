package sample;

import dbObject.Comment;
import dbObject.Rating;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;

public class RatingController {

    @FXML
    private TextField ratingVote;
    @FXML
    private TextField newsTitle;
    @FXML
    private TextField userNickname;

    public int iterator = -1;
    public ArrayList<Rating> allRatings = new ArrayList<Rating>();
    HandlerDb gestore = new HandlerDb();

    public RatingController()  {
        gestore.openDb();
        allRatings = gestore.getAllRatings();
    }

    public void setRatingArray(int i, ArrayList<Rating> allRatings)   {
        ratingVote.setText(String.valueOf(allRatings.get(i).getVote()));
        newsTitle.setText(gestore.getNews(allRatings.get(i).getNewsId()).get(0).getTitle());
        userNickname.setText(gestore.getUser(allRatings.get(i).getUserId()).get(0).getNickname());
    }

    public void RatingNext(ActionEvent actionEvent) {
        if (allRatings.size() == 0) {
            // mostra un messaggio di errore all'utente tramite un popup
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No rating found");
            alert.setContentText("There are no ratings to scroll!");

            alert.showAndWait();
        } else {
            if (iterator == allRatings.size() - 1){
                iterator = 0;
            } else{
                iterator = iterator + 1;
            }
            setRatingArray(iterator, allRatings);
        }
    }


    public void RatingBack(ActionEvent actionEvent) {
        if (allRatings.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No rating found");
            alert.setContentText("There are no ratings to scroll!");
            alert.showAndWait();
        } else if (iterator == 0){
            iterator = allRatings.size() - 1;
            setRatingArray(iterator, allRatings);
        } else if (iterator == -1){
            iterator = 0;
            setRatingArray(iterator, allRatings);
        }else{
            iterator = iterator - 1;
            setRatingArray(iterator, allRatings);
        }
    }

}

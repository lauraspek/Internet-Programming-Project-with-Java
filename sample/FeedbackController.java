package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import dbObject.*;
import java.util.ArrayList;

public class FeedbackController {


    @FXML
    private TextField commentText;
    @FXML
    private TextField commentMarked;
    @FXML
    private TextField newsTitle;
    @FXML
    private TextField userNickname;

    //inizializzo iteratore per scorrere
    public int iterator = -1;
    public ArrayList<Comment> allComments = new ArrayList<Comment>();
    HandlerDb gestore = new HandlerDb();

    public FeedbackController() {
        gestore.openDb();
        allComments = gestore.getAllComments();
    }

    public void setCommentArray(int i, ArrayList<Comment> allComments) {
        commentText.setText(allComments.get(i).getText());
        commentMarked.setText(String.valueOf(allComments.get(i).getMarked()));
        newsTitle.setText(gestore.getCommentNewsTitle(allComments.get(i)));
        userNickname.setText(gestore.getCommentUser(allComments.get(i)));
    }

    public void CommentNext(ActionEvent actionEvent){
        if (allComments.size() == 0) {
            // mostra un messaggio di errore all'utente tramite un popup
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");

            alert.showAndWait();
        } else {
            if (iterator == allComments.size() - 1){
                iterator = 0;
            } else{
                iterator = iterator + 1;
            }
            setCommentArray(iterator, allComments);
        }
    }


    public void CommentBack(ActionEvent actionEvent){
        if (allComments.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");
            alert.showAndWait();
        } else if (iterator == 0){
            iterator = allComments.size() - 1;
            setCommentArray(iterator, allComments);
        } else if (iterator == -1){
            iterator = 0;
            setCommentArray(iterator, allComments);
        }else{
            iterator = iterator - 1;
            setCommentArray(iterator, allComments);
        }
    }


    public void onDeleteComment(ActionEvent actionEvent) {
       gestore.deleteComment( allComments.get(iterator));
        allComments = gestore.getAllComments();
        if (iterator > allComments.size() - 1) {
            setCommentArray(0, allComments);
        }else {
            setCommentArray(iterator, allComments);
        }
    }



    public void onUnmarkComment(ActionEvent actionEvent) {
       String input = commentMarked.getText();
        if (input.equals("true")){
            gestore.deleteMarkComment(allComments.get(iterator));
            allComments = gestore.getAllComments();
            setCommentArray(iterator, allComments);
        }
        //else  {
           //gestore.markComment(allComments.get(iterator));
           //allComments = gestore.getAllComments();
           //setCommentArray(iterator, allComments);
        //}
    }

}




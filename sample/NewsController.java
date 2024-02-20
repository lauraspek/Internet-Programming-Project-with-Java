package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import dbObject.*;
import java.util.ArrayList;



public class NewsController {

    @FXML
    private TextField news_title;
    @FXML
    private TextField news_date;
    @FXML
    private TextField news_description;
    @FXML
    private TextField news_author;
    @FXML
    private TextField news_url;
    @FXML
    private TextField news_totComment;
    @FXML
    private TextField news_totRatings;

    //inizializzo iteratore per scorrere
    public int iterator = -1;
    public ArrayList<News> allNews = new ArrayList<News>();
    HandlerDb gestore = new HandlerDb();

    public NewsController() {
        gestore.openDb();
        allNews = gestore.getAllNews();
    }


    public void setNewsArray(int i, ArrayList<News> allNews) {
        news_title.setText(allNews.get(i).getTitle());
        news_description.setText(allNews.get(i).getDescription());
        news_date.setText(allNews.get(i).getDate().toString());
        news_author.setText(allNews.get(i).getAuthor());
        news_url.setText(allNews.get(i).getUrl());
        news_totComment.setText(gestore.getNumberCommentsNews(allNews.get(i).getId()) + "");
        news_totRatings.setText(gestore.getNumberRatingNews(allNews.get(i).getId()) + "");

    }

    @FXML
    public void NewsNext(ActionEvent actionEvent) {
        if (iterator == allNews.size() - 1){
            iterator = 0;
        } else{
            iterator = iterator + 1;
        }
        setNewsArray(iterator, allNews);
    }

    @FXML
    public void NewsBack(ActionEvent actionEvent){
        if (iterator == 0){
            iterator = allNews.size() - 1;
        } else if (iterator == -1){
            iterator = 0;
        }else{
            iterator = iterator - 1;
        }
        setNewsArray(iterator, allNews);

    }

    @FXML
    public void onRemove(ActionEvent actionEvent){
        gestore.deleteNews(allNews.get(iterator));
        allNews = gestore.getAllNews();
        if (iterator > allNews.size() - 1) {
            setNewsArray(0, allNews);
        }else {
            setNewsArray(iterator, allNews);
        }

    }

}

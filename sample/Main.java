package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import NabootPackage.*;

public class Main extends Application {


    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        NabootThread botThread = new NabootThread(new Naboot("5444520458:AAHSSxCaQoRMXISV04tB5h6KtzCTl44sfcQ"));
        botThread.start();

        System.out.println("DEBUG: post bot thread");

        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    public void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPane.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setMainApp(this);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showHomeScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homePane.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();
        homeController.setMain(this, primaryStage); // Passa l'oggetto Stage corrente al HomeController

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)  {
        launch(args);
    }

}

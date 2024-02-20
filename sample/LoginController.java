package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;


public class LoginController {

    @FXML
    TextField username_field;
    @FXML
    PasswordField password_field;
    @FXML
    Label status_label;

    String username;
    String password;
    private Main mainApp;
    HandlerDb gestore = new HandlerDb();

    public LoginController() {
        gestore.openDb();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }


    public void loginAction(ActionEvent actionEvent) throws IOException {
        username = username_field.getText();
        password = password_field.getText();

        if (username.equals("") || password.equals("") || gestore.login(username, password) == false) {
            status_label.setText("Login status: WRONG CREDENTIALS, PLEASE RETRY!");
            username_field.setText("");
            password_field.setText("");
        }else {
            // Chiamata al metodo showHomeScreen() presente nel Main per passare alla schermata di home
            mainApp.showHomeScreen();
        }
    }

}
    





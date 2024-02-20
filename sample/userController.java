package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import dbObject.*;
import java.util.ArrayList;

public class userController {
    @FXML
    TextField userTelegramId;
    @FXML
    TextField userNickname;
    @FXML
    TextField userRole;
    @FXML
    TextField userApproved;


    //inizializzo iteratore per scorrere
    public int iterator = -1;
    public ArrayList<User> allUsers = new ArrayList<User>();
    HandlerDb gestore = new HandlerDb();

    public userController()  {
        gestore.openDb();
        allUsers = gestore.getActiveUser();
    }

    public void setUserArray(int i, ArrayList<User> allUsers) {
            userTelegramId.setText(allUsers.get(i).getTelegramId());
            userNickname.setText(allUsers.get(i).getNickname());
            userRole.setText(allUsers.get(i).getRole());
            userApproved.setText(String.valueOf(allUsers.get(i).getApproved()));

    }

    public void UserNext(ActionEvent actionEvent) {
        if (allUsers.size() == 0) {
            // mostra un messaggio di errore all'utente tramite un popup
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");

            alert.showAndWait();
        } else {
            if (iterator == allUsers.size() - 1){
                iterator = 0;
            } else{
                iterator = iterator + 1;
            }
            setUserArray(iterator, allUsers);
        }
    }

    public void UserBack(ActionEvent actionEvent){
        if (allUsers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No item found");
            alert.setContentText("There are no items to scroll!");
            alert.showAndWait();
        } else if (iterator == 0){
            iterator = allUsers.size() - 1;
        } else if (iterator == -1){
            iterator = 0;
        }else{
            iterator = iterator - 1;
        }
        setUserArray(iterator, allUsers);
    }

    public void onDeactivateUser(ActionEvent actionEvent) {
        gestore.deleteUser( allUsers.get(iterator));
        allUsers = gestore.getActiveUser();
        // Aggiorno l'iteratore per evitare di rimanere puntati ad una posizione non esistente --> se elimino ultimo utente in array mi passa a primo(cioe il successivo)
        if (allUsers.isEmpty()) {
            iterator = -1;
        } else if (iterator >= allUsers.size()) {
            iterator = 0;
            setUserArray(iterator, allUsers);
        }

    }

    public void onAddUser(ActionEvent actionEvent) {
        String telegramId = userTelegramId.getText();
        String nickname = userNickname.getText();
        String role = userRole.getText();

        // verifica se tutti i campi sono stati inseriti
        if (telegramId.isEmpty() || nickname.isEmpty() || role.isEmpty()) {
            // mostra un messaggio di errore all'utente tramite un popup
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing information");
            alert.setContentText("Please fill in all the fields!");

            alert.showAndWait();
        } else {
            // Aggiungi il nuovo utente al database
                gestore.addUserFromAdmin(nickname, telegramId, role);
                // Aggiorna la lista di tutti gli utenti attivi
                allUsers = gestore.getActiveUser();
                // Imposta l'iteratore all'ultimo utente aggiunto
                iterator = allUsers.size() - 1;
                // Aggiorna la GUI con i dati dell'ultimo utente aggiunto
                setUserArray(iterator, allUsers);
            }

    }

    public void onUpdateRole(ActionEvent actionEvent) {
        // Aggiorno il ruolo dell'utente nel database
        gestore.changeUserRole(allUsers.get(iterator));
        // Aggiorno il ruolo dell'utente nella lista di utenti
        allUsers = gestore.getActiveUser();
        // Aggiorno il campo userRole nella UI
       setUserArray(iterator, allUsers);
    }

    public void onApproveStatus(ActionEvent actionEvent){
        String input = userApproved.getText();
        if (input.equals("false")) {
            gestore.updateApproveUser(allUsers.get(iterator));
            allUsers = gestore.getActiveUser();
            setUserArray(iterator, allUsers);
        }
    }


    public void onClearFields(ActionEvent actionEvent) {
        userTelegramId.setText("");
        userNickname.setText("");
        userRole.setText("");
        userApproved.setText("");
    }


}

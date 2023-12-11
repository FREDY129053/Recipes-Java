package com.example.recipes;

import com.example.recipes.Checker;
import com.example.recipes.DataBaseConductor;
import com.example.recipes.MainRecipesWindow;
import com.example.recipes.EnterForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegForm implements Initializable {
  @FXML
  private Hyperlink enter_link;
  @FXML
  private Label error_label;
  @FXML
  private TextField login;
  @FXML
  private TextField pass1;
  @FXML
  private TextField pass2;
  @FXML
  private Button reg_btn;
  private MainRecipesWindow mainWindowController;

  public void setMainWindowController(MainRecipesWindow controller) {
    this.mainWindowController = controller;
  }

  private void openEnterForm() throws IOException {
    Stage curr_stage = (Stage) enter_link.getScene().getWindow();
    FXMLLoader enter_form = new FXMLLoader(getClass().getResource("enter_form.fxml"));
    Parent root = enter_form.load();
    EnterForm entrance = enter_form.getController();
    entrance.setParentController(this.mainWindowController);

    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    stage.show();
    curr_stage.close();
  }

  private void regUser() throws SQLException, ClassNotFoundException, IOException {
    Stage curr_stage = (Stage) enter_link.getScene().getWindow();
    Checker checkReg = new Checker();
    DataBaseConductor dbConn = new DataBaseConductor();

    if (checkReg.isUserInDB(login.getText()))
      error_label.setText("Логин занят. Выберите другой!");
    else if (!checkReg.isCorrectPassword(pass1.getText()))
      error_label.setText("Пароль слишком короткий!");
    else if (checkReg.isSamePasswords(pass1.getText(), pass2.getText()))
      error_label.setText("Пароли не совпадают!");
    else {
      dbConn.signUpUser(login.getText(), pass1.getText());
      mainWindowController.getUser(login.getText());
      curr_stage.close();
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle)
  {
    enter_link.setOnAction(event -> {
      try {
        openEnterForm();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    reg_btn.setOnAction(e -> {
      try {
        regUser();
      } catch (SQLException | ClassNotFoundException | IOException ex) {
        throw new RuntimeException(ex);
      }
    });
  }
}

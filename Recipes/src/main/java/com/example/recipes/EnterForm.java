package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EnterForm implements Initializable {
  @FXML
  private Hyperlink reg_link;
  @FXML
  private TextField login;
  @FXML
  private TextField password;
  @FXML
  private Button enter_btn;
  @FXML
  private Label error_label;

  private MainRecipesWindow parent_controller;

  public void setParentController(MainRecipesWindow controller)
  {
    this.parent_controller = controller;
  }

  private void openRegForm() throws IOException {
    Stage curr_stage = (Stage) reg_link.getScene().getWindow();
    FXMLLoader reg_form = new FXMLLoader(getClass().getResource("reg_form.fxml"));
    Parent root = reg_form.load();
    RegForm registration = reg_form.getController();
    registration.setMainWindowController(this.parent_controller);

    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    stage.setTitle("Регистрация");
    stage.show();
    curr_stage.close();
  }

  private void enterCheck() throws IOException, SQLException, ClassNotFoundException {
    Stage curr_stage = (Stage) reg_link.getScene().getWindow();

    DataBaseConductor dbConn = new DataBaseConductor();

    if (dbConn.signInUser(login.getText(), password.getText()))
    {
      parent_controller.getUser(login.getText());
      curr_stage.close();
    } else {
      error_label.setText("Неверный логин или пароль!");
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle)
  {
    reg_link.setOnAction(event -> {
      try {
        openRegForm();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    enter_btn.setStyle("-fx-background-color: none; -fx-text-fill: #1B8057; -fx-border-color: #1B8057; -fx-border-radius: 15px; -fx-font-family: 'Inter'; -fx-font-size: 13px;");
    enter_btn.setOnAction(e -> {
      try {
        enterCheck();
      } catch (IOException | SQLException | ClassNotFoundException ex) {
        throw new RuntimeException(ex);
      }
    });
  }
}

package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateTest implements Initializable
{
  @FXML
  private TextField input_cook_time;

  @FXML
  private ScrollPane create_recipe_ScrollPane;

  @FXML
  private AnchorPane create_recipe_AnchorPane;

  @FXML
  private VBox create_recipe_VBox;

  @FXML
  private Label title;

  @FXML
  private Button tmp;

  @FXML
  private VBox VBox_Scroll;

  @FXML
  private ScrollPane Scroll_2;
  // TODO доделать окно и разобраться со списком

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle)
  {
    Scroll_2.setStyle("-fx-border-width: 0;");
    VBox_Scroll.setStyle("-fx-border-width: 0;");
    Scroll_2.setLayoutY(650);
    for (int i = 0; i < 151; i ++)
      VBox_Scroll.getChildren().add(new Label("Ok" + i));

  }
}

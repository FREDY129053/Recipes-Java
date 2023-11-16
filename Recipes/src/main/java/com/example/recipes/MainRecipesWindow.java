package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainRecipesWindow  implements Initializable {
  @FXML
  private FlowPane cards;
  @FXML
  private MenuButton category_choice;

  @FXML
  private Label category_name;

  @FXML
  private AnchorPane filters_AnchorPane;

  @FXML
  private VBox filters_VBox;

  @FXML
  private HBox logining_user_options;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Меняем у всех CheckBox местоположение галочки и текста(теперь галочка справа)
    for (var node : filters_AnchorPane.getChildren())
    {
      if (node instanceof CheckBox checkbox)
      {
        checkbox.setGraphicTextGap(10);
        checkbox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
      }
    }

    for (int i = 0; i < 5; i++)
    {
      // Используем другой другой виджет внутри
      FXMLLoader recipe_card = new FXMLLoader(getClass().getResource("recipe_small_card.fxml"));

      try {
        // Загрузка крточки
        Parent card = recipe_card.load();
        // Добавление карточки
        cards.getChildren().add(card);
        // Вертикальный отступ
        cards.setVgap(10);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}

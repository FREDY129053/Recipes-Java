package com.example.recipes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class RecipeController implements Initializable {

  public VBox create_recipe_widgets;
  @FXML
  public VBox steps;
  @FXML
  private AnchorPane create_recipes_widgets;
  // Label чтобы проверить что пользователь поменялся
  @FXML
  private Label test_user;

  // Кнопки при логине
  @FXML
  private HBox buttons_for_test_login;

  // Переменная для хранения текущего пользователя
  private String currUser = null;

  // Функция входа
  @FXML
  private void login()
  {
    // Чтобы вечно не добавлять в виджет кнопки, проверка чтобы виджетов было < 3
    if (buttons_for_test_login.getChildren().size() < 3) {
      // Создание кнопки выхода из акка
      Button logout = new Button("Log Out");
      // Функция выхода из аккаунта
      logout.setOnAction(event -> {
        // Удаление виджетов из HBox buttons_for_test_login
        HBox h = (HBox) logout.getParent();
        h.getChildren().removeAll(h.getChildren());
        currUser = null;
        test_user.setText(currUser);
      });
      // Кнопки для добавления при входе в аккаунт
      Button fav = new Button("Favorites");
      Button cart = new Button("Cart");
      Label user = new Label();
      // Смена пользователя
      currUser = "Fredy";
      user.setText(currUser);
      test_user.setText(currUser);
      // Добавление кнопок в виджет
      buttons_for_test_login.getChildren().addAll(logout, fav, cart, user);
    }
  }


  @FXML
  private Button add_ingredients;
  
  // Вертикальный виджет для рецептов
  @FXML
  private VBox ingredientsVBox;
  
  // Панель скролла для пролистывания ингредиентов
  @FXML
  private ScrollPane ingredients;

  // Виджет для расположения ингредиентов в строку и автоматического переноса
  @FXML
  private FlowPane vertical_ingredients;

// Добавление ингредиентов в панель ингредиентов

  // Список для формирования выпадающего списка в каком объеме будет ингредиент
  private final String[] count = {"кг", "гр", "л", "мл"};
  // Список для формирования выпадающего списка ингредиентов
  // TO DO не массив строк, а отдельный класс ингредиента
  private final String[] ingredients_list = {"помидор", "молоко", "лук", "сахар"};

  // Функция добавления ингредиентов в список "Ингредиенты"
  @FXML
  private void addIngredients()
  {
    // Если нет ингредиентов, то создается виджет куда добавлять
    if (ingredientsVBox == null)
    {
      ingredientsVBox = new VBox();
      ingredientsVBox.prefWidthProperty().bind(ingredients.widthProperty().subtract(2));
      vertical_ingredients = new FlowPane();
      vertical_ingredients.setHgap(30);
      vertical_ingredients.setVgap(10);
      ingredientsVBox.getChildren().add(vertical_ingredients);
      ingredients.setContent(ingredientsVBox);
    }

    // Создание выпадающих списков
    ObservableList<String> count_list = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(count)));
    ObservableList<String> ingredients_from_db = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(ingredients_list)));

    // Поля для выбора ингредиентов, ввода кол-ва, выбора кол-ва
    HBox ingred = new HBox();
    ComboBox<String> ingredient_count = new ComboBox<>(count_list);
    ComboBox<String> ingredients_list = new ComboBox<>(ingredients_from_db);
    TextField int_ingredient = new TextField();
    int_ingredient.setPrefWidth(50);
    int_ingredient.setAlignment(Pos.CENTER);

    ingred.getChildren().addAll(ingredients_list, int_ingredient, ingredient_count);
    vertical_ingredients.getChildren().add(ingred);
  }

  // Функция удаления последнего ингредиента из списка
  @FXML
  private void removeIngredients()
  {
    if (vertical_ingredients != null)
    {
      // Получение всех виджетов в списке
      var added_ingredients = vertical_ingredients.getChildren();
      // Проверка на наличие добавленных ингредиентов
      if (!added_ingredients.isEmpty())
      {
        Node last = added_ingredients.get(added_ingredients.size() - 1);
        added_ingredients.remove(last);
      }
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle)
  {
    // Подгон виджетов по ширине чтобы убрать скролл влево-впарво
    ingredients.setFitToWidth(true);


    AnchorPane.setTopAnchor(steps, 10.0);
    AnchorPane.setBottomAnchor(steps, 10.0);
    AnchorPane.setLeftAnchor(steps, 10.0);
    AnchorPane.setRightAnchor(steps, 10.0);

    for (int i = 0; i < 51; i++)
      steps.getChildren().add(new Label("Ok" + i));
  }
}

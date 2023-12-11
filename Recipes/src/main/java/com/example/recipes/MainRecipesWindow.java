package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class MainRecipesWindow {
  @FXML
  private FlowPane cards;
  @FXML
  private Button enter_btn;
  @FXML
  private MenuButton category_choice;
  @FXML
  private AnchorPane filters_AnchorPane;
  @FXML
  private VBox filters_VBox;
  @FXML
  private HBox logining_user_options;
  @FXML
  private MenuButton sort;
  @FXML
  private Button use_filters;

  private String user = null;
  public void getUser(String login) throws SQLException, IOException, ClassNotFoundException {
    user = login;
    initialize1();
  }

  private void openEnterForm() throws IOException {
    FXMLLoader enter_form = new FXMLLoader(getClass().getResource("enter_form.fxml"));
    Parent root = enter_form.load();
    EnterForm controllerChild = enter_form.getController();
    controllerChild.setParentController(this);

    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    stage.show();
  }

  private ArrayList<String> selectedFilters() {
    ArrayList<String> selected = new ArrayList<>();
    for (var i : filters_AnchorPane.getChildren()) {
      if (i instanceof CheckBox){
        if (((CheckBox) i).isSelected()) {
          selected.add(((CheckBox) i).getText());
        }
      }
    }

    return selected;
  }

//  @Override
  public void initialize1() throws SQLException, ClassNotFoundException, IOException {
    sort.setText("Сортировать");
    category_choice.setText("Категория");
    sort.getItems().clear();
    category_choice.getItems().clear();
    for (var i : filters_AnchorPane.getChildren()) {
      if (i instanceof CheckBox){
        if (((CheckBox) i).isSelected()) {
          ((CheckBox) i).setSelected(false);
        }
      }
    }
    cards.getChildren().clear();
    DataBaseConductor dataBase = new DataBaseConductor();

    if (user == null)
    {
      logining_user_options.getChildren().clear();
      logining_user_options.getChildren().add(enter_btn);
      enter_btn.setText("Войти");
    }
    else
    {
      logining_user_options.getChildren().clear();

      Button logOut = new Button("Выйти");
      logOut.setOnAction(event -> {
        user = null;
        try {
          initialize1();
        } catch (SQLException | ClassNotFoundException | IOException e) {
          throw new RuntimeException(e);
        }
      });

      Button favorites = new Button("Избранное");
      favorites.setOnAction(event -> {
        try {
          var allFav = dataBase.getAllFavRecipesID(user);
          ArrayList<TmpRecipeClass> recipes = new ArrayList<>();
          for (int i : allFav) {
            recipes.add(dataBase.getRecipe(i));
          }
          Stage stage = new Stage();
          FlowPane pane = new FlowPane();
          VBox paneMain = new VBox();

          VBox.setMargin(pane, new Insets(15, 0, 0, 60));
          ScrollPane scroll = new ScrollPane();

          if (recipes.size() != 0) {
            Drawer drawer = new Drawer();
            drawer.clearNode(pane);
            drawer.drawAllRecipes(recipes, pane, user, false);
          } else {
            pane.getChildren().add(new Label("Нет Избранных рецептов!"));
          }

          paneMain.getChildren().add(pane);
          scroll.setContent(paneMain);
          stage.setScene(new Scene(scroll, 430, 600));
          stage.setTitle("Избранное");
          stage.show();
        } catch (SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });

      Button cart = new Button("Корзина");
      cart.setOnAction(event -> {
        try {
          var cartUser = dataBase.getUserCart(user);
          Stage stage = new Stage();
//          FlowPane pane = new FlowPane();
          VBox paneMain = new VBox();
          VBox ofHbox = new VBox();
          VBox.setMargin(ofHbox, new Insets(15, 0, 0, 60));
          ScrollPane scroll = new ScrollPane();

          HBox ingredInfo = new HBox();
          Label name = new Label();

          for (var ingredient : cartUser.entrySet()) {
            HBox ingredInfoFromCart = new HBox(new Label(ingredient.getKey()), new Label(ingredient.getValue() + "гр."));
            ofHbox.getChildren().add(ingredInfoFromCart);
          }
          paneMain.getChildren().add(ofHbox);
          scroll.setContent(paneMain);
          stage.setScene(new Scene(scroll, 430, 600));
          stage.setTitle("Корзина продуктов");
          stage.show();
        } catch (SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });

      Button addRecipe = new Button("Добавить рецепт +");
      addRecipe.setOnAction(ev -> {
        FXMLLoader add_recipe_window = new FXMLLoader(getClass().getResource("add_recipe.fxml"));
        try {
          Parent root = add_recipe_window.load();
          AddRecipe controllerToGetUser = add_recipe_window.getController();
          controllerToGetUser.getUser(user);
          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      Button myRecipes = new Button("Мои рецепты");
      myRecipes.setOnAction(event -> {
        try {
          var allAuthor = dataBase.getAllUserRecipesID(user);
          ArrayList<TmpRecipeClass> recipes = new ArrayList<>();
          for (int i : allAuthor) {
            if (i == 17 || i == 18){
              continue;
            }
            recipes.add(dataBase.getRecipe(i));
          }
          Stage stage = new Stage();
          FlowPane pane = new FlowPane();
          VBox paneMain = new VBox();

          VBox.setMargin(pane, new Insets(15, 0, 0, 60));
          ScrollPane scroll = new ScrollPane();

          if (recipes.size() != 0) {
            Drawer drawer = new Drawer();
            drawer.clearNode(pane);
            drawer.drawAllRecipes(recipes, pane, user, false);
          } else {
            pane.getChildren().add(new Label("Нет Созданных рецептов!"));
          }

          paneMain.getChildren().add(pane);
          scroll.setContent(paneMain);
          stage.setScene(new Scene(scroll, 430, 600));
          stage.setTitle("Мои рецепты");
          stage.show();
        } catch (SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });

      logining_user_options.getChildren().addAll(addRecipe, myRecipes, favorites, cart, logOut);
    }

    enter_btn.setOnAction(event -> {
      try {
        openEnterForm();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    // Меняем у всех CheckBox местоположение галочки и текста(теперь галочка справа)
    for (var node : filters_AnchorPane.getChildren())
    {
      if (node instanceof CheckBox checkbox)
      {
        checkbox.setGraphicTextGap(10);
        checkbox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
      }
    }

    var tmpAllRecipes = dataBase.getAllRecipes();
    var allRecipes = new ArrayList<TmpRecipeClass>(tmpAllRecipes);

    Drawer drawCards = new Drawer();
    drawCards.drawAllRecipes(allRecipes, cards, user, true);

    ArrayList<String> tmp2 = dataBase.getAllCategory();
    ArrayList<String> categoriesToChoice = new ArrayList<>();
    categoriesToChoice.add("Все рецепты");
    categoriesToChoice.addAll(tmp2);


    ArrayList<String> sortItems = new ArrayList<>();
    sortItems.add("Без сортировки");
    sortItems.add("Новые -> Старые");
    sortItems.add("Старые -> Новые");
    sortItems.add("Легко -> Сложно");
    sortItems.add("Сложно -> Легко");
    sortItems.add("Быстрее -> Дольше");
    sortItems.add("Дольше -> Быстрее");

    use_filters.setOnAction(event -> {
      drawCards.clearNode(cards);
      try {
        drawCards.drawAllRecipes(dataBase.getAllRecipesByFilters(category_choice.getText(), selectedFilters(), sort.getText()), cards, user, true);
      } catch (SQLException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    });

    for (var i : categoriesToChoice) {
      MenuItem item = new MenuItem(i);
      item.setOnAction(event -> {
        category_choice.setText(item.getText());

        drawCards.clearNode(cards);
        try {
          drawCards.drawAllRecipes(dataBase.getAllRecipesByFilters(item.getText(), selectedFilters(), sort.getText()), cards, user, true);
        } catch (SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });
      category_choice.getItems().add(item);
    }

    for (var i : sortItems) {
      MenuItem item = new MenuItem(i);
      item.setOnAction(event -> {
        sort.setText(i);

        drawCards.clearNode(cards);
        try {
          drawCards.drawAllRecipes(dataBase.getAllRecipesByFilters(category_choice.getText(), selectedFilters(), item.getText()), cards, user, true);
        } catch (SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });
      sort.getItems().add(item);
    }
  }
}

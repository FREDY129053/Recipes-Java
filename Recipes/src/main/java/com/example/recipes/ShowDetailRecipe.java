package com.example.recipes;

import javafx.fxml.FXML;

import com.example.recipes.TmpRecipeClass;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ShowDetailRecipe {
  @FXML
  private ScrollPane recipe_preview_window;
  // Экземпляр рецепта, который будет передаваться
  private TmpRecipeClass recipe;
  private String user;

  public void setRecipe (TmpRecipeClass recipeObject, String user) throws MalformedURLException {
    this.recipe = recipeObject;
    this.user = user;
    create_window();
  }

  /** @noinspection OptionalGetWithoutIsPresent*/
  public void create_window() throws MalformedURLException {
    Pane full_window = new Pane();
    VBox main_Vbox = new VBox();
//    main_Vbox.setPrefWidth(690);
//    main_Vbox.setPrefHeight(3000);

    // Начало описания рецепта
    Pane recipe_desc_main = new Pane();
    HBox recipe_desc = new HBox();
//    recipe_desc.setPrefWidth(690);
    full_window.setStyle("-fx-background-color: #F1F0D0;");
    main_Vbox.setStyle("-fx-background-color: #F1F0D0;");
    recipe_desc.setStyle("-fx-background-color: #F1F0D0;");
    recipe_desc.setStyle("-fx-background-color: #F1F0D0;");
    Pane recipe_pane = new Pane();

    File file = new File(recipe.recipe_main_photo);
    URL fileURL = file.toURI().toURL();
    ImageView dish_photo = new ImageView();
    dish_photo.setImage(new Image(fileURL.toString()));

    dish_photo.setFitWidth(300);
    dish_photo.setFitHeight(250);
    dish_photo.setLayoutX(3);
    dish_photo.setLayoutY(5);

    Label dish_name = new Label(recipe.recipe_name);

    SVGPath cook = new SVGPath();
    cook.setContent("M533,374.184 L533,369 C533,368.448 532.553,368 532,368 C531.447,368 531,368.448 531,369 L531,374.184 C529.838,374.597 529,375.695 529,377 C529,378.657 530.343,380 532,380 C533.657,380 535,378.657 535,377 C535,375.695 534.162,374.597 533,374.184 L533,374.184 Z M532,388 C525.925,388 521,383.075 521,377 C521,370.925 525.925,366 532,366 C538.075,366 543,370.925 543,377 C543,383.075 538.075,388 532,388 L532,388 Z M532.99,364.05 C532.991,364.032 533,364.018 533,364 L533,362 L537,362 C537.553,362 538,361.553 538,361 C538,360.447 537.553,360 537,360 L527,360 C526.447,360 526,360.447 526,361 C526,361.553 526.447,362 527,362 L531,362 L531,364 C531,364.018 531.009,364.032 531.01,364.05 C524.295,364.558 519,370.154 519,377 C519,384.18 524.82,390 532,390 C539.18,390 545,384.18 545,377 C545,370.154 539.705,364.558 532.99,364.05 L532.99,364.05 Z");
    cook.setFill(Color.web("#1b8057"));

    Label cook_time = new Label();
    int totalMin = recipe.cook_time;
    String days = (totalMin / (24*60) + "д").equals("0д") ? "" : recipe.cook_time / (24*60) + " д ";
    String hours = (((totalMin % (24*60)) / 60) + "ч").equals("0ч") ? "" : recipe.cook_time / 60 + " ч ";
    String minutes = (totalMin % 60 + "м").equals("0м") ? "" : recipe.cook_time % 60 + " мин";
    String itog = (days + hours + minutes).equals("") ? "0" : days + hours + minutes;
    cook_time.setText(itog);

    Label difficult = new Label(recipe.difficult);

    Label category = new Label(recipe.category);

    recipe_pane.getChildren().addAll(dish_photo, dish_name, cook, cook_time, difficult, category);
    dish_name.setLayoutX(320 + 5);
    dish_name.setLayoutY(5);
    dish_name.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 600; -fx-font-size: 24px; -fx-text-fill: #1B8057;");

    cook.setLayoutX(-200);
    cook.setLayoutY(-300);

    cook_time.setLayoutX(360);
    cook_time.setLayoutY(63);
    cook_time.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 700; -fx-font-size: 18px; -fx-text-fill: #1B8057;");

    difficult.setLayoutX(500);
    difficult.setLayoutY(63);
    difficult.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");

    category.setLayoutX(580);
    category.setLayoutY(63);
    category.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");

    HBox kbzu = new HBox();
    Label k = new Label(Math.round(recipe.calInfo.get(0)) + " ккал");
    Label b = new Label("Белков " + Math.round(recipe.calInfo.get(1)) + " гр");
    Label z = new Label("Жиров " + Math.round(recipe.calInfo.get(2)) + " гр");
    Label u = new Label("Углеводов " + Math.round(recipe.calInfo.get(3)) + " гр");
    k.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");
    b.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");
    z.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");
    u.setStyle("-fx-font-family: 'Inter'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");
    kbzu.getChildren().addAll(k, b, z, u);
    kbzu.setSpacing(20);

    recipe_desc.getChildren().add(recipe_pane);
    recipe_desc_main.getChildren().addAll(recipe_desc, kbzu);
    kbzu.setLayoutX(315);
    kbzu.setLayoutY(150);
    // Конец описания рецепта

    // Ингредиенты
    // VBox для всех элементов
    VBox ingredientsVBox = new VBox();
    // Pane для размещения элементов
    Pane ingredientsPane = new Pane();
    // VBox для описанных ингредентов
    VBox ingredientsDescriptionVBox = new VBox();

    Label ingredientsHeader = new Label("Ингредиенты");
    ingredientsHeader.setStyle("-fx-font-family: 'Merriweather'; -fx-font-weight: 700; -fx-font-size: 24px; -fx-text-fill: #1B8057;");

    for (int i = 0; i < recipe.ingredients_name.size(); i++)
    {
      Label tmpLabel = new Label(recipe.ingredients_name.get(i) + " --- " + Math.round(Float.parseFloat(recipe.ingredients_count.get(i))) + " " + recipe.ingredients_v.get(i));
      tmpLabel.setStyle("-fx-font-family: 'Merriweather'; -fx-font-weight: 500; -fx-font-size: 22px; -fx-text-fill: #1B8057;");
      ingredientsDescriptionVBox.getChildren().add(tmpLabel);
    }

    ingredientsPane.getChildren().addAll(ingredientsHeader, ingredientsDescriptionVBox);
    ingredientsHeader.setLayoutX(10);
    ingredientsHeader.setLayoutY(15);
    ingredientsDescriptionVBox.setLayoutX(15);
    ingredientsDescriptionVBox.setLayoutY(50);

    ingredientsVBox.getChildren().add(ingredientsPane);
    // Конец ингредиентов

    // Шаги
    VBox mainVBoxSteps = new VBox();
    Pane steps = new Pane();
    VBox allSteps = new VBox();
    Optional<String> maxDesc = recipe.steps.keySet().stream().max(Comparator.naturalOrder());
    allSteps.setPrefHeight((recipe.steps.size() * (maxDesc.get().length() * 10) + 20) / 10.0);

    Label stepsHeader = new Label("ИНСТРУКЦИЯ ПРИГОТОВЛЕНИЯ");
    stepsHeader.setStyle("-fx-font-family: 'Merriweather'; -fx-font-weight: 700; -fx-font-size: 24px; -fx-text-fill: #1B8057;");

//    mainVBoxSteps.setPrefHeight(recipe.steps.size() * 260 + 10);
    int i = 0;
    for (String key : recipe.steps.keySet())
    {
      HBox tmpHBox = new HBox();
      Label count = new Label(String.valueOf(i + 1) + ". ");
      count.setStyle("-fx-font-family: 'Merriweather'; -fx-font-weight: 700; -fx-font-size: 22px; -fx-text-fill: #1B8057;");
      Label desc = new Label(key);
      desc.setStyle("-fx-font-family: 'Merriweather'; -fx-font-weight: 500; -fx-font-size: 18px; -fx-text-fill: #1B8057;");
      desc.setPrefWidth(380);
      desc.setWrapText(true);

//      System.out.println(recipe.steps.get(key));
//      System.out.println(key);
      File file2 = new File(recipe.steps.get(key));
      URL fileURL2 = file2.toURI().toURL();
      ImageView img = new ImageView();
      img.setImage(new Image(fileURL2.toString()));

      img.setFitWidth(300);
      img.setFitHeight(250);

      tmpHBox.getChildren().addAll(count, desc, img);

      HBox empty = new HBox();
      empty.setPrefHeight(25);
      allSteps.getChildren().addAll(tmpHBox, empty);
      i++;
    }

    steps.getChildren().addAll(stepsHeader, allSteps);
    stepsHeader.setLayoutX(10);
    stepsHeader.setLayoutY(15);
    allSteps.setLayoutX(15);
    allSteps.setLayoutY(60);

    mainVBoxSteps.getChildren().add(steps);

    full_window.getChildren().addAll(recipe_desc_main, ingredientsVBox, mainVBoxSteps);
    recipe_desc_main.setLayoutX(5);
    recipe_desc_main.setLayoutY(5);

    ingredientsVBox.setLayoutX(5);
    ingredientsVBox.setLayoutY(300 - 20);

    mainVBoxSteps.setLayoutX(5);
    mainVBoxSteps.setLayoutY((recipe.ingredients_count.size() * 40) + 305);

    if (user != null) {
      VBox addToCart = new VBox();
      Button add = new Button("В корзину");

      add.setOnAction(event -> {
        try {
          DataBaseConductor db = new DataBaseConductor();
          db.addAllIngredientsOfRecipe(db.getRecipeIdByName(recipe.recipe_name), user);
        } catch (IOException | ClassNotFoundException | SQLException e) {
          throw new RuntimeException(e);
        }
      });
      add.setStyle("-fx-background-color: none; -fx-text-fill: #1B8057; -fx-border-color: #1B8057; -fx-border-radius: 15px; -fx-font-family: 'Inter'; -fx-font-size: 13px;");

      Button changeRecipe = new Button("Изменить рецепт");
      changeRecipe.setFocusTraversable(false);
      changeRecipe.setStyle("-fx-background-color: none; -fx-text-fill: #1B8057; -fx-border-color: #1B8057; -fx-border-radius: 15px; -fx-font-family: 'Inter'; -fx-font-size: 13px;");
      if (!Objects.equals(user, recipe.author)) {
        changeRecipe.setVisible(false);
      } else {
        changeRecipe.setVisible(true);
        changeRecipe.setOnAction(event -> {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("crud_recipes.fxml"));
          Parent root = null;
          try {
            root = loader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          CrudRecipes controller = loader.getController();
          try {
            controller.setRecipe(recipe, user);
          } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
          }

          Stage curr = (Stage) changeRecipe.getScene().getWindow();

          Stage stage = new Stage();
          stage.setScene(new Scene(root, 800, 600));
          stage.show();
          curr.close();
        });
      }

      add.setFocusTraversable(false);
      VBox.setMargin(add, new Insets(15, 0, 20, 360));
      VBox.setMargin(changeRecipe, new Insets(15, 0, 20, 340));
      addToCart.getChildren().addAll(add, changeRecipe);
      main_Vbox.getChildren().addAll(full_window, addToCart);
    } else {
      main_Vbox.getChildren().add(full_window);
    }

    recipe_preview_window.setStyle("-fx-background-color: #F1F0D0;");
    recipe_preview_window.setContent(main_Vbox);
  }
}

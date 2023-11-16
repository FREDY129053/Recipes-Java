package com.example.recipes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RecipeApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("main_recipes_window.fxml"));
//    FXMLLoader fxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("recipe_small_card.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);

    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
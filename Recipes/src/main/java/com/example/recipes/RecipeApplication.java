package com.example.recipes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RecipeApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
//    FXMLLoader fxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("recipe-create-view.fxml"));
    FXMLLoader fxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("main_recipes_window.fxml"));
    Parent root = fxmlLoader.load();
    com.example.recipes.MainRecipesWindow contr = fxmlLoader.getController();
    contr.initialize1();


    stage.setTitle("Менеджер рецептов");
    stage.setScene(new Scene(root));
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RecipeSmallCard {
  @FXML
  private Label callories;
  @FXML
  private Label category;

  @FXML
  private Label difficult;

  @FXML
  private Label name;

  @FXML
  private ImageView photo_recipe;
  @FXML
  private ToggleButton to_favorites;

  @FXML
  private Label time;

  private TmpRecipeClass recipe;
  private String user;
  public void addRecipes(TmpRecipeClass recipe, String login) {
    this.user = login;
    this.recipe = recipe;
    initializeRecipe();
  }
  public void initializeRecipe() {
    File file = new File(recipe.recipe_main_photo);
    try {
      var photoUrl = file.toURI().toURL();
      photo_recipe.setImage(new Image(photoUrl.toString()));

      name.setText(recipe.recipe_name);

      int totalMin = recipe.cook_time;
      String days = (totalMin / (24*60) + "д").equals("0д") ? "" : recipe.cook_time / (24*60) + " д ";
      String hours = (((totalMin % (24*60)) / 60) + "ч").equals("0ч") ? "" : recipe.cook_time / 60 + " ч ";
      String minutes = (totalMin % 60 + "м").equals("0м") ? "" : recipe.cook_time % 60 + " мин";
      String itog = (days + hours + minutes).equals("") ? "0" : days + hours + minutes;
      time.setText(itog);

      category.setText(recipe.category);
      difficult.setText(recipe.difficult);
      callories.setText(Math.round(recipe.calInfo.get(0)) + "");

      DataBaseConductor db = new DataBaseConductor();
      to_favorites.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
          try {
            db.addToFav(db.getRecipeIdByName(recipe.recipe_name), db.getUserIdByName(user));
          } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }
        else {
          try {
            db.delFromFav(db.getRecipeIdByName(recipe.recipe_name), db.getUserIdByName(user));
          } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

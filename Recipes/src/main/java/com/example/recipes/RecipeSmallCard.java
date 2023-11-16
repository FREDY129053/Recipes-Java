package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class RecipeSmallCard implements Initializable {
  @FXML
  private ImageView photo_recipe;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    File file = new File("Recipes/src/images/photo_2022-10-13_22-44-52.jpg");
    try {
      var photoUrl = file.toURI().toURL();
      photo_recipe.setImage(new Image(photoUrl.toString()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}

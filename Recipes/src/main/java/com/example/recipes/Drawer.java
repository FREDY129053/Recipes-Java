package com.example.recipes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Drawer {
  public void drawAllRecipes(ArrayList<TmpRecipeClass> recipes, FlowPane whereDraw, String user, boolean needH) {
    for (TmpRecipeClass recipe : recipes) {
      // Используем другой другой виджет внутри
      FXMLLoader recipe_card = new FXMLLoader(getClass().getResource("recipe_small_card.fxml"));

      try {
        // Загрузка крточки
        Parent card = recipe_card.load();
        RecipeSmallCard controllerSmall = recipe_card.getController();
        ToggleButton toFav = (ToggleButton) card.lookup("#to_favorites");
        if (user == null)
        {
          toFav.setVisible(false);
        } else {
          DataBaseConductor db = new DataBaseConductor();
          if (!db.getAllFavRecipesID(user).isEmpty()) {
            if (db.getAllFavRecipesID(user).contains(db.getRecipeIdByName(recipe.recipe_name))) {
              toFav.setSelected(true);
            }
          }
        }
        controllerSmall.addRecipes(recipe, user);
        // Добавление карточки
        whereDraw.getChildren().add(card);
        card.setOnMouseClicked(openCard -> {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("show_detail_recipe.fxml"));
          Parent root;
          try {
            root = loader.load();
            ShowDetailRecipe controller = loader.getController();
            recipe.setAuthor(user);
            controller.setRecipe(recipe, user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 855, 600));
            stage.show();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
        // Вертикальный отступ
        if (needH) {
          whereDraw.setHgap(50);
        }
        whereDraw.setVgap(25);
      } catch (IOException | SQLException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void clearNode(FlowPane drawPane) {
    drawPane.getChildren().clear();
  }
}

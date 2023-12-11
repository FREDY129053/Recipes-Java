package com.example.recipes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AddRecipe {
  @FXML
  private Button auto_recipe_btn;
  @FXML
  private Label error;
  @FXML
  private Button handle_recipe;
  @FXML
  private TextField url_address;
  private String user = null;

  // передача пользователя через констроллеры
  public void getUser(String login) {
    user = login;
    initializeWithUser();
  }

  public void initializeWithUser() {
      handle_recipe.setOnAction(openHandleRecipeEditor -> {
        FXMLLoader recipe_editor_window = new FXMLLoader(getClass().getResource("recipe-create-view.fxml"));
        try {
          Parent root = recipe_editor_window.load();
          RecipeController controllerToGetUser = recipe_editor_window.getController();
          controllerToGetUser.getUser(user);

          Stage stage = new Stage();
          stage.setScene(new Scene(root));
          stage.show();

          ((Stage) handle_recipe.getScene().getWindow()).close();
        } catch (IOException | SQLException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      });

      auto_recipe_btn.setOnAction(openParserEditor -> {
        Parser parse = new Parser(url_address.getText());
        parse.getRecipe();

        if (parse.isCatchError()) {
          error.setText("Неверная ссылка на рецепт!");
        } else {
          error.setText("");

          FXMLLoader loader = new FXMLLoader(getClass().getResource("preview_recipe_card.fxml"));
          Parent root = null;
          try {
            root = loader.load();
            PreviewRecipeCard controller = loader.getController();
            TmpRecipeClass recipe = parse.getRecipe();
            recipe.setAuthor(user);
            controller.setRecipe(recipe);
            Stage curr = (Stage) auto_recipe_btn.getScene().getWindow();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 855, 600));
            stage.show();
            curr.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      });
  }
}

package com.example.recipes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class CrudRecipes {
  @FXML
  private HBox dish_photo_HBox;
  @FXML
  private ChoiceBox<String> category_list;
  @FXML
  private ImageView image;
  @FXML
  private Label difficult;
  @FXML
  private Slider diff_slider;
  @FXML
  private AnchorPane create_recipes_widgets;

  // Вертикальный виджет для рецептов
  @FXML
  private VBox ingredientsVBox;
  @FXML
  private Button add_ingredients;
  @FXML
  private Button remove_ingredients;
  // Панель скролла для пролистывания ингредиентов
  @FXML
  private ScrollPane ingredients;

  // Виджет для расположения ингредиентов в строку и автоматического переноса
  @FXML
  private FlowPane vertical_ingredients;
  @FXML
  private TextField dish_name;
  @FXML
  private TextField cook_time;
  HBox ingred = new HBox();
  ComboBox<String> ingredient_count = new ComboBox<>();
  ComboBox<String> ingredients_list = new ComboBox<>();
  TextField int_ingredient = new TextField();
  private TmpRecipeClass recipe;
  private String user;
  private final String[] count = {"гр", "кг", "шт", "л", "мл", "чайн.л.", "стол.л.", "зубч."};

  public void setRecipe(TmpRecipeClass recipe, String user) throws SQLException, IOException, ClassNotFoundException {
    this.recipe = recipe;
    this.user = user;
    initializeCRUD();
  }

  private void addIngredients() throws SQLException, ClassNotFoundException, IOException {
    DataBaseConductor dataBaseCon = new DataBaseConductor();
    ArrayList<Ingredient> allIngredients = dataBaseCon.getAllIngredients();

    // Если нет ингредиентов, то создается виджет куда добавлять
    if (ingredientsVBox == null)
    {
      ingredientsVBox = new VBox();
      ingredientsVBox.prefWidthProperty().bind(ingredients.widthProperty().subtract(2));
      vertical_ingredients = new FlowPane();
      vertical_ingredients.setStyle("-fx-border-width: 0;");
      vertical_ingredients.setHgap(30);
      vertical_ingredients.setVgap(10);
      ingredientsVBox.getChildren().add(vertical_ingredients);
      ingredients.setContent(ingredientsVBox);
    }

    // Создание выпадающих списков
    ObservableList<String> count_list = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(count)));
    ObservableList<String> ingredients_from_db = FXCollections.observableArrayList();

    for (Ingredient ingredient : allIngredients) {
      ingredients_from_db.add(ingredient.name);
    }

    // Поля для выбора ингредиентов, ввода кол-ва, выбора кол-ва
    ComboBox<String> ingredients_list = new ComboBox<>(ingredients_from_db);
    TextField int_ingredient = new TextField();
    ComboBox<String> ingredient_count = new ComboBox<>(count_list);
    ingredients_list.setPrefWidth(160);
    int_ingredient.setPrefWidth(50);
    int_ingredient.setAlignment(Pos.CENTER);
    ingred = new HBox();

    ingred.getChildren().addAll(ingredients_list, int_ingredient, ingredient_count);
    vertical_ingredients.getChildren().add(ingred);
  }

  private void removeIngredients()
  {
    if (vertical_ingredients != null)
    {
      // Получение всех виджетов в списке
      ObservableList<Node> added_ingredients = vertical_ingredients.getChildren();
      // Проверка на наличие добавленных ингредиентов
      if (!added_ingredients.isEmpty())
      {
        Node last = added_ingredients.get(added_ingredients.size() - 1);
        added_ingredients.remove(last);
      }
    }
  }

  // Метод выбора фотографии
  @FXML
  private void openFile(ImageView image, HBox imageHbox)
  {
    FileChooser fc = new FileChooser();
    fc.setTitle("Images");

    FileChooser.ExtensionFilter png_filter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
    FileChooser.ExtensionFilter jpeg_filter = new FileChooser.ExtensionFilter("JPEG files (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");
    fc.getExtensionFilters().addAll(jpeg_filter, png_filter);

    File sel = fc.showOpenDialog(image.getScene().getWindow());

    if (sel != null)
    {
      Image im = new Image(sel.toURI().toString());
      image.setImage(im);
      imageHbox.setStyle("-fx-border-width: 0;");
    }
  }

  private int i = 0;
  private void addStep(ObservableList<Node> list_to_ad)
  {
    FXMLLoader step_card = new FXMLLoader(getClass().getResource("step_card.fxml"));

    try {
      i++;
      Node step  = step_card.load();
      Label lb = (Label) step.lookup("#step_i");
      ImageView img = (ImageView) step.lookup("#step_photo");
      HBox delBorder = (HBox) step.lookup("#step_photo_HBox");
      img.setOnMouseClicked(event1 -> openFile(img, delBorder));
      lb.setText("Шаг " + i);
      list_to_ad.add(step);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void removeStep(ObservableList<Node> list_to_remove) {
    if (!list_to_remove.isEmpty()) {
      int lastIndex = list_to_remove.size() - 1;
      list_to_remove.remove(lastIndex);
      i--;
    }
  }

  // Информация о рецепте
  public TmpRecipeClass getRecipeItems() throws IOException, SQLException, ClassNotFoundException {
    String imageURL = image.getImage() != null ? image.imageProperty().get().getUrl() : String.valueOf(new File("Recipes/src/images/no-image.jpg").toURI().toURL());
    String dish_name_str = !dish_name.getText().isEmpty() ? dish_name.getText() : "Empty name";
    int cook_time_str = !cook_time.getText().isEmpty() ? Integer.parseInt(cook_time.getText()) : 0;
    String category = category_list.getValue() != null ? category_list.getValue() : "Empty";

    double difficultCount = diff_slider.getValue();
    String difficult = "";
    if (difficultCount >= 1 && difficultCount < 2)
      difficult = "Легко";
    else if (difficultCount >= 2 && difficultCount < 3)
      difficult = "Средне";
    else
      difficult = "Тяжело";

    ArrayList<String> ingredients_names = new ArrayList<>();
    ArrayList<String> ingredients_count = new ArrayList<>();
    ArrayList<String> ingredients_v = new ArrayList<>();
    ArrayList<String> steps_descriptions = new ArrayList<>();
    ArrayList<String> steps_photo = new ArrayList<>();

    double recipeCal = 0;
    double recipeFats = 0;
    double recipeProtein = 0;
    double recipeCarb = 0;

    ArrayList<String> ingrNames = new ArrayList<>();
    ArrayList<String> ingrCount = new ArrayList<>();
    ArrayList<String> ingrV = new ArrayList<>();

    // Считывание и добавление информации об ингредиентах в соответсвующие массивы
    for (Node node : vertical_ingredients.getChildren())
    {
      int i = 0;
      if (node instanceof HBox tmp)
      {
        for (Node hboxNode : tmp.getChildren())
        {
          if (hboxNode instanceof ComboBox<?> combobox) {
            String value = (String) combobox.getValue();
            if (i % 2 == 0) {
              ingredients_names.add(value);
              ingrNames.add(value);
            } else if (i % 2 == 1) {
              ingredients_v.add(value);
              ingrV.add(value);
            }
            i++;
          }
          else if (hboxNode instanceof TextField count_field){
            var count_value = count_field.getText();
            ingredients_count.add(count_value);
            ingrCount.add(count_value);
          }
        }
      }
    }

    DataBaseConductor db = new DataBaseConductor();
    var ingreds = db.getIngredientsInfo(ingrNames);

    for (int i = 0; i < ingredients_count.size(); i++) {
      double ingredInGrams = 0.0;
      switch (ingredients_v.get(i)) {
        case ("кг"), ("л") -> ingredInGrams += 1000 * Double.parseDouble(ingredients_count.get(i));
        case ("гр"), ("мл") -> ingredInGrams += Double.parseDouble(ingredients_count.get(i));
        case ("чайн.л.") -> ingredInGrams += 5.5 * Double.parseDouble(ingredients_count.get(i));
        case ("стол.л.") -> ingredInGrams += 15 * Double.parseDouble(ingredients_count.get(i));
        case ("шт") -> ingredInGrams += 105 * Double.parseDouble(ingredients_count.get(i));
        case ("зубч.") -> ingredInGrams += 5 * Double.parseDouble(ingredients_count.get(i));
      }

      recipeCal += (ingreds.get(i).calories / 100) * ingredInGrams;
      recipeFats += (ingreds.get(i).fats / 100) * ingredInGrams;
      recipeProtein += (ingreds.get(i).protein / 100) * ingredInGrams;
      recipeCarb += (ingreds.get(i).carbohydrates / 100) * ingredInGrams;
    }

    int j = 1;
    // Считываем информацию с добавленных шагов
    for (Node node : create_recipes_widgets.getChildren())
    {
      if (node instanceof ListView<?> steps_list)
      {
        for (Object step_info : steps_list.getItems())
        {
          var main_node = (AnchorPane) step_info;
          TextArea desc = (TextArea) main_node.lookup("#step_desc");
          ImageView photo = (ImageView) main_node.lookup("#step_photo");

          steps_descriptions.add(desc.getText().equals("") ? ("Пусто " + j) : desc.getText());
//            steps_photo.add(photo.imageProperty().get().getUrl());
          steps_photo.add(photo.getImage() != null ? photo.imageProperty().get().getUrl() : String.valueOf(new File("Recipes/src/images/no-image.jpg").toURI().toURL()));
//            System.out.println((desc.getText().equals("") ? "Пусто" : desc.getText()) + " = " + (photo.getImage() != null ? photo.imageProperty().get().getUrl() : String.valueOf(new File("Recipes/src/images/no-image.jpg").toURI().toURL())));
          j++;
        }
      }
    }
    TmpRecipeClass finalRecipe = new TmpRecipeClass(imageURL, dish_name_str, cook_time_str, category, difficult, ingredients_names, ingredients_count, ingredients_v, steps_descriptions, steps_photo);
    finalRecipe.setAuthor(user);
    finalRecipe.setCalInfo(recipeCal, recipeFats, recipeProtein, recipeCarb);

    return finalRecipe;
  }

  public void initializeCRUD() throws IOException, SQLException, ClassNotFoundException {
    ObservableList<String> categ_lst = FXCollections.observableArrayList(new DataBaseConductor().getAllCategory());
    category_list.getItems().addAll(categ_lst);

    // CRUD ингредиентов
    {
      add_ingredients.setOnAction(event -> {
        try {
          addIngredients();
        } catch (SQLException | ClassNotFoundException | IOException e) {
          throw new RuntimeException(e);
        }
      });
      remove_ingredients.setOnAction(event -> removeIngredients());

      DataBaseConductor dataBaseCon = new DataBaseConductor();
      ArrayList<Ingredient> allIngredients = dataBaseCon.getAllIngredients();
      ingredientsVBox = new VBox();
      ingredientsVBox.prefWidthProperty().bind(ingredients.widthProperty().subtract(2));
      vertical_ingredients = new FlowPane();
      vertical_ingredients.setStyle("-fx-border-width: 0;");
      vertical_ingredients.setHgap(30);
      vertical_ingredients.setVgap(10);
      ingredientsVBox.getChildren().add(vertical_ingredients);
      ingredients.setContent(ingredientsVBox);

      ObservableList<String> count_list = FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(count)));
      ObservableList<String> ingredients_from_db = FXCollections.observableArrayList();

      for (Ingredient ingredient : allIngredients) {
        ingredients_from_db.add(ingredient.name);
      }

      // Поля для выбора ингредиентов, ввода кол-ва, выбора кол-ва
      ingredient_count.setItems(count_list);
      ingredients_list.setItems(ingredients_from_db);
      ingredients_list.setPrefWidth(160);
      int_ingredient.setPrefWidth(50);
      int_ingredient.setAlignment(Pos.CENTER);
      for (int i = 0; i < recipe.ingredients_name.size(); i++) {
        ComboBox<String> ingredients_list = new ComboBox<>(ingredients_from_db);
        TextField int_ingredient = new TextField();
        ComboBox<String> ingredient_count = new ComboBox<>(count_list);
        ingredient_count.setValue(recipe.ingredients_v.get(i));
        ingredients_list.setValue(recipe.ingredients_name.get(i));
        int_ingredient.setText(recipe.ingredients_count.get(i));
        HBox ingred = new HBox();
        ingred.getChildren().addAll(ingredients_list, int_ingredient, ingredient_count);
        vertical_ingredients.getChildren().add(ingred);
      }
    }



    // Шаги
    ListView<Node> listView = new ListView<>();
    listView.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
      @Override
      public ListCell<Node> call(ListView<Node> param) {
        return new CustomListCell();
      }
    });

    ObservableList<Node> items = FXCollections.observableArrayList();

    // CRUD шагов
    try {
      for (var entry : recipe.steps.entrySet()) {
        FXMLLoader stepL = new FXMLLoader(getClass().getResource("step_card.fxml"));
        i++;
        Node step = stepL.load();
        Label lb = (Label) step.lookup("#step_i");
        ImageView img = (ImageView) step.lookup("#step_photo");
        TextArea desc = (TextArea) step.lookup("#step_desc");
        HBox delBorder = (HBox) step.lookup("#step_photo_HBox");
        img.setOnMouseClicked(event1 -> openFile(img, delBorder));
        lb.setText("Шаг " + i);
        File photoFile = new File(entry.getValue());
        URL fileUrl = photoFile.toURI().toURL();
        img.setImage(new Image(fileUrl.toString()));
        desc.setText(entry.getKey());
        items.add(step);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Button create_recipe = new Button("Изменить рецепт");
    create_recipes_widgets.getChildren().add(create_recipe);
    create_recipe.setLayoutX(350);
    create_recipe.setLayoutY(1000);
    create_recipe.setOnAction(event -> {
      DataBaseConductor db = null;
      try {
        db = new DataBaseConductor();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      try {
        var newRecipe = getRecipeItems();

        PreviewRecipeCard download = new PreviewRecipeCard();
        newRecipe.recipe_main_photo = download.downloadAllPhotos(newRecipe.recipe_main_photo).get(0);


        db.updateRecipe(newRecipe, recipe.recipe_name);
        Stage curr = (Stage) create_recipe.getScene().getWindow();
        curr.close();
      } catch (IOException | SQLException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    });

    listView.setItems(items);
    listView.setPrefHeight(300);
    listView.setPrefWidth(650);
    create_recipes_widgets.setPrefHeight(1050);
    create_recipes_widgets.getChildren().add(listView);
    listView.setLayoutY(600);
    listView.setLayoutX(45);

    Button list_btn = new Button("Добавить шаг +");
    Button remove_step = new Button("Удалить последний шаг -");
    create_recipes_widgets.getChildren().addAll(list_btn, remove_step);
    list_btn.setLayoutY(950);
    list_btn.setLayoutX(45);
    remove_step.setLayoutY(950);
    remove_step.setLayoutX(160);
    list_btn.setOnAction(event -> addStep(items));
    remove_step.setOnAction(event -> removeStep(items));

    File photoFile = new File(recipe.recipe_main_photo);
    URL fileUrl = photoFile.toURI().toURL();
    image.setImage(new Image(fileUrl.toString()));
    difficult.setText(recipe.difficult);
    dish_name.setText(recipe.recipe_name);
    cook_time.setText(recipe.cook_time + "");
    category_list.setValue(recipe.category);

    image.setOnMouseClicked(event -> openFile(image, dish_photo_HBox));

    // Подгон виджетов по ширине чтобы убрать скролл влево-впарво
    ingredients.setFitToWidth(true);
    ingredients.setStyle("-fx-border-width: 0; -fx-background-insets: 0;");

    // Св-ва ползунка
    diff_slider.setMin(1);
    diff_slider.setMax(3);
    diff_slider.setBlockIncrement(1);
    diff_slider.setShowTickLabels(true);

    switch (recipe.difficult) {
      case ("Легко") -> diff_slider.setValue(1.0);
      case ("Средне") -> diff_slider.setValue(2.1);
      case ("Тяжело") -> diff_slider.setValue(3.0);
    }
    // Считывание динамического изменения ползунка
    diff_slider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
        double tmp = t1.doubleValue();
        if (tmp >= 1 && tmp < 2)
          difficult.setText("Легко");
        else if (tmp >= 2 && tmp < 3)
          difficult.setText("Средне");
        else
          difficult.setText("Тяжело");
      }
    });
  }
}

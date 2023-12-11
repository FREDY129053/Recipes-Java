package com.example.recipes;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DataBaseConductor extends Configs {
  Connection dbConnection;
  ArrayList<String> photos;

  public DataBaseConductor() throws IOException {
  }

  public Connection getDbConnection() throws ClassNotFoundException, SQLException
  {
    String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

    Class.forName("com.mysql.jdbc.Driver");

    dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

    return dbConnection;
  }

  public void signUpUser(String login, String pass1)
  {
    String query = "INSERT INTO users(`login`, `password`) VALUES (?, ?);";
    try {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      statement.setString(1, login);
      statement.setString(2, pass1);
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean signInUser(String login, String inputPass) throws SQLException, ClassNotFoundException, IOException {
    Checker checkUser = new Checker();

    if (checkUser.isUserInDB(login))
    {
      String getPass = "SELECT password FROM users WHERE login='" + login + "';";
      Statement statement = getDbConnection().createStatement();
      ResultSet resPass = statement.executeQuery(getPass);
      resPass.next();
      String passFromDB = resPass.getString(1);
      return passFromDB.equals(inputPass);
    }

    return false;
  }

  public int getUserIdByName(String login) throws SQLException, ClassNotFoundException {
    String query = "SELECT id FROM users WHERE login = '" + login + "'";
    Statement statement = getDbConnection().createStatement();
    ResultSet resId = statement.executeQuery(query);
    resId.next();

    return resId.getInt(1);
  }

  public ArrayList<Ingredient> getAllIngredients() throws SQLException, ClassNotFoundException {
    ArrayList<Ingredient> allIngredients = new ArrayList<>();
    String getAllIngredientsQuery = "SELECT * FROM ingredients;";
    Statement statement = getDbConnection().createStatement();
    ResultSet result = statement.executeQuery(getAllIngredientsQuery);

    while (result.next()) {
      int tmpId = result.getInt(1);
      String tmpName = result.getString(2);
      double tmpCalories = result.getDouble(3);
      double tmpFats = result.getDouble(4);
      double tmpProtein = result.getDouble(5);
      double tmpCarbohydrates = result.getDouble(6);
      Ingredient tmpIngredient = new Ingredient(tmpId, tmpName, tmpCalories, tmpFats, tmpProtein, tmpCarbohydrates);
      allIngredients.add(tmpIngredient);
    }

    return allIngredients;
  }

  public void addAllIngredientsOfRecipe(int recipeId, String user) throws SQLException, ClassNotFoundException {
    String queryIngreds = "SELECT ingredients.*, recipes_and_ingredients.* " +
            "FROM recipes " +
            "JOIN recipes_and_ingredients ON recipes.id = recipes_and_ingredients.recipe_id " +
            "JOIN ingredients ON recipes_and_ingredients.ingredient_id = ingredients.id " +
            "WHERE recipes.id = " + recipeId + ";";
    Statement statement1 = getDbConnection().createStatement();
    ResultSet res = statement1.executeQuery(queryIngreds);

    while(res.next()) {
      String ingredientName = res.getString(2);
      double ingredientCount = res.getDouble(9);
      String ingredientV = res.getString(10);

      double ingredInGramms = 0.0;
      switch (ingredientV) {
        case ("кг"), ("л") -> ingredInGramms += 1000 * ingredientCount;
        case ("гр"), ("мл") -> ingredInGramms += ingredientCount;
        case ("чайн.л.") -> ingredInGramms += 5.5 * ingredientCount;
        case ("стол.л.") -> ingredInGramms += 15 * ingredientCount;
        case ("шт") -> ingredInGramms += 105 * ingredientCount;
        case ("зубч.") -> ingredInGramms += 5 * ingredientCount;
        case ("по вкусу") -> ingredInGramms += 0;
      }

      String existQuery = "SELECT EXISTS(SELECT 1 FROM cart WHERE name = '" + ingredientName + "')";
      Statement statement2 = getDbConnection().createStatement();
      ResultSet resTmp = statement2.executeQuery(existQuery);
      resTmp.next();
      int user_id = getUserIdByName(user);
      if (resTmp.getString(1).equals("0")) {
        String addQuery = "INSERT INTO cart(user_id, name, gramms) VALUES(?,?,?)";
        PreparedStatement statementAdd = getDbConnection().prepareStatement(addQuery);
        statementAdd.setInt(1, user_id);
        statementAdd.setString(2, ingredientName);
        statementAdd.setDouble(3, ingredInGramms);

        statementAdd.executeUpdate();
      } else {
        String updateIngred = "UPDATE cart SET gramms = gramms + ? WHERE name = ?";
        PreparedStatement statementUpdate = getDbConnection().prepareStatement(updateIngred);
        statementUpdate.setDouble(1, ingredInGramms);
        statementUpdate.setString(2, ingredientName);

        statementUpdate.executeUpdate();
      }
    }
  }

  public TmpRecipeClass getRecipe(int recipeId) throws SQLException, ClassNotFoundException {
    String mainInfo = "SELECT * FROM recipes WHERE id = " + recipeId;
    Statement statement = getDbConnection().createStatement();
    ResultSet result = statement.executeQuery(mainInfo);
    result.next();
    double recipeCal = 0;
    double recipeFats = 0;
    double recipeProtein = 0;
    double recipeCarb = 0;

    ArrayList<String> ingrNames = new ArrayList<>();
    ArrayList<String> ingrCount = new ArrayList<>();
    ArrayList<String> ingrV = new ArrayList<>();
    ArrayList<String> stepDesc = new ArrayList<>();
    ArrayList<String> stepPhoto = new ArrayList<>();

    int recipe_id = result.getInt(1);
    String recipe_name = result.getString(2);
    String author_login = result.getString(3);
    String recipe_photo = result.getString(4);
    String time_to_cook = result.getString(5);
    String difficult = result.getString(6);
    String category = result.getString(7);

    if (!getAllCategory().contains(category)) {
      String queryCategory = "INSERT INTO category(category_name) VALUES(?);";
      PreparedStatement statement1 = getDbConnection().prepareStatement(queryCategory);
      statement1.setString(1, category);
      statement1.executeUpdate();
    }

    String queryIngreds = "SELECT ingredients.*, recipes_and_ingredients.* " +
            "FROM recipes " +
            "JOIN recipes_and_ingredients ON recipes.id = recipes_and_ingredients.recipe_id " +
            "JOIN ingredients ON recipes_and_ingredients.ingredient_id = ingredients.id " +
            "WHERE recipes.id = " + recipe_id + ";";
    Statement statement1 = getDbConnection().createStatement();
    ResultSet res = statement1.executeQuery(queryIngreds);

    while(res.next()) {
      String ingredientName = res.getString(2);
      double ingredientCalories = res.getDouble(3);
      double ingredientFats = res.getDouble(4);
      double ingredientProtein = res.getDouble(5);
      double ingredientCarbohydrates = res.getDouble(6);
      double ingredientCount = res.getDouble(9);
      String ingredientV = res.getString(10);

      ingrNames.add(ingredientName);
      ingrCount.add(String.valueOf(ingredientCount));
      ingrV.add(ingredientV);

      double ingredInGramms = 0.0;
      switch (ingredientV) {
        case ("кг"), ("л") -> ingredInGramms += 1000 * ingredientCount;
        case ("гр"), ("мл") -> ingredInGramms += ingredientCount;
        case ("чайн.л.") -> ingredInGramms += 5.5 * ingredientCount;
        case ("стол.л.") -> ingredInGramms += 15 * ingredientCount;
        case ("шт") -> ingredInGramms += 105 * ingredientCount;
        case ("зубч.") -> ingredInGramms += 5 * ingredientCount;
      }

      recipeCal += (ingredientCalories / 100) * ingredInGramms;
      recipeFats += (ingredientFats / 100) * ingredInGramms;
      recipeProtein += (ingredientProtein / 100) * ingredInGramms;
      recipeCarb += (ingredientCarbohydrates / 100) * ingredInGramms;
    }
    String stepsQuery = "SELECT * FROM steps WHERE recipe_id = " + recipe_id + ";";
    Statement statement2 = getDbConnection().createStatement();
    ResultSet resSteps = statement2.executeQuery(stepsQuery);

    while(resSteps.next()) {
      stepDesc.add(resSteps.getString(3));
      stepPhoto.add(resSteps.getString(4));
    }
    TmpRecipeClass recipe = new TmpRecipeClass(recipe_photo, recipe_name, Integer.parseInt(time_to_cook), category, difficult, ingrNames, ingrCount, ingrV, stepDesc, stepPhoto);
    recipe.setAuthor(author_login);
    recipe.setCalInfo(recipeCal, recipeFats, recipeProtein, recipeCarb);

    return recipe;
  }

  public void insertRecipe(TmpRecipeClass recipe) throws SQLException, ClassNotFoundException, IOException {
    photos = new PreviewRecipeCard().downloadAllPhotos(recipe);
    String query = "INSERT INTO recipes(name, author, photo, time_to_cook, difficult, category) VALUES (?,?,?,?,?,?);";

    PreparedStatement statement = getDbConnection().prepareStatement(query);
    statement.setString(1, recipe.recipe_name);
    statement.setString(2, recipe.author);
    statement.setString(3, photos.get(0));
    statement.setInt(4, recipe.cook_time);
    statement.setString(5, recipe.difficult);
    statement.setString(6, recipe.category);

    statement.executeUpdate();
  }

  public int getRecipeId() throws SQLException, ClassNotFoundException {
    String query = "SELECT MAX(id) FROM recipes;";
    Statement statement = getDbConnection().createStatement();
    ResultSet resId = statement.executeQuery(query);
    resId.next();

    return resId.getInt(1);
  }

  public int getRecipeIdByName(String recipe_name) throws SQLException, ClassNotFoundException {
    String query = "SELECT id FROM recipes WHERE name = '" + recipe_name + "'";
    Statement statement = getDbConnection().createStatement();
    ResultSet resId = statement.executeQuery(query);
    resId.next();

    return resId.getInt(1);
  }

  public void isAllIngredientsInDb(TmpRecipeClass recipe) throws SQLException, ClassNotFoundException {
    for (var ingredient : recipe.ingredients_name) {
      String query = "SELECT id FROM ingredients WHERE name='" + ingredient + "';";
      Statement statement = getDbConnection().createStatement();
      ResultSet result = statement.executeQuery(query);
      if (!result.next()) {
        String query1 = "INSERT INTO ingredients(name, calories, fats, protein, carbohydrates) VALUES(?,?,?,?,?);";
        PreparedStatement statement1 = getDbConnection().prepareStatement(query1);
        statement1.setString(1, ingredient);
        statement1.setInt(2, new Random().nextInt(925 - 11 + 1) + 11);
        statement1.setInt(3, new Random().nextInt(175 + 1));
        statement1.setInt(4, new Random().nextInt(87 + 1));
        statement1.setInt(5, new Random().nextInt(275));
        statement1.executeUpdate();
      }
    }
  }

  public int getIngredientId(String ingredient_name) throws SQLException, ClassNotFoundException {
    String query = "SELECT id FROM ingredients WHERE name='" + ingredient_name + "';";
    Statement statement = getDbConnection().createStatement();
    ResultSet resId = statement.executeQuery(query);
    if (resId == null) {
      return 0;
    } else {
      resId.next();
      return resId.getInt(1);
    }
  }

  public ArrayList<Ingredient> getIngredientsInfo(ArrayList<String> name) throws SQLException, ClassNotFoundException {
    ArrayList<Ingredient> ingredsInfo = new ArrayList<>();

    for(var i : name) {
      String query = "SELECT * FROM ingredients WHERE name = '" + i + "'";
      Statement statement = getDbConnection().createStatement();
      ResultSet result = statement.executeQuery(query);

      while (result.next()) {
        ingredsInfo.add(new Ingredient(result.getInt(1), result.getString(2), result.getDouble(3), result.getDouble(4), result.getDouble(5), result.getDouble(6)));
      }
    }

    return ingredsInfo;
  }

  public void insertIngredients(TmpRecipeClass recipe) throws SQLException, ClassNotFoundException {
    int recipeId = getRecipeId();

    for (int i = 0; i < recipe.ingredients_name.size(); i++) {
      int ingredientId = getIngredientId(recipe.ingredients_name.get(i));

      String query = "INSERT INTO recipes_and_ingredients(recipe_id, ingredient_id, count, amount) VALUES(?, ?, ?, ?);";
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      statement.setInt(1, recipeId);
      statement.setInt(2, ingredientId);
      statement.setDouble(3, !recipe.ingredients_count.get(i).equals("") ? Integer.parseInt(recipe.ingredients_count.get(i)) : 0);
      statement.setString(4, recipe.ingredients_v.get(i));

      statement.executeUpdate();
    }
  }

  public void insertSteps(TmpRecipeClass recipe) throws SQLException, ClassNotFoundException, IOException {
    int recipeId = getRecipeId();
    var tmp = photos;
    ArrayList<String> stepsPhotos = new ArrayList<>(tmp.subList(1, tmp.size()));
    var keys = new ArrayList<>(recipe.steps.keySet());
    for (int i = 0; i < keys.size(); i++) {
      String query2 = "INSERT INTO steps(recipe_id, description, photo) VALUES(?, ?, ?);";
      PreparedStatement statement = getDbConnection().prepareStatement(query2);
      statement.setInt(1, recipeId);
      statement.setString(2, keys.get(i));
      statement.setString(3, stepsPhotos.get(i));

      statement.executeUpdate();
    }
  }

  public ArrayList<TmpRecipeClass> getAllRecipes() throws SQLException, ClassNotFoundException {
    ArrayList<TmpRecipeClass> allRecipes = new ArrayList<>();
    String mainInfo = "SELECT * FROM recipes";
    Statement statement = getDbConnection().createStatement();
    ResultSet result = statement.executeQuery(mainInfo);

    while (result.next()) {
      double recipeCal = 0;
      double recipeFats = 0;
      double recipeProtein = 0;
      double recipeCarb = 0;

      ArrayList<String> ingrNames = new ArrayList<>();
      ArrayList<String> ingrCount = new ArrayList<>();
      ArrayList<String> ingrV = new ArrayList<>();
      ArrayList<String> stepDesc = new ArrayList<>();
      ArrayList<String> stepPhoto = new ArrayList<>();

      int recipe_id = result.getInt(1);
      if (recipe_id == 17 || recipe_id == 18) {
        continue;
      }
      String recipe_name = result.getString(2);
      String author_login = result.getString(3);
      String recipe_photo = result.getString(4);
      String time_to_cook = result.getString(5);
      String difficult = result.getString(6);
      String category = result.getString(7);

      if (!getAllCategory().contains(category)) {
        String queryCategory = "INSERT INTO category(category_name) VALUES(?);";
        PreparedStatement statement1 = getDbConnection().prepareStatement(queryCategory);
        statement1.setString(1, category);
        statement1.executeUpdate();
      }

      String queryIngreds = "SELECT ingredients.*, recipes_and_ingredients.* " +
              "FROM recipes " +
              "JOIN recipes_and_ingredients ON recipes.id = recipes_and_ingredients.recipe_id " +
              "JOIN ingredients ON recipes_and_ingredients.ingredient_id = ingredients.id " +
              "WHERE recipes.id = " + recipe_id + ";";
      Statement statement1 = getDbConnection().createStatement();
      ResultSet res = statement1.executeQuery(queryIngreds);

      while(res.next()) {
        String ingredientName = res.getString(2);
        double ingredientCalories = res.getDouble(3);
        double ingredientFats = res.getDouble(4);
        double ingredientProtein = res.getDouble(5);
        double ingredientCarbohydrates = res.getDouble(6);
        double ingredientCount = res.getDouble(9);
        String ingredientV = res.getString(10);

        ingrNames.add(ingredientName);
        ingrCount.add(String.valueOf(ingredientCount));
        ingrV.add(ingredientV);

        double ingredInGramms = 0.0;
        switch (ingredientV) {
          case ("кг"), ("л") -> ingredInGramms += 1000 * ingredientCount;
          case ("гр"), ("мл") -> ingredInGramms += ingredientCount;
          case ("чайн.л.") -> ingredInGramms += 5.5 * ingredientCount;
          case ("стол.л.") -> ingredInGramms += 15 * ingredientCount;
          case ("шт") -> ingredInGramms += 105 * ingredientCount;
          case ("зубч.") -> ingredInGramms += 5 * ingredientCount;
        }

        recipeCal += (ingredientCalories / 100) * ingredInGramms;
        recipeFats += (ingredientFats / 100) * ingredInGramms;
        recipeProtein += (ingredientProtein / 100) * ingredInGramms;
        recipeCarb += (ingredientCarbohydrates / 100) * ingredInGramms;
      }
      String stepsQuery = "SELECT * FROM steps WHERE recipe_id = " + recipe_id + ";";
      Statement statement2 = getDbConnection().createStatement();
      ResultSet resSteps = statement2.executeQuery(stepsQuery);

      while(resSteps.next()) {
        stepDesc.add(resSteps.getString(3));
        stepPhoto.add(resSteps.getString(4));
      }
      TmpRecipeClass recipe = new TmpRecipeClass(recipe_photo, recipe_name, Integer.parseInt(time_to_cook), category, difficult, ingrNames, ingrCount, ingrV, stepDesc, stepPhoto);
      recipe.setAuthor(author_login);
      recipe.setCalInfo(recipeCal, recipeFats, recipeProtein, recipeCarb);
      allRecipes.add(recipe);
      }
    return allRecipes;
    }

  public ArrayList<String> getAllCategory() throws SQLException, ClassNotFoundException {
    String query = "SELECT category_name FROM category;";
    Statement statement = getDbConnection().createStatement();
    ResultSet result = statement.executeQuery(query);
    ArrayList<String> allCategories = new ArrayList<>();

    while(result.next()) {
      allCategories.add(result.getString(1));
    }
    Collections.sort(allCategories);

    return allCategories;
  }

  public ArrayList<TmpRecipeClass> getAllRecipesByFilters(String categoryFilter, ArrayList<String> checkBoxFilters, String sortType) throws SQLException, ClassNotFoundException {
    ArrayList<TmpRecipeClass> allRecipes = new ArrayList<>();
    StringBuilder mainInfo = new StringBuilder();
    if (categoryFilter.equals("Все рецепты") || categoryFilter.equals("Категория")) {
      mainInfo = new StringBuilder("SELECT * FROM recipes WHERE category like '%'");
    } else {
      mainInfo = new StringBuilder("SELECT * FROM recipes WHERE category='" + categoryFilter + "'");
    }

    for (var i : checkBoxFilters) {
      switch (i) {
        case ("До 15 шагов") ->
                mainInfo.append(" AND id IN (SELECT recipe_id FROM steps GROUP BY recipe_id HAVING COUNT(*) < 15)");
        case ("Без помидоров") ->
                mainInfo.append(" AND id NOT IN (SELECT ri.recipe_id FROM recipes_and_ingredients ri JOIN ingredients i ON ri.ingredient_id = i.id WHERE i.name = 'Помидоры')");
        case ("До 1 часа") -> mainInfo.append(" AND time_to_cook < 60");
      }
    }

    switch (sortType) {
      case ("Без сортировки"), ("Сортировать"), ("Старые -> Новые") -> mainInfo.append(" ORDER BY id");
      case ("Новые -> Старые") -> mainInfo.append(" ORDER BY id DESC");
      case ("Легко -> Сложно") -> mainInfo.append(" ORDER BY difficult");
      case ("Сложно -> Легко") -> mainInfo.append(" ORDER BY difficult DESC");
      case ("Быстрее -> Дольше") -> mainInfo.append(" ORDER BY time_to_cook");
      case ("Дольше -> Быстрее") -> mainInfo.append(" ORDER BY time_to_cook DESC");
    }

    Statement statement = getDbConnection().createStatement();
    ResultSet result = statement.executeQuery(mainInfo.toString());

    while (result.next()) {
      double recipeCal = 0;
      double recipeFats = 0;
      double recipeProtein = 0;
      double recipeCarb = 0;

      ArrayList<String> ingrNames = new ArrayList<>();
      ArrayList<String> ingrCount = new ArrayList<>();
      ArrayList<String> ingrV = new ArrayList<>();
      ArrayList<String> stepDesc = new ArrayList<>();
      ArrayList<String> stepPhoto = new ArrayList<>();

      int recipe_id = result.getInt(1);
      if (recipe_id == 17 || recipe_id == 18) {
        continue;
      }
      String recipe_name = result.getString(2);
      String author_login = result.getString(3);
      String recipe_photo = result.getString(4);
      String time_to_cook = result.getString(5);
      String difficult = result.getString(6);
      String category = result.getString(7);

      if (!getAllCategory().contains(category)) {
        String queryCategory = "INSERT INTO category(category_name) VALUES(?);";
        PreparedStatement statement1 = getDbConnection().prepareStatement(queryCategory);
        statement1.setString(1, category);
        statement1.executeUpdate();
      }

      String queryIngreds = "SELECT ingredients.*, recipes_and_ingredients.* " +
              "FROM recipes " +
              "JOIN recipes_and_ingredients ON recipes.id = recipes_and_ingredients.recipe_id " +
              "JOIN ingredients ON recipes_and_ingredients.ingredient_id = ingredients.id " +
              "WHERE recipes.id = " + recipe_id + ";";
      Statement statement1 = getDbConnection().createStatement();
      ResultSet res = statement1.executeQuery(queryIngreds);

      while(res.next()) {
        String ingredientName = res.getString(2);
        double ingredientCalories = res.getDouble(3);
        double ingredientFats = res.getDouble(4);
        double ingredientProtein = res.getDouble(5);
        double ingredientCarbohydrates = res.getDouble(6);
        double ingredientCount = res.getDouble(9);
        String ingredientV = res.getString(10);

        ingrNames.add(ingredientName);
        ingrCount.add(String.valueOf(ingredientCount));
        ingrV.add(ingredientV);

        double ingredInGramms = 0.0;
        switch (ingredientV) {
          case ("кг"), ("л") -> ingredInGramms += 1000 * ingredientCount;
          case ("гр"), ("мл") -> ingredInGramms += ingredientCount;
          case ("чайн.л.") -> ingredInGramms += 5.5 * ingredientCount;
          case ("стол.л.") -> ingredInGramms += 15 * ingredientCount;
          case ("шт") -> ingredInGramms += 105 * ingredientCount;
          case ("зубч.") -> ingredInGramms += 5 * ingredientCount;
        }

        recipeCal += (ingredientCalories / 100) * ingredInGramms;
        recipeFats += (ingredientFats / 100) * ingredInGramms;
        recipeProtein += (ingredientProtein / 100) * ingredInGramms;
        recipeCarb += (ingredientCarbohydrates / 100) * ingredInGramms;
      }
      String stepsQuery = "SELECT * FROM steps WHERE recipe_id = " + recipe_id + ";";
      Statement statement2 = getDbConnection().createStatement();
      ResultSet resSteps = statement2.executeQuery(stepsQuery);

      while(resSteps.next()) {
        stepDesc.add(resSteps.getString(3));
        stepPhoto.add(resSteps.getString(4));
      }
      TmpRecipeClass recipe = new TmpRecipeClass(recipe_photo, recipe_name, Integer.parseInt(time_to_cook), category, difficult, ingrNames, ingrCount, ingrV, stepDesc, stepPhoto);
      recipe.setAuthor(author_login);
      recipe.setCalInfo(recipeCal, recipeFats, recipeProtein, recipeCarb);
      allRecipes.add(recipe);
    }
    return allRecipes;
  }

  public void addToFav(int recipeID, int userID) throws SQLException, ClassNotFoundException {
    String query = "INSERT INTO favorites(user_id, recipe_id) VALUES(?, ?);";
    PreparedStatement statement = getDbConnection().prepareStatement(query);
    statement.setInt(1, userID);
    statement.setInt(2, recipeID);

    statement.executeUpdate();
  }

  public void delFromFav(int recipeID, int userID) throws SQLException, ClassNotFoundException {
    String query = "DELETE FROM favorites WHERE user_id = ? AND recipe_id = ?";
    PreparedStatement statement = getDbConnection().prepareStatement(query);
    statement.setInt(1, userID);
    statement.setInt(2, recipeID);
    statement.executeUpdate();
  }

  public ArrayList<Integer> getAllFavRecipesID(String user) throws SQLException, ClassNotFoundException {
    String query = "SELECT recipe_id FROM favorites WHERE user_id = " + getUserIdByName(user);
    ArrayList<Integer> allFavRecipes = new ArrayList<>();
    Statement statement = getDbConnection().createStatement();
    ResultSet resIds = statement.executeQuery(query);
    while (resIds.next()) {
      allFavRecipes.add(resIds.getInt(1));
    }

    return allFavRecipes;
  }

  public ArrayList<Integer> getAllUserRecipesID(String user) throws SQLException, ClassNotFoundException {
    String query = "SELECT id FROM recipes WHERE author = '" + user + "'";
    ArrayList<Integer> allAuthorRecipes = new ArrayList<>();
    Statement statement = getDbConnection().createStatement();
    ResultSet resIds = statement.executeQuery(query);
    while (resIds.next()) {
      allAuthorRecipes.add(resIds.getInt(1));
    }

    return allAuthorRecipes;
  }

  public Map<String, String> getUserCart(String user) throws SQLException, ClassNotFoundException {
    String query = "SELECT name, gramms FROM cart WHERE user_id = " + getUserIdByName(user);
    Map<String, String> cart = new HashMap<>();
    Statement statement = getDbConnection().createStatement();
    ResultSet resIds = statement.executeQuery(query);
    while (resIds.next()) {
      cart.put(resIds.getString(1), resIds.getString(2));
    }

    return cart;
  }
}

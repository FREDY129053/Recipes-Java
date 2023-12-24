package com.example.recipes;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static java.util.Map.entry;

public class Parser {
  private final Map<String, String> pathsToGetItems = Map.ofEntries(
    entry("name", "#main > div.container.wrap.columns.is-centered > main > h1"),
    entry("dish_photo", "#pt_info > div.recipe-top.columns > div:nth-child(1) > div.main-photo-recipe.ib-s > div > a > img"),
    entry("ingredients", "#recept-list > div.ingredient.list-item > div.list-column.no-shrink > meta"),
    entry("cooking_time", "#pt_steps > span > span.label > strong"),
    entry("steps_photos", "#pt_steps > ol > li > a > img"),
    entry("steps_descriptions", "#pt_steps > ol > li > p"),
    entry("category", "#main > div.container.wrap.columns.is-centered > main > ol > li:nth-child(2) > a > span")
  );
  private final String linkToParse;
  private Document page;
  private URL url;
  public boolean isError;

  public Parser(String linkToParse) {
    this.linkToParse = linkToParse;
  }

  public boolean isCatchError() {return isError;}

  private int cookTimeInMinutes(String time) {
    String[] elements = time.split(" ");
    ArrayList<Integer> timeCount = new ArrayList<Integer>();
    ArrayList<String> timeName = new ArrayList<String>();
    int minutesToCook = 0;

    for (int i = 0; i < elements.length; i++) {
      if (i % 2 == 0)
        timeCount.add(Integer.parseInt(elements[i]));
      else
        timeName.add(elements[i]);
    }

    for (int i = 0; i < timeCount.size(); i++) {
      switch (timeName.get(i)) {
        case ("д") -> minutesToCook += timeCount.get(i) * 1440;
        case ("ч") -> minutesToCook += timeCount.get(i) * 60;
        case ("мин") -> minutesToCook += timeCount.get(i);
      }
    }

    return minutesToCook;
  }

  public TmpRecipeClass getRecipe() {
    try {
      url = new URL(linkToParse);
      page = Jsoup.connect(String.valueOf(url)).get();
      String dishName = page.select(pathsToGetItems.get("name")).get(0).text();
      String dishPhoto = "https:" + page.select(pathsToGetItems.get("dish_photo")).get(0).attr("src");
      String category = page.select(pathsToGetItems.get("category")).get(0).text();
      String difficult = "";

      Elements ingredients = page.select(pathsToGetItems.get("ingredients"));
      Map<String, String> ingredientsPortions = new HashMap<>();
      for (Element s : ingredients) {
        String[] tmp = s.attr("content").split(" - ");
        ingredientsPortions.put(tmp[0], tmp[1]);
      }

      // Ингредиенты для класса
      ArrayList<String> ingredientName = new ArrayList<>();
      ArrayList<String> ingredientCount = new ArrayList<>();
      ArrayList<String> ingredientCountName = new ArrayList<>();

      for (var entry : ingredientsPortions.entrySet()) {
        ingredientName.add(entry.getKey());
        ingredientCount.add(entry.getValue().split(" ")[0]);
        ingredientCountName.add(!entry.getValue().split(" ")[1].equals("по") ? entry.getValue().split(" ")[1] : "по вкусу");
      }

      int cookTime = cookTimeInMinutes(page.select(pathsToGetItems.get("cooking_time")).get(0).text());
      Elements stepsPhotos = page.select(pathsToGetItems.get("steps_photos"));
      Elements stepsDescriptions = page.select(pathsToGetItems.get("steps_descriptions"));

      ArrayList<String> stepsPhotosList = new ArrayList<>();
      ArrayList<String> stepsDescriptionsList = new ArrayList<>();

      for (int i = 0; i < stepsPhotos.size(); i++) {
        stepsPhotosList.add("https:" + stepsPhotos.get(i).attr("src"));
        stepsDescriptionsList.add(stepsDescriptions.get(i).text());
      }

      double kDifficult = stepsPhotosList.size() * 0.5 + cookTime / 30.0;

      if (kDifficult < 5) {
        difficult += "Легко";
      } else if (kDifficult < 10) {
        difficult += "Средне";
      } else {
        difficult += "Тяжело";
      }

      TmpRecipeClass finalRec =  new TmpRecipeClass(dishPhoto, dishName, cookTime, category, difficult, ingredientName, ingredientCount, ingredientCountName, stepsDescriptionsList, stepsPhotosList);
      finalRec.setCalInfo(0, 0, 0, 0);

      return finalRec;
    } catch (IOException e) {
      isError = true;
    }

    return new TmpRecipeClass(null, null, 0, null, null, new ArrayList<String>(List.of("Null")), new ArrayList<String>(List.of("Null")), new ArrayList<String>(List.of("Null")), new ArrayList<String>(List.of("Null")), new ArrayList<String>(List.of("Null")));
  }
}

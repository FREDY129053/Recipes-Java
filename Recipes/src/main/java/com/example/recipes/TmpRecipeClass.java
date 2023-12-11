package com.example.recipes;

import java.util.*;

public class TmpRecipeClass {
  public String recipe_main_photo;
  public ArrayList<Double> calInfo = new ArrayList<>();
  public String recipe_name;
  public String author;
  public int cook_time;
  public String category;
  public String difficult;
  public List<String> ingredients_name;
  public List<String> ingredients_count;
  public List<String> ingredients_v;
  public Map<String, String> steps = new LinkedHashMap<>();

  public void setAuthor(String login) {
    this.author = login;
  }

  public void setCalInfo(double cal, double b, double zh, double u) {
    this.calInfo.add(cal);
    this.calInfo.add(b);
    this.calInfo.add(zh);
    this.calInfo.add(u);
  }

  public TmpRecipeClass(String item1, String item2, int item3, String item4, String item5, ArrayList<String> item6, ArrayList<String> item7, ArrayList<String> item8, ArrayList<String> item9, ArrayList<String> item10) {
    this.recipe_main_photo = item1;
    this.recipe_name = item2;
    this.cook_time = item3;
    this.category = item4;
    this.difficult = item5;
    this.ingredients_name = item6;
    this.ingredients_count = item7;
    this.ingredients_v = item8;

    for (int i = 0; i < item9.size(); i++)
    {
      this.steps.put(item9.get(i), item10.get(i));
    }
  }
}

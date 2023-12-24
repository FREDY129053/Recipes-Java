package com.example.recipes;

public class Ingredient {
  public int id;
  public String name;
  public double calories;
  public double fats;
  public double protein;
  public double carbohydrates;

  public Ingredient(int idInput, String nameInput, double caloriesInput, double fatsInput, double proteinInput, double carbohydratesInput){
    this.id = idInput;
    this.name = nameInput;
    this.calories = caloriesInput;
    this.fats = fatsInput;
    this.protein = proteinInput;
    this.carbohydrates = carbohydratesInput;
  }
}

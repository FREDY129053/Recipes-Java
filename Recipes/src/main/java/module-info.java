module com.example.recipes {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.example.recipes to javafx.fxml;
  exports com.example.recipes;
}
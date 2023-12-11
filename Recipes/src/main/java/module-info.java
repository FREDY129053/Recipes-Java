module com.example.recipes {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql;
  requires mysql.connector.j;
  requires org.jsoup;
  requires java.desktop;


  opens com.example.recipes to javafx.fxml;
  exports com.example.recipes;
}
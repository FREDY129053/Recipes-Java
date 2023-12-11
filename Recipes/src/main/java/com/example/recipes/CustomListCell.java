package com.example.recipes;

import javafx.scene.Node;
import javafx.scene.control.ListCell;

public class CustomListCell extends ListCell<Node>
{
  @Override
  protected void updateItem(Node item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      setGraphic(item);
    }
  }
}

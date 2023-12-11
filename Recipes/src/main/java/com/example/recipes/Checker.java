package com.example.recipes;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Checker {
  private final DataBaseConductor dbConnector = new DataBaseConductor();

  public Checker() throws IOException {
  }

  public boolean isUserInDB(String login) throws ClassNotFoundException {
    String query = "SELECT EXISTS (SELECT 1 FROM users WHERE login='" + login + "');";
    try {
      Statement statement = dbConnector.getDbConnection().createStatement();
      ResultSet result = statement.executeQuery(query);

      if (result.next())
      {
        int tmp = result.getInt(1);
        if (tmp == 0)
          return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return true;
  }

  public boolean isCorrectPassword(String password) {
    return password.length() >= 4;
  }

  public boolean isSamePasswords(String pass1, String pass2) {
      return !pass1.equals(pass2);
  }
}

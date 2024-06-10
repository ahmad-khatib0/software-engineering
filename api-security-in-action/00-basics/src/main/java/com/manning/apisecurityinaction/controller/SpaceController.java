package com.manning.apisecurityinaction.controller;

import java.sql.SQLException;

import org.dalesbred.Database;
import org.json.*;
import spark.*;

public class SpaceController {
  private final Database database;

  public SpaceController(Database database) {
    this.database = database;
  }

  public JSONObject createSpace(Request request, Response response) throws SQLException {
    var json = new JSONObject(request.body());
    var spaceName = json.getString("name");
    var owner = json.getString("owner");

    if (spaceName.length() > 255) {
      throw new IllegalArgumentException("space name too long");
    }

    // alter your exceptions to not echo back malformed user input in any case.
    // Although the security headers should prevent any bad effects, it’s best
    // practice not to include user input in error responses just to be sure. It’s
    // easy for a security header to be accidentally removed, so you should avoid
    // the issue in the first place by returning a more generic error message:
    if (!owner.matches("[a-zA-Z][a-zA-Z0-9]{1,29}")) {
      throw new IllegalArgumentException("invalid username: " + owner);
    }

    return database.withTransaction(tx -> {
      var spaceId = database.findUniqueLong("SELECT NEXT VALUE FOR space_id_seq;");
      database.updateUnique(
          "INSERT INTO spaces(space_id, name, owner) " + "VALUES(?, ?, ?);",
          spaceId,
          spaceName,
          owner);
      response.status(201);
      response.header("Location", "/spaces/" + spaceId);
      return new JSONObject().put("name", spaceName).put("uri", "/spaces/" + spaceId);
    });

  }
}

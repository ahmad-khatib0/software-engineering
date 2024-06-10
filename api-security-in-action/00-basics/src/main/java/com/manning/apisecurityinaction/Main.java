package com.manning.apisecurityinaction;

import static spark.Spark.after;
import static spark.Spark.afterAfter;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.halt;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.post;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.dalesbred.Database;
import org.dalesbred.result.EmptyResultException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.JSONException;
import org.json.JSONObject;

import com.manning.apisecurityinaction.controller.SpaceController;

import spark.Request;
import spark.Response;

public class Main {
  public static void main(String... args) throws Exception {
    // Initialize the database schema as the privileged user
    var datasource = JdbcConnectionPool.create("jdbc:h2:mem:natter", "natter", "password");
    var database = Database.forDataSource(datasource);

    datasource = JdbcConnectionPool.create("jdbc:h2:mem:natter", "natter_api_user", "password");
    // Switch to the natter_api_user and recreate the database objects.
    database = Database.forDataSource(datasource);

    createTables(database);

    var spaceController = new SpaceController(database);

    before(((request, response) -> {
      if (request.requestMethod().equals("POST") && !"application/json".equals(request.contentType())) {
        halt(415, new JSONObject().put("error", "Only application/json supported").toString());
      }
    }));

    afterAfter((request, response) -> {
      response.type("application/json;charset=utf-8");
      response.header("X-Content-Type-Options", "nosniff"); // prevent browser guessing the correct Content-Type.
      response.header("X-Frame-Options", "DENY"); // prevent responses being loaded in a frame
      response.header("X-XSS-Protection", "0"); // turn off built-in protections
      response.header("Cache-Control", "no-store");
      response.header("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'; sandbox");
      // default-src Prevents the response from loading any scripts or resources.
      // frame-ancestors A replacement for X-Frame-Options,
      // sandbox Disables scripts and other potentially dangerous content from being
      // executed.
      response.header("Server", "");
    });

    post("/spaces", spaceController::createSpace);
    after((request, response) -> {
      response.type("application/json");
    });

    internalServerError(new JSONObject().put("error", "internal server error").toString());
    notFound(new JSONObject().put("error", "not found").toString());

    exception(IllegalArgumentException.class, Main::badRequest);
    exception(JSONException.class, Main::badRequest);
    exception(EmptyResultException.class, (e, request, response) -> response.status(404));

  }

  private static void badRequest(Exception ex, Request request, Response response) {
    response.status(400);
    response.body(new JSONObject().put("error", ex.getMessage()).toString());
  }

  private static void createTables(Database database) throws Exception {
    var path = Paths.get(Main.class.getResource("/schema.sql").toURI());
    database.update(Files.readString(path));
  }
}

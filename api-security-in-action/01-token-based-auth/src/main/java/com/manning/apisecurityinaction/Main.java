package com.manning.apisecurityinaction;

import static spark.Spark.after;
import static spark.Spark.afterAfter;
import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Service.SPARK_DEFAULT_PORT;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Set;

import org.dalesbred.Database;
import org.dalesbred.result.EmptyResultException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.util.concurrent.RateLimiter;
import com.manning.apisecurityinaction.controller.AuditController;
import com.manning.apisecurityinaction.controller.ModeratorController;
import com.manning.apisecurityinaction.controller.SpaceController;
import com.manning.apisecurityinaction.controller.TokenController;
import com.manning.apisecurityinaction.controller.UserController;
import com.manning.apisecurityinaction.token.CookieTokenStore;
import com.manning.apisecurityinaction.token.DatabaseTokenStore;
import com.manning.apisecurityinaction.token.HmacTokenStore;
import com.manning.apisecurityinaction.token.TokenStore;

import spark.Request;
import spark.Response;

public class Main {
  public static void main(String... args) throws Exception {
    secure("localhost.p12", "changeit", null, null);
    port(args.length > 0 ? Integer.parseInt(args[0]) : SPARK_DEFAULT_PORT);

    var datasource = JdbcConnectionPool.create("jdbc:h2:mem:natter", "natter", "password");
    var database = Database.forDataSource(datasource);
    createTables(database);

    datasource = JdbcConnectionPool.create("jdbc:h2:mem:natter", "natter_api_user", "password");
    database = Database.forDataSource(datasource);

    var spacesController = new SpaceController(database);
    var userController = new UserController(database);
    var auditController = new AuditController(database);
    var moderatorController = new ModeratorController(database);

    var rateLimiter = RateLimiter.create(2.0d);
    before((request, response) -> {
      if (!rateLimiter.tryAcquire()) {
        response.header("Retry-After", "2");
        halt(429);
      }
    });
    before(new CorsFilter(Set.of("https://localhost:9999")));

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
      response.header("Server", "");
    });

    var keyPassword = System.getProperty("keystore.password", "changeit").toCharArray();
    var keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(new FileInputStream("keystore.p12"), keyPassword);
    var macKey = keyStore.getKey("hmac-key", keyPassword);

    var databaseTokenStore = new DatabaseTokenStore(database);
    // TokenStore tokenStore = databaseTokenStore;
    // TokenStore tokenStore = new CookieTokenStore();
    var tokenStore = new HmacTokenStore(databaseTokenStore, macKey);
    var tokenController = new TokenController(tokenStore);

    // support either method of authentication, providing flexibility for clients
    before(userController::authenticate);
    before(tokenController::validateToken);

    before(auditController::auditRequestStart); // must be after the authenticate
    afterAfter(auditController::auditRequestEnd);

    before("/sessions", userController::requireAuthentication);
    post("/sessions", tokenController::login);
    delete("/sessions", tokenController::logout);

    before("/spaces/:spaceId/messages", userController.requirePermission("POST", "w"));
    post("/spaces/:spaceId/messages", spacesController::postMessage);
    before("/spaces/:spaceId/messages/*", userController.requirePermission("GET", "r"));
    get("/spaces/:spaceId/messages/:msgId", spacesController::readMessage);
    before("/spaces/:spaceId/messages", userController.requirePermission("GET", "r"));
    get("/spaces/:spaceId/messages", spacesController::findMessages);
    before("/spaces/:spaceId/members", userController.requirePermission("POST", "rwd"));
    post("/spaces/:spaceId/members", spacesController::addMember);

    before("/spaces/:spaceId/messages/*", userController.requirePermission("DELETE", "d"));
    delete("/spaces/:spaceId/messages/:msgId", moderatorController::deletePost);

    post("/spaces", spacesController::createSpace);
    post("/users", userController::registerUser);

    after((request, response) -> {
      response.type("application/json");
    });

    get("/logs", auditController::readAuditLog);
    before("/expired_tokens", userController::requireAuthentication);
    delete("/expired_tokens", (request, response) -> {
      databaseTokenStore.deleteExpiredTokens();
      return new JSONObject();
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

package com.manning.apisecurityinaction.controller;

import static java.time.Instant.now;
import java.time.temporal.ChronoUnit;
import org.json.JSONObject;
import com.manning.apisecurityinaction.token.TokenStore;
import spark.Request;
import spark.Response;

public class TokenController {

  private final TokenStore tokenStore;

  public TokenController(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public JSONObject login(Request request, Response response) {
    String subject = request.attribute("subject");
    var expiry = now().plus(10, ChronoUnit.MINUTES);

    var token = new TokenStore.Token(expiry, subject);
    var tokenId = tokenStore.create(request, token);

    response.status(201);
    return new JSONObject().put("token", tokenId);
  }

  // cookies based
  // public void validateToken(Request request, Response response) {
  // var tokenId = request.headers("X-CSRF-Token");
  // if (tokenId == null)
  // return;

  // tokenStore.read(request, tokenId).ifPresent(token -> {
  // if (now().isBefore(token.expiry)) {
  // request.attribute("subject", token.username);
  // token.attributes.forEach(request::attribute);
  // }
  // });
  // }

  public void validateToken(Request request, Response response) {
    var tokenId = request.headers("Authorization");
    if (tokenId == null || !tokenId.startsWith("Bearer ")) {
      return;
    }
    tokenId = tokenId.substring(7);

    tokenStore.read(request, tokenId).ifPresent(token -> {
      if (now().isBefore(token.expiry)) {
        request.attribute("subject", token.username);
        token.attributes.forEach(request::attribute);
      } else {
        response.header("WWW-Authenticate", "Bearer error=\"invalid_token\"," + "error_description=\"Expired\"");
      }
    });
  }

  // public JSONObject logout(Request request, Response response) {
  // var tokenId = request.headers("X-CSRF-Token");
  // if (tokenId == null)
  // throw new IllegalArgumentException("missing token header");

  // tokenStore.revoke(request, tokenId);

  // response.status(200);
  // return new JSONObject();
  // }

  public JSONObject logout(Request request, Response response) {
    var tokenId = request.headers("Authorization");
    if (tokenId == null || !tokenId.startsWith("Bearer ")) {
      throw new IllegalArgumentException("missing token header");
    }
    tokenId = tokenId.substring(7);

    tokenStore.revoke(request, tokenId);

    response.status(200);
    return new JSONObject();
  }

}

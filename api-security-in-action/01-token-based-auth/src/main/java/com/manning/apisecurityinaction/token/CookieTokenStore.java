package com.manning.apisecurityinaction.token;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import spark.Request;

public class CookieTokenStore implements TokenStore {

  @Override
  public String create(Request request, Token token) {

    var session = request.session(false);
    // Preventing session fixation attacks
    if (session != null) {
      session.invalidate();
    }
    session = request.session(true);

    session.attribute("username", token.username);
    session.attribute("expiry", token.expiry);
    session.attribute("attrs", token.attributes);

    // implement hash-based double-submit cookies
    return Base64url.encode(sha256(session.id()));
  }

  @Override
  public Optional<Token> read(Request request, String tokenId) {

    var session = request.session(false);
    if (session == null) {
      return Optional.empty();
    }

    var provided = Base64url.decode(tokenId);
    var computed = sha256(session.id());

    // you should compare the computed value against the provided value using a
    // constant-time equality function to avoid timing attacks that would allow an
    // attacker to recover the CSRF token value just by observing how long it takes
    // your API to compare the provided value to the computed value. Java provides
    // the MessageDigest.isEqual method to compare two byte-arrays for equality in
    // constant time
    if (!MessageDigest.isEqual(computed, provided)) {
      return Optional.empty();
    }

    var token = new Token(session.attribute("expiry"), session.attribute("username"));
    token.attributes.putAll(session.attribute("attrs"));

    return Optional.of(token);
  }

  @Override
  public void revoke(Request request, String tokenId) {
    var session = request.session(false);

    // Although it is less critical on a logout endpoint, you should enforce CSRF
    // defenses here too to prevent an attacker maliciously logging out your users
    // to annoy them. To do this, verify the SHA-256 anti-CSRF token:
    if (session == null)
      return;

    var provided = Base64url.decode(tokenId);
    var computed = sha256(session.id());

    if (!MessageDigest.isEqual(computed, provided)) {
      return;
    }

    session.invalidate();
  }

  static byte[] sha256(String tokenId) {
    try {
      var sha256 = MessageDigest.getInstance("SHA-256");
      return sha256.digest(tokenId.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}

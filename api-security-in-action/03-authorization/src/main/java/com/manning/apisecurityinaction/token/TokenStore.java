package com.manning.apisecurityinaction.token;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import spark.Request;

public interface TokenStore {
  String create(Request request, Token token);
  Optional<Token> read(Request request, String tokenId);
  void revoke(Request request, String tokenId);

  class Token {
    public final Instant expiry;
    public final String username;
    public final Map<String, String> attributes;

    public Token(Instant expiry, String username) {
      this.expiry = expiry;
      this.username = username;
      this.attributes = new ConcurrentHashMap<>();
      // Use a concurrent map if the token will be accessed from multiple threads.
    }
  }
}


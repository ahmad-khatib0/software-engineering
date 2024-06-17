package com.manning.apisecurityinaction.token;

import com.github.nitram509.jmacaroons.*;
import com.github.nitram509.jmacaroons.verifier.TimestampCaveatVerifier;
import spark.Request;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

// MAC=$(curl -u demo:changeit -H 'Content-Type: application/json' \
// -d '{"owner":"demo","name":"test"}' https://localhost:4567/spaces | 
//  jq -r '.["messages-rw"]' | cut -d= -f2)
// 
// You can then append a caveat, for example setting the expiry time a minute or so into the future:
// 
// NEWMAC=$(mvn -q exec:java -Dexec.mainClass= com.manning.apisecurityinaction.CaveatAppender \
// -Dexec.args="$MAC 'time < 2020-08-03T12:05:00Z'")
// 
// You can then use this new macaroon to read any messages in the space until it expires:
// curl -u demo:changeit -i "https://localhost:4567/spaces/1/messages?access_token=$NEWMAC"
//
// After the new time limit expires, the request will return a 403 Forbidden error, but the
// original token will still work (just change $NEWMAC to $MAC in the query to test this).
// 
public class MacaroonTokenStore implements SecureTokenStore {
  private final TokenStore delegate;
  private final Key macKey;

  private MacaroonTokenStore(TokenStore delegate, Key macKey) {
    this.delegate = delegate;
    this.macKey = macKey;
  }

  public static SecureTokenStore wrap(ConfidentialTokenStore tokenStore, Key macKey) {
    return new MacaroonTokenStore(tokenStore, macKey);
  }

  public static AuthenticatedTokenStore wrap(TokenStore tokenStore, Key macKey) {
    return new MacaroonTokenStore(tokenStore, macKey);
  }

  @Override
  public String create(Request request, Token token) {
    var identifier = delegate.create(request, token);
    // The location hint is not included in the authentication tag and is intended
    // only as a hint to the client. Its value shouldn’t be trusted because it can
    // be tampered with.
    var macaroon = MacaroonsBuilder.create("", macKey.getEncoded(), identifier);
    return macaroon.serialize();
  }

  @Override
  public Optional<Token> read(Request request, String tokenId) {
    var macaroon = MacaroonsBuilder.deserialize(tokenId);

    var verifier = new MacaroonsVerifier(macaroon);
    verifier.satisfyGeneral(new TimestampCaveatVerifier());
    verifier.satisfyExact("method = " + request.requestMethod());
    verifier.satisfyGeneral(new SinceVerifier(request));

    if (verifier.isValid(macKey.getEncoded())) {
      return delegate.read(request, macaroon.identifier);
    }
    return Optional.empty();
  }

  @Override
  public void revoke(Request request, String tokenId) {
    var macaroon = MacaroonsBuilder.deserialize(tokenId);
    delegate.revoke(request, macaroon.identifier);
  }

  private static class SinceVerifier implements GeneralCaveatVerifier {
    private final Request request;

    private SinceVerifier(Request request) {
      this.request = request;
    }

    @Override
    public boolean verifyCaveat(String caveat) {
      if (caveat.startsWith("since > ")) {
        var minSince = Instant.parse(caveat.substring(8));
        var reqSince = Instant.now().minus(1, ChronoUnit.DAYS);
        if (request.queryParams("since") != null) {
          reqSince = Instant.parse(request.queryParams("since"));
        }
        return reqSince.isAfter(minSince);
      }

      return false;
    }
  }
}

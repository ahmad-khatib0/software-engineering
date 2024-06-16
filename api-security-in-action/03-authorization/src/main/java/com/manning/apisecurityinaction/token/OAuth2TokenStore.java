package com.manning.apisecurityinaction.token;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.*;
import java.security.cert.*;
import java.time.Instant;
import java.util.*;

import javax.net.ssl.*;

import org.json.JSONObject;

import spark.Request;

public class OAuth2TokenStore implements SecureTokenStore {

  private final URI introspectionEndpoint;
  private final String authorization;

  private final HttpClient httpClient;

  public OAuth2TokenStore(URI introspectionEndpoint, String clientId, String clientSecret) {
    this.introspectionEndpoint = introspectionEndpoint;

    var credentials = URLEncoder.encode(clientId, UTF_8) + ":" + URLEncoder.encode(clientSecret, UTF_8);
    this.authorization = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(UTF_8));

    var sslParams = new SSLParameters();
    sslParams.setProtocols(new String[] { "TLSv1.3", "TLSv1.2" });
    sslParams.setCipherSuites(new String[] {
        // TLS 1.3 cipher suites
        "TLS_AES_128_GCM_SHA256",
        "TLS_AES_256_GCM_SHA384",
        "TLS_CHACHA20_POLY1305_SHA256",

        // TLS 1.2 cipher suites
        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256",
        "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256"
    });
    sslParams.setUseCipherSuitesOrder(true);
    sslParams.setEndpointIdentificationAlgorithm("HTTPS");

    try {
      var trustedCerts = KeyStore.getInstance("PKCS12");
      trustedCerts.load(new FileInputStream("as.example.com.ca.p12"), "changeit".toCharArray());
      var tmf = TrustManagerFactory.getInstance("PKIX");
      // Enable revocation checking (not in the book).
      var pkixParams = new PKIXBuilderParameters(trustedCerts, null);
      // You can either set pkixParams.setRevocationEnabled(true) to use
      // the default revocation mechanisms configured in java.security.
      // Here, we explicitly configure a revocation checker to ensure OCSP
      // is turned on (it's off by default). If your CA doesn't support
      // revocation checking and you can't fix that (!), then you should
      // instead call pkixParams.setRevocationChecking(false) and
      // comment out the following code that adds the revocation checker.
      var revocationChecker = (PKIXRevocationChecker) CertPathValidator.getInstance("PKIX").getRevocationChecker();
      // You can configure default OCSP responder URI and other options
      // using setters on the revocationChecker if required.
      pkixParams.addCertPathChecker(revocationChecker);
      var tmParams = new CertPathTrustManagerParameters(pkixParams);
      tmf.init(tmParams);
      var sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, tmf.getTrustManagers(), null);

      this.httpClient = HttpClient.newBuilder().sslParameters(sslParams).sslContext(sslContext).build();

    } catch (GeneralSecurityException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String create(Request request, Token token) {
    // Throw an exception to disable direct login and logout
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Token> read(Request request, String tokenId) {
    if (!tokenId.matches("[\\x20-\\x7E]{1,1024}")) {
      return Optional.empty();
    }

    // hint parameter to indicate that it’s an access token, but this is optional.
    var form = "token=" + URLEncoder.encode(tokenId, UTF_8) + "&token_type_hint=access_token";
    var httpRequest = HttpRequest.newBuilder()
        .uri(introspectionEndpoint)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Authorization", authorization) // Call the introspection endpoint using your client cred
        .POST(BodyPublishers.ofString(form))
        .build();

    try {
      var httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());

      if (httpResponse.statusCode() == 200) {
        var json = new JSONObject(httpResponse.body());
        if (json.getBoolean("active")) {
          return processResponse(json);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }

    return Optional.empty();
  }

  private Optional<Token> processResponse(JSONObject response) {
    var expiry = Instant.ofEpochSecond(response.getLong("exp"));
    var subject = response.getString("sub");

    var token = new Token(expiry, subject);

    token.attributes.put("scope", response.getString("scope"));
    token.attributes.put("client_id", response.optString("client_id"));

    return Optional.of(token);
  }

  @Override
  public void revoke(Request request, String tokenId) {
    // Throw an exception to disable direct login and logout
    throw new UnsupportedOperationException();
  }
}

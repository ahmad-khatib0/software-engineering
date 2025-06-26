Here's a first part of Envoy configuration cheat sheet — covering core components and their
relationships. We'll go deeper in subsequent messages to complete the full “100%” overview!

---

## 📘 Envoy Configuration Overview

Envoy’s configuration is hierarchical:

- **Bootstrap**: Loads static (`static_resources`) and dynamic (`dynamic_resources`)
  config, plus admin, tracing, logging, overload manager.
- **static_resources** / **dynamic_resources**:

  - Define **listeners** (for incoming traffic)
  - Define **clusters** (outgoing/upstream targets)
  - Optionally use xDS (LDS, CDS, EDS, RDS) for dynamic config ([stackoverflow.com][1], [envoyproxy.io][2], [codilime.com][3])

---

## 🔊 Listeners → Filter Chains → Filters

- **Listeners**:

  - Bind to IP/port, protocol (TCP/HTTP) ([codilime.com][3])
  - Hold one or more **filter_chains**, each matching on criteria (TLS, protocol, etc)

- **Filter chains**:

  - Select based on connection metadata
  - Contain a sequence of **filters** (network-level or HTTP-level)

- **Filters**:

  - **Network filters** (e.g., `tcp_proxy`, `http_connection_manager`)
  - **HTTP filters** (within HCM → operate on HTTP requests/responses)
  - End of chain: proxy or router filter forwards traffic to upstream

Example (HTTP):

```yaml
listeners:
  - name: listener_0
    address:
      socket_address: { address: 0.0.0.0, port_value: 10000 }
    filter_chains:
      - filters:
          - name: envoy.filters.network.http_connection_manager
            typed_config: # HCM wrapper for HTTP filters
              stat_prefix: ingress_http
              codec_type: AUTO|HTTP1|HTTP2 :contentReference[oaicite:15]{index=15}
              http_filters:
                - name: envoy.filters.http.router
```

---

## ⛓️ Filter Chain Flow

1. **Accepts** a connection at Listener
2. **Selects** matching `filter_chain_match`
3. **Applies filters** in order (e.g., TCP proxy, then HCM with HTTP filters)
4. **Router filter** sends request to a cluster
5. Data returns through filters in **reverse order** ([developer.hashicorp.com][4], [codilime.com][3])

---

## 🧩 Clusters & Load Assignment

- **Cluster** represents an upstream service group:

  - `name`, `type` (STATIC, STRICT_DNS, EDS, etc), `connect_timeout`, `lb_policy`
    (e.g. ROUND_ROBIN, LEAST_REQUEST) ([envoyproxy.io][5])
  - **`load_assignment`**: maps cluster to endpoints for STATIC/DNS types ([envoyproxy.io][5])

    ```yaml
    clusters:
      - name: books-server
        type: STATIC
        lb_policy: ROUND_ROBIN
        load_assignment:
          endpoints:
            - lb_endpoints:
                - endpoint:
                    address:
                      {
                        socket_address:
                          { address: 127.0.0.1, port_value: 10100 },
                      }
    ```

- **LB policy** determines how Envoy distributes requests (round-robin, random, ring hash...)

- **Health checks** and **transport_socket_match** enable advanced routing
  (mTLS, plain text) ([envoyproxy.io][5])

---

## 🌍 HTTP: Virtual Hosts & Routes

Within HTTP Connection Manager (`HCM`):

- **`route_config`**:

  - `name`
  - `virtual_hosts`: list of domains/host header matchers ([istio-insider.mygraphql.com][6], [envoyproxy.io][7])

    - Each **virtual_host**:

      - `domains` (e.g. `["*"]`)
      - `routes`: match on path, headers → actions:

        - **route** → `cluster`
        - **direct_response** → static responses
        - `prefix_rewrite`, `host_rewrite`, etc&#x20;

Example:

```yaml
route_config:
  virtual_hosts:
    - name: backend
      domains: ["*"]
      routes:
        - match: { prefix: "/books/" }
          route:
            prefix_rewrite: "/"
            cluster: books-server
        - match: { prefix: "/" }
          direct_response: { status: 403 }
```

---

## ⚙️ typed_config & codec_type

- **`@type`** specifies full protobuf message (filter config) ([envoyproxy.io][2])
- **`typed_config`** embeds structured configuration (e.g., HCM, TcpProxy)
- **codec_type** (in HCM): how HTTP is parsed — `AUTO`, `HTTP1`, or `HTTP2` ([codilime.com][3], [medium.com][8])

---

## 📚 Dynamic vs Static Resources

- `static_resources`: listeners & clusters defined at startup ([envoyproxy.io][2])
- `dynamic_resources` (via xDS):

  - **LDS** = listener configs
  - **CDS** = cluster configs
  - **EDS** = endpoints / load_assignment updates
  - **RDS** = route_config updates ([codilime.com][3])

Envoy can hot-reload listeners/clusters/routes without restarting.

---

## 🧩 How They Connect

```
[downstream connection]
        ↓ listener (port/socket)
        ↓ filter_chain → filters → (proxy/router)
                ↓ router → cluster[name]
                ↓ cluster → load_assignment → endpoint(s)
```

- Listeners pick filter chains
- Filter chains run filters for functionality
- Router/TcpProxy forwards to clusters
- Clusters use load_assignment & lb_policy to pick endpoints
- typed_config shapes filter behavior
- In HTTP: virtual_hosts & routes route to clusters

---

This covers the foundation (listeners, filters, clusters, load assignment, virtual hosts, routes, typed_config, codec_type, lb_policy, dynamic/static resources). Next, we’ll dig into:

- **Admin interface**, **overload_manager**, **access logging/tracing**, **filter specifics**
  (ext_proc, RBAC, TLS context), and **xDS details**.

[1]: https://stackoverflow.com/questions/58894457/how-to-configure-fallbacks-in-envoyproxy?utm_source=chatgpt.com "How to configure fallbacks in envoyproxy? - Stack Overflow"
[2]: https://www.envoyproxy.io/docs/envoy/latest/start/quick-start/configuration-static?utm_source=chatgpt.com "Configuration: Static — envoy 1.35.0-dev-8f3122 documentation"
[3]: https://codilime.com/blog/envoy-configuration/?utm_source=chatgpt.com "Envoy Configuration in a Nutshell: Listeners, Clusters And More"
[4]: https://developer.hashicorp.com/consul/docs/reference/proxy/envoy?utm_source=chatgpt.com "Envoy proxy configuration reference | Consul - HashiCorp Developer"
[5]: https://www.envoyproxy.io/docs/envoy/latest/api-v3/config/cluster/v3/cluster.proto?utm_source=chatgpt.com "Cluster configuration (proto) - Envoy proxy"
[6]: https://istio-insider.mygraphql.com/en/latest/ch2-envoy/envoy-istio-metrics/envoy-stat.html?utm_source=chatgpt.com "Envoy Metrics — Istio & Envoy Insider"
[7]: https://www.envoyproxy.io/docs/envoy/latest/api-v3/config/route/v3/route_components.proto?utm_source=chatgpt.com "HTTP route components (proto) - Envoy proxy"
[8]: https://medium.com/%40nynptel/envoy-proxy-reverse-proxy-basic-example-d3f9b1c001b9?utm_source=chatgpt.com "Envoy proxy Reverse Proxy Basic Example | by NAYAN PATEL"

---

---

---

Great! Here's **Part 2** of the complete Envoy cheat sheet — focused on advanced features, observability, security, and extensibility. This fills in the remaining 100% coverage.

---

## 🔍 Admin Interface

Envoy provides a powerful **admin server**:

```yaml
admin:
  access_log_path: /tmp/admin_access.log
  address:
    socket_address: { address: 127.0.0.1, port_value: 9901 }
```

- Access via `curl localhost:9901/stats`, `.../config_dump`, `.../clusters`, `.../listeners`, etc.
- Useful endpoints:

  - `/stats`, `/stats/prometheus`
  - `/config_dump`, `/listeners`, `/clusters`
  - `/ready`, `/healthcheck/fail`, `/runtime`
  - `/quitquitquit`

---

## 📈 Observability

### 1. **Access Logs**

```yaml
typed_config:
  access_log:
    - name: envoy.access_loggers.file
      typed_config:
        "@type": type.googleapis.com/envoy.extensions.access_loggers.file.v3.FileAccessLog
        path: /var/log/envoy/access.log
        log_format:
          text_format_source:
            inline_string: "[%START_TIME%] \"%REQ(:METHOD)% %REQ(X-ENVOY-ORIGINAL-PATH?:PATH)% ... \n"
```

Supports JSON or custom formats.

---

### 2. **Metrics & Stats**

- **Scopes**: listener, cluster, route, etc.
- Use `/stats/prometheus` for scraping.
- Sample stat: `cluster.bookstore.upstream_rq_200`

---

### 3. **Tracing**

```yaml
tracing:
  http:
    name: envoy.tracers.zipkin
    typed_config:
      "@type": type.googleapis.com/envoy.config.trace.v3.ZipkinConfig
      collector_cluster: zipkin
      collector_endpoint: "/api/v2/spans"
```

Supports:

- **Zipkin**
- **Jaeger**
- **Datadog**
- **Lightstep**
- **OpenTelemetry**

Use `x-request-id` headers for trace propagation.

---

## 🔒 Security

### 1. **TLS (Downstream/Upstream)**

```yaml
transport_socket:
  name: envoy.transport_sockets.tls
  typed_config:
    "@type": type.googleapis.com/envoy.extensions.transport_sockets.tls.v3.DownstreamTlsContext
    common_tls_context:
      tls_certificates:
        - certificate_chain: { filename: cert.pem }
          private_key: { filename: key.pem }
```

- Use `DownstreamTlsContext` (listener)
- Use `UpstreamTlsContext` (cluster)

---

### 2. **SDS (Secret Discovery Service)**

For dynamic TLS:

```yaml
common_tls_context:
  tls_certificate_sds_secret_configs:
    - name: "tls_cert"
      sds_config:
        api_config_source:
          api_type: GRPC
          grpc_services: ...
```

---

### 3. **RBAC (Role-Based Access Control)**

```yaml
name: envoy.filters.http.rbac
typed_config:
  "@type": type.googleapis.com/envoy.extensions.filters.http.rbac.v3.RBAC
  rules:
    policies:
      "allow-admin":
        permissions: [{ url_path: { path: "/admin" } }]
        principals: [{ authenticated: { principal_name: { exact: "admin" } } }]
```

Controls **who** can access **what** based on attributes like path, IP, JWT claims, etc.

---

## ⚙️ Advanced Filters

- **ext_authz**: Call external authorization service
- **ext_proc**: External HTTP processing
- **gzip**: Response compression
- **lua**: Embed Lua scripting logic
- **ratelimit**: Rate limiting via external server

---

## ⚙️ Common Network Filters

- `envoy.filters.network.tcp_proxy`: For non-HTTP protocols
- `envoy.filters.network.redis_proxy`: For Redis
- `envoy.filters.network.mongo_proxy`: For Mongo
- `envoy.filters.network.mysql_proxy`: For MySQL

---

## 🚀 Dynamic Config (xDS APIs)

You can offload config to an xDS server:

### 1. **dynamic_resources**

```yaml
dynamic_resources:
  lds_config:
    ads: {}
  cds_config:
    ads: {}
  ads_config:
    api_type: GRPC
    grpc_services:
      - envoy_grpc:
          cluster_name: xds_cluster
```

- **LDS** = Listener Discovery Service
- **CDS** = Cluster Discovery Service
- **RDS** = Route Discovery Service
- **EDS** = Endpoint Discovery Service
- **ADS** = Aggregated Discovery Service (combines LDS/CDS/RDS/EDS)

### 2. **xDS server** = your control plane (e.g., Istio, Contour, custom)

---

## 📦 Runtime Config Layering

Supports hot config changes (e.g. enabling/disabling features):

```yaml
runtime:
  layers:
    - name: static_layer
      static_layer:
        envoy.deprecated_features: true
```

Use `/runtime` admin endpoint to inspect or override.

---

## 📁 Config Dump Debugging

Use:

```bash
curl localhost:9901/config_dump | jq .
```

Inspect:

- Listeners
- Clusters
- Routes
- Endpoints
- Secrets
- Runtime

---

## 🔄 Hot Reload / Live Updates

- `envoy --drain-time-s --parent-shutdown-time-s` for smooth restarts
- Use **hot restart** to reload binary/config without dropping traffic
- With xDS, updates happen live without restarts

---

## 🧠 Tips for Reading Envoy Configs

1. **Start from listener**

   - Which port? → Which filters? → HCM?

2. **Follow HCM to route_config**

   - Which virtual_host & route? → What cluster?

3. **Look at cluster**

   - Where does it send? Which LB? TLS?

4. **Check filters** in order

   - Any RBAC? ext_authz? rate limit?

5. **Debug with** `/config_dump`, `/stats`, access logs

---

## ✅ Summary Diagram

```
+-----------+
| Listener  | ← Incoming traffic
+-----------+
     |
     v
[ Filter Chain ]
     |
     v
[ Filters ] → [ HTTP Manager ] → [ Router ] → Cluster → Endpoint
                         |
              [ Route Config + Virtual Hosts ]
```

---

---

---

Walk through **real-world Envoy configuration examples**, with **working YAML snippets**
and explanation for each use case.

---

## ✅ 1. **Mutual TLS (mTLS) Between Envoy and Upstream**

### Use Case

Secure communication between Envoy and an upstream service using **mutual TLS**.

### Example: Cluster Config with `UpstreamTlsContext`

```yaml
clusters:
  - name: secure_service
    type: STRICT_DNS
    connect_timeout: 1s
    lb_policy: ROUND_ROBIN
    load_assignment:
      cluster_name: secure_service
      endpoints:
        - lb_endpoints:
            - endpoint:
                address:
                  socket_address:
                    address: secure.service.local
                    port_value: 8443
    transport_socket:
      name: envoy.transport_sockets.tls
      typed_config:
        "@type": type.googleapis.com/envoy.extensions.transport_sockets.tls.v3.UpstreamTlsContext
        common_tls_context:
          tls_certificates:
            - certificate_chain: { filename: "/etc/envoy/client.crt" }
              private_key: { filename: "/etc/envoy/client.key" }
          validation_context:
            trusted_ca: { filename: "/etc/envoy/ca.crt" }
```

🧠 **What it does**:

- Presents a **client certificate**
- Validates the **server certificate**
- Uses **TLS** over port `8443`

---

## 🌀 2. **Canary Routing via Header Match**

### Use Case

Send a percentage of traffic to a **canary** cluster based on request headers or routes.

### Example: HTTP Route with Header Match

```yaml
route_config:
  virtual_hosts:
    - name: canary_demo
      domains: ["*"]
      routes:
        - match:
            prefix: "/api"
            headers:
              - name: "x-canary"
                exact_match: "true"
          route:
            cluster: api-canary
        - match:
            prefix: "/api"
          route:
            cluster: api-stable
```

🧠 **What it does**:

- If header `x-canary: true` is present → send to `api-canary`
- Otherwise → `api-stable`

🔁 **Alternative**: Use **runtime fractional routing** (for gradual rollout)

```yaml
route:
  weighted_clusters:
    clusters:
      - name: api-canary
        weight: 10
      - name: api-stable
        weight: 90
```

---

## 📦 3. **gRPC Proxy with HTTP/2 & Timeout**

### Use Case

Proxy gRPC calls from clients to backend, with support for streaming and retries.

### Listener → HTTP Connection Manager

```yaml
name: envoy.filters.network.http_connection_manager
typed_config:
  "@type": type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager
  codec_type: HTTP2
  stat_prefix: grpc
  route_config:
    virtual_hosts:
      - name: grpc_services
        domains: ["*"]
        routes:
          - match:
              prefix: "/helloworld.Greeter/"
            route:
              cluster: grpc-backend
              timeout: 0s # Required for streaming gRPC
  http_filters:
    - name: envoy.filters.http.router
```

Cluster with `HTTP2` support:

```yaml
clusters:
  - name: grpc-backend
    connect_timeout: 2s
    type: STRICT_DNS
    lb_policy: ROUND_ROBIN
    http2_protocol_options: {} # Required
    load_assignment:
      cluster_name: grpc-backend
      endpoints:
        - lb_endpoints:
            - endpoint:
                address:
                  socket_address:
                    address: grpc.backend.local
                    port_value: 50051
```

🧠 **What it does**:

- Parses HTTP/2 requests (gRPC)
- Uses prefix `/helloworld.Greeter/` to match gRPC service
- Disables timeout for long-lived streaming

---

## 🛡️ 4. **External Authorization (ext_authz)**

### Use Case

Call external service (e.g., OPA, custom server) to decide if a request should be allowed.

```yaml
http_filters:
  - name: envoy.filters.http.ext_authz
    typed_config:
      "@type": type.googleapis.com/envoy.extensions.filters.http.ext_authz.v3.ExtAuthz
      http_service:
        server_uri:
          uri: http://auth.local:8000
          cluster: ext-authz
          timeout: 1s
        authorization_request:
          allowed_headers:
            patterns:
              - exact: "x-user-id"
```

Cluster definition:

```yaml
clusters:
  - name: ext-authz
    type: STRICT_DNS
    connect_timeout: 1s
    lb_policy: ROUND_ROBIN
    load_assignment:
      cluster_name: ext-authz
      endpoints:
        - lb_endpoints:
            - endpoint:
                address:
                  socket_address:
                    address: auth.local
                    port_value: 8000
```

🧠 **What it does**:

- Sends incoming HTTP request metadata to the external service
- Continues only if it responds `200 OK`

---

## 💥 5. **Direct Response for Maintenance Page**

### Use Case

Temporarily block traffic to a service with a static response.

```yaml
routes:
  - match: { prefix: "/" }
    direct_response:
      status: 503
      body:
        inline_string: "Service temporarily down for maintenance"
```

🧠 **What it does**:

- Returns a **503 error** with a message instead of forwarding to upstream

---

## 📶 6. **Rate Limiting with External Service**

```yaml
http_filters:
  - name: envoy.filters.http.ratelimit
    typed_config:
      "@type": type.googleapis.com/envoy.extensions.filters.http.ratelimit.v3.RateLimit
      domain: envoy
      rate_limit_service:
        grpc_service:
          envoy_grpc:
            cluster_name: rate-limit-cluster
```

🧠 **Requires**:

- A rate limit server implementing [Envoy’s RateLimit Service gRPC API](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/other_features/global_rate_limiting)

---

## 📌 Want More? See also

- **JWT Authentication**
- **WebSocket proxying**
- **Multi-tenant routing**
- **Circuit breakers & retries**
- **Advanced HCM config (idle timeouts, retries, etc.)**
- **Custom Lua filters**

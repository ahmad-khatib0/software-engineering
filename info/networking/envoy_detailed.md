# 🔎 Part 1: Envoy Core Concepts, Architecture, and Terminology

---

## 🧠 What Is Envoy?

**Envoy** is a **high-performance L4/L7 proxy** designed for cloud-native and
service-mesh environments. It was originally built at Lyft and is now a
**graduated CNCF project**. It’s written in C++ for performance and supports:

- HTTP/1.1, HTTP/2, and HTTP/3 (QUIC)
- gRPC and gRPC-Web
- Load balancing, retries, circuit breaking
- Service discovery
- mTLS and rate limiting
- Metrics (Prometheus, statsd)
- Dynamic config via xDS APIs (for control planes like Istio, Consul, etc.)

---

## 🧩 Core Architecture Overview

Here’s how Envoy works at a high level:

```
          ┌────────────┐
          │  Listener  │  ← Accepts inbound connections (e.g., port 8080)
          └────┬───────┘
               │
      ┌────────▼──────────┐
      │ Filter Chain      │  ← Network/HTTP filters (auth, logging, etc.)
      └────────┬──────────┘
               │
      ┌────────▼──────────┐
      │ Route Matching    │  ← Match requests to routes based on prefix/header/etc.
      └────────┬──────────┘
               │
      ┌────────▼──────────┐
      │ Cluster Selection │  ← Logical backend group
      └────────┬──────────┘
               │
      ┌────────▼──────────┐
      │ Load Balancer     │  ← Round-robin, least-request, etc.
      └────────┬──────────┘
               │
        ┌──────▼───────┐
        │ Upstream Host│  ← Actual backend (e.g., gRPC server)
        └──────────────┘
```

---

## 🔑 Key Terminology Explained

---

### 1. **Listener**

- Defines **what port** and **protocol** Envoy listens on.
- You can have multiple listeners (e.g., one for HTTP, one for HTTPS).
- Bound to `0.0.0.0:<port>` or a specific IP.

📌 **Example:**

```yaml
listeners:
  - address:
      socket_address:
        address: 0.0.0.0
        port_value: 8080
```

---

### 2. **Filter Chain**

- A pipeline of **filters** that modify or inspect traffic.
- Two kinds:
  - **Network filters** → e.g., `tcp_proxy`
  - **HTTP filters** → e.g., `router`, `authn`, `cors`, `ext_authz`

📌 **Common filters:**

- `envoy.filters.http.router`: Sends request to backend
- `envoy.filters.http.jwt_authn`: Validates JWTs
- `envoy.filters.network.tcp_proxy`: For L4 TCP proxying

---

### 3. **Route / Virtual Host**

- Routing logic inside the HTTP connection manager.
- Routes are grouped into **virtual hosts**, matched by `Host` header.
- You can match based on:
  - Path prefix
  - Exact path
  - Headers
  - Query parameters
  - Methods

📌 **Example:**

```yaml
routes:
  - match:
      prefix: "/api"
    route:
      cluster: my_api_cluster
```

---

### 4. **Cluster**

- A logical group of backends.
- You configure:
  - Load balancing policy (round_robin, least_request, etc.)
  - DNS or static endpoints
  - Health checks
  - HTTP/2 settings (for gRPC)

📌 **Example:**

```yaml
clusters:
  - name: my_api_cluster
    type: strict_dns
    http2_protocol_options: {}
    load_assignment:
      endpoints:
        - lb_endpoints:
            - endpoint:
                address:
                  socket_address:
                    address: my-service
                    port_value: 50051
```

---

### 5. **Endpoint**

- A specific IP+port (or DNS name+port) inside a cluster.

---

### 6. **xDS API**

- A family of APIs for **dynamic configuration**:
  - LDS: Listener Discovery Service
  - RDS: Route Discovery Service
  - CDS: Cluster Discovery Service
  - EDS: Endpoint Discovery Service

Used by control planes (Istio, Consul Connect, etc.) to dynamically update Envoy.

---

### 7. **Admin Interface**

- Default: `localhost:9901`
- Offers:
  - Metrics
  - Configuration dump
  - Health info
  - Log level control

📌 Visit: `http://localhost:9901/stats`

---

### 8. **Bootstrap File**

- The main YAML file that starts Envoy.
- Defines listeners, clusters, and other static config.

Usually passed like:

```bash
envoy -c envoy.yaml
```

---

### 9. **Statistics and Tracing**

- Native support for:
  - Prometheus
  - StatsD
  - OpenTelemetry
  - Zipkin, Jaeger

Every metric is prefixed with things like:

- `listener.`
- `cluster.`
- `http.<stat_prefix>.`

---

### 10. **gRPC Awareness**

- Envoy is natively aware of gRPC because:
  - It understands HTTP/2
  - Can proxy streams and unary calls
  - Can inspect metadata
  - Can enforce retries, circuit breakers, timeouts per method

---

## 🧱 Basic Envoy Workflow

1. Client sends HTTP/gRPC request to Envoy on port 8080
2. Envoy **listener** accepts connection
3. Request passes through **filter chain**
4. Envoy matches a **route** based on path/host
5. Route points to a **cluster**
6. Envoy picks an **endpoint** from the cluster
7. Request forwarded to backend (gRPC, REST, etc.)

---

---

---

# ⚙️ Part 2: Real-World Use Cases, gRPC Proxying, Configuration Patterns, Routing, Filters, TLS/mTLS

This section is very **practical**. You'll learn how Envoy actually works in real systems,
including gRPC and secure communication.

---

## ✅ 1. Real-World Use Cases for Envoy

---

### 🌐 **As an API Gateway**

- Route external traffic to internal services.
- Add JWT validation, rate limiting, and CORS.
- Terminate TLS at the edge.

📌 _Example:_

```http
Client → Envoy (TLS) → Route → UserService
```

---

### 🔁 **As a Sidecar in a Service Mesh**

- Deployed with every microservice.
- Handles mTLS, telemetry, retries, circuit breakers.
- Controlled via xDS by Istio or Consul.

📌 _Example:_

```http
Service A → Envoy A ↔ Envoy B → Service B
```

---

### 📡 **As a gRPC Gateway**

- Terminate gRPC-Web from browser
- Forward to internal gRPC server
- Supports streaming, unary, metadata

📌 _Example:_

```http
Browser → gRPC-Web → Envoy → gRPC Server
```

---

## ✅ 2. Envoy Config for gRPC Proxying

---

Let’s say your gRPC server is running on port `50051`.

Here's a full working **Envoy config** to proxy gRPC:

```yaml
static_resources:
  listeners:
    - name: grpc_listener
      address:
        socket_address:
          address: 0.0.0.0
          port_value: 8080
      filter_chains:
        - filters:
            - name: envoy.filters.network.http_connection_manager
              typed_config:
                "@type": type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager
                stat_prefix: grpc_proxy
                codec_type: AUTO
                route_config:
                  name: grpc_route
                  virtual_hosts:
                    - name: backend
                      domains: ["*"]
                      routes:
                        - match:
                            prefix: "/"
                          route:
                            cluster: grpc_service
                http_filters:
                  - name: envoy.filters.http.router

  clusters:
    - name: grpc_service
      connect_timeout: 5s
      type: strict_dns
      http2_protocol_options: {} # Required for gRPC!
      load_assignment:
        cluster_name: grpc_service
        endpoints:
          - lb_endpoints:
              - endpoint:
                  address:
                    socket_address:
                      address: my-grpc-service
                      port_value: 50051

admin:
  access_log_path: "/dev/null"
  address:
    socket_address:
      address: 127.0.0.1
      port_value: 9901
```

---

### 🔁 gRPC-Web Support

To support **gRPC-Web** from a browser, add this filter:

```yaml
http_filters:
  - name: envoy.filters.http.grpc_web
  - name: envoy.filters.http.cors
  - name: envoy.filters.http.router
```

This enables:

- Cross-origin support (CORS)
- gRPC-Web framing translation

---

## ✅ 3. Routing Patterns (L7)

---

### Prefix-Based Routing

```yaml
routes:
  - match:
      prefix: "/users"
    route:
      cluster: users_cluster
```

---

### Method-Based Routing

```yaml
match:
  grpc:
    method:
      service: "user.v1.UserService"
      name: "GetUser"
```

---

### Header Matching

```yaml
match:
  headers:
    - name: x-user-role
      exact_match: admin
```

---

## ✅ 4. Filters: HTTP and Network

---

### 🧱 HTTP Filters (L7)

Used inside `http_connection_manager`:

| Filter       | Purpose                                   |
| ------------ | ----------------------------------------- |
| `router`     | Routes request to backend                 |
| `cors`       | Enables CORS                              |
| `grpc_web`   | Supports gRPC-Web from browsers           |
| `jwt_authn`  | Validates JWTs                            |
| `ext_authz`  | External authorization via gRPC/HTTP call |
| `rate_limit` | Rate limiting                             |
| `fault`      | Inject latency/faults (for testing)       |

---

### 🌐 Network Filters (L4)

Used directly inside `filter_chains` (non-HTTP):

| Filter        | Purpose                          |
| ------------- | -------------------------------- |
| `tcp_proxy`   | TCP passthrough (e.g., DB proxy) |
| `sni_cluster` | Routes based on TLS SNI          |
| `mongo_proxy` | MongoDB sniffing                 |

---

## ✅ 5. TLS / mTLS

---

### 🔒 Terminating TLS at Envoy

```yaml
transport_socket:
  name: envoy.transport_sockets.tls
  typed_config:
    "@type": type.googleapis.com/envoy.extensions.transport_sockets.tls.v3.DownstreamTlsContext
    common_tls_context:
      tls_certificates:
        - certificate_chain:
            filename: "/etc/certs/server.crt"
          private_key:
            filename: "/etc/certs/server.key"
```

---

### 🔐 Mutual TLS (mTLS)

For **both** sides (client and server) to authenticate:

```yaml
validation_context:
  trusted_ca:
    filename: "/etc/certs/ca.crt"
```

Combined with the `tls_certificates`, you get full mTLS.

---

### 📜 Client Certificate Auth

You can even enforce **client certificate verification** for external clients.

---

## ✅ 6. Envoy in Your Project (Dev Example)

---

### Project Setup

```bash
project-root/
├── envoy.yaml
├── certs/
│   ├── server.crt
│   └── server.key
├── grpc-server/           # Go/Node/TS gRPC server
├── grpc-client/           # Client or browser app
└── docker-compose.yml     # Optional
```

---

### Run Envoy

```bash
envoy -c envoy.yaml
```

---

### Test gRPC

If your service is on localhost:8080:

```ts
new grpc.MyService("http://localhost:8080");
```

If using gRPC-Web:

```ts
new grpc.MyService("http://localhost:8080", null, {
  transport: grpc.CrossBrowserHttpTransport({ ... }),
})
```

---

---

---

Let’s finish strong with **Part 3**, which focuses on observability, debugging,
deployment, performance, and best practices.

---

# 📊 Part 3: Debugging, Observability, Deployment, Performance Tuning, Best Practices & Cheat Sheet

---

## 🪛 1. Debugging & Introspection

---

### 🔍 Admin Interface

Envoy exposes a powerful admin interface.

```yaml
admin:
  access_log_path: "/tmp/envoy_admin.log"
  address:
    socket_address:
      address: 127.0.0.1
      port_value: 9901
```

Access via:
`http://localhost:9901`

---

### Useful Endpoints

| Endpoint        | Description                      |
| --------------- | -------------------------------- |
| `/config_dump`  | Dump full running config         |
| `/clusters`     | Check upstream health, endpoints |
| `/stats`        | Runtime metrics                  |
| `/listeners`    | Active listeners                 |
| `/routes`       | View current routing tables      |
| `/server_info`  | Version, hot restart info        |
| `/ready`        | Health check                     |
| `/quitquitquit` | Graceful shutdown                |

---

## 📈 2. Observability (Metrics, Logs, Tracing)

---

### 🔢 Metrics

Envoy supports Prometheus out of the box:

```yaml
stats_sinks:
  - name: envoy.stat_sinks.prometheus
    typed_config:
      "@type": type.googleapis.com/envoy.config.metrics.v3.PrometheusSink
```

Then scrape `/stats` on the admin interface.

---

### 🧾 Access Logs

```yaml
access_log:
  - name: envoy.access_loggers.file
    typed_config:
      "@type": type.googleapis.com/envoy.extensions.access_loggers.file.v3.FileAccessLog
      path: /var/log/envoy/access.log
      log_format:
        text_format: "[%START_TIME%] %RESPONSE_CODE% %REQ(:METHOD)% %REQ(X-REQUEST-ID)%\n"
```

---

### 🔎 Distributed Tracing

Supports:

- Zipkin
- Jaeger
- OpenTelemetry
- Datadog

Example:

```yaml
http:
  name: envoy.tracers.zipkin
  typed_config:
    "@type": type.googleapis.com/envoy.config.trace.v3.ZipkinConfig
    collector_cluster: zipkin
    collector_endpoint: "/api/v2/spans"
    trace_id_128bit: true
```

---

## 🚀 3. Deployment Patterns

---

### 🐳 Standalone with Docker

```dockerfile
FROM envoyproxy/envoy:v1.30.1
COPY envoy.yaml /etc/envoy/envoy.yaml
CMD ["envoy", "-c", "/etc/envoy/envoy.yaml"]
```

---

### 🧱 Sidecar Deployment (Service Mesh)

- Deployed **next to your app**.
- Handles traffic transparently.
- Often injected via Istio, Consul, etc.

```bash
Service ↔ Envoy ↔ Envoy ↔ Service
```

---

### 📦 Envoy in Kubernetes

You can run Envoy in many ways:

- As a **DaemonSet** (entry gateway)
- As a **sidecar container**
- As part of a **Service Mesh** (e.g. Istio)

```yaml
containers:
  - name: envoy
    image: envoyproxy/envoy:v1.30.1
    args: ["-c", "/etc/envoy/envoy.yaml"]
    ports:
      - containerPort: 8080
```

---

## 🏎️ 4. Performance Tuning

---

### 💡 Important Flags

- `--concurrency` = number of worker threads (defaults to CPU cores)
- `--hot-restart-version` = see hot reload info
- `--disable-hot-restart` = for constrained environments

---

### ⚙️ Runtime Features (Hot Reloadable)

Some settings can be **live reloaded** via:

```bash
curl -X POST http://localhost:9901/runtime_modify?foo.bar=value
```

Use `/runtime` endpoint to inspect.

---

### 🧠 Resource Management

Configure limits:

```yaml
overload_manager:
  refresh_interval: 0.25s
  resource_monitors:
    - name: envoy.resource_monitors.fixed_heap
      typed_config:
        "@type": type.googleapis.com/envoy.extensions.resource_monitors.fixed_heap.v3.FixedHeapConfig
        max_heap_size_bytes: 2147483648
```

---

## ✅ 5. Best Practices

---

### 🔒 Secure by Default

- Use TLS/mTLS between everything.
- Restrict admin interface (`127.0.0.1` only).
- Rate-limit external entrypoints.

---

### 🔁 Always Enable Health Checks

Example:

```yaml
health_checks:
  - timeout: 1s
    interval: 5s
    unhealthy_threshold: 3
    healthy_threshold: 2
    grpc_health_check:
      service_name: "my.package.Service"
```

---

### 🔄 Circuit Breakers / Retries

```yaml
circuit_breakers:
  thresholds:
    - priority: DEFAULT
      max_connections: 1000
      max_pending_requests: 100
```

Add retries:

```yaml
retry_policy:
  retry_on: 5xx,connect-failure,refused-stream
  num_retries: 3
```

---

## 📌 6. Envoy Cheat Sheet (Quick Reference)

---

### 📦 Clusters

- Logical backend groups
- Required for all routing
- Load balancing happens here

---

### 🧭 Listeners

- Bound to a port (TCP or HTTP)
- Accept traffic
- Use filters to process

---

### 🧱 Filter Chains

- Series of filters on a listener
- Can use SNI or ALPN to choose chain

---

### 🛣️ Routes

- Match paths, headers, grpc method
- Map to a cluster
- Can have redirects, rewrites

---

### 🔍 Admin

- `/stats` → Prometheus metrics
- `/config_dump` → debug current config
- `/clusters` → health check and endpoints

---

### 🎯 gRPC-Specific

- Always include:

  ```yaml
  http2_protocol_options: {}
  ```

- For gRPC-Web, add:

  ```yaml
  - name: envoy.filters.http.grpc_web
  ```

---

## 📚 Want to Learn More?

Here’s where to go next:

- 📘 Official: [https://www.envoyproxy.io/docs](https://www.envoyproxy.io/docs)
- 🔬 Debugging: `/config_dump`, `/clusters`, `/routes`
- 📺 Istio/Consul use Envoy under the hood

---

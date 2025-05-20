OpenTelemetry (OTel) can indeed feel overwhelming. Here's a **structured cheat sheet**
covering its core components, workflows, and common tools:

---

### **1. OpenTelemetry Core Concepts**

| Term                    | Description                                                              |
| ----------------------- | ------------------------------------------------------------------------ |
| **Traces**              | Records of operations (e.g., HTTP requests, function calls) with timing. |
| **Spans**               | Individual units of work within a trace (e.g., "database query").        |
| **Metrics**             | Numerical measurements (e.g., CPU usage, request rate).                  |
| **Logs**                | Structured event data (now integrated into OTel).                        |
| **Context Propagation** | Passes trace context across services (e.g., via HTTP headers).           |

---

### **2. Key Components**

#### **a) OpenTelemetry Collector**

| Component      | Role                                                        |
| -------------- | ----------------------------------------------------------- |
| **Receiver**   | Ingests data (e.g., OTLP, Jaeger, Prometheus).              |
| **Processor**  | Modifies data (e.g., batch, filter, add attributes).        |
| **Exporter**   | Sends data to backends (e.g., Jaeger, Prometheus, Datadog). |
| **Extensions** | Non-data-pipeline features (e.g., health monitoring).       |

**Deployment Modes**:

- **Agent**: Runs alongside apps (sidecar/daemonset).
- **Gateway**: Centralized collector (for multi-cluster/tenant setups).

#### **b) SDKs & Auto-Instrumentation**

| Language   | SDK Package                          | Auto-Instrumentation Tools                            |
| ---------- | ------------------------------------ | ----------------------------------------------------- |
| Go         | `go.opentelemetry.io/otel`           | `otelgin`, `otelhttp`                                 |
| Python     | `opentelemetry-sdk`                  | `opentelemetry-instrumentation-flask`                 |
| JavaScript | `@opentelemetry/sdk-trace-node`      | `@opentelemetry/instrumentation-express`              |
| Java       | `io.opentelemetry:opentelemetry-sdk` | Java Agent (`-javaagent:opentelemetry-javaagent.jar`) |

#### **c) Protocols & Formats**

| Protocol   | Purpose                            | Example Backends         |
| ---------- | ---------------------------------- | ------------------------ |
| **OTLP**   | OTel's native protocol (gRPC/HTTP) | Collector, Jaeger, Tempo |
| **Jaeger** | Legacy protocol                    | Jaeger                   |
| **Zipkin** | Legacy protocol                    | Zipkin, Elasticsearch    |

---

### **3. Common Packages & Tools**

#### **a) Collector Components**

| Package                       | Purpose                                     |
| ----------------------------- | ------------------------------------------- |
| `otelcol-contrib`             | All-in-one collector (receivers/exporters). |
| `otelcol-receiver-jaeger`     | Jaeger protocol support.                    |
| `otelcol-exporter-prometheus` | Exports metrics to Prometheus.              |

#### **b) SDK Packages (Python Example)**

```python
from opentelemetry import trace
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.resources import Resource
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.trace.export import BatchSpanProcessor

# Setup
trace.set_tracer_provider(TracerProvider(
    resource=Resource.create({"service.name": "my-service"})
))
trace.get_tracer_provider().add_span_processor(
    BatchSpanProcessor(OTLPSpanExporter(endpoint="http://collector:4317"))
)
```

#### **c) Auto-Instrumentation**

```bash
# Python (Flask example)
opentelemetry-instrument --traces_exporter otlp flask run

# Node.js (Express example)
node -r @opentelemetry/auto-instrumentations-node/register server.js
```

---

### **4. Deployment Workflow**

1. **Instrument Apps**
   - Use SDKs or auto-instrumentation.
2. **Configure Collector** (`otel-collector-config.yaml`):
   ```yaml
   receivers:
     otlp:
       protocols:
         grpc:
   processors:
     batch:
   exporters:
     logging:
       logLevel: debug
     jaeger:
       endpoint: "jaeger:14250"
   service:
     pipelines:
       traces:
         receivers: [otlp]
         processors: [batch]
         exporters: [jaeger]
   ```
3. **Run Collector**:
   ```bash
   docker run -p 4317:4317 -v ./config.yaml:/etc/otel-config.yaml otel/opentelemetry-collector --config /etc/otel-config.yaml
   ```

---

### **5. Backend Integrations**

| Backend        | Supported Signals (T/M/L) | Exporter Package              |
| -------------- | ------------------------- | ----------------------------- |
| **Jaeger**     | Traces                    | `otelcol-exporter-jaeger`     |
| **Prometheus** | Metrics                   | `otelcol-exporter-prometheus` |
| **Loki**       | Logs                      | `otelcol-exporter-loki`       |
| **Datadog**    | T/M/L                     | `otelcol-exporter-datadog`    |

---

### **6. Debugging Tips**

| Command/Tool                    | Purpose                       |
| ------------------------------- | ----------------------------- |
| `otel-collector --help`         | Collector CLI options.        |
| `http://localhost:8888/metrics` | Collector Prometheus metrics. |
| `opentelemetry-diag`            | Diagnose SDK issues (Python). |

---

### **7. Anti-Patterns**

- ❌ **Skipping batching**: Always use `BatchSpanProcessor` in production.
- ❌ **Over-collecting**: Filter spans/metrics to reduce costs.
- ❌ **Hardcoding endpoints**: Use environment variables for exporter URLs.

---

### **8. Learning Resources**

- [OpenTelemetry Docs](https://opentelemetry.io/docs/)
- [OTel Collector GitHub](https://github.com/open-telemetry/opentelemetry-collector)
- [Awesome-OpenTelemetry](https://github.com/robertkowalski/awesome-opentelemetry)

---

This cheat sheet covers ~80% of daily OTel use cases. For deep dives, refer to the official
docs for your language/backend!

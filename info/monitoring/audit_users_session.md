Let’s design a **real-time dashboard** for tracking **power users, idle users, and session analytics** 
using **Grafana, Prometheus, and Redis**. This will give you actionable insights at a glance.

---

## **Architecture Overview**
1. **Data Sources**:
   - **Redis**: Live session activity (`last_active`, `api_call_count`).
   - **PostgreSQL**: Historical audit logs (synced from Redis).
   - **Prometheus**: Metrics (e.g., active sessions, API calls/sec).
2. **Tools**:
   - **Grafana**: Visualization (dashboards).
   - **Prometheus**: Time-series metrics storage.
   - **RedisTimeSeries** (optional): For high-frequency Redis metrics.

---

# **Step 1: Set Up Prometheus Metrics**
We’ll expose metrics from Go and Redis.

### **1. Instrument Go Service with Prometheus**
Add these to your Go service (e.g., `main.go`):
```go
import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
	activeSessions = prometheus.NewGauge(prometheus.GaugeOpts{
		Name: "sessions_active_total",
		Help: "Current active sessions",
	})

	apiCallsCount = prometheus.NewCounterVec(prometheus.CounterOpts{
		Name: "api_calls_total",
		Help: "Total API calls per user",
	}, []string{"user_id"})

	sessionDuration = prometheus.NewHistogram(prometheus.HistogramOpts{
		Name:    "session_duration_seconds",
		Help:    "Session duration distribution",
		Buckets: []float64{60, 300, 900, 3600, 86400}, // 1min, 5min, 15min, 1h, 1d
	})
)

func init() {
	prometheus.MustRegister(activeSessions, apiCallsCount, sessionDuration)
}

func main() {
	// ... (existing code)

	// Expose Prometheus metrics
	http.Handle("/metrics", promhttp.Handler())
	go http.ListenAndServe(":2112", nil)

	// Update metrics
	go updateSessionMetrics(rdb)
}

func updateSessionMetrics(rdb *redis.Client) {
	for {
		// Count active sessions
		keys, _ := rdb.Keys(ctx, "session:*").Result()
		activeSessions.Set(float64(len(keys)))

		time.Sleep(10 * time.Second) // Update every 10s
	}
}
```

### **2. Scrape Redis Metrics**
Use **Redis Exporter** to expose Redis stats to Prometheus:
```bash
docker run -d --name redis_exporter -p 9121:9121 oliver006/redis_exporter \
  --redis.addr=redis://localhost:6379
```
Add to `prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'redis'
    static_configs:
      - targets: ['localhost:9121']
  - job_name: 'go_app'
    static_configs:
      - targets: ['localhost:2112']
```

---

# **Step 2: Grafana Dashboard Setup**
### **1. Install Grafana**
```bash
docker run -d --name grafana -p 3000:3000 grafana/grafana
```
- Access `http://localhost:3000` (login: `admin/admin`).

### **2. Add Data Sources**
1. **Prometheus** (`http://prometheus:9090`).
2. **PostgreSQL** (for historical queries).

---

# **Step 3: Key Dashboard Panels**
Here’s what to track:

### **1. Real-Time Session Activity**
- **Active Sessions** (Gauge):
  ```
  sessions_active_total
  ```
- **Sessions by Status** (Pie chart):
  ```sql
  SELECT 
    CASE 
      WHEN last_active > NOW() - INTERVAL '5m' THEN 'active_now'
      WHEN last_active > NOW() - INTERVAL '1h' THEN 'recent'
      ELSE 'idle'
    END AS status,
    COUNT(*)
  FROM audit_sessions
  GROUP BY status
  ```

### **2. Power Users vs. Idle Users**
- **Top Power Users** (Table):
  ```sql
  SELECT user_id, COUNT(*) as api_calls 
  FROM audit_sessions 
  WHERE last_active > NOW() - INTERVAL '1 day'
  GROUP BY user_id 
  ORDER BY api_calls DESC 
  LIMIT 10
  ```
- **Idle Users** (Stat):
  ```sql
  SELECT COUNT(*) 
  FROM audit_sessions 
  WHERE last_active < NOW() - INTERVAL '7 days'
  ```

### **3. Security Alerts**
- **Failed Logins** (Graph):
  ```
  sum(rate(login_failures_total[5m])) by (user_id)
  ```
- **Suspicious IPs** (Logs panel):
  ```sql
  SELECT ip_address, user_agent, COUNT(*) as attempts
  FROM audit_sessions
  WHERE event_type = 'failed_login'
  GROUP BY ip_address, user_agent
  ORDER BY attempts DESC
  ```

### **4. Session Duration Distribution**
- **Histogram**:
  ```
  session_duration_seconds_bucket
  ```

---

# **Step 4: Example Dashboard JSON**
Import this into Grafana (or build manually):
```json
{
  "title": "User Session Analytics",
  "panels": [
    {
      "title": "Active Sessions",
      "type": "gauge",
      "targets": [{"expr": "sessions_active_total"}]
    },
    {
      "title": "Top Power Users (Last 24h)",
      "type": "table",
      "datasource": "PostgreSQL",
      "targets": [{"rawSql": "SELECT user_id, COUNT(*) as calls FROM audit_sessions WHERE last_active > NOW() - INTERVAL '1 day' GROUP BY user_id ORDER BY calls DESC LIMIT 5"}]
    },
    {
      "title": "Failed Logins",
      "type": "graph",
      "targets": [{"expr": "sum(rate(login_failures_total[5m])) by (user_id)"}]
    }
  ]
}
```

---

# **Optimizations**
1. **RedisTimeSeries**  
   - Track high-frequency metrics (e.g., API calls/sec) directly in Redis.
   ```bash
   TS.ADD api_calls:user123 * 1
   ```
2. **Grafana Alerts**  
   - Trigger Slack/email if:
     - `active_sessions` drops suddenly (outage?).
     - `failed_logins > 5/min` (brute force?).

3. **Caching**  
   - Pre-aggregate daily stats in PostgreSQL (e.g., with materialized views).

---

# **Final Result**
You’ll have a dashboard showing:
✅ **Live session counts**  
✅ **Power users ranking**  
✅ **Idle user detection**  
✅ **Security anomalies**  

---
---
---

Below is a **complete `docker-compose.yml`** to spin up the entire monitoring stack with:  
- **Grafana** (Dashboard)  
- **Prometheus** (Metrics)  
- **Redis** (Session store) + **Redis Exporter** (for Prometheus)  
- **PostgreSQL** (Audit logs)  
- **Go App** (Your JWT/Redis service)  

---

# **`docker-compose.yml`**
```yaml
version: '3.8'

services:
  # --- Redis (Session Storage) ---
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: ["redis-server", "--save", "60", "1"]  # Persist every 60s if ≥1 key changes

  # --- Redis Exporter (Prometheus metrics) ---
  redis_exporter:
    image: oliver006/redis_exporter:latest
    ports:
      - "9121:9121"
    environment:
      - REDIS_ADDR=redis://redis:6379
    depends_on:
      - redis

  # --- PostgreSQL (Audit Logs) ---
  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=audit_logs
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

  # --- Prometheus (Metrics Aggregation) ---
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    depends_on:
      - redis_exporter
      - go_app

  # --- Grafana (Dashboard) ---
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    depends_on:
      - prometheus
      - postgres

  # --- Go App (Your JWT/Redis Service) ---
  go_app:
    build: .
    ports:
      - "8080:8080"      # Your API port
      - "2112:2112"      # Prometheus metrics port
    environment:
      - REDIS_URL=redis://redis:6379
      - POSTGRES_URL=postgres://postgres:postgres@postgres:5432/audit_logs?sslmode=disable
    depends_on:
      - redis
      - postgres

volumes:
  redis_data:
  postgres_data:
  grafana_data:

```

---

# **`prometheus.yml`**
Place this in the same directory as `docker-compose.yml`:
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'redis'
    static_configs:
      - targets: ['redis_exporter:9121']

  - job_name: 'go_app'
    static_configs:
      - targets: ['go_app:2112']
```

---

# **`Dockerfile` (for Go App)**
```dockerfile
FROM golang:1.21-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o /jwt-redis-service

FROM alpine:latest
COPY --from=builder /jwt-redis-service /jwt-redis-service
EXPOSE 8080 2112
ENTRYPOINT ["/jwt-redis-service"]
```

---

# **How to Run**
1. Save all files in a directory:
   ```
   your-project/
   ├── docker-compose.yml
   ├── prometheus.yml
   ├── Dockerfile
   └── (your Go app code)
   ```
2. Start the stack:
   ```bash
   docker-compose up -d
   ```
3. Access services:
   - **Grafana**: `http://localhost:3000` (login: `admin/admin`)
     - Add **Prometheus** (`http://prometheus:9090`) and **PostgreSQL** as data sources.
   - **Prometheus**: `http://localhost:9090`
   - **Redis**: `redis://localhost:6379`
   - **PostgreSQL**: `postgres://postgres:postgres@localhost:5432/audit_logs`
   - **Go App**: `http://localhost:8080`

---

# **Preconfigured Grafana Dashboard**
1. **Import the dashboard JSON** (from my previous message) into Grafana.
2. Or use **Grafana’s Redis/Prometheus templates**:
   - Redis: [Dashboard ID 763](https://grafana.com/grafana/dashboards/763-redis-dashboard/)
   - PostgreSQL: [Dashboard ID 9628](https://grafana.com/grafana/dashboards/9628-postgresql-database/)

---

# **Key Features**
✅ **Full-stack monitoring** (Redis, Go, PostgreSQL)  
✅ **Auto-scraping** by Prometheus every 15s  
✅ **Persistent storage** (Redis/PostgreSQL data survives restarts)  
✅ **Scalable** (add more services as needed)  



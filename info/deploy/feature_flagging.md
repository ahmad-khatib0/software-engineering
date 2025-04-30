## What [splitio](github.com/splitio/go-client) used for?

The `github.com/splitio/go-client` package is a **Go SDK/Split Software client library** 
used for **feature flagging** and **progressive delivery**. Here's what it does and why 
you might use it:

---

### **Key Purpose**
1. **Feature Flag Management**  
   - Enables you to **toggle features on/off** in production without code deploys  
   - Example:  
     ```go
     if splitClient.IsFeatureEnabled("new_checkout_flow", userID) {
         // New feature code
     } else {
         // Old feature code
     }
     ```

2. **Targeted Rollouts**  
   - Release features to **specific user segments** (e.g., "10% of users", "VIP customers")  
   - Works with Split's **web dashboard** for rule configuration  

3. **A/B Testing**  
   - Measure impact of features before full rollout  

---

### **Common Use Cases**
- **Kill switches** for problematic features  
- **Canary releases** (gradual rollouts)  
- **Permission gating** (e.g., "admin-only features")  
- **Multi-variant testing** (A/B/n tests)  

---

### **Basic Usage Example**
```go
import (
	"github.com/splitio/go-client/v6/splitio/client"
	"github.com/splitio/go-client/v6/splitio/conf"
)

func main() {
	cfg := conf.Default()
	cfg.BlockUntilReady = 10 // Wait up to 10s for SDK initialization
	
	// Initialize client (API key from Split dashboard)
	splitFactory, _ := client.NewSplitFactory("YOUR_API_KEY", cfg)
	splitClient := splitFactory.Client()
	
	// Check a feature flag
	userID := "user123"
	if splitClient.IsFeatureEnabled("new_ui", userID) {
		fmt.Println("New UI enabled!")
	}
}
```

---

### **Key Components**
| Component | Purpose |
|-----------|---------|
| **SplitFactory** | Main entry point to create clients |
| **Client** | Evaluates feature flags locally |
| **Manager** | Inspects flag configurations |
| **Conf** | SDK configuration (timeouts, etc.) |

---

### **Alternatives**
- **Open-source**: `Unleash`, `Flagr`  
- **Cloud-based**: LaunchDarkly, Optimizely  

---

### **When to Use Split.io**
- If your team **already uses Split.io's platform**  
- For **enterprise-grade** feature flagging with advanced targeting  
- When you need **real-time flag updates** without restarts  

The SDK caches flag rules locally for fast evaluation, with background sync to 
the Split cloud service.

---
---
---

## Isn't something like: "VIP customers" is achiveable with a simple query to db so we can to check if a user is VIP or not ?

Absolutely right! A simple database query **can** determine if a user is VIP, but feature 
flagging tools like Split.io offer **additional benefits** that make them valuable even for
this seemingly simple case. Here's why you might prefer a feature flag system over a direct 
DB check:

---

### **1. Dynamic Control Without Code Changes**
- **DB Query**: Requires code changes/deploys to modify VIP logic (e.g., adding new VIP tiers).  
- **Feature Flag**:  
  ```go
  if isFeatureEnabled("vip_early_access", userID) { ... }
  ```
  - Toggle VIP rules **instantly** via Split.io dashboard (no redeploy needed).  
  - Example: Temporarily grant VIP status for a holiday promotion.

---

### **2. Gradual Rollouts & A/B Testing**
- **DB Query**: All-or-nothing (either VIP or not).  
- **Feature Flag**:  
  - Release to **5% of VIPs** first, monitor metrics, then ramp up.  
  - A/B test **different VIP perks** (e.g., `vip_tier1` vs `vip_tier2`).

---

### **3. Complex Targeting Beyond DB Fields**
- **DB Query**: Limited to data in your database.  
- **Feature Flag**: Combine attributes from **multiple sources**:  
  ```go
  // Target: VIPs FROM the EU WITH >100 purchases
  splitClient.IsFeatureEnabled("vip_eu_discount", userID, map[string]interface{}{
      "region":       user.Region,       // From DB
      "total_spend":  analytics.Spend,   // From data warehouse
      "device_type":  request.UserAgent, // From HTTP request
  })
  ```

---

### **4. Kill Switch for Emergencies**
- **DB Query**: If your VIP logic has a bug, you need a hotfix.  
- **Feature Flag**: Disable the feature **globally in 1 click** while you debug.

---

### **5. Audit Logs & Change History**
- **DB Query**: Hard to track who changed VIP status and when.  
- **Feature Flag**: Split.io logs **every flag change** (who, when, why).

---

### **When a DB Query Is Sufficient**
Use simple DB checks when:  
- VIP logic is **static** (rarely changes).  
- You don’t need gradual rollouts or A/B tests.  
- Your team prefers KISS (Keep It Simple, Stupid).  

Example:
```go
// Simple DB check (no Split.io needed)
isVIP := user.Tier == "vip" 
```

---

### **Key Tradeoffs**
| Approach | Pros | Cons |
|----------|------|------|
| **DB Query** | Simple, no external dependency | Harder to modify quickly |
| **Feature Flag** | Dynamic control, rich targeting | Adds complexity (SDK, network calls) |

---

### **Bottom Line**
For **basic** VIP checks, a DB query works. For **dynamic, measurable, and scalable** 
feature control, tools like Split.io add significant value. Choose based on your team’s needs!



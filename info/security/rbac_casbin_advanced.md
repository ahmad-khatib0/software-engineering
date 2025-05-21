### **1. Underscores (`_`) in Role Definitions**

#### **Meaning**:

- `g = _, _` → Standard RBAC (Role-Based Access Control):
  - `g, alice, admin` means "alice **is an** admin".
- `g2 = _, _, _` → RBAC with **domains/hierarchy**:
  - `g2, alice, admin, ecommerce` means "alice **is an** admin **in the** ecommerce domain".
- Underscores are **placeholders** showing the expected number of fields (but you replace them with actual values).

#### **Example**:

```csv
# Standard RBAC (g = _, _)
g, alice, admin

# Domain-specific RBAC (g2 = _, _, _)
g2, bob, manager, finance
```

---

### **2. Multiple Fields (Beyond `sub, dom, obj, act`)**

#### **No Need for Multiple Files**

- Casbin’s `model.conf` supports **unlimited custom fields** in one file.
- Example with 6 fields (user, domain, object, action, time, IP):

  ```ini
  [request_definition]
  r = sub, dom, obj, act, time, ip

  [policy_definition]
  p = sub, dom, obj, act, time, ip
  ```

#### **Policy Examples**:

```csv
p, admin, ecommerce, server, restart, 09:00, 192.168.1.100
p, user, support, ticket, view, *, 10.0.0.0/8
```

---

### **3. `p` vs. `g` in Policies**

| Prefix | Purpose                             | Example                              |
| ------ | ----------------------------------- | ------------------------------------ |
| `p`    | **Permission** rule                 | `p, admin, ecommerce, order, delete` |
| `g`    | **Role assignment** (RBAC)          | `g, alice, admin`                    |
| `g2`   | **Domain-specific role assignment** | `g2, bob, manager, finance`          |

---

### **4. Storing Multiple `r` Fields (Request Definitions)**

- You can define **as many fields as needed** in `request_definition`.
- Example with 3 custom fields:

  ```ini
  [request_definition]
  r = sub, dom, obj, act, time, ip, device
  ```

- Then use them in `matchers`:
  ```ini
  [matchers]
  m = r.device == "mobile" && r.time == "working_hours"
  ```

---

### **5. RBAC vs. ABAC**

| Model      | Description                              | Casbin Example                                    |
| ---------- | ---------------------------------------- | ------------------------------------------------- |
| **RBAC**   | Role-Based Access Control                | `g, alice, admin` → Alice is an admin.            |
| **ABAC**   | Attribute-Based Access Control           | `r.sub == "alice" && r.ip == "10.0.0.1"`          |
| **Hybrid** | Combine both (common in real-world apps) | `g(r.sub, p.sub) && r.department == p.department` |

#### **Why Choose Hybrid?**

- RBAC for **coarse-grained** roles (e.g., "admin").
- ABAC for **fine-grained** rules (e.g., "only from corporate IP").

---

### **6. Why `v0, v1, v2` in Database?**

- These are **generic column names** in Casbin’s DB schema.
- **Order matters**! They map to `policy_definition` fields:
  ```sql
  v0 = sub, v1 = dom, v2 = obj, v3 = act, v4 = time, ...
  ```
- Example DB row for `p, admin, ecommerce, order, delete, 17:00`:  
  | ptype | v0 | v1 | v2 | v3 | v4 |
  |-------|-------|-----------|-------|-------|-------|
  | p | admin | ecommerce | order | delete| 17:00 |

---

### **7. Dynamic Policy Storage (DB/Redis)**

#### **PostgreSQL Example**

```go
adapter, _ := gormadapter.NewAdapter("postgres", "user=postgres dbname=casbin")
e, _ := casbin.NewEnforcer("model.conf", adapter)

// Add a policy dynamically
e.AddPolicy("admin", "ecommerce", "order", "approve", "09:00", "192.168.1.1")
```

#### **Redis Example**

```go
adapter, _ := redisadapter.NewAdapter("tcp", "localhost:6379")
e, _ := casbin.NewEnforcer("model.conf", adapter)
```

---

### **8. Advanced `model.conf` Example**

#### **Hybrid RBAC + ABAC + Time/IP**

```ini
[request_definition]
r = sub, dom, obj, act, time, ip

[policy_definition]
p = sub, dom, obj, act, time, ip

[role_definition]
g = _, _
g2 = _, _, _

[matchers]
m = (g(r.sub, p.sub) || g2(r.sub, p.sub, r.dom)) &&  # RBAC
    r.obj == p.obj &&
    r.act == p.act &&
    r.time.After("09:00") &&  # ABAC: Time
    r.ip.Matches("10.0.0.0/8")  # ABAC: IP range
```

#### **Policy Examples**

```csv
# RBAC
g, alice, admin
g2, bob, manager, finance

# ABAC
p, admin, *, server, restart, 09:00-17:00, 192.168.1.*
p, user, support, ticket, view, *, 10.0.0.0/8
```

---

### **9. Key Takeaways**

1. **Underscores** are placeholders (replace with values).
2. **One `model.conf`** handles all fields (no need for multiple files).
3. **`p`** = permissions, **`g`** = role assignments.
4. **Order matters** in DB columns (`v0` = first field in `policy_definition`).
5. **RBAC** = roles, **ABAC** = attributes, **Hybrid** = best of both.

---

### **10. Further Learning**

- [Casbin Documentation](https://casbin.org/docs/get-started)
- [Advanced Model Examples](https://casbin.org/docs/syntax-for-models)

This flexibility lets you model **any access control scenario**, from simple RBAC to
complex spacetime-aware ABAC.

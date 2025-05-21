Clarify how to define and use **multiple policy definitions (`p`, `p2`, `p3`, etc.)** in
Casbin's model file, along with the requirements and conventions.

---

### **1. Understanding Policy Definitions (`p`, `p2`, `p3`)**

Casbin allows **multiple policy definitions** (e.g., `p`, `p2`, `p3`) to model complex
scenarios where different types of policies coexist. Each policy definition can have
its own format and purpose.

#### **Key Points**:

- **`p` is the default policy definition** (used for standard permissions).
- **`p2`, `p3`, etc.** are additional policy types (e.g., for exceptions, overrides, or different rule sets).
- Each policy definition **must align with its usage in `matchers`**.

---

### **2. How to Define Multiple Policy Definitions**

#### **Example: `model.conf` with `p`, `p2`, `p3`**

```ini
[request_definition]
r = sub, dom, obj, act  # Standard request format

[policy_definition]
p = sub, dom, obj, act   # Standard permissions (e.g., "admin can delete orders")
p2 = sub, obj, act       # Simplified permissions (no domain)
p3 = sub, act            # Global permissions (no domain/object)

[role_definition]
g = _, _, _              # Standard role assignments (user, role, domain)

[policy_effect]
e = some(where (p.eft == allow)) || some(where (p2.eft == allow))  # Allow if either p or p2 permits

[matchers]
m = (g(r.sub, p.sub, r.dom) && r.dom == p.dom && r.obj == p.obj && r.act == p.act) ||  # p: Domain-specific
    (r.obj == p2.obj && r.act == p2.act) ||                                           # p2: Domain-agnostic
    (r.act == p3.act)                                                                  # p3: Global actions
```

---

### **3. Policy File (`policy.csv`) Examples**

#### **Standard Policy (`p`)**

```csv
# Format: p, sub, dom, obj, act
p, admin, ecommerce, order, delete
p, user, support, ticket, view
```

#### **Simplified Policy (`p2`)**

```csv
# Format: p2, sub, obj, act (no domain)
p2, auditor, log, read
p2, developer, config, edit
```

#### **Global Policy (`p3`)**

```csv
# Format: p3, sub, act (no domain/object)
p3, everyone, ping
p3, guest, browse
```

---

### **4. How Casbin Evaluates Multiple Policies**

For a request like `Enforce("alice", "ecommerce", "order", "delete")`:

1. Checks `p` policies first (domain-specific).
   - If `alice` is an `admin` in `ecommerce`, allow.
2. Falls back to `p2` if no `p` match.
   - If `alice` is an `auditor` (domain-agnostic), allow `log` access.
3. Finally checks `p3` (global).
   - If `alice` is `everyone`, allow `ping`.

---

### **5. Requirements and Conventions**

1. **Consistent Field Counts**:

   - If `p = sub, dom, obj, act`, every `p` policy must have 4 fields.
   - `p2` and `p3` can have fewer fields (e.g., `p2 = sub, obj, act`).

2. **Matcher Logic**:

   - The `matchers` section must reference all policy types (`p`, `p2`, `p3`).
   - Example:
     ```ini
     m = (p_match) || (p2_match) || (p3_match)
     ```

3. **Policy Effects**:

   - `policy_effect` must account for all policy types (e.g., `some(where (p.eft == allow)) || some(where (p2.eft == allow))`).

4. **Storage**:
   - In databases, policies are stored with a `ptype` column to distinguish `p`, `p2`, `p3`:  
     | ptype | v0 | v1 | v2 | v3 |
     |-------|---------|-----------|-------|-------|
     | p | admin | ecommerce | order | delete|
     | p2 | auditor | log | read | |
     | p3 | everyone| ping | | |

---

### **6. When to Use Multiple Policy Definitions**

| Policy Type | Use Case                                 | Example                              |
| ----------- | ---------------------------------------- | ------------------------------------ |
| `p`         | Domain-specific permissions (RBAC/ABAC)  | `p, admin, ecommerce, order, delete` |
| `p2`        | Domain-agnostic rules (e.g., logging)    | `p2, auditor, log, read`             |
| `p3`        | Global permissions (e.g., health checks) | `p3, everyone, ping`                 |

---

### **7. Full Workflow Example**

#### **Step 1: Define `model.conf`**

```ini
[request_definition]
r = sub, dom, obj, act

[policy_definition]
p = sub, dom, obj, act
p2 = sub, obj, act
p3 = sub, act

[role_definition]
g = _, _, _

[policy_effect]
e = some(where (p.eft == allow)) || some(where (p2.eft == allow))

[matchers]
m = (g(r.sub, p.sub, r.dom) && r.dom == p.dom && r.obj == p.obj && r.act == p.act) ||
    (r.obj == p2.obj && r.act == p2.act) ||
    (r.act == p3.act)
```

#### **Step 2: Populate `policy.csv`**

```csv
# p: Domain-specific
p, admin, ecommerce, order, delete

# p2: Domain-agnostic
p2, auditor, log, read

# p3: Global
p3, everyone, ping
```

#### **Step 3: Enforce Requests**

```go
e.Enforce("admin", "ecommerce", "order", "delete")   // Allowed by `p`
e.Enforce("auditor", "any", "log", "read")          // Allowed by `p2`
e.Enforce("guest", "any", "any", "ping")            // Allowed by `p3`
```

---

### **8. Key Takeaways**

1. **Multiple `p` Types**: Use `p`, `p2`, `p3` to model different rule sets.
2. **Matchers**: Combine conditions for all policy types.
3. **Storage**: DBs use `ptype` to distinguish policies.
4. **Flexibility**: Mix RBAC, ABAC, and global rules in one model.

This approach lets you handle **complex real-world scenarios**
(e.g., domain-specific roles + global exceptions) without duplicating logic.

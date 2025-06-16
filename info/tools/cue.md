# **CUE Language Cheat Sheet: Constants, Enums, and Configuration**

CUE (Configure, Unify, Execute) is a powerful configuration language from Google that
combines type checking, templating, and validation. It's ideal for:

- **Shared constants/enums** across languages
- **Configuration validation**
- **Code generation**
- **Schema definitions**

---

## **1. Basic Concepts**

### **Install CUE**

```bash
brew install cue-lang/tap/cue  # macOS
go install cuelang.org/go/cmd/cue@latest  # Go
```

---

## **2. Defining Constants & Enums**

### **File: `shared.cue`**

```cue
// Simple constants
#MAX_RETRIES: 3
#TIMEOUT:    "5s"

// Typed enums
#Environment: "dev" | "staging" | "production"

// Enum with values
#ErrorCode: {
    NOT_FOUND:    404
    UNAUTHORIZED: 401
    SERVER_ERROR: 500
}

// Structured config
#AppConfig: {
    retries:    #MAX_RETRIES
    timeout:    #TIMEOUT
    env:        #Environment
    features: {
        newUI:          bool | *true   // Optional (default: true)
        experimental:    bool | *false  // Optional (default: false)
    }
}
```

---

## **3. Generating Output**

### **(A) Export JSON (for other tools)**

```bash
cue export shared.cue --out json
```

**Output:**

```json
{
  "MAX_RETRIES": 3,
  "TIMEOUT": "5s",
  "Environment": "dev",
  "ErrorCode": {
    "NOT_FOUND": 404,
    "UNAUTHORIZED": 401,
    "SERVER_ERROR": 500
  },
  "AppConfig": {
    "retries": 3,
    "timeout": "5s",
    "env": "dev",
    "features": {
      "newUI": true,
      "experimental": false
    }
  }
}
```

### **(B) Generate Go Code**

```bash
cue gen -t "lang=go" shared.cue
```

**Output (`shared_gen.go`):**

```go
package config

const (
    MaxRetries    = 3
    Timeout       = "5s"
)

type Environment string

const (
    EnvDev       Environment = "dev"
    EnvStaging   Environment = "staging"
    EnvProd      Environment = "production"
)

type ErrorCode int

const (
    NotFound     ErrorCode = 404
    Unauthorized ErrorCode = 401
    ServerError  ErrorCode = 500
)

type AppConfig struct {
    Retries    int          `json:"retries"`
    Timeout    string       `json:"timeout"`
    Env        Environment  `json:"env"`
    Features   struct {
        NewUI         bool `json:"newUI"`
        Experimental bool `json:"experimental"`
    } `json:"features"`
}
```

### **(C) Generate TypeScript**

```bash
cue gen -t "lang=typescript" shared.cue
```

**Output (`shared_gen.ts`):**

```typescript
export const MAX_RETRIES = 3;
export const TIMEOUT = "5s";

export type Environment = "dev" | "staging" | "production";

export enum ErrorCode {
  NOT_FOUND = 404,
  UNAUTHORIZED = 401,
  SERVER_ERROR = 500,
}

export interface AppConfig {
  retries: number;
  timeout: string;
  env: Environment;
  features: {
    newUI: boolean;
    experimental: boolean;
  };
}
```

---

## **4. Advanced Features**

### **(A) Defaults & Validation**

```cue
#ServiceConfig: {
    port: int & >=3000 & <=9000  // Must be between 3000-9000
    logLevel: "debug" | "info" | "warn" | *"info"  // Default: "info"
}
```

### **(B) Type Composition**

```cue
#Database: {
    host:     string
    port:     int
    username: string
    password: string
}

#ProdDB: #Database & {
    host:     "db.prod.example.com"
    port:     5432
}
```

### **(C) Generate for Multiple Languages**

```bash
cue gen -t "lang=go" -t "lang=typescript" shared.cue
```

---

## **5. Key Benefits**

✅ **Single source of truth** for constants/enums  
✅ **Type-safe validation** (catch errors early)  
✅ **Generate code** for Go, TypeScript, Python, Java, etc.  
✅ **Works with Kubernetes, Terraform, JSON Schema**

---

## **When to Use CUE vs. Quicktype/Protobuf?**

| **Tool**      | **Best For**                                  |
| ------------- | --------------------------------------------- |
| **CUE**       | Configs, validation, multi-language constants |
| **Quicktype** | Generating types from JSON/JSON Schema        |
| **Protobuf**  | gRPC contracts, binary serialization          |

---

### **Example Workflow**

1. Define everything in `shared.cue`
2. Generate language-specific files (`cue gen`)
3. Commit generated code (or automate in CI)

This keeps your constants **consistent** across services! 🚀

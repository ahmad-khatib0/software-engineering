`quicktype` is a powerful tool that can generate types, constants, and serialization
code from JSON/JSON Schema/YAML inputs for multiple languages. Here's a comprehensive guide:

## What Quicktype Can Do

1. **Generate code for 20+ languages** (TypeScript, Go, C#, Java, Swift, etc.)
2. **Convert between** JSON ↔ JSON Schema ↔ TypeScript ↔ etc.
3. **Produce**:
   - Type definitions
   - Constants/enums
   - Serialization/deserialization code
   - Runtime type validators

## Installation

```bash
npm install -g quicktype
```

## Example: Shared Constants/Enums/Vars

### 1. Create Input File (`shared.json`)

```json
{
  "definitions": {
    "Environment": {
      "enum": ["DEV", "STAGING", "PRODUCTION"],
      "description": "Runtime environment"
    },
    "ErrorCode": {
      "enum": ["NOT_FOUND", "UNAUTHORIZED", "INTERNAL_ERROR"],
      "values": {
        "NOT_FOUND": 404,
        "UNAUTHORIZED": 401,
        "INTERNAL_ERROR": 500
      }
    },
    "Constants": {
      "properties": {
        "MAX_RETRIES": {
          "type": "integer",
          "value": 3
        },
        "TIMEOUT_MS": {
          "type": "integer",
          "value": 5000
        },
        "FEATURE_FLAGS": {
          "type": "object",
          "properties": {
            "NEW_UI": { "type": "boolean", "value": true },
            "EXPERIMENTAL_API": { "type": "boolean", "value": false }
          }
        }
      }
    }
  }
}
```

### 2. Generate Output for Different Languages

#### TypeScript:

```bash
quicktype -s schema shared.json -o shared.ts --just-types
```

Output (`shared.ts`):

```typescript
export type Environment = "DEV" | "STAGING" | "PRODUCTION";

export enum ErrorCode {
  NotFound = "NOT_FOUND",
  Unauthorized = "UNAUTHORIZED",
  InternalError = "INTERNAL_ERROR",
}

export const ErrorCodeValue: Record<ErrorCode, number> = {
  [ErrorCode.NotFound]: 404,
  [ErrorCode.Unauthorized]: 401,
  [ErrorCode.InternalError]: 500,
};

export interface Constants {
  MAX_RETRIES: number;
  TIMEOUT_MS: number;
  FEATURE_FLAGS: {
    NEW_UI: boolean;
    EXPERIMENTAL_API: boolean;
  };
}

export const CONSTANTS: Constants = {
  MAX_RETRIES: 3,
  TIMEOUT_MS: 5000,
  FEATURE_FLAGS: {
    NEW_UI: true,
    EXPERIMENTAL_API: false,
  },
};
```

#### Go:

```bash
quicktype -s schema shared.json -o shared.go --package shared --just-types
```

Output (`shared.go`):

```go
package shared

type Environment string

const (
	EnvironmentDEV      Environment = "DEV"
	EnvironmentSTAGING  Environment = "STAGING"
	EnvironmentPRODUCTION Environment = "PRODUCTION"
)

type ErrorCode string

const (
	ErrorCodeNOT_FOUND       ErrorCode = "NOT_FOUND"
	ErrorCodeUNAUTHORIZED    ErrorCode = "UNAUTHORIZED"
	ErrorCodeINTERNAL_ERROR ErrorCode = "INTERNAL_ERROR"
)

var ErrorCodeValue = map[ErrorCode]int{
	ErrorCodeNOT_FOUND:       404,
	ErrorCodeUNAUTHORIZED:    401,
	ErrorCodeINTERNAL_ERROR: 500,
}

type Constants struct {
	MAX_RETRIES int64 `json:"MAX_RETRIES"`
	TIMEOUT_MS  int64 `json:"TIMEOUT_MS"`
	FEATURE_FLAGS struct {
		NEW_UI          bool `json:"NEW_UI"`
		EXPERIMENTAL_API bool `json:"EXPERIMENTAL_API"`
	} `json:"FEATURE_FLAGS"`
}

var CONSTANTS = Constants{
	MAX_RETRIES: 3,
	TIMEOUT_MS:  5000,
	FEATURE_FLAGS: struct {
		NEW_UI          bool `json:"NEW_UI"`
		EXPERIMENTAL_API bool `json:"EXPERIMENTAL_API"`
	}{
		NEW_UI:          true,
		EXPERIMENTAL_API: false,
	},
}
```

## Advanced Features

### 1. Custom Naming/Rules

Add `x-` properties to control generation:

```json
{
  "definitions": {
    "ErrorCode": {
      "enum": ["NOT_FOUND", "UNAUTHORIZED"],
      "x-golang-enum-name": "HTTPError",
      "x-typescript-interface": "IErrorCode"
    }
  }
}
```

### 2. Generate Runtime Validators

```bash
quicktype -s schema shared.json -o validator.ts --runtime-typecheck
```

### 3. Convert Between Formats

Convert TypeScript → JSON Schema:

```bash
quicktype -l typescript -o shared.json shared.ts
```

## Integration Tips

1. **Make it part of your build process**:

   ```json
   // package.json
   "scripts": {
     "generate:constants": "quicktype -s schema shared.json -o src/shared.ts"
   }
   ```

2. **Combine with protobuf**:

   - Use protobuf for service contracts
   - Use quicktype for shared constants/config

3. **Version your schema**:
   Keep `shared.json` in source control and regenerate when changed.

This approach gives you a language-neutral way to maintain shared constants
with proper type safety across your entire stack!

---

---

---

Here's **Quicktype Cheat Sheet** based on JSON Schema patterns:

---

### **1. Basic Structure**

```json
{
  "definitions": {
    "[TYPE_NAME]": {
      // Configuration here
    }
  }
}
```

---

### **2. Enum Generation**

#### Schema:

```json
{
  "enum": ["VAL1", "VAL2"],
  "description": "Optional docs",
  "x-[LANG]-[OPTION]": "value" // Language-specific tweaks
}
```

#### Generates:

- **TypeScript**: `type Name = "VAL1" | "VAL2"`
- **Go**: `type Name string` + `const (NameVAL1 Name = "VAL1")`
- **Java**: `enum Name { VAL1, VAL2 }`

---

### **3. Constants/Object Generation**

#### Schema:

```json
{
  "properties": {
    "CONST_NAME": {
      "type": "[TYPE]", // string/number/boolean
      "value": "actual_value",
      "description": "Docs"
    }
  }
}
```

#### Generates:

- **TypeScript**:
  ```ts
  interface Name {
    CONST_NAME: [TYPE];
  }
  const NAME: Name = { CONST_NAME: actual_value };
  ```
- **Go**:
  ```go
  type Name struct {
    CONST_NAME [Type] `json:"CONST_NAME"`
  }
  var NAME = Name{ CONST_NAME: actual_value }
  ```

---

### **4. Value Mappings (Enum + Values)**

#### Schema:

```json
{
  "enum": ["KEY1", "KEY2"],
  "values": {
    "KEY1": "value1",
    "KEY2": "value2"
  }
}
```

#### Generates:

- **TypeScript**:
  ```ts
  enum Name { KEY1 = "KEY1", KEY2 = "KEY2" }
  const NameValue = { [Name.KEY1]: "value1", ... };
  ```
- **Go**:
  ```go
  type Name string
  var NameValue = map[Name]interface{}{ "KEY1": "value1", ... }
  ```

---

### **5. Language-Specific Overrides**

| Directive                  | Effect                          |
| -------------------------- | ------------------------------- |
| `"x-typescript-interface"` | Renames interface in TS         |
| `"x-golang-type"`          | Changes Go type (e.g., `int32`) |
| `"x-java-package"`         | Sets Java package               |
| `"x-[LANG]-[OPTION]"`      | Language-specific customization |

Example:

```json
{
  "x-golang-type": "int",
  "x-typescript-name": "CustomName"
}
```

---

### **6. CLI Cheat Sheet**

| Command                                       | Action                  |
| --------------------------------------------- | ----------------------- |
| `quicktype -s schema input.json -o output.ts` | TS from JSON Schema     |
| `quicktype -l go input.json`                  | Go from JSON (stdout)   |
| `--just-types`                                | Skip serialization code |
| `--runtime-typecheck`                         | Adds validation helpers |
| `--package shared`                            | Sets Go package name    |

---

### **Pattern Recap**

1. **`Something.enum`** → Creates language-native enums
2. **`Something.properties`** → Generates:
   - Interfaces/structs (type definitions)
   - Constants objects (instantiated values)
3. **`Something.values`** → Creates enum-to-value mappings

---

### **Pro Tip**

For shared constants across gRPC and frontend:

```json
{
  "definitions": {
    "gRPCErrorCode": {
      "enum": ["NOT_FOUND", "UNAUTHORIZED"],
      "x-protobuf-enum-name": "ErrorCode",
      "x-typescript-name": "GrpcError"
    }
  }
}
```

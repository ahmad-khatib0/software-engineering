### **What is `hashicorp/go-plugin`?**
`hashicorp/go-plugin` is a **Go library** for building **plugin-based systems** where 
  plugins run as **separate processes** but communicate with the main application via 
  **RPC (Remote Procedure Calls)** over gRPC or net/rpc. It’s designed for:  
- **Isolation**: Plugins crash without affecting the main app.  
- **Multi-language support**: Plugins can be written in any language (Go, Python, etc.).  
- **Versioning/Safety**: Main app and plugins can evolve independently.  

Developed by HashiCorp (creators of Terraform, Vault), it’s widely used in tools like 
**Terraform providers**, **Vault secrets engines**, and **Nomad drivers**.

---

### **Key Use Cases**
1. **Extensible Architectures**  
   - Add features without recompiling the main binary (e.g., Terraform providers).  
2. **Security Isolation**  
   - Run untrusted code (e.g., user-supplied scripts) in a sandboxed process.  
3. **Multi-Language Plugins**  
   - Mix plugins written in Go, Python, or other languages.  

---

### **Example: Building a Calculator Plugin**
#### **1. Define the Plugin Interface (shared)**
```go
// calculator/calculator.go
package calculator

type Calculator interface {
  Add(a, b int) (int, error)
}
```

#### **2. Implement the Plugin (runs in a separate process)**
```go
// plugin/main.go
package main

import (
  "github.com/hashicorp/go-plugin"
  "calculator"
)

type CalculatorPlugin struct{}

func (c *CalculatorPlugin) Add(a, b int) (int, error) {
  return a + b, nil
}

func main() {
  plugin.Serve(&plugin.ServeConfig{
    HandshakeConfig: plugin.HandshakeConfig{
      ProtocolVersion:  1,
      MagicCookieKey:   "CALC_PLUGIN",
      MagicCookieValue: "hello",
    },
    Plugins: map[string]plugin.Plugin{
      "calculator": &calculator.Plugin{Impl: &CalculatorPlugin{}},
    },
  })
}
```

#### **3. Main App (consumes the plugin)**
```go
// main.go
package main

import (
  "fmt"
  "github.com/hashicorp/go-plugin"
  "calculator"
)

func main() {
  // 1. Launch the plugin (starts as a subprocess)
  client := plugin.NewClient(&plugin.ClientConfig{
    HandshakeConfig: plugin.HandshakeConfig{
      ProtocolVersion:  1,
      MagicCookieKey:   "CALC_PLUGIN",
      MagicCookieValue: "hello",
    },
    Plugins:          map[string]plugin.Plugin{"calculator": &calculator.Plugin{}},
    Cmd:              exec.Command("./plugin/calculator-plugin"), // Path to plugin binary
  })
  defer client.Kill()

  // 2. Connect via RPC
  rpcClient, err := client.Client()
  if err != nil {
    panic(err)
  }

  // 3. Request the calculator implementation
  raw, err := rpcClient.Dispense("calculator")
  if err != nil {
    panic(err)
  }

  calc := raw.(calculator.Calculator)
  sum, _ := calc.Add(5, 3)
  fmt.Println("5 + 3 =", sum) // Output: 5 + 3 = 8
}
```

#### **4. Build and Run**
```bash
# Build the plugin
go build -o plugin/calculator-plugin plugin/main.go

# Run the main app
go run main.go
```

---

### **How It Works**
1. **Plugin Process**: Runs independently, exposes `Calculator` via RPC.  
2. **Main Process**: Communicates with the plugin over gRPC (default) or net/rpc.  
3. **Handshake**: Ensures compatibility via `MagicCookieKey/Value`.  

---

### **Real-World Examples**
1. **Terraform Providers**  
   - Each provider (AWS, GCP) is a plugin loaded dynamically.  
2. **Vault Secrets Engines**  
   - Plugins add support for new secret backends (e.g., Kubernetes, AWS IAM).  
3. **Custom SaaS Extensions**  
   - Allow customers to write plugins in their preferred language.  

---

### **Alternatives**
| Tool                | Use Case                          |
|---------------------|-----------------------------------|
| **Go Plugins**      | Native Go plugins (no isolation). |
| **gRPC**            | Custom plugin systems.            |
| **Wasm**            | Sandboxed plugins via WebAssembly.|

---

### **When to Use `go-plugin`**
✅ Need **process isolation** (security/stability).  
✅ Support **multi-language** plugins.  
✅ Building **modular systems** (e.g., Terraform).  

🚫 Overkill for simple Go plugins (use `plugin` package).  


Here's a comprehensive **cheat sheet for `.proto` files**, covering syntax, data types,
options, tricks, and common patterns used in **Protocol Buffers (Protobuf)** — including
both standard and gRPC usage.

---

# 📘 Protobuf Cheat Sheet (`.proto` files)

---

## 🧱 File Structure

```proto
syntax = "proto3";

package user.v1;

option go_package = "github.com/yourorg/yourapp/gen/user/v1;userv1";

import "google/protobuf/timestamp.proto";

service UserService {
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
}

message GetUserRequest {
  int64 user_id = 1;
}

message GetUserResponse {
  User user = 1;
}

message User {
  int64 id = 1;
  string name = 2;
  google.protobuf.Timestamp created_at = 3;
}
```

---

## 🔑 Keywords

| Keyword             | Purpose                                                |
| ------------------- | ------------------------------------------------------ |
| `syntax`            | Required. Use `"proto3"` (proto2 is legacy).           |
| `package`           | Declares the namespace in generated code.              |
| `option go_package` | Required for Go codegen, includes full Go module path. |
| `import`            | Include other proto files or Google well-known types.  |
| `message`           | Defines a data structure (like a class/struct).        |
| `enum`              | Declares an enumeration.                               |
| `service`           | Declares an RPC service (only used with gRPC).         |
| `rpc`               | Defines an RPC method inside a `service`.              |
| `repeated`          | List/array of a field.                                 |
| `oneof`             | Mutually exclusive fields.                             |
| `map<key, val>`     | Map/dictionary fields.                                 |
| `reserved`          | Reserve field numbers or names for compatibility.      |

---

## 🧮 Scalar Types

| Protobuf           | Go Type               | Notes                       |
| ------------------ | --------------------- | --------------------------- |
| `int32`            | `int32`               | Efficient for small numbers |
| `int64`            | `int64`               | Use for timestamps or IDs   |
| `uint32`/`uint64`  | Unsigned ints         | Avoid negative numbers      |
| `bool`             | `bool`                | Boolean                     |
| `string`           | `string`              | UTF-8                       |
| `bytes`            | `[]byte`              | Raw binary                  |
| `float` / `double` | `float32` / `float64` | Precision tradeoff          |

> 💡 Use `int64` or `google.protobuf.Timestamp` for time fields.

---

## 🧭 Field Rules

```proto
message Example {
  string name = 1;               // Regular field
  repeated string tags = 2;      // List of strings
  map<string, int32> views = 3;  // Map from string to int
  oneof status {
    string error = 4;
    int32 code = 5;
  }
}
```

- **Field numbers** must be between `1` and `2^29 - 1` (avoid 19000–19999, reserved).
- Field numbers are critical for backwards compatibility.

---

## 📦 `google.protobuf.*` (Well-Known Types)

| Type                        | Use Case                            |
| --------------------------- | ----------------------------------- |
| `google.protobuf.Timestamp` | Date/time                           |
| `google.protobuf.Duration`  | Time durations                      |
| `google.protobuf.Struct`    | Dynamic/untyped JSON-like structure |
| `google.protobuf.Any`       | Pack arbitrary messages             |
| `google.protobuf.Empty`     | Void / empty message                |

---

## 🛠 Tips & Tricks

### ✅ Optional Fields (since proto3.15+)

```proto
optional string nickname = 1;
```

Enable with:

```proto
syntax = "proto3";
```

Compiler flag: `--experimental_allow_proto3_optional`

### 💡 Default Values

| Type    | Default          |
| ------- | ---------------- |
| string  | `""`             |
| bool    | `false`          |
| number  | `0`              |
| message | `null` (not set) |

### 🚫 Reserved Fields

Prevent breaking compatibility:

```proto
message User {
  reserved 3, 4;
  reserved "old_name", "old_email";
}
```

---

## 🔌 gRPC Service Definitions

```proto
service AuthService {
  rpc Login(LoginRequest) returns (LoginResponse);
  rpc Logout(google.protobuf.Empty) returns (google.protobuf.Empty);
}
```

### gRPC Streaming

```proto
rpc Upload(stream UploadRequest) returns (UploadResponse);
rpc Chat(stream ChatMessage) returns (stream ChatMessage);
```

---

## 🧬 Versioning Best Practices

- Only **add fields** (don’t remove or reuse old numbers).
- **Reserve** removed fields.
- Use `oneof` for mutually exclusive logic.
- Add `repeated` fields instead of modifying field type.
- Start enum with `ENUM_UNSPECIFIED = 0;` for safety.

---

## 📁 File Layout Tips

- Group messages per domain (`user.proto`, `order.proto`)
- Use subfolders per version: `proto/user/v1/user.proto`
- Use clear, global `go_package` values

```proto
option go_package = "github.com/acme/api/gen/user/v1;userv1";
```

---

## 🧰 Common Plugins

| Plugin                    | Output                 |
| ------------------------- | ---------------------- |
| `protoc-gen-go`           | Go message types       |
| `protoc-gen-go-grpc`      | Go gRPC client/server  |
| `protoc-gen-grpc-gateway` | REST proxy from gRPC   |
| `protoc-gen-openapiv2`    | Swagger/OpenAPI docs   |
| `buf` (tool)              | Wrapper for lint/build |

---

## 🔍 Resources

- [protobuf.dev](https://protobuf.dev/)
- [buf.build](https://buf.build/)
- [grpc.io](https://grpc.io/)
- [Awesome gRPC](https://github.com/grpc-ecosystem/awesome-grpc)

---

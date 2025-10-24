In **proto3**, each field and message can have **a few standard field options** beyond `json_name`.
Here’s a concise reference grouped by what you can actually use and what they mean

---

### Common **field-level options**

| Option           | Applies to                     | Purpose / Example                                                                                                                                  |
| ---------------- | ------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`json_name`**  | field                          | Customizes JSON key in generated JSON (e.g. `task_uid` → `"taskUid"`).                                                                             |
| **`deprecated`** | field                          | Marks field as deprecated. Example:<br>`int32 old_field = 2 [deprecated = true];` — compilers show warnings.                                       |
| **`packed`**     | repeated numeric fields        | Store repeated primitives compactly in wire format. Proto3 *auto-packs* numeric types, so usually unnecessary.                                     |
| **`ctype`**      | string/bytes fields (C++ only) | Controls C++ string representation (`STRING`, `CORD`, `STRING_PIECE`).                                                                             |
| **`jstype`**     | integer fields                 | Affects generated JavaScript type (`JS_NORMAL`, `JS_STRING`, `JS_NUMBER`). Use when JS may lose precision:<br>`int64 id = 1 [jstype = JS_STRING];` |
| **`default`**    | proto2 only ❌                  | Not supported in proto3. Default values are implicit.                                                                                              |

---

### **Message / file options**

| Option                            | Applies to         | Example / Effect                                   |
| --------------------------------- | ------------------ | -------------------------------------------------- |
| **`option go_package`**           | file               | Controls Go import path + package name.            |
| **`option java_package`**         | file               | Sets Java package for generated code.              |
| **`option java_outer_classname`** | file               | Sets outer class name in generated Java file.      |
| **`option cc_enable_arenas`**     | file               | Enables arena allocation for C++.                  |
| **`option objc_class_prefix`**    | file               | Prefix for Objective-C class names.                |
| **`option swift_prefix`**         | file               | Prefix for Swift classes.                          |
| **`option csharp_namespace`**     | file               | Sets C# namespace.                                 |
| **`option optimize_for`**         | file               | `SPEED` (default), `CODE_SIZE`, or `LITE_RUNTIME`. |
| **`option (custom_option)`**      | file/message/field | Used for custom extensions (see below).            |

---

### **Custom / extension options**

You can define your own metadata tags and attach them to fields or messages.
Example:

```proto
import "google/protobuf/descriptor.proto";

extend google.protobuf.FieldOptions {
  string db_column = 50001;
}

message User {
  string name = 1 [(db_column) = "user_name"];
}
```

This is how frameworks like **gRPC-Gateway**, **Buf**, or **Connect** attach HTTP rules
(`google.api.http`), validation annotations, etc.

---

### **Summary of most useful ones in real projects**

| Context                    | Common Options                                                   |
| -------------------------- | ---------------------------------------------------------------- |
| JSON APIs                  | `json_name`, `jstype`                                            |
| Language packaging         | `go_package`, `java_package`, `csharp_namespace`, `swift_prefix` |
| Memory/performance         | `cc_enable_arenas`, `optimize_for`, `packed`                     |
| Documentation / versioning | `deprecated`                                                     |
| Custom extensions          | `google.api.http`, `validate.rules`, etc.                        |

---

Swapping two numbers without using a temporary variable is a classic programming trick that leverages 
**bitwise XOR operations**. Here's a detailed explanation of how it works:

---

### The XOR Swap Algorithm
The XOR swap algorithm uses the properties of the **XOR (`^`)** operator to swap two numbers without 
needing a temporary variable.

#### Key Properties of XOR:
1. **Commutative**: \(a \oplus b = b \oplus a\)
2. **Associative**: \(a \oplus (b \oplus c) = (a \oplus b) \oplus c\)
3. **Identity**: \(a \oplus 0 = a\)
4. **Self-Inverse**: \(a \oplus a = 0\)

---

### How It Works
Let’s say we have two numbers, `a` and `b`. The steps to swap them are:

1. **Step 1**: `a = a ^ b`
   - Store the result of `a XOR b` in `a`.
   - Now, `a` holds the combined information of both `a` and `b`.

2. **Step 2**: `b = a ^ b`
   - XOR the new `a` (which is `a ^ b`) with the original `b`.
   - This cancels out the original `b`, leaving the original `a` in `b`.

3. **Step 3**: `a = a ^ b`
   - XOR the new `a` (which is `a ^ b`) with the new `b` (which is the original `a`).
   - This cancels out the original `a`, leaving the original `b` in `a`.

---

### Example in Go
Here’s how the XOR swap algorithm looks in Go:

```go
package main

import "fmt"

func main() {
    a, b := 5, 10

    fmt.Println("Before swap:", a, b)

    // Step 1: a = a ^ b
    a = a ^ b

    // Step 2: b = a ^ b
    b = a ^ b

    // Step 3: a = a ^ b
    a = a ^ b

    fmt.Println("After swap:", a, b)
}
```

**Output:**
```
Before swap: 5 10
After swap: 10 5
```

---

### Step-by-Step Breakdown
Let’s break down the steps with the example values `a = 5` and `b = 10`.

#### Step 1: `a = a ^ b`
- Binary representation:
  - `a = 5`  → `0101`
  - `b = 10` → `1010`
- XOR operation:
  ```
  0101 (5)
  ^ 1010 (10)
  --------
  1111 (15)
  ```
- Now, `a = 15`.

#### Step 2: `b = a ^ b`
- Binary representation:
  - `a = 15` → `1111`
  - `b = 10` → `1010`
- XOR operation:
  ```
  1111 (15)
  ^ 1010 (10)
  --------
  0101 (5)
  ```
- Now, `b = 5` (original value of `a`).

#### Step 3: `a = a ^ b`
- Binary representation:
  - `a = 15` → `1111`
  - `b = 5`  → `0101`
- XOR operation:
  ```
  1111 (15)
  ^ 0101 (5)
  --------
  1010 (10)
  ```
- Now, `a = 10` (original value of `b`).

---

### Why Does This Work?
The XOR swap algorithm works because of the properties of the XOR operation:
1. **Step 1** combines the values of `a` and `b` into `a`.
2. **Step 2** extracts the original `a` by XORing the combined value with `b`.
3. **Step 3` extracts the original `b` by XORing the combined value with the new `b` (which is the original `a`).

---

### Caveats
While the XOR swap algorithm is a neat trick, it has some limitations:
1. **Readability**: It’s less intuitive and harder to understand than using a temporary variable.
2. **Performance**: Modern compilers and CPUs are optimized for temporary variable swaps, so the XOR 
    swap may not be faster (and can even be slower).
3. **Applicability**: It only works for integers. For floating-point numbers or complex data types, 
    a temporary variable is required.

---

### Alternative: Using Arithmetic
You can also swap two numbers without a temporary variable using arithmetic operations:

```go
a = a + b
b = a - b
a = a - b
```

However, this method can cause overflow issues with large numbers, so the XOR swap is generally 
preferred for integers.

---


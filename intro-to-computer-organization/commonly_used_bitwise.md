Below is a **cheat sheet** of some well-known and commonly used bitwise calculations, along with 
explanations and examples. These are often used in programming for optimization, encoding, 
and low-level operations.

---

### 1. **Power of Two Calculations**
Bitwise shifts are often used to calculate powers of two quickly.

| Calculation       | Result | Explanation                          |
|-------------------|--------|--------------------------------------|
| `1 << n`          | \(2^n\)| Shifting `1` left by `n` positions.  |
| `1 << 0`          | 1      | \(2^0 = 1\)                          |
| `1 << 1`          | 2      | \(2^1 = 2\)                          |
| `1 << 2`          | 4      | \(2^2 = 4\)                          |
| `1 << 3`          | 8      | \(2^3 = 8\)                          |
| `1 << 4`          | 16     | \(2^4 = 16\)                         |
| `1 << 5`          | 32     | \(2^5 = 32\)                         |
| `1 << 10`         | 1024   | \(2^{10} = 1024\) (1 KB)             |
| `1 << 20`         | 1,048,576 | \(2^{20} = 1,048,576\) (1 MB)     |
| `1 << 30`         | 1,073,741,824 | \(2^{30} = 1,073,741,824\) (1 GB) |

---

### 2. **Division by Powers of Two**
Right shifts are used to divide by powers of two.

| Calculation       | Result | Explanation                          |
|-------------------|--------|--------------------------------------|
| `16 >> 1`         | 8      | \(16 / 2^1 = 8\)                     |
| `16 >> 2`         | 4      | \(16 / 2^2 = 4\)                     |
| `32 >> 3`         | 4      | \(32 / 2^3 = 4\)                     |
| `1024 >> 10`      | 1      | \(1024 / 2^{10} = 1\)                |

---

### 3. **Common Bitmask Operations**
Bitwise operations are often used to create or manipulate bitmasks.

| Calculation       | Result | Explanation                          |
|-------------------|--------|--------------------------------------|
| `0xFF`            | 255    | 8-bit mask (binary: `11111111`)      |
| `0xFF << 8`       | 65280  | 16-bit mask (binary: `1111111100000000`) |
| `0xFFFF`          | 65535  | 16-bit mask (binary: `1111111111111111`) |
| `0xFFFFFFFF`      | 4,294,967,295 | 32-bit mask (binary: `11111111111111111111111111111111`) |

---

### 4. **Famous Constants**
Some bitwise calculations are used to define constants in programming.

| Calculation       | Result | Explanation                          |
|-------------------|--------|--------------------------------------|
| `1 << 31`         | 2,147,483,648 | Maximum 32-bit signed integer + 1 (often used in overflow checks). |
| `(1 << 32) - 1`   | 4,294,967,295 | Maximum 32-bit unsigned integer.     |
| `1 << 63`         | 9,223,372,036,854,775,808 | Maximum 64-bit signed integer + 1. |
| `(1 << 64) - 1`   | 18,446,744,073,709,551,615 | Maximum 64-bit unsigned integer. |

---

### 5. **Common Bitwise Tricks**
These are some famous bitwise tricks used in algorithms and optimizations.

| Calculation                     | Result | Explanation                          |
|---------------------------------|--------|--------------------------------------|
| `x & (x - 1)`                   | Clears the lowest set bit of `x`. Useful for counting set bits. |
| `x & -x`                        | Extracts the lowest set bit of `x`. |
| `x | (x + 1)`                   | Sets the lowest unset bit of `x`.    |
| `x ^ x`                         | 0      | XORing a number with itself always results in 0. |
| `x ^ 0`                         | x      | XORing a number with 0 leaves it unchanged. |
| `x & 1`                         | Checks if `x` is odd (1) or even (0). |
| `x << 1`                        | Multiplies `x` by 2.                |
| `x >> 1`                        | Divides `x` by 2 (integer division). |

---

### 6. **Real-World Examples**
Here are some real-world examples of bitwise operations:

#### a) **Checking if a Number is a Power of Two**
```go
func isPowerOfTwo(x int) bool {
    return x > 0 && (x & (x - 1)) == 0
}
```
- Explanation: A power of two has exactly one bit set in its binary representation. `x & (x - 1)` 
clears the lowest set bit, so if the result is `0`, it's a power of two.

#### b) **Swapping Two Numbers Without a Temporary Variable Using The XOR Swap Algorithm (^)**
```go
a, b := 5, 10
a = a ^ b
b = a ^ b
a = a ^ b
fmt.Println(a, b) // Output: 10 5
```
- Explanation: XOR swap algorithm uses bitwise XOR to swap values.

#### c) **Counting Set Bits (Hamming Weight)**
```go
func countSetBits(x int) int {
    count := 0
    for x != 0 {
        x = x & (x - 1) // Clear the lowest set bit
        count++
    }
    return count
}
```
- Explanation: This algorithm counts the number of `1`s in the binary representation of `x`.

---

### 7. **Famous Bitwise Calculations**
Here are some famous bitwise calculations used in practice:

| Calculation                     | Result | Explanation                          |
|---------------------------------|--------|--------------------------------------|
| `2 << 32`                       | 8,589,934,592 | Often used in hashing and cryptography. |
| `16 >> 2`                       | 4      | Dividing 16 by 4.                    |
| `0xFFFFFFFF << 4`               | 4,294,967,280 | Shifting a 32-bit mask left by 4.    |
| `0xFFFFFFFF >> 4`               | 268,435,455 | Shifting a 32-bit mask right by 4.   |

---


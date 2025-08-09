# Detailed Explanation of Binary Addition and Subtraction Logic

Explain the exact logic gates and step-by-step operations used in binary addition
and subtraction, including why you see specific carry/borrow behavior.

## Binary Addition Logic (0100 + 1110)

The fundamental building block for addition is the **Full Adder** circuit, which consists of:

1. **XOR gate** for the sum bit
2. **AND gates** for carry generation
3. **OR gate** for carry propagation

### Step-by-step addition of 0100 (4) + 1110 (14):

```
Position: 3 2 1 0  (bit positions)
         0 1 0 0  (4)
       + 1 1 1 0  (14)
```

**Bit Position 0 (LSB): 0 + 0**

- Sum: 0 XOR 0 = 0
- Carry out: 0 AND 0 = 0

**Bit Position 1: 0 + 1**

- Sum: 0 XOR 1 = 1
- Carry out: 0 AND 1 = 0

**Bit Position 2: 1 + 1** (this is where it gets interesting)

- Sum: 1 XOR 1 = 0
- Carry out: 1 AND 1 = 1 (this carry propagates to position 3)

**Bit Position 3 (MSB): 0 + 1 + carry-in 1**

- First stage (0 + 1):
  - Partial sum: 0 XOR 1 = 1
  - Partial carry: 0 AND 1 = 0
- Second stage (partial sum 1 + carry-in 1):
  - Final sum: 1 XOR 1 = 0
  - Final carry: 1 AND 1 = 1 (this becomes CF)

Final result: 0010 with CF=1

## Binary Subtraction Logic

Subtraction uses the same circuitry as addition through **two's complement**:

1. Negate the subtrahend (bitwise NOT + 1)
2. Add using normal addition

### Example: 4 - 6 (0100 - 0110)

1. Find two's complement of 6 (0110):
   - Invert bits: 1001
   - Add 1: 1001 + 1 = 1010 (-6 in two's complement)

2. Perform addition: 0100 (4) + 1010 (-6) = 1110 (-2)

```
Position: 3 2 1 0
         0 1 0 0
       + 1 0 1 0
```

**Bit Position 0: 0 + 0**

- Sum: 0 XOR 0 = 0
- Carry: 0 AND 0 = 0

**Bit Position 1: 0 + 1**

- Sum: 0 XOR 1 = 1
- Carry: 0 AND 1 = 0

**Bit Position 2: 1 + 0**

- Sum: 1 XOR 0 = 1
- Carry: 1 AND 0 = 0

**Bit Position 3: 0 + 1**

- Sum: 0 XOR 1 = 1
- Carry: 0 AND 1 = 0

Result: 1110 (-2) with CF=0 (no borrow needed)

## Why 1 - 1 = 1 in the Carry/Borrow Chain

This is a common point of confusion. Let me clarify with an example:

### Example: 5 - 3 (0101 - 0011)

Using subtraction directly (not two's complement):

```
  0101
- 0011
```

**Bit Position 0: 1 - 1**

- Direct subtraction: 1 - 1 = 0
- No borrow needed

**Bit Position 1: 0 - 1**

- Can't do this, so we borrow:
  - Borrow makes position 1: 10 (binary 2)
  - Now: 10 - 1 = 1
  - The borrow flag (CF) would be set to 1 here

**Bit Position 2: 0 - 0** (after borrow)

- 0 - 0 = 0

**Bit Position 3: 0 - 0**

- 0 - 0 = 0

Final result: 0010 (2) with CF=0 (the borrow was resolved)

The "1 - 1 = 1" you're seeing in the carry/borrow chain refers to the fact that:

- When we need to borrow, we're essentially doing (10)₂ - 1 = 1 (with a borrow out of 1)
- The borrow propagates through the higher bits

## Hardware Implementation

Actual CPUs use:

1. **Ripple Carry Adders** (series of full adders) - simple but slow
2. **Carry Look-Ahead Adders** - more complex but faster
3. **Carry Select Adders** - compromise between speed and complexity

For subtraction, they either:

1. Use dedicated subtraction logic (with borrow propagation)
2. More commonly: Use two's complement and reuse the adder circuitry

## Key Takeaways

1. **Addition uses XOR for sum and AND for carry**:
   - Sum = A XOR B
   - Carry = A AND B

2. **Subtraction can be done two ways**:
   - Direct subtraction (with borrows)
   - Two's complement addition (more common in hardware)

3. **The carry/borrow chain shows propagation**:
   - A "1" in the chain means either:
     - For addition: carry was generated
     - For subtraction: borrow was needed

4. **Flags are set based on these operations**:
   - CF (Carry Flag) = carry out of MSB in addition
   - CF = borrow needed in subtraction
   - Other flags (ZF, SF, OF) are set based on the result

---

---

---

# Dealing with negative result

Break down the subtraction **4 - 8** (which equals **-4**) in **4-bit binary**, including
how the logic handles negative results using **two's complement** and how the
**Carry Flag (CF)** behaves.

---

## **Step 1: Represent Numbers in 4-Bit Binary**

- **4** in 4-bit binary: `0100`
- **8** in 4-bit binary: `1000`

Since we're working with **4 bits**, the range for **unsigned** numbers is **0 to 15**,
but for **signed** (two's complement), it's **-8 to +7**.

- **8** is **outside** the 4-bit signed range, but we'll still compute it.

---

## **Step 2: Compute 4 - 8 Using Two’s Complement**

Since subtraction is **addition of the negative**, we:

1. Find the **two’s complement of 8** (which represents **-8**).
2. Add it to **4**.

### **(A) Find Two’s Complement of 8 (1000)**

1. **Invert all bits** (one’s complement):  
   `1000` → `0111`
2. **Add 1** to get the two’s complement:  
   `0111 + 1 = 1000` (same as original, because **-8** is the most negative 4-bit number).

So, **-8** in 4-bit two’s complement is still `1000`.

### **(B) Perform Addition: 4 (0100) + (-8) (1000)**

```
  0100  (4)
+ 1000  (-8)
-------
  1100  (Result)
```

#### **Bit-by-Bit Breakdown:**

1. **Bit 0 (LSB):** `0 + 0 = 0`
2. **Bit 1:** `0 + 0 = 0`
3. **Bit 2:** `1 + 0 = 1`
4. **Bit 3 (MSB):** `0 + 1 = 1`
   - No carry occurs because `0 + 1 = 1` (no overflow).

**Final result:** `1100`

---

## **Step 3: Interpret the Result**

- `1100` in **4-bit two’s complement** means:
  1. **Invert bits:** `1100` → `0011`
  2. **Add 1:** `0011 + 1 = 0100` (which is **4**).
  3. **Apply sign:** `-4`.

So, `1100` = **-4** (correct result).

---

## **Step 4: Check the Carry Flag (CF)**

- In **subtraction**, the **Carry Flag (CF)** acts as a **"borrow" flag**:
  - **CF = 1** if a borrow was needed (unsigned underflow).
  - **CF = 0** if no borrow was needed.

In our case (**4 - 8**):

- We **did not need a borrow** (because `0100 + 1000` did not produce a carry out).
- **CF = 0** (but this is **misleading** because the result is negative).

### **Why CF=0 is Misleading Here**

- CF is **primarily for unsigned arithmetic** (where `4 - 8` would wrap around to `12`).
- For **signed arithmetic**, we check the **Overflow Flag (OF)** instead.
- **OF = 1** if the result is incorrect (e.g., adding two positives gives a negative).

But in **4-bit signed**, `-8` is valid, so **OF=0** here.

---

## **Summary of 4 - 8 in 4-Bit Binary**

| Operation | Binary               | Decimal | CF  | OF  |
| --------- | -------------------- | ------- | --- | --- |
| 4 - 8     | `0100 + 1000 = 1100` | -4      | 0   | 0   |

- **Result:** `1100` (**-4**, correct in two’s complement).
- **CF=0** (no borrow in unsigned interpretation).
- **OF=0** (no signed overflow).

---

## **Key Takeaways**

1. **Subtraction is performed using two’s complement addition**.
2. **CF (Carry Flag)** indicates **unsigned overflow** (but can be misleading for signed numbers).
3. **OF (Overflow Flag)** indicates **signed overflow** (when result exceeds range).
4. **-8 is a special case** in 4-bit because its two’s complement is itself (`1000`).

---

---

---

# Complement of itself, and Overflow flag (OF)

## **1. Why -8 is Special in 4-Bit Two's Complement**

In **4-bit two's complement**:

- The range is **-8 to +7**.
- **-8** is represented as `1000` (no positive counterpart since `+8` doesn't fit in 4 bits).

### **How Two's Complement Normally Works**

For any number `N`, its negative is computed as:

1. Invert all bits (`~N`).
2. Add `1`.

But for **-8 (`1000`)**:

1. `8` in binary = `1000`.
2. Invert bits → `0111`.
3. Add `1` → `1000` (same as original!).

This means:

- **-8 is its own two's complement** (a unique case).
- **There is no +8** in 4-bit signed arithmetic (max positive is `0111` = +7).

### **Implications**

- If you compute `-(-8)`, you **still get -8** (because `1000` flipped and incremented loops back to `1000`).
- This is why **-8 is an edge case** in signed arithmetic.

---

## **2. Overflow Flag (OF) — When Does It Occur?**

The **Overflow Flag (OF)** is set when:

- **Signed arithmetic produces an incorrect result** (outside the valid range).

### **When OF = 1?**

| Operation  | Example (4-bit) | Result      | OF    | Reason                         |
| ---------- | --------------- | ----------- | ----- | ------------------------------ |
| **+7 + 1** | `0111 + 0001`   | `1000` (-8) | **1** | Positive + Positive = Negative |
| **-8 - 1** | `1000 + 1111`   | `0111` (+7) | **1** | Negative + Negative = Positive |
| **4 - 8**  | `0100 + 1000`   | `1100` (-4) | **0** | Valid result                   |

### **How to Detect OF Mathematically**

- **OF = Carry into MSB XOR Carry out of MSB**
  - If they differ, OF=1 (overflow occurred).

---

## **3. Example: 3 - 5 (4-bit Signed Subtraction)**

Let’s compute **3 - 5** step-by-step.

### **(A) Convert to Binary**

- `3` = `0011`
- `5` = `0101`

### **(B) Find Two's Complement of 5 (for -5)**

1. Invert `0101` → `1010`
2. Add `1` → `1011` (this is `-5` in two's complement).

### **(C) Perform Addition: 3 + (-5)**

```
  0011  (3)
+ 1011  (-5)
-----------
  1110  (-2)
```

#### **Bit-by-Bit Breakdown**

1. **Bit 0 (LSB):** `1 + 1 = 0` (carry `1`)
2. **Bit 1:** `1 + 1 + carry 1 = 1` (carry `1`)
3. **Bit 2:** `0 + 0 + carry 1 = 1` (carry `0`)
4. **Bit 3 (MSB):** `0 + 1 = 1` (carry `0`)

**Final result:** `1110` (which is **-2** in two's complement — correct!).

### **(D) Check Flags**

| Flag                   | Value | Meaning                               |
| ---------------------- | ----- | ------------------------------------- |
| **CF (Carry Flag)**    | `0`   | No unsigned borrow needed             |
| **OF (Overflow Flag)** | `0`   | Result is valid (-2 is in range)      |
| **SF (Sign Flag)**     | `1`   | Result is negative (`1110` has MSB=1) |
| **ZF (Zero Flag)**     | `0`   | Result is not zero                    |

---

## **Summary**

1. **-8 is special** because its two's complement is itself (`1000`).
2. **OF (Overflow Flag)** detects **signed arithmetic errors** (e.g., +7 + 1 = -8 → OF=1).
3. **3 - 5 = -2** (`1110`) with **CF=0, OF=0** (valid result).

---

---

---

# Overflow flag (OF)

## **Example: -4 - 5 (4-bit Signed Subtraction)**

Let’s compute **-4 - 5** step-by-step in **4-bit two's complement**.  
This will demonstrate **signed overflow (OF=1)**.

---

### **Step 1: Represent Numbers in 4-Bit Two's Complement**

| Decimal | Binary (4-bit) |
| ------- | -------------- |
| **-4**  | `1100`         |
| **5**   | `0101`         |

Since we're doing **-4 - 5**, we can rewrite it as:  
**-4 + (-5)**

So, we need:

1. **-4** = `1100` (already in two's complement)
2. **-5** = Two's complement of `5` (`0101`)

---

### **Step 2: Find Two's Complement of 5 (for -5)**

1. **Invert all bits** of `0101` → `1010`
2. **Add 1** → `1011`
   - Now, `1011` represents **-5**.

---

### **Step 3: Perform Addition (-4 + -5)**

```
  1100  (-4)
+ 1011  (-5)
-----------
 (1)0111  (Result, but we discard the extra carry)
```

#### **Bit-by-Bit Breakdown**

1. **Bit 0 (LSB):** `0 + 1 = 1` (no carry)
2. **Bit 1:** `0 + 1 = 1` (no carry)
3. **Bit 2:** `1 + 0 = 1` (no carry)
4. **Bit 3 (MSB):** `1 + 1 = 0` (carry `1` out)
   - **Carry into MSB:** `1` (from Bit 2 addition)
   - **Carry out of MSB:** `1` (discarded in 4-bit arithmetic)

**Final 4-bit result:** `0111` (**+7**)

Wait, but **-4 - 5 = -9**, which is **outside 4-bit signed range (-8 to +7)**!  
This means we have **signed overflow (OF=1)**.

---

### **Step 4: Check Flags**

| Flag                   | Value   | Meaning                                                             |
| ---------------------- | ------- | ------------------------------------------------------------------- |
| **CF (Carry Flag)**    | `1`     | There was a carry out of MSB (but irrelevant for signed arithmetic) |
| **OF (Overflow Flag)** | **`1`** | **Signed Overflow!** (Two negatives added to a positive)            |
| **SF (Sign Flag)**     | `0`     | Result is positive (`0111` has MSB=0)                               |
| **ZF (Zero Flag)**     | `0`     | Result is not zero                                                  |

### **Why OF=1?**

- **Rule:** If **two negatives add to a positive**, or **two positives add to a negative**, OF=1.
- Here:
  - `-4 (1100)` + `-5 (1011)` = `+7 (0111)` → **Invalid!** (OF=1)

---

### **Step 5: Correct Interpretation**

- The true result (**-9**) is outside 4-bit signed range.
- The computed `0111` (**+7**) is **incorrect due to overflow**.
- **CPU detects OF=1**, indicating the result is unreliable.

---

## **Summary of -4 - 5 in 4-Bit Binary**

| Operation | Binary               | Decimal        | CF  | OF    | Notes         |
| --------- | -------------------- | -------------- | --- | ----- | ------------- |
| -4 - 5    | `1100 + 1011 = 0111` | +7 (incorrect) | 1   | **1** | **Overflow!** |

### **Key Takeaways**

1. **Signed overflow (OF=1)** occurs when:
   - Two negatives add to a positive.
   - Two positives add to a negative.
2. **-4 - 5 = -9**, but **4-bit arithmetic wraps to +7** (OF=1 warns us).
3. **CF is irrelevant here** (it’s for unsigned arithmetic).

---

## **Bonus: Another Example (7 + 1)**

Let’s see **another overflow case** (two positives → negative).

### **Compute 7 + 1 (4-bit signed)**

```
  0111  (+7)
+ 0001  (+1)
-----------
  1000  (-8)  (OF=1, because +7 + +1 = -8 is invalid)
```

- **OF=1** (two positives gave a negative).
- **SF=1** (result is negative).
- **CF=0** (no unsigned carry).

---

### **Final Thoughts**

- **OF** helps detect **invalid signed results**.
- **CF** is mostly for **unsigned arithmetic**.
- **Edge cases** (like `-8`) behave unusually in two's complement.

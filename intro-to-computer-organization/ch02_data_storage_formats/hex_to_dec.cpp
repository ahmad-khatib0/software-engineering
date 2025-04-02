
// Algorithm
//
// - Initialize a variable dec_value with 0 to store the decimal value.
// - Traverse the hexadecimal string from right to left and check,
//  . If the current character is a number 0-9, convert it to its corresponding
//    integer by subtracting ‘0’ from its ASCII value.
//  . If the character is a letter from ‘A’ to ‘F’, convert it to its
//    corresponding integer by subtracting ‘A’ from its ASCII value and adding
//    10 to it.
// - Multiply each digit of the hexadecimal number with the proper base
//   (Power of 16) and add it to the variable dec_value.
// - Update the base value in each iteration by multiplying it by 16.
// - After traversing all the digits of the hexadecimal number, the variable
//   dec_value will store the equivalent decimal number.
//

#include <bits/stdc++.h>
using namespace std;

int hexa_to_decimal(string hex_val) {
  int len = hex_val.size();

  // Initializing base value to 1, i.e 16^0 representing 16^0
  // (the least significant digit).
  int base = 1;

  int dec_val = 0;

  // from right to left
  for (int i = len - 1; i >= 0; i--) {
    //. If character lies in '0'-'9', converting it to integral 0-9 by
    //  subtracting 48 from ASCII value
    //
    //. int(hex_val[i]) converts the character to its ASCII value.
    //
    //. Subtracting 48 from the ASCII value converts the character to
    //  its corresponding integer value (since the ASCII value of '0' is 48).
    //
    //. This integer value is then multiplied by the current base
    //  (which is a power of 16) and added to dec_val.

    cout << base << "\n";
    if (hex_val[i] >= '0' && hex_val[i] <= '9') {
      dec_val += (int(hex_val[i]) - 48) * base;

      // base is then updated to the next power of 16 by multiplying it by 16.
      base = base * 16;
    }

    else if (hex_val[i] >= 'A' && hex_val[i] <= 'F') {
      //. If character lies in 'A'-'F' , converting it to integral 10 - 15 by
      //  subtracting 55 from ASCII value
      //. Subtracting 55 from the ASCII value converts the character to its
      //  corresponding integer value (since the ASCII value of 'A' is 65,
      //  and 65 - 55 = 10, which is the decimal value for 'A').
      dec_val += (int(hex_val[i]) - 55) * base;

      // Incrementing base by power
      base = base * 16;
    }
  }

  return dec_val;
}

int main(int argc, char *argv[]) {
  string hex_num = "1ABC22";
  cout << (hexa_to_decimal(hex_num));
  return 0;
}

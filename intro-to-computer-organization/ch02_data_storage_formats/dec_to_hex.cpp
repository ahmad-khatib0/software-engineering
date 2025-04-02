//
// Algorithm:
// - Store the remainder when the number is divided by 16 in a temporary
//   variable temp. If the temp is less than 10, insert (48 + temp) in a
//   character array otherwise if the temp is greater than or equal to 10,
//   insert (55 + temp) in the character array.
// - Divide the number by 16 now
// - Repeat the above two steps until the number is not equal to 0.
// - Print the array in reverse order now.
//
//

// Example: If the given decimal number is 2545.
//
// Step 1: Calculate the remainder when 2545 is divided by 16 is 1. Therefore,
//    temp = 1. As temp is less than 10. So, arr[0] = 48 + 1 = 49 = ‘1’.
//
// Step 2: Divide 2545 by 16. The new number is 2545/16 = 159. Step
//
// 3: Calculate the remainder when 159 is divided by 16 is 15. Therefore,
//    temp = 15. As temp is greater than 10. So, arr[1] = 55 + 15 = 70 = ‘F’.
//
// Step 4: Divide 159 by 16. The new number is 159/16 = 9. Step 5: Calculate the
//    remainder when 9 is divided by 16 is 9. Therefore, temp = 9. As temp is
//    less than 10. So, arr[2] = 48 + 9 = 57 = ‘9’.
//
// Step 6: Divide 9 by 16. The new number is 9/16 = 0.
//
// Step 7: Since the number becomes = 0. Stop repeating steps and print the
// array in reverse order. Therefore, the equivalent hexadecimal number is 9F1.
//

#include <iostream>
using namespace std;

string dec_to_hex(int n) {
  // ans string to store hexadecimal number
  string ans = "";

  while (n != 0) {
    // remainder variable to store remainder
    int rem = 0;

    // ch variable to store each character
    char ch;
    rem = n % 16;

    if (rem < 10) {
      ch = rem + 48;
    } else {
      ch = rem + 55;
    }

    ans += ch;
    n = n / 16;
  }

  // reversing the ans string to get the final result
  int i = 0, j = ans.size() - 1;
  while (i <= j) {
    swap(ans[i], ans[j]);
    i++;
    j--;
  }

  return ans;
}

int main(int argc, char *argv[]) {
  cout << "Please enter a number to convert it to Hexadecimal";

  string num;
  cin >> num;
  int n = stoi(num);

  string result = dec_to_hex(n);
  cout << "The hexadecimal result is " << result << endl;

  return 0;
}

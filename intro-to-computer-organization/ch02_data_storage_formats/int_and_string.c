
/*
   Read and display an integer and a text string.
*/

#include <stdio.h>

int main(int argc, char *argv[]) {
  unsigned int anInt;
  char a_string[10];

  printf("Enter a number in the hexadecimal: ");
  scanf("%x", &anInt);

  printf("Enter it again ");
  scanf("%s", a_string);

  printf("The integer is %u and the string is %s\n", anInt, a_string);
  return 0;
}

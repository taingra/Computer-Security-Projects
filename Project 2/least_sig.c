
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[])
{
  printf("Sizeof Char %d\n", sizeof(char));

  if (argc < 3) {
    printf("Usage: %s num bits\n", argv[0]);
    return EXIT_FAILURE;
  }

  int num = atoi(argv[1]);
  int bits = atoi(argv[2]);

  // Cut off bits
  int out = num & ((1 << bits) - 1);
  printf("%d becomes %d\n", num, out);
  return EXIT_SUCCESS;
}

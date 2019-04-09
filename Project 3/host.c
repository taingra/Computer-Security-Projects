/* Dummie host program to test virus program */

#include <stdio.h>
#include <stdlib.h>

int main(int argc, char* argv[])
{
  if (argc < 2) {
    printf("Usage: %s FILE\n", argv[0]);
    return EXIT_FAILURE;
  }

  for (int i = 1; i < argc; i++) {
    FILE* fd = fopen(argv[i], "r");
    if (fd == NULL) {
      perror(argv[i]);
      return EXIT_FAILURE;
    }

    fseek(fd, 0, SEEK_END);
    long size = ftell(fd);

    printf("%-8s %12ld\n", argv[i], size);
  }
  return EXIT_SUCCESS;
}

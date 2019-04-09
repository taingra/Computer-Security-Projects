#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include <sys/types.h>

int main(int argc, char *argv[])
{

  printf("%.*s\n", 3, argv[1]);

  return 0;
}

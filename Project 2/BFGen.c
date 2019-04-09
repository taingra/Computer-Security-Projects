/* BFGen.c -- Generate Bloom filter
   by Thomas Ingram */

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <limits.h>
#include <openssl/evp.h>
#include <openssl/hmac.h>

int main(int argc, char* argv[])
{
  if (argc < 4) {
    printf("Incorrect number of arguments\n");
    printf("Usage: %s [FILE] m x\n", argv[0]);
    return EXIT_FAILURE;
  }

  FILE* passfile = fopen(argv[1], "r");
  if (passfile == NULL) {
    perror("Bad password file");
    return EXIT_FAILURE;
  }

  int m = atoi(argv[2]); // Size of bloom filter
  int x = atoi(argv[3]); // How many bits from digest used

  bool bloom[m]; // Bloom Filter
  for (int i = 0; i < m; i++) // Set default value to false
    bloom[i] = false;
  char* keys[10] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  unsigned char *digest;
  char passwd[64];

  while (fgets(passwd, 64, passfile)) { // Read until EOF
    int passlen;
    if (strstr(passwd, "\n")) // Remove \n
      passlen = strlen(passwd) - 1;
    else
      passlen = strlen(passwd);

    // Hash passwords and set bits in Bloom filter
    for (int i = 0; i < 10; i++) {
      digest = HMAC(EVP_sha1(), keys[i], strlen(keys[i]), (unsigned char *) passwd,
		    passlen, NULL, NULL);

      // Get the x least significant bits from digest
      int index = 0;
      int tmp = 0;

      for (int j = 0; j < (x / CHAR_BIT); j++) {
	// SHA1 produces a 20-byte hash value, so 19 is least significant byte
	tmp = digest[19 - j];
	index += tmp << (j * CHAR_BIT);
      }
      if ((x % CHAR_BIT) != 0) { // Delete trailing bits
	tmp = digest[19 - (x / CHAR_BIT)] << (CHAR_BIT - (x % CHAR_BIT));
	tmp = tmp >> (CHAR_BIT - (x % CHAR_BIT));
	index = tmp << ((x / CHAR_BIT) * CHAR_BIT);
      }

      bloom[index] = true;
    }
  }
  fclose(passfile);

  // Write Bloom filter data to file
  FILE* datafile = fopen("BFData.txt", "w");
  if (datafile == NULL) {
    perror("BFData.txt");
    return EXIT_FAILURE;
  }

  for (int i = 0; i < m; i++) {
    if (bloom[i])
      putc('1', datafile);
    else
      putc('0', datafile);
  }
  fprintf(datafile, " %d", x); // Store x for BFPasswordCheck.c
  fclose(datafile);

  return EXIT_SUCCESS;
}

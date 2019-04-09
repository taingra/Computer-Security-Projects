/* BFPasswordCheck.c -- finds bad passwords from a given Bloom filter
   by Thomas Ingram */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <limits.h>
#include <math.h>
#include <openssl/evp.h>
#include <openssl/hmac.h>

int main(int argc, char* argv[])
{
  if (argc < 3) {
    printf("Incorrect number of arguments\n");
    printf("Usage: %s [BFData] [passwords.txt]\n", argv[0]);
    return EXIT_FAILURE;
  }

  FILE* bfdata = fopen(argv[1], "r");
  if (bfdata == NULL) {
    perror("BFData file");
    return EXIT_FAILURE;
  }

  bool bf[100000]; // Bloom Filter
  char bit;  // Temporary value
  int bflen = 0; // Basically our m value

  while ((bit = fgetc(bfdata)) != EOF && bit != ' ') { // Load Bloom Filter data
    if (bit == '1')
      bf[bflen] = true;
    else
      bf[bflen] = false;

    ++bflen;
  }

  // Get x value from end of BFData file
  int x = 0;
  fscanf(bfdata, "%d", &x);

  fclose(bfdata);

#ifdef DEBUG
  printf("Got x: %d\n", x);
#endif

  FILE* passwds = fopen(argv[2], "r");
  if (passwds == NULL) {
    perror("Passwords file");
    return EXIT_FAILURE;
  }

  FILE* badpass = fopen("badpass.txt", "w");
  if (badpass == NULL) {
    perror("BadPass file");
    return EXIT_FAILURE;
  }

  char passwd[64];
  char* keys[10] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  unsigned char* digest;
  while(fgets(passwd, 64, passwds)) { // Get password
    int passlen;
    if (strstr(passwd, "\n")) // Remove \n
      passlen = strlen(passwd) - 1;
    else
      passlen = strlen(passwd);

    // Test password
    bool bad = true;
    for (int i = 0; i < 10; i++) {
      digest = HMAC(EVP_sha1(), keys[i], strlen(keys[i]), (unsigned char *) passwd,
		    passlen, NULL, NULL);


      // Get the x least significant bits from digest
      int index = 0;
      int tmp = 0;
      for (int j = 0; j < (x / CHAR_BIT); j++) {
	// Note SHA1 produces a 20-byte hash value
	tmp = digest[19 - j];
	index += tmp << (j * CHAR_BIT);
      }
      if ((x % CHAR_BIT) != 0) { // Deal get trailing bits
	tmp = digest[19 - (x / CHAR_BIT)] << (CHAR_BIT - (x % CHAR_BIT));
	tmp = tmp >> (CHAR_BIT - (x % CHAR_BIT));
	index = tmp << ((x / CHAR_BIT) * CHAR_BIT);
      }

      if (bf[index] == false) {
	bad = false;
	break;
      }
    }

    // Write bad password
    if (bad == true) {
      fprintf(badpass, "%s", passwd);
    }
  }
  fclose(passwds);
  fclose(badpass);

  return EXIT_SUCCESS;
}

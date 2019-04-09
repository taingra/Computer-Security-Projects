/* Helper functions for dealing with files in virus project.
   Written by Thomas Ingram <taingram@mtu.edu> */

#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <unistd.h>
#include "vhlpr.h"

/* Helper to verify file is opened properly. */
void check_file(FILE *file, const char *name)
{
  if (file == NULL) {
#ifdef DEBUG
    fprintf(stderr, "debug: File opening error\n");
    perror(name);
#endif
    exit(EXIT_FAILURE);
  }
}


/* Reads file to end of seperator (e.g. 0xDEADBEEF).
   Returns file position, if no seperator found returns -1 */
long seek_to_sep(FILE* seed, int sep[4])
{
  fseek(seed, 0, SEEK_SET);
  int byte = 0;
  while ((byte = fgetc(seed)) != EOF) {
    if (sep[0] == byte  &&
	sep[1] == fgetc(seed) &&
	sep[2] == fgetc(seed) &&
	sep[3] == fgetc(seed)) {
      return ftell(seed);
    }
  }

  return -1;
}


/* Creates filename with pid to avoid collisions.
   Note: the string created must be freed by the user of this function. */
char* gen_unique_filename(const char *filename)
{
  char* name =  malloc(strlen(filename) + 11); // hard coded max length pid_t
  sprintf(name,"%s-%d", filename, getpid());
  return name;
}


/* Copy in file to out file from current start position to end. */
void copy_file(FILE *in, FILE *out, long start, long end)
{
  int bufsize = 1024;
  char buf[bufsize];
  size_t bytes;
  if (end == 0) {
    fseek(in, 0, SEEK_END);
    end = ftell(in);
  }
  fseek(in, start, SEEK_SET);
  if (end < ftell(in) + bufsize) {
    bufsize = end - ftell(in);
  }
  while ((bytes = fread(buf, 1, bufsize, in)) != 0) {
    if (fwrite(buf, 1, bytes, out) != bytes)
      return;
    if (end < ftell(in) + bufsize) {
      bufsize = end - ftell(in);
    }
  }
}

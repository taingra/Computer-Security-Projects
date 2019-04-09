/* Helper functions for dealing with files in virus project.
   Written by Thomas Ingram <taingram@mtu.edu> */
#ifndef _VHLPR_H
#define _VHLPR_H

#include <stdio.h>

/* Helper to verify file is opened properly. */
void check_file(FILE *file, const char *name);

/* Reads file to end of seperator (e.g. 0xDEADBEEF).
   Returns file position, if no seperator found returns -1 */
long seek_to_sep(FILE* seed, int sep[4]);

/* Creates filename with pid to avoid collisions.
   Note: the string created must be freed by the user of this function. */
char* gen_unique_filename(const char *filename);

/* Copy in file to out file from current start position to end. */
void copy_file(FILE *in, FILE *out, long start, long end);

#endif	/* _VHLPR_H */

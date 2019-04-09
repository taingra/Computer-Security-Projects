Thomas Ingram

To compile my program run 'make' in the same directory that should compile both
BFGen.c and BFPasswordCheck.c into the corresponding BFGen and BFPasswordCheck
respectively. I also provided a 'make test' command for running the program with
my calculated m and x values and then running BFPasswordCheck with the proper
arguments after that.

make       # Compiles BFGen.c and BFPasswordcheck.c

make test  # Runs program with m = 66209 and x = 16

make clean # removes BFGen and BFPasswordCheck

1. The value of m = -k n /ln(1-P^{1/k})
   - k is the number of hash functions k = 10
   - n number of items (passwords?) n = 3545
   - P false positive rate P = 0.00015

   m = -10 * 3545 / ln(1 - 0.00015^{1/10}) = 66208.8376392

2. The value of x

   2^16 = 65536
   2^17 = 131072

   So we only need the first 16 least significant bits of 2^160 so that x <= m

   x = 16

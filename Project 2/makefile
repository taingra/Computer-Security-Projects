CFLAGS=-std=c99 -pedantic -Wall -Wextra

all: BFGen BFPasswordCheck

BFGen: BFGen.c
	$(CC) -I/usr/include/openssl -o $@ -lcrypto -lm $^ $(CFLAGS)

BFPasswordCheck: BFPasswordCheck.c
	$(CC) -I/usr/include/openssl -o $@ -lcrypto -lm $^ $(CFLAGS)

test: BFGen
	./BFGen passlist.txt 66209 16
	./BFPasswordCheck BFData.txt passwords.txt

clean:
	$(RM) BFGen BFPasswordCheck

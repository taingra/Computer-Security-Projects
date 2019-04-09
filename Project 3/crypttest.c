#define _XOPEN_SOURC
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <crypt.h>

int main(void)
{
    char key[64];
    char orig[ 9 ]= "myblocko";
    char buf[64];
    char txt[ 9 ];
    int i, j;

    for ( i = 0; i < 64; i++ )
        key[ i ] = rand( ) & 1;

    printf("Before encrypting: %s\n", orig);
    for ( i = 0; i < 8; i++ )
        for ( j = 0; j < 8; j++ )
            buf[ i * 8 + j ] = orig[ i ] >> j & 1;
    setkey(key);

    encrypt(buf, 0);
    for ( i = 0; i < 8; i++ )
        for ( j = 0, txt[ i ] = '\0'; j < 8; j++ )
            txt[ i ] |= buf[ i * 8 + j ] << j;
    txt[ 9 ] = '\0';
    printf("After encrypting:  %s\n", txt);

    encrypt(buf, 1);
    for ( i = 0; i < 8; i++ )
        for ( j = 0, txt[ i ] = '\0'; j < 8; j++ )
            txt[ i ] |= buf[ i * 8 + j ] << j;
    txt[ 9 ] = '\0';
    printf("After decrypting:  %s\n", txt);
    return 0;
}

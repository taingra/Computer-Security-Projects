/**
 * @author Thomas Ingram
 *
 * This skeleton is provided to help students in completing the DES implementation for project 1.
 *
 * You have to fill the code to make the functions work under DES.
 *
 * You can add your own functions, but please keep the original functions. Do not change the
 * function declarations (only add your implementation for the functions definitions).
 *
 * This program adopts boolean array, for arithmetics, true = 1, false = 0; moreover, if you get a
 * number whose binary representation is 100, then it can also be converted back to boolean array
 * {true, false, false}
 *
 * You can find the points for every step, e.g., if you finish writing function Key_Schedule, and
 * your code can successfully pass the test, then you can get 15 points.
 *
 * The total number of points is 100.
 *
 * The notation in this skeletion corresponds to the DES section in Chapter 7 of Handbook of Applied
 * Cryptography (http://cacr.uwaterloo.ca/hac/about/chap7.pdf).
 *
 * Please do not distribute this file without permission.
 */

import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;

public class des {
  /* Some constants used in the project */
  private static final int KEY_NO = 16;           // 16 round keys for DES
  private static final int ROUND_KEY_LENGTH = 48; // every round key is 48-bit in length
  private static final int KEY_LENGTH = 64;       // length of key 56 bits + 8 bits of parity
  private static final int HALF_LENGTH = 32;      // length in bits of half a DES block
  private static final int BLOCK_LENGTH = 64;     // length in bits of a DES block
  private static final int BYTE_LENGTH = 8;

  /* Tables for DES */
  /* Note: All the tables are already defined, but if you prefer your own way of defining the
   * tables, you can choose your own way and make sure that your tables are correct (as defined in
   * the HAC book) */

  /* Corresponding to table 7.2 */
  private static final int IP[] = {58, 50, 42, 34, 26, 18, 10, 2,
                                   60, 52, 44, 36, 28, 20 ,12, 4,
                                   62, 54, 46, 38, 30, 22, 14, 6,
                                   64, 56, 48, 40, 32, 24, 16, 8,
                                   57, 49, 41, 33, 25, 17, 9, 1,
                                   59, 51, 43, 35, 27, 19, 11, 3,
                                   61, 53, 45, 37, 29, 21, 13, 5,
                                   63, 55, 47, 39, 31, 23, 15, 7};

  /* Corresponding to table 7.2 */
  private static final int IP_INVERSE[] = {40, 8, 48, 16, 56, 24, 64,32,
                                           39, 7, 47, 15, 55, 23, 63, 31,
                                           38, 6, 46, 14, 54, 22, 62, 30,
                                           37, 5, 45, 13,53, 21, 61, 29,
                                           36, 4, 44, 12, 52, 20, 60, 28,
                                           35, 3, 43, 11, 51, 19, 59, 27,
                                           34, 2, 42, 10, 50, 18, 58, 26,
                                           33, 1, 41, 9, 49, 17, 57, 25};

  /* Corresponding to table 7.3 */
  private static final int E[] = {32, 1, 2, 3, 4, 5,
                                  4, 5, 6, 7, 8, 9,
                                  8, 9, 10, 11, 12, 13,
                                  12, 13, 14, 15, 16, 17,
                                  16, 17, 18, 19, 20, 21,
                                  20, 21, 22, 23, 24, 25,
                                  24, 25, 26, 27, 28, 29,
                                  28, 29, 30, 31, 32 ,1};

  /* Corresponding to table 7.3 */
  private static final int P[] = {16, 7, 20, 21,
                                  29, 12, 28, 17,
                                  1, 15, 23, 26,
                                  5, 18, 31, 10,
                                  2, 8, 24, 14,
                                  32, 27, 3, 9,
                                  19, 13, 30, 6,
                                  22, 11, 4, 25};

  /* Corresponding to table 7.4 */
  private static final int PC1[] = {57, 49, 41, 33, 25, 17, 9,
                                    1, 58, 50, 42, 34, 26, 18,
                                    10, 2, 59, 51, 43, 35, 27,
                                    19, 11, 3, 60, 52, 44, 36,
                                    // Above for C_i; below for D_i
                                    63, 55, 47, 39, 31, 23, 15,
                                    7, 62, 54, 46, 38, 30, 22,
                                    14, 6, 61, 53, 45, 37, 29,
                                    21, 13, 5, 28, 20, 12, 4};

  /* Corresponding to table 7.4 */
  private static final int PC2[] = {14, 17, 11, 24, 1, 5,
                                    3, 28, 15, 6, 21, 10,
                                    23, 19, 12 ,4, 26, 8,
                                    16, 7, 27, 20, 13, 2,
                                    41, 52, 31, 37, 47, 55,
                                    30, 40, 51, 45, 33, 48,
                                    44, 49, 39, 56, 34, 53,
                                    46, 42, 50, 36, 29, 32};

  /* Corresponding to table 7.8 */
  private static final int S1[][] = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                                     {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                                     {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                                     {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}};

  /* Corresponding to table 7.8 */
  private static final int S2[][] = {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                                     {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                                     {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                                     {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}};

  /* Corresponding to table 7.8 */
  private static final int S3[][] = {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                                     {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                                     {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                                     {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}};

  /* Corresponding to table 7.8 */
  private static final int S4[][] = {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                                     {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                                     {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                                     {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}};

  /* Corresponding to table 7.8 */
  private static final int S5[][] = {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                                     {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                                     {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                                     {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}};

  /* Corresponding to table 7.8 */
  private static final int S6[][] = {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                                     {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                                     {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                                     {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}};

  /* Corresponding to table 7.8 */
  private static final int S7[][] = {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                                     {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                                     {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                                     {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}};

  /* Corresponding to table 7.8 */
  private static final int S8[][] = {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                                     {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                                     {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                                     {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};


  /**
   * DES key schedule (15 points)
   * generate 16 round keys from original key K
   * @param key A 64-bit key K= k_1 ... k_64
   * @return sixteen 48-bit round keys K_i, for 1<=k<=16
   */
  public boolean[][] Key_Schedule(boolean key[])
  {
    boolean round_key_gen[][] = new boolean[KEY_NO][ROUND_KEY_LENGTH];
    //{{true, true},{true ,false}};
    //[][]={{1,2}, {2,3}};

    if(key.length != KEY_LENGTH) {
      System.out.printf("Key_Schedule: Wrong input. The key should be 64-bit in length\n");
      return round_key_gen;
    }

    // fill your code here, the return value should be put into round_key_gen
    boolean[] T = new boolean[56];
    for (int i = 0; i < 56; i++) {
      T[i] = key[PC1[i] - 1];
    }

    for (int k = 0; k < KEY_NO; k++) { // k = key number
      int v;
      if (k + 1 == 1 || k + 1 == 2 || k + 1 == 9 || k + 1 == 16)
        v = 1;
      else
        v = 2;

      boolean[] C = new boolean[28];
      boolean[] D = new boolean[28];
      for (int i = 0; i < 56; i++) {
        if (i < 28) { // C_i
          if (i - v < 0)
            C[i + 28 - v] = T[i];
          else
            C[i - v] = T[i];
        } else { // D_i
          if (i - v < 28)
            D[i + 28 - v - 28] = T[i];
          else
            D[i - 28 - v] = T[i];
        }
      }

      for (int i = 0; i < 56; i++) {
        if (i < 28)
          T[i] = C[i];
        else
          T[i] = D[i - 28];
      }

      for (int i = 0; i < 48; i++) {
        round_key_gen[k][i] = T[PC2[i] - 1];
      }
    }

    return round_key_gen;
  }

  /**
   * Initial Permutation (10 points)
   * the first step for DES
   * @param block The original 64-bit block
   * @return the block after permutation according to IP table (table 7.2)
   */
  public boolean[] Initial_Permutation(boolean block[])
  {
    boolean block_after_ip[] = new boolean[BLOCK_LENGTH];
    if (block.length != BLOCK_LENGTH)
    {
      System.out.printf("Initial_Permutation: Wrong input! The input block should have length 64 bits\n");
      return block_after_ip;
    }

    for (int i = 0; i < block.length; i++) {
      block_after_ip[i] = block[IP[i] - 1];
    }

    return block_after_ip;
  }


  /**
   * Expansion (function E) (5 points)
   *
   * Expand 32 bits to 48 bits according to E table (table 7.3), used by function f= P(S(E(R_i-1)
   * XOR K_i))
   *
   * @param array 32-bit array
   * @return 48-bit array
   */
  public boolean[] Expansion(boolean array[])
  {
    boolean expandedArray[] = new boolean[ROUND_KEY_LENGTH];
    if (array.length != HALF_LENGTH)
    {
      System.out.printf("Expansion: Wrong input! The array for Expansion should have length  " +
                        HALF_LENGTH + " bits\n");
      return expandedArray;
    }

    for (int i = 0; i < ROUND_KEY_LENGTH; i++) {
      expandedArray[i] = array[E[i] - 1];
    }

    return expandedArray;
  }

  /**
   * XOR two bit-arrays (5 points), used by function f= P(S(E(R_i-1) XOR K_i)), also used by
   * One_Round function
   *
   * @param array_1 bit-array
   * @param array_2 bit-array
   * @return the result of XOR
   * Note: make sure that array_1 and array_2 have equal length
   */
  public boolean[] XOR(boolean array1[],  boolean array2[])
  {
    if(array1.length != array2.length)
    {
      System.out.printf("XOR: Wrong input for XOR function! Arrays size must be equal length\n");
      return new boolean[1];
    }

    boolean[] result_xor = new boolean[array1.length];

    for (int i = 0; i < array1.length; i++) {
      if (array1[i] && array2[i]) {
        result_xor[i] = false;
      } else if (array1[i] || array2[i]) {
        result_xor[i] = true;
      } else {
        result_xor[i] = false;
      }
    }

    return result_xor;
  }



  /**
   * SboxesSubstitution (function S) (15 points), used by function f= P(S(E(R_i-1) XOR K_i)) convert
   * the 48-bit array into the 32-bit array
   *
   * @param array 48-bit array
   * @return: 32-bit array
   */
  public boolean[] SboxesSubstitution(boolean array[])
  {
    boolean array_after_substitution[] = new boolean[HALF_LENGTH];
    if(array.length != ROUND_KEY_LENGTH)
    {
      System.out.printf("SboxesSubstitution: Wrong input! The input array should have length  " +
                        ROUND_KEY_LENGTH + " bits\n");
      return array_after_substitution;
    }

    for (int s = 0; s < 8; s++) {
      int[] bits = new int[6];
      for (int index = 0; index < bits.length; index++) {
        if (array[s * 6 + index])
          bits[index] = 1;
        else
          bits[index] = 0;
      }

      int i = 2 * bits[0] + bits[5];
      int j = bits[1] * 8 + bits[2] * 4 + bits[3] * 2 + bits[4];

      int val;
      if (s == 0)
        val = S1[i][j];
      else if (s == 1)
        val = S2[i][j];
      else if (s == 2)
        val = S3[i][j];
      else if (s == 3)
        val = S4[i][j];
      else if (s == 4)
        val = S5[i][j];
      else if (s == 5)
        val = S6[i][j];
      else if (s == 6)
        val = S7[i][j];
      else
        val = S8[i][j];

      String binary = Integer.toBinaryString(val);

      // Shift bits over to reflect proper place
      int shift = 0;
      if (4 > binary.length()) {
        shift = 4 - binary.length();
      }

      for (int k = 0; k < shift; k++) {
        array_after_substitution[(s * 4) + k] = false;
      }

      for (int k = shift; k < 4; k++) {
        if (binary.charAt(k - shift) == '1')
          array_after_substitution[(s * 4) + k] = true;
        else
          array_after_substitution[(s * 4) + k] = false;
      }
    }

    return array_after_substitution;
  }



  /**
   * Permutation (function P) (5 points), used by function f= P(S(E(R_i-1) XOR K_i)) permute the
   *  array after substitution according to table P (table 7.3)
   *
   * @param array 32-bit array
   * @return 32-bit array
   */
  public boolean[] Permutation_f(boolean array[])
  {
    boolean array_after_permutation[] = new boolean[HALF_LENGTH];
    if(array.length != HALF_LENGTH)
    {
      System.out.printf("Permutation: Wrong input! The input arry for Permutation should have length  "
                        + HALF_LENGTH + " bits\n");
      return array_after_permutation;
    }

    for (int i = 0; i < array_after_permutation.length; i++) {
      array_after_permutation[i] = array[P[i] - 1];
    }

    return array_after_permutation;
  }


  /**
   * one round in DES (15 points)
   * @param block 64-bit array (L_i-1, R_i-1)
   * @param round_key the corresponding round key K_i
   * @return 64-bit array (L_i, R_i), and L_i = R_i-1, R_i = L_i-1 XOR f(R_i-1, K_i) = L_i-1 XOR
   * P(S(E(R_i-1) XOR K_i))
   */
  public boolean[] One_Round(boolean block[], boolean round_key[])
  {
    boolean block_after_round[] = new boolean[BLOCK_LENGTH];
    if (block.length != BLOCK_LENGTH) {
      System.out.printf("One_Round: Wrong input! The input block should have length 64 bits\n");
      return block_after_round;
    }
    if (round_key.length != ROUND_KEY_LENGTH) {
      System.out.printf("One_Round: Wrong input! The round key should have length  "
                        + ROUND_KEY_LENGTH + " bits\n");
      return block_after_round;
    }

    // fill your code here
    boolean leftBlock[] = Arrays.copyOfRange(block, 0, HALF_LENGTH);
    boolean rightBlock[] = Arrays.copyOfRange(block, 32, BLOCK_LENGTH);

    // function f on R_i
    boolean[] fpermutation = new boolean[32];
    fpermutation = Permutation_f(SboxesSubstitution(XOR (Expansion(rightBlock), round_key)));

    // XOR of L_i and R_i after function f
    leftBlock = XOR(leftBlock, fpermutation);

    // Combine left and right
    for (int i = 0; i < BLOCK_LENGTH; i++) {
      if (i < HALF_LENGTH) {
        block_after_round[i] = rightBlock[i];
      } else {
        block_after_round[i] = leftBlock[i % HALF_LENGTH];
      }
    }

    return block_after_round;
  }

  /**
   * Inverse IP (5 points)
   * the final step for DES, final cyphertext will be generated after applying Inverse IP
   *
   * @param block the 64-bit block after 16 rounds
   * @return the block after permuation according to IP^-1 table (table 7.2)
   */
  public boolean[] Inverse_IP(boolean block[])
  {
    boolean block_inverse_ip[] = new boolean[BLOCK_LENGTH];
    if (block.length != BLOCK_LENGTH) {
      System.out.printf("Inverse_IP: Wrong input! The input block should be length 64 bits\n");
      return block_inverse_ip;
    }

    for (int i = 0; i < block_inverse_ip.length; i++) {
      block_inverse_ip[i] = block[IP_INVERSE[i] - 1];
    }

    return block_inverse_ip;
  }

  /**
   * Encryption (10 points)
   * encrypt a 64-bit block using DES
   * @param block 64-bit plaintext
   * @param key 64-bit key
   * @return 64-bit cyphertext
   */
  public boolean[] encryption_DES(boolean block[], boolean key[])
  {
    boolean cypher_text[]= new boolean[BLOCK_LENGTH];
    if (block.length != BLOCK_LENGTH) {
      System.out.printf("encryption_DES: Wrong input! The input block should have length 64 bits\n");
      return cypher_text;
    }
    if (key.length != BLOCK_LENGTH) {
      System.out.printf("encryption_DES: Wrong input! The key should have length  64 bits\n");
      return cypher_text;
    }

    // fill your code here
    cypher_text = Initial_Permutation(block);

    boolean[][] round_keys = Key_Schedule(key);

    for (int k = 0; k < KEY_NO; k++) {
      cypher_text = One_Round(cypher_text, round_keys[k]);
    }

    // Irregular Swap
    boolean[] left = Arrays.copyOfRange(cypher_text, 0, HALF_LENGTH);
    boolean[] right = Arrays.copyOfRange(cypher_text, HALF_LENGTH, BLOCK_LENGTH);
    for (int i = 0; i < BLOCK_LENGTH; i++) {
      if (i < HALF_LENGTH)
        cypher_text[i] = right[i];
      else
        cypher_text[i] = left[i - HALF_LENGTH];
    }

    cypher_text = Inverse_IP(cypher_text);

    return cypher_text;
  }


  /**
   * Decryption (10 points)
   * decrypt a 64-bit block using DES
   * @param block 64-bit cyphertext
   * @param key 64-bit key
   * @return 64-bit plaintext
   */
  public boolean[] decryption_DES(boolean block[], boolean key[])
  {
    boolean plain_text[]= new boolean[BLOCK_LENGTH];
    if (block.length != BLOCK_LENGTH)
    {
      System.out.printf("decryption_DES: Wrong input! The input block should have length 64 bits\n");
      return plain_text;
    }
    if (key.length != BLOCK_LENGTH)
    {
      System.out.printf("decryption_DES: Wrong input! The key should have length 64 bits\n");
      return plain_text;
    }

    // fill your code here
    plain_text = Initial_Permutation(block);

    boolean[][] round_keys = Key_Schedule(key);
    for (int k = KEY_NO - 1; k >= 0; k--) {
      plain_text = One_Round(plain_text, round_keys[k]);
    }

    // Irregular Swap
    boolean[] left = Arrays.copyOfRange(plain_text, 0, HALF_LENGTH);
    boolean[] right = Arrays.copyOfRange(plain_text, HALF_LENGTH, BLOCK_LENGTH);
    for (int i = 0; i < BLOCK_LENGTH; i++) {
      if (i < HALF_LENGTH)
        plain_text[i] = right[i];
      else
        plain_text[i] = left[i - HALF_LENGTH];
    }

    plain_text = Inverse_IP(plain_text);

    return plain_text;
  }

  /*
   * documentation (5 points)
   * You should provide clear documentation (comments) for your program, so that your program can
   * easily be read by others
   */


  /*
    show the actual bit value for the corresponding boolean array
    e.g., the boolean array {true, true, false}, then the output will be 110
  */
  public void showBooleanArray(boolean array[])
  {
    int i = 0;
    for(i=0;i<array.length;i++)
    {
      if(array[i])
      {
        System.out.printf("1");
      }
      else System.out.printf("0");

      if((i+1) % BYTE_LENGTH == 0) System.out.printf(" ");

    }

  }

  /*
    write the actual bit value for the corresponding boolean array to output file
    e.g., the boolean array {true, true, false}, then we will write 110 to file
  */
  public void writeBooleanArrayToFile(FileWriter fw, boolean array[])
  {
    int i = 0;

    try
    {
      for(i=0;i<array.length;i++)
      {
        if(array[i])
        {
//        System.out.printf("1");
          fw.write("1");
        }
        else fw.write("0");//System.out.printf("0");

        if((i+1) % BYTE_LENGTH == 0) fw.write(" ");//System.out.printf(" ");
      }
    }
    catch(IOException e)
    {
      System.out.printf("Exception when writing the output: "+e.toString()+"\n");
    }

  }

  /*
    get boolean array for the input bit string
    e.g., the input string is 1001, then you will get a boolean string {true, false, false, true}
  */
  public boolean[] getBooleanArray(String input)
  {
    int i = 0;
    int length = input.length();
    boolean array[] = new boolean[length];

    for(i=0;i<length;i++)
    {
      if(input.charAt(i) == '1')
        array[i] = true;
      else array[i] = false;
    }

    return array;
  }


  /*
    The main function will be used by the grader to test the functions you have written.
    note: You don't need to fill any code in main function.

    usage: java des args[0] args[1] args[2] args[3] args[4] args[5] args[6] args[7] args[8] args[9], all of args[*] should be big string, e.g., 1110001100
    args[0] stores the 64-bit key
    args[1] stores the plain text (64 bits) to be encrypted, which is also used for testing Initial Permutation
    args[2] stores the test input (32 bits) for Expansion function.
    args[3] and args[4] store the test input (48 bits) for XOR function.
    args[5] stores the test input (48 bits) for SboxesSubstitution function.
    args[6] stores the test input (32 bits) for Permutation function.
    args[7] stores the test content (64 bits) for one round, while args[8] stores the corresponding round key (48 bits)
    args[9] stores the test input (64 bits) for Inverse IP.
  */
  public static void main(String[] args)
  {
    int i = 0;

    des d = new des();

    FileWriter fw;
    try
    {
      fw = new FileWriter("output.txt");


      System.out.printf("\n\nStart testing ... \n\n");
      fw.write("\n\nStart testing ... \n\n");

      /*test DES key schedule*/
      System.out.printf("Testing DES key schedule ...\n");
      fw.write("Testing DES key schedule ...\n");
      System.out.printf("The key for testing is: "+args[0]+"\n");
      fw.write("The key for testing is: "+args[0]+"\n");
      boolean gen_key[][] = d.Key_Schedule(d.getBooleanArray(args[0]));
      System.out.printf("The round keys generated are: \n");
      fw.write("The round keys generated are: \n");
      for(i=1;i<=KEY_NO;i++)
      {
        System.out.print("round key"+i+": ");
        fw.write("round key"+i+": ");
        d.showBooleanArray(gen_key[i-1]);
        d.writeBooleanArrayToFile(fw, gen_key[i-1]);
        System.out.printf("\n");
        fw.write("\n");
      }
      System.out.printf("\n");
      fw.write("\n");


      /*test Initial Permutation*/
      System.out.printf("Testing Initial Permutation ...\n");
      fw.write("Testing Initial Permutation ...\n");
      System.out.printf("The block for testing is: "+args[1]+"\n");
      fw.write("The block for testing is: "+args[1]+"\n");
      boolean block[]= d.Initial_Permutation(d.getBooleanArray(args[1]));
      System.out.printf("The block after Initial Permutation is: ");
      fw.write("The block after Initial Permutation is: ");
      d.showBooleanArray(block);
      d.writeBooleanArrayToFile(fw, block);
      System.out.print("\n\n");
      fw.write("\n\n");


      /*test Expansion*/
      System.out.printf("Testing Expansion Function ...\n");
      fw.write("Testing Expansion Function ...\n");
      System.out.printf("The bit string for testing is: "+args[2]+"\n");
      fw.write("The bit string for testing is: "+args[2]+"\n");
      boolean array_after_expansion[]= d.Expansion(d.getBooleanArray(args[2]));
      System.out.printf("The bit string after Expansion is: \n");
      fw.write("The bit string after Expansion is: \n");
      d.showBooleanArray(array_after_expansion);
      d.writeBooleanArrayToFile(fw, array_after_expansion);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test XOR*/
      System.out.printf("Testing XOR Function ...\n");
      fw.write("Testing XOR Function ...\n");
      System.out.printf("The bit strings for testing are: "+args[3]+" and "+args[4]+"\n");
      fw.write("The bit strings for testing are: "+args[3]+" and "+args[4]+"\n");
      boolean array_xor[]= d.XOR(d.getBooleanArray(args[3]),  d.getBooleanArray(args[4]));
      System.out.printf("The result after XOR is: ");
      fw.write("The result after XOR is: ");
      d.showBooleanArray(array_xor);
      d.writeBooleanArrayToFile(fw, array_xor);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test SboxesSubstitution*/
      System.out.printf("Testing SboxesSubstitution Function ...\n");
      fw.write("Testing SboxesSubstitution Function ...\n");
      System.out.printf("The bit string for testing is: "+args[5]+"\n");
      fw.write("The bit string for testing is: "+args[5]+"\n");
      boolean array_after_substitution[]= d.SboxesSubstitution(d.getBooleanArray(args[5]));
      System.out.printf("The bit string after Substitution is: ");
      fw.write("The bit string after Substitution is: ");
      d.showBooleanArray(array_after_substitution);
      d.writeBooleanArrayToFile(fw, array_after_substitution);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test Permutation*/
      System.out.printf("Testing Permutation Function ...\n");
      fw.write("Testing Permutation Function ...\n");
      System.out.printf("The bit string for testing is: "+args[6]+"\n");
      fw.write("The bit string for testing is: "+args[6]+"\n");
      boolean array_after_permutation[]= d.Permutation_f(d.getBooleanArray(args[6]));
      System.out.printf("The bit string after Permutation is: ");
      fw.write("The bit string after Permutation is: ");
      d.showBooleanArray(array_after_permutation);
      d.writeBooleanArrayToFile(fw, array_after_permutation);
      System.out.print("\n\n");
      fw.write("\n\n");


      /*test one round*/
      System.out.printf("Testing One_Round Function ...\n");
      fw.write("Testing One_Round Function ...\n");
      System.out.printf("The test input for one round is: "+args[7]+"\n");
      fw.write("The test input for one round is: "+args[7]+"\n");
      System.out.printf("The corresponding round key is: "+args[8]+"\n");
      fw.write("The corresponding round key is: "+args[8]+"\n");
      boolean block_one_round[]= d.One_Round(d.getBooleanArray(args[7]), d.getBooleanArray(args[8]));
      System.out.printf("The result of one round: ");
      fw.write("The result of one round: ");
      d.showBooleanArray(block_one_round);
      d.writeBooleanArrayToFile(fw, block_one_round);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test Inverse Initial Permutation*/
      System.out.printf("Testing Inverse Initial Permutation ...\n");
      fw.write("Testing Inverse Initial Permutation ...\n");
      System.out.printf("The bit string for testing is: "+args[9]+"\n");
      fw.write("The bit string for testing is: "+args[9]+"\n");
      boolean block_after_inverse_ip[]= d.Inverse_IP(d.getBooleanArray(args[9]));
      System.out.printf("The bit string after Inverse Initial Permutation is: ");
      fw.write("The bit string after Inverse Initial Permutation is: ");
      d.showBooleanArray(block_after_inverse_ip);
      d.writeBooleanArrayToFile(fw, block_after_inverse_ip);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test DES encryption*/
      System.out.printf("Testing encryption Function ...\n");
      fw.write("Testing encryption Function ...\n");
      System.out.printf("The plain text to be encrypted is: "+args[1]+"\n");
      fw.write("The plain text to be encrypted is: "+args[1]+"\n");
      System.out.printf("The key for encryption is: "+args[0]+"\n");
      fw.write("The key for encryption is: "+args[0]+"\n");
      boolean cypher_text[]= d.encryption_DES(d.getBooleanArray(args[1]), d.getBooleanArray(args[0]));
      System.out.printf("The cypher text is: ");
      fw.write("The cypher text is: ");
      d.showBooleanArray(cypher_text);
      d.writeBooleanArrayToFile(fw, cypher_text);
      System.out.print("\n\n");
      fw.write("\n\n");

      /*test DES decryption*/
      System.out.printf("Testing decryption Function ...\n");
      fw.write("Testing decryption Function ...\n");
      System.out.printf("The cypher text to be decrypted is: ");
      fw.write("The cypher text to be decrypted is: ");
      d.showBooleanArray(cypher_text);
      d.writeBooleanArrayToFile(fw, cypher_text);
      System.out.print("\n");
      fw.write("\n");
      System.out.printf("The key for decryption is: "+args[0]+"\n");
      fw.write("The key for decryption is: "+args[0]+"\n");
      boolean plain_text[]= d.decryption_DES(cypher_text, d.getBooleanArray(args[0]));
      System.out.printf("Decrypt and get the plain text: ");
      fw.write("Decrypt and get the plain text: ");
      d.showBooleanArray(plain_text);
      d.writeBooleanArrayToFile(fw, plain_text);
      System.out.print("\n\n");
      fw.write("\n\n");


      // close the output file
      fw.close();
    }
    catch(IOException e)
    {
      System.out.printf("Output file exception: "+e.toString()+"\n");
    }
  }
}

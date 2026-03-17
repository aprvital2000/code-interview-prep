package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Key Bit Manipulation Tricks to Remember:
 * x & 1 - Check if odd
 * x & (x - 1) - Remove rightmost set bit
 * x & -x - Get rightmost set bit
 * x ^ x = 0 (XOR cancels)
 * x ^ 0 = x (XOR with 0 keeps value)
 * ~x - Flip all bits
 **/
public class BitManipulationTest {

    @ParameterizedTest
    @CsvSource(value = {"120:1111000", "63:111111"}, delimiter = ':')
    void printBinary(int x, String expected) {
        assertEquals(expected, Integer.toBinaryString(x));
    }

    /**
     * Problem: Check if a number is odd.
     * Odd numbers will have right most bit set (1). AND with 1 should result 1
     **/
    @ParameterizedTest
    @CsvSource(value = {"24:false", "63:true"}, delimiter = ':')
    void isOdd(int x, boolean expected) {
        assertEquals(expected, (x & 1) == 1);
    }

    /**
     * Problem: Check if a number is even.
     * Even numbers will have right most bit as 0. AND with 1 should result 0
     **/
    @ParameterizedTest
    @CsvSource(value = {"24:true", "63:false"}, delimiter = ':')
    void isEven(int x, boolean expected) {
        assertEquals(expected, (x & 1) == 0);
    }

    /**
     * Problem: Turnoff rightmost set bit.
     * x & (x - 1) - Remove rightmost set bit.
     **/
    @ParameterizedTest
    @CsvSource(value = {"01110000:1100000", "0111110:111100"}, delimiter = ':')
    void turnOffRightMostSetBit(String input, String expected) {
        int x = Integer.valueOf(input, 2);
        int result = x & (x - 1);
        assertEquals(expected, Integer.toBinaryString(result));
    }

    /**
     * Problem: Check if a number is a power of 2.
     * x if Power of 2 will have only one bit set. (x-1) will have ALL OTHER bits set.
     * AND x with x-1 should result in 0.
     **/
    @ParameterizedTest
    @CsvSource(value = {"64:true", "63:false"}, delimiter = ':')
    void powerOf2(int x, boolean expected) {
        assertEquals(expected, ((x & (x - 1)) == 0));
    }

    /**
     * Problem: Count the number of 1's in an integer.
     * Optimized using Brian Kernighan's algorithm - hammingWeight.
     * Recursively {@link #turnOffRightMostSetBit(String, String)}
     **/
    @ParameterizedTest
    @CsvSource(value = {"01010101:4", "00111111:6"}, delimiter = ':')
    void countOnes(String input, int expected) {
        int x = Integer.valueOf(input, 2);
        int ones = 0;
        while (x > 0) {
            x &= x - 1;
            ones++;
        }
        assertEquals(expected, ones);
    }

    /**
     * Problem: Swap without using a temporary variable.
     **/
    @ParameterizedTest
    @CsvSource(value = {"2:3:3:2", "3:4:4:3"}, delimiter = ':')
    void swap(int a, int b, int expectedA, int expectedB) {
        a = a ^ b;
        b = a ^ b;  // b = (a ^ b) ^ b = a
        a = a ^ b;  // a = (a ^ b) ^ a = b
        assertEquals(expectedA, a);
        assertEquals(expectedB, b);
    }

    /**
     * Problem: Add two integers using bit manipulation.
     * TODO - Understand
     **/
    @ParameterizedTest
    @CsvSource(value = {"10:12:22", "63:64:127"}, delimiter = ':')
    void getSum(int a, int b, int expected) {
        while (b != 0) {
            int carry = (a & b) << 1;   // Calculate carry
            a = a ^ b;                  // Sum without carry
            b = carry;                  // Continue with carry
        }
        assertEquals(expected, a);
    }

    /**
     * Problem: Check if the i-th bit is set in a number. (Count index from 0)
     * Take input as binary for easy understanding about input
     **/
    @ParameterizedTest
    @CsvSource(value = {"1010100:6:true", "1001100:4:false"}, delimiter = ':')
    void isBitSet(String input, int i, boolean expected) {
        int num = Integer.valueOf(input, 2);
        assertEquals(expected, (num & (1 << i)) != 0);
    }

    /**
     * Problem: Set the i-th bit
     * Take input as binary for easy understanding about input
     **/
    @ParameterizedTest
    @CsvSource(value = {"1010100:3:1011100", "1010100:4:1010100"}, delimiter = ':')
    void setBit(String input, int i, String expected) {
        int num = Integer.valueOf(input, 2);
        int result = num | (1 << i);
        assertEquals(expected, Integer.toBinaryString(result));
    }

    /**
     * Clear the i-th bit
     * Take input as binary for easy understanding about input
     **/
    @ParameterizedTest
    @CsvSource(value = {"1010100:2:1010000", "1010100:4:1000100"}, delimiter = ':')
    void clearBit(String input, int i, String expected) {
        int num = Integer.valueOf(input, 2);
        int result = num & ~(1 << i);
        assertEquals(expected, Integer.toBinaryString(result));
    }

    /**
     * Toggle the i-th bit
     * Take input as binary for easy understanding about input
     **/
    @ParameterizedTest
    @CsvSource(value = {"1010100:2:1010000", "1010100:4:1000100"}, delimiter = ':')
    void toggleBit(String input, int i, String expected) {
        int num = Integer.valueOf(input, 2);
        int result = num ^ (1 << i);
        assertEquals(expected, Integer.toBinaryString(result));
    }

    /**
     * Problem: Given an array where every element appears twice except one, find that single element.
     * XOR cancels out duplicates
     *
     **/
    @ParameterizedTest
    @CsvSource(value = {"[1,1,2,2,3]:3", "[1,2,2,3,3]:1"}, delimiter = ':')
    void singleNumber(@ConvertWith(IterableConverter.class) int[] nums, int expected) {
        int result = 0;
        for (int num : nums) {
            result ^= num;  // XOR cancels out duplicates
        }
        assertEquals(expected, result);
    }

    /**
     * Problem: Find the missing number in array [0, n].
     * XOR cancels out duplicates. XOR with array index as array contains 0…n
     * Refer to {@link #singleNumber(int[], int)}
     **/
    @ParameterizedTest
    @CsvSource(value = {"[0,1,2,3,4,6]:5", "[0,1,2,4,5,6]:3"}, delimiter = ':')
    void missingNumber(@ConvertWith(IterableConverter.class) int[] nums, int expected) {
        int xor = nums.length;
        for (int i = 0; i < nums.length; i++) {
            xor ^= i ^ nums[i];
        }
        assertEquals(expected, xor);
    }

    /**
     * Problem: All elements appear twice except two. Find them.
     * Refer {@link #singleNumber(int[], int)}, {@link #missingNumber(int[], int)}
     * TODO: Understand
     **/
    @ParameterizedTest
    @CsvSource(value = {"[0,1,1,0,3,2]:[3,2]", "[0,1,1,0,4,6]:[6,4]"}, delimiter = ':')
    void twoSingleNumbersArray(@ConvertWith(IterableConverter.class) int[] nums, @ConvertWith(IterableConverter.class) int[] expected) {
        int xor = 0;
        for (int num : nums) {
            xor ^= num;  // XOR of the two unique numbers
        }

        // Find rightmost set bit
        int rightmostBit = xor & -xor;

        int num1 = 0, num2 = 0;
        for (int num : nums) {
            if ((num & rightmostBit) != 0) {
                num1 ^= num;
            } else {
                num2 ^= num;
            }
        }
        List<Integer> expectedL = Arrays
                .stream(expected)
                .boxed()
                .toList();
        assertTrue(expectedL.contains(num1));
        assertTrue(expectedL.contains(num2));
    }

    /**
     * Problem: Every element appears 3 times except one. Find that element.
     * TODO - Understand
     **/
    @ParameterizedTest
    @CsvSource(value = {"[0,1,1,0,1,0,2]:2", "[0,1,2,0,1,2,0,1,2,3]:3"}, delimiter = ':')
    void singleNumber3(@ConvertWith(IterableConverter.class) int[] nums, int expected) {
        int ones = 0, twos = 0;
        for (int num : nums) {
            // 'ones' holds bits that have appeared 1 or 4 or 7... times
            ones = (ones ^ num) & ~twos;
            // 'twos' holds bits that have appeared 2 or 5 or 8... times
            twos = (twos ^ num) & ~ones;
        }
        assertEquals(expected, ones);
    }

    /**
     * Problem: Reverse the bits of a 32-bit unsigned integer.
     * TODO - Understand
     **/
    @ParameterizedTest
    @CsvSource(value = {"00000010100101000001111010011100:00111001011110000010100101000000"}, delimiter = ':')
    void reverseBits(String input, String expected) {
        int n = Integer.valueOf(input, 2);
        int output = Integer.valueOf(expected, 2);
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result <<= 1;           // Make space for next bit
            result |= (n & 1);      // Add last bit of n
            n >>>= 1;               // Move to next bit
        }
        assertEquals(output, result);
    }
}

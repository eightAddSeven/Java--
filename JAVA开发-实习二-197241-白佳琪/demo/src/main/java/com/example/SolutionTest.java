package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SolutionTest {
    @Test
    public void testCase1() {
        Solution sol = new Solution();
        // 假设是力扣第一题：两数之和
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] expected = {0, 1};
        assertArrayEquals(expected, sol.twoSum(nums, target));
    }
}
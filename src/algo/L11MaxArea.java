package algo;

import java.util.Arrays;

/**
 * LeetCode 11. 盛最多水的容器（Medium）
 * https://leetcode.cn/problems/container-with-most-water/
 *
 * 规矩：
 *   1. maxArea 的方法体自己写，不许看题解、不许看这段注释以外的东西
 *   2. 写不出就先在纸上画 [1,8,6,2,5,4,8,3,7]，手动推双指针走一遍
 *   3. 卡超过 30 分钟 → 看题解 → 合上重写
 *
 * 自检三问（写之前先在脑子里过一遍，写在下面注释里）：
 *   Q1 面积公式是什么？两个乘数分别是谁？
 *   Q2 循环什么时候停？为什么是这个条件？
 *   Q3 每一步移动哪一边？依据是哪两根柱子的比较？
 *
 * 你的答案：
 *   Q1:
 *   Q2:
 *   Q3:
 */
public class L11MaxArea {

    static int maxArea(int[] height) {
        // TODO: 从这里开始写
        int left = 0;
        int right = height.length - 1;
        int max = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                max = Math.max(max, height[left] * (right - left));
                left++;
            }else {
                max = Math.max(max, height[right] * (right - left));
                right--;
            }
        }
        return max;
        //throw new UnsupportedOperationException("TODO: maxArea 还没写");
    }

    public static void main(String[] args) {
        int[][] cases = {
                {1, 8, 6, 2, 5, 4, 8, 3, 7},
                {1, 1},
                {1},
                {4, 3, 2, 1, 4},
                {1, 2, 1},
        };
        int[] expected = {49, 1, 0, 16, 2};

        int pass = 0, fail = 0;
        for (int i = 0; i < cases.length; i++) {
            int actual;
            try {
                actual = maxArea(cases[i]);
            } catch (UnsupportedOperationException e) {
                actual = Integer.MIN_VALUE;
            }
            boolean ok = actual == expected[i];
            if (ok) pass++; else fail++;
            System.out.printf("[%s] 第%d个用例 %s → 期望 %d，实际 %s%n",
                    ok ? "PASS" : "FAIL", i + 1,
                    Arrays.toString(cases[i]), expected[i],
                    actual == Integer.MIN_VALUE ? "未实现" : String.valueOf(actual));
        }
        System.out.printf("%n通过 %d 项，失败 %d 项%n", pass, fail);
        if (fail == 0) System.out.println("全部通过 ✓");
    }
}

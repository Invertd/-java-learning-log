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
 *   Q1:height[left] * (right - left)，底和高
 *   Q2:左指针等于右指针的时候，这时面积为0
 *   Q3:短的一边，左右指针指向的两个柱子
 *
 * 标准答案（2026-11-06 晚补）：
 *   Q1：面积 = min(height[left], height[right]) × (right - left)。
 *       宽 = 两个下标的差；高 = 两根柱子中【较矮】的那根 —— 水会从矮的一侧溢出，
 *       所以高永远由短板决定，既不是较高那根，也不是两者的平均值。
 *   Q2：while (left < right)，left 与 right 相邻就停。
 *       left == right 时只剩一根柱子，宽度 = 0，面积必然 = 0，没有计算价值。
 *   Q3：移动【较矮】的那一边（两边相等时移哪边都行，但要固定写一边）。
 *       面试必背理由：设 height[left] <= height[right]，此刻面积 = height[left] × (right-left)。
 *       把 left 钉死不动，right 无论往左移到哪个位置，高度都不可能超过 height[left]
 *       （因为 height[left] 本身就是短板），而宽度只会越来越小
 *       → 所有「以当前 left 为左端点」的组合，面积都 ≤ 现在这个值，全都不可能更优。
 *       所以 left 这个位置可以【永久淘汰】，放心 left++。反过来去移动高边，这个论证就不成立。
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

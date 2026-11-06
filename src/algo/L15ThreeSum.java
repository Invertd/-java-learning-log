package algo;

import java.util.*;

/**
 * LeetCode 15. 三数之和（Medium）
 * https://leetcode.cn/problems/3sum/
 *
 * 规矩同 L11：方法体自己写，不许看题解，卡 30 分钟再看、看完合上重写。
 *
 * 自检三问（写代码前先在下面作答）：
 *   Q1 为什么要先给数组排序？排序帮你解决了什么？
 *   Q2 固定一个数之后，剩下两个数用什么方法找？两个指针的初始位置在哪、每一步移动谁？
 *   Q3 这题最坑的是「不能有重复三元组」。你在哪几个地方需要跳过重复值？
 *      少跳过任何一个，会多出什么样的答案？
 *
 * 你的答案：
 *   Q1:
 *   Q2:
 *   Q3:
 */
public class L15ThreeSum {

    static List<List<Integer>> threeSum(int[] nums) {
        // TODO: 从这里开始写
        throw new UnsupportedOperationException("TODO: threeSum 还没写");
    }

    public static void main(String[] args) {
        int[][] cases = {
                {-1, 0, 1, 2, -1, -4},
                {0, 1, 1},
                {0, 0, 0},
                {},
                {0, 0, 0, 0},
                {-2, 0, 1, 1, 2},
                {1, 2, -2, -1},
        };
        // 期望答案（三元组内部顺序、三元组之间顺序都不重要，用 norm() 归一化后比较）
        String[] expected = {
                "[[-1, -1, 2], [-1, 0, 1]]",
                "[]",
                "[[0, 0, 0]]",
                "[]",
                "[[0, 0, 0]]",
                "[[-2, 0, 2], [-2, 1, 1]]",
                "[]",
        };

        int pass = 0, fail = 0;
        for (int i = 0; i < cases.length; i++) {
            String actual;
            try {
                List<List<Integer>> res = threeSum(cases[i].clone());
                actual = norm(res);
            } catch (UnsupportedOperationException e) {
                actual = "未实现";
            } catch (Exception e) {
                actual = "异常: " + e.getClass().getSimpleName();
            }
            boolean ok = actual.equals(expected[i]);
            if (ok) pass++; else fail++;
            System.out.printf("[%s] 第%d个用例 %s%n", ok ? "PASS" : "FAIL", i + 1, Arrays.toString(cases[i]));
            if (!ok) {
                System.out.println("       期望: " + expected[i]);
                System.out.println("       实际: " + actual);
            }
        }
        System.out.printf("%n通过 %d 项，失败 %d 项%n", pass, fail);
        if (fail == 0) System.out.println("全部通过 ✓");
    }

    /** 归一化：每个三元组内部排序，三元组之间再排序，统一成可直接比较的字符串 */
    private static String norm(List<List<Integer>> res) {
        if (res == null) return "null";
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> t : res) {
            List<Integer> x = new ArrayList<>(t);
            Collections.sort(x);
            copy.add(x);
        }
        copy.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int c = Integer.compare(a.get(i), b.get(i));
                if (c != 0) return c;
            }
            return Integer.compare(a.size(), b.size());
        });
        return copy.toString();
    }
}

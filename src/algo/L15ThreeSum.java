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
 *   Q1:排序可以让重复数字相邻，方便去重
 *   Q2:双指针相向移动，判断和与0的关系，小于零左指针右移初始位于i+1，大于0右指针左移初始位于n-1
 *   Q3:3处跳过，最外层i的循环需要跳过，等于0时左右指针移动需要跳过，少一个都会有重复三元组
 *
 * 标准答案（2026-11-06 晚补）：
 *   Q1：排序解决两件事 ——
 *       ① 让相同的值【相邻】，去重才能用一行 while 跳过去。
 *          哈希表没有「相邻」这个概念，这就是为什么这题不用 Set 去重：
 *          得先给每个三元组排序才能比较，代价反超双指针。
 *       ② 让数组【有序】，双指针才能根据 sum 的大小决定动哪一边（单调性）。
 *   Q2：排序后固定 nums[i]，剩下两个数用【左右双指针相向而行】找：
 *       left = i+1，right = n-1；
 *       sum < 0 → left++（和太小，需要更大的数）
 *       sum > 0 → right--（和太大，需要更小的数）
 *       sum = 0 → 记录答案，再 left++、right-- 继续找下一组。
 *       复杂度 O(n²)：外层 n 次，内层最多 n 次。
 *   Q3：一共【3 处】必须跳过重复，位置和后果一一对应 ——
 *       ① 外层 i：if (i > 0 && nums[i] == nums[i-1]) continue;
 *          漏了 → 同一个起点值被当成多个起点，同组答案成倍重复。
 *          （注意是跟 i-1 比：判断「这个起点值是不是刚用过」，不是跟 i+1 比）
 *       ② sum == 0 之后 left 跳：while (left < right && nums[left] == nums[left+1]) left++;
 *       ③ sum == 0 之后 right 跳：while (left < right && nums[right] == nums[right-1]) right--;
 *          漏了 ② 或 ③ → 同一个 i 下，同一组三元组被反复加进 list。
 *       三处缺任何一处，LeetCode 上是「答案重复」而不是「答案错误」——
 *       最难自查的一类 bug，因为每次跑出来的前几组看起来都是对的。
 */
public class L15ThreeSum {

    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> list = new ArrayList<>();
        for(int i = 0; i < n-1;i++){
            int left = i+1;
            int right = n-1;
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            while(left<right){
                int sum = nums[right]+nums[i]+nums[left];
                if(sum<0) left++;
                else if(sum>0) right--;
                else{
                    list.add(Arrays.asList(nums[left],nums[i],nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                }

            }
        }
        return list;
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
                {-2, -1, 0, 1, 2, 3},                                  // 8 严格：三个答案都要找到
                {-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6},     // 9 严格：大量重复值
                {5, -5, -1, 0, 1, 2},                                  // 10 严格：跨越两端
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
                "[[-2, -1, 3], [-2, 0, 2], [-1, 0, 1]]",
                "[[-4, -2, 6], [-4, 0, 4], [-4, 1, 3], [-4, 2, 2], [-2, -2, 4], [-2, 0, 2]]",
                "[[-5, 0, 5], [-1, 0, 1]]",
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

package algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 3. 无重复字符的最长子串
 * https://leetcode.cn/problems/longest-substring-without-repeating-characters/
 *
 * 要求：自己写滑动窗口，不许 clear() 重来。
 *
 * ── 三个必须自己回答的问题（写完填，别空着）──────────────────
 * Q1. 右指针每动一格，先做什么、后做什么、max 在哪一步算？
 *     答（俊祥）：判断是否重复，不重复放入set，更新max；重复移动左指针
 *     标准：以窗口 [left, right) 为准，一整个循环做五件事 ——
 *           ① 右指针尽量往右扩，每扩一格就 add 一个字符；
 *           ② 撞到重复就停，**右指针原地不动**；
 *           ③ 左指针右移一格，把移出窗口的那个字符 remove 掉；
 *           ④ 回到 ① 继续扩，直到右指针再也扩不动；
 *           ⑤ 这时才算 best = max(best, right - left)。
 *           一句话：先扩后收，收完再扩；best 记在「不能再扩」那一刻。
 *           你的顺序对，但漏了一条：遇到重复时右指针是「停住」，不是自己往前走。
 *
 * Q2. 什么时候左指针动？动一次还是动到某个条件为止？
 *     答（俊祥）：右指针遇到重复的字符的时候，移动到重复字符第一个的下一个
 *     标准：两种解法答案完全不同，别混 ——
 *           解法一（Set）：左指针一轮挪【一格】，靠外层 for 反复挪，
 *                         直到右指针指向的字符不在 Set 里为止。
 *                         「直到」是外层循环实现的，内层 while 只负责扩。
 *           解法二（Map）：左指针一步【跳】到位，left = max(left, last[c] + 1)，
 *                         只跳一次，不需要任何循环。
 *     ⚠批注：你这句写的是解法二（跳），但 Q2 问的是你手上这份解法一（挪一格）。
 *            另外动的仍然是「左指针」不是「右指针」—— 主语又反了，和 Q3 追问那次同一个毛病。
 *
 * Q3. 为什么这题不能用「遇到重复就把 Set 清空、从当前位置重来」？
 *     （提示：拿 "dvdf" 手推一遍，看你会得到什么答案）
 *     答（俊祥）：会丢失当前位置之前的字符
 *     标准：手推 "dvdf" —— 清空版：d,v 进窗 → 遇第二个 d 清空 → 只剩 d,f → 得 2；
 *           正解是 3（"vdf"）。被丢掉的 v 恰恰是答案的一部分。
 *           本质：窗口表示「以 right 结尾的最长无重复子串」，清空等于把起点强行重置到
 *                right，放弃了所有更靠左、可能让窗口更长的起点。
 *                左指针只能一格一格往外挤，不能一把清空。
 * ─────────────────────────────────────────────────────────
 */
public class L3LengthOfLongestSubstring {

    public static int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int right = 0;
        int best = 0;
        for(int left = 0;left<s.length();left++){
            if (left !=0){
                set.remove(s.charAt(left-1));
            }
            while(right <s.length()&&!set.contains(s.charAt(right))){
                set.add(s.charAt(right));
                right++;
            }
            best = Math.max(best,right-left);
        }
        return best;
    }

    /**
     * 解法二：Map 存「每个字符最后出现的下标」，左指针直接跳，省掉 while。
     * 要求：不许用 Set，窗口内不许 while 收缩，只许一层 for。
     */
    public static int lengthOfLongestSubstring2(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left,map.get(c) + 1);
            }
            map.put(c, right);
            best = Math.max(best, right - left+1);
        }
        return best;
    }

    // ================= 测试 =================
    static int pass = 0, fail = 0;

    static void check(String s, int expect) {
        int got;
        try {
            got = L3LengthOfLongestSubstring.lengthOfLongestSubstring(s);
        } catch (Throwable e) {
            System.out.printf("[FAIL] \"%s\" 期望 %d，抛异常 %s%n", s, expect, e);
            fail++;
            return;
        }
        if (got == expect) {
            System.out.printf("[PASS] \"%s\" -> %d%n", s, got);
            pass++;
        } else {
            System.out.printf("[FAIL] \"%s\" 期望 %d，实际 %d%n", s, expect, got);
            fail++;
        }
    }

    public static void main(String[] args) {
        check("abcabcbb", 3);   // abc
        check("bbbbb", 1);      // b
        check("pwwkew", 3);     // wke
        check("", 0);
        check(" ", 1);
        check("dvdf", 3);       // vdf ← 专杀「清空重来」写法
        check("abba", 2);       // ab / bb / ba
        check("tmmzuxt", 5);    // mzuxt ← 专杀「左指针不取 max」写法

        System.out.printf("%n===== 解法一：通过 %d 项，失败 %d 项 =====%n", pass, fail);
        System.out.println(fail == 0 ? "全部通过" : "还有失败用例");

        System.out.println("\n---------- 解法二（Map 跳左指针）----------");
        String[][] cases = {
            {"abcabcbb", "3"}, {"bbbbb", "1"}, {"pwwkew", "3"}, {"", "0"},
            {" ", "1"}, {"dvdf", "3"}, {"abba", "2"}, {"tmmzuxt", "5"}
        };
        int p2 = 0, f2 = 0;
        for (String[] c : cases) {
            int expect = Integer.parseInt(c[1]);
            int got;
            try {
                got = lengthOfLongestSubstring2(c[0]);
            } catch (UnsupportedOperationException e) {
                System.out.printf("[SKIP] \"%s\" 未实现%n", c[0]);
                continue;
            } catch (Throwable e) {
                System.out.printf("[FAIL] \"%s\" 抛异常 %s%n", c[0], e);
                f2++;
                continue;
            }
            if (got == expect) { System.out.printf("[PASS] \"%s\" -> %d%n", c[0], got); p2++; }
            else { System.out.printf("[FAIL] \"%s\" 期望 %d，实际 %d%n", c[0], expect, got); f2++; }
        }
        System.out.printf("%n===== 解法二：通过 %d 项，失败 %d 项 =====%n", p2, f2);
    }
}

/**
 * MyHashMap 自检程序。
 *
 * 用法：
 *   javac -encoding UTF-8 MyHashMap.java MyHashMapTest.java
 *   java -Dfile.encoding=UTF-8 MyHashMapTest
 *
 * 全部通过会打印「全部通过」。有 TODO 没写完会打印 [TODO] xxx。
 */
public class MyHashMapTest {

    static int passed = 0;
    static int failed = 0;

    static void ok(String name, boolean cond) {
        if (cond) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }

    static void eq(String name, Object expect, Object actual) {
        boolean pass = expect == null ? actual == null : expect.equals(actual);
        if (pass) {
            passed++;
            System.out.println("  PASS  " + name + " = " + expect);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + "  期望=" + expect + "  实际=" + actual);
        }
    }

    interface Block {
        void run() throws Exception;
    }

    static void section(String title, Block body) {
        System.out.println("\n==== " + title + " ====");
        try {
            body.run();
        } catch (UnsupportedOperationException e) {
            System.out.println("  [TODO] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        section("1. 初始化", () -> {
            MyHashMap<String, Integer> m = new MyHashMap<>();
            eq("capacity", 16, m.capacity());
            eq("threshold", 12, m.threshold);
            eq("size", 0, m.size());
        });

        section("2. put / get 基本", () -> {
            MyHashMap<String, Integer> m = new MyHashMap<>();
            eq("put 返回 null", null, m.put("a", 1));
            eq("get(a)", 1, m.get("a"));
            eq("get(不存在)", null, m.get("zzz"));
            eq("size", 1, m.size());
        });

        section("3. 覆盖同一个 key", () -> {
            MyHashMap<String, Integer> m = new MyHashMap<>();
            m.put("a", 1);
            eq("第二次 put 返回旧值", 1, m.put("a", 2));
            eq("get(a) 是新值", 2, m.get("a"));
            eq("size 不变", 1, m.size());
        });

        section("4. 冲突（两个 key 落同一个桶）", () -> {
            MyHashMap<Integer, String> m = new MyHashMap<>();
            m.put(1, "one");
            m.put(17, "seventeen");   // 1 和 17 在容量 16 时下标相同
            eq("get(1)", "one", m.get(1));
            eq("get(17)", "seventeen", m.get(17));
            eq("size", 2, m.size());
            ok("桶 1 的链表长度 = 2", m.bucketLength(1) == 2);
        });

        section("5. 扩容（13 个元素，其中 1 个桶有 2 个节点的链）", () -> {
            MyHashMap<Integer, Integer> m = new MyHashMap<>();
            // 1 和 17 在容量 16 时都落在下标 1，形成链表；其余各占一个桶
            int[] keys = {1, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            for (int k : keys) {
                m.put(k, k * 10);
            }
            eq("size", 13, m.size());
            eq("capacity 翻倍成 32", 32, m.capacity());
            eq("threshold 变成 24", 24, m.threshold);
            boolean allOk = true;
            for (int k : keys) {
                if (!Integer.valueOf(k * 10).equals(m.get(k))) {
                    allOk = false;
                    System.out.println("    扩容后丢了 key=" + k);
                }
            }
            ok("扩容后 13 个元素全都还能取到", allOk);
        });

        section("6. null key", () -> {
            MyHashMap<String, String> m = new MyHashMap<>();
            m.put(null, "nullValue");
            eq("get(null)", "nullValue", m.get(null));
            eq("size", 1, m.size());
        });

        section("7. toString", () -> {
            MyHashMap<String, Integer> m = new MyHashMap<>();
            m.put("x", 1);
            eq("toString", "{x=1}", m.toString());
        });

        System.out.println("\n----------------------------------------");
        System.out.println("通过 " + passed + " 项，失败 " + failed + " 项");
        if (failed == 0 && passed > 0) {
            System.out.println("全部通过 ✅");
        }
        System.exit(failed == 0 && passed > 0 ? 0 : 1);
    }
}

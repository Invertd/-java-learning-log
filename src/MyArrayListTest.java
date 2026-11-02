/**
 * MyArrayList 的验收测试。全部 PASS 才算任务 4 完成。
 *
 * 运行：
 *   javac -encoding UTF-8 MyArrayList.java MyArrayListTest.java
 *   java -Dfile.encoding=UTF-8 MyArrayListTest
 */
public class MyArrayListTest {

    static int pass = 0, fail = 0;

    static void ok(String name, boolean condition) {
        if (condition) {
            pass++;
            System.out.println("  [PASS] " + name);
        } else {
            fail++;
            System.out.println("  [FAIL] " + name);
        }
    }

    interface Body { void run() throws Exception; }

    static void section(String title, Body body) {
        System.out.println(title);
        try {
            body.run();
        } catch (UnsupportedOperationException e) {
            fail++;
            System.out.println("  [TODO] " + e.getMessage());
        } catch (Exception e) {
            fail++;
            System.out.println("  [ERROR] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("===== MyArrayList 验收测试 =====\n");

        section("1. 无参构造：容量 0、size 0", () -> {
            MyArrayList<Integer> l = new MyArrayList<>();
            ok("size() == 0", l.size() == 0);
            ok("isEmpty() == true", l.isEmpty());
            ok("capacity() == 0", l.capacity() == 0);
        });

        section("2. 第一次 add：容量自动变成 10", () -> {
            MyArrayList<Integer> l = new MyArrayList<>();
            l.add(1);
            ok("size() == 1", l.size() == 1);
            ok("capacity() == 10", l.capacity() == 10);
            ok("get(0) == 1", l.get(0) == 1);
        });

        section("3. 加 100 个元素：size 正确、每个都取得到", () -> {
            MyArrayList<Integer> l = new MyArrayList<>();
            for (int i = 0; i < 100; i++) l.add(i);
            ok("size() == 100", l.size() == 100);
            boolean allRight = true;
            for (int i = 0; i < 100; i++) if (l.get(i) != i) allRight = false;
            ok("get(0..99) 全部正确", allRight);
            ok("capacity() == 109（10→15→22→33→49→73→109）", l.capacity() == 109);
        });

        section("4. 指定容量构造：一次扩容都不用", () -> {
            MyArrayList<String> l = new MyArrayList<>(50);
            ok("capacity() == 50", l.capacity() == 50);
            for (int i = 0; i < 50; i++) l.add("x" + i);
            ok("加满 50 个，capacity 仍是 50", l.capacity() == 50);
            ok("再加第 51 个，capacity 变成 75", l.add("y") && l.capacity() == 75);
        });

        section("5. set 返回旧值", () -> {
            MyArrayList<String> l = new MyArrayList<>();
            l.add("a");
            l.add("b");
            String old = l.set(1, "B");
            ok("set(1, \"B\") 返回 \"b\"", "b".equals(old));
            ok("set 之后 get(1) == \"B\"", "B".equals(l.get(1)));
            ok("set 不改 size", l.size() == 2);
        });

        section("6. remove：后面的元素前移", () -> {
            MyArrayList<String> l = new MyArrayList<>();
            l.add("a");
            l.add("b");
            l.add("c");
            String removed = l.remove(0);
            ok("remove(0) 返回 \"a\"", "a".equals(removed));
            ok("size 变成 2", l.size() == 2);
            ok("剩下的是 [b, c]", "[b, c]".equals(l.toString()));
        });

        section("7. 越界要抛 IndexOutOfBoundsException", () -> {
            MyArrayList<Integer> l = new MyArrayList<>();
            l.add(1);
            boolean caught = false;
            try {
                l.get(5);
            } catch (IndexOutOfBoundsException e) {
                caught = true;
            }
            ok("get(5) 抛 IndexOutOfBoundsException", caught);
        });

        section("8. 负数容量要抛 IllegalArgumentException", () -> {
            boolean caught = false;
            try {
                new MyArrayList<Integer>(-1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            ok("new MyArrayList<>(-1) 抛 IllegalArgumentException", caught);
        });

        System.out.println("\n=================================");
        System.out.println("通过 " + pass + " 项，失败 " + fail + " 项");
        if (fail == 0) {
            System.out.println("全部通过，任务 4 完成！");
        } else {
            System.out.println("还没全绿，继续改 MyArrayList.java");
        }
    }
}

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * 亲手把 fail-fast 的机制跑出来：
 * 每一轮都打印 cursor / modCount / expectedModCount，看着两个数字对不上然后抛异常。
 *
 * 运行：
 *   javac -encoding UTF-8 FailFastDemo.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED FailFastDemo
 */
public class FailFastDemo {

    static Field findField(Class<?> c, String name) throws NoSuchFieldException {
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    static Object get(Object obj, String name) throws Exception {
        Field f = findField(obj.getClass(), name);
        f.setAccessible(true);
        return f.get(obj);
    }

    public static void main(String[] args) throws Exception {

        // ========== 第一部分：普通 for 下标循环 —— 不报错，但悄悄漏掉元素 ==========
        System.out.println("===== 第一部分：用下标 for 循环，边遍历边删 =====");
        List<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        a.add("c");
        System.out.println("原始 list = " + a);

        for (int i = 0; i < a.size(); i++) {
            String s = a.get(i);
            System.out.println("  读到 [" + s + "] (下标 " + i + ")");
            if (s.equals("a")) {
                a.remove(s);
                System.out.println("    删除后 list = " + a + "，长度变成 " + a.size());
            }
        }
        System.out.println("遍历结束，list = " + a);
        System.out.println(">>> 注意：没有报任何错，但 \"b\" 从未被读到过。这就是静默 bug。");
        System.out.println();

        // ========== 第二部分：迭代器 —— 同样的事，直接抛异常 ==========
        System.out.println("===== 第二部分：用迭代器，边遍历边删 =====");
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println("list = " + list + "，此时 modCount = " + get(list, "modCount"));

        Iterator<String> it = list.iterator();
        System.out.println("迭代器创建，它抄了一份 expectedModCount = " + get(it, "expectedModCount"));
        System.out.println("-------------------------------------------------------");

        try {
            while (it.hasNext()) {
                String s = it.next();
                System.out.println("next() 读到 [" + s + "]  cursor 现在 = " + get(it, "cursor"));
                if (s.equals("a")) {
                    System.out.println("   -> 调用 list.remove(\"a\")");
                    list.remove("a");
                    System.out.println("   -> list = " + list
                            + "  modCount 变成 " + get(list, "modCount")
                            + "，但迭代器手里还是 " + get(it, "expectedModCount"));
                    System.out.println("   -> 下一次 next() 一核对：" + get(list, "modCount")
                            + " != " + get(it, "expectedModCount") + "，抛异常");
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println();
            System.out.println("!!! 捕获到 " + e.getClass().getName());
            System.out.println("!!! 出错位置：" + e.getStackTrace()[0]);
        }
        System.out.println();

        // ========== 第三部分：正确写法 ==========
        System.out.println("===== 第三部分：正确写法 =====");

        List<String> b = new ArrayList<>();
        b.add("a");
        b.add("b");
        b.add("c");
        Iterator<String> it2 = b.iterator();
        while (it2.hasNext()) {
            if (it2.next().equals("a")) {
                it2.remove();     // 迭代器自己的 remove，会同步 expectedModCount
            }
        }
        System.out.println("iterator.remove() 之后： " + b);

        List<String> c = new ArrayList<>();
        c.add("a");
        c.add("b");
        c.add("c");
        c.removeIf("a"::equals);
        System.out.println("removeIf 之后：          " + c);
    }
}

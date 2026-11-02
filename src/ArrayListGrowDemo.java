import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 用反射把 ArrayList 内部的 elementData 数组长度打出来，
 * 亲眼看见「1.5 倍扩容」到底发生了几次、每次长多少。
 *
 * 运行：
 *   javac -encoding UTF-8 ArrayListGrowDemo.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED ArrayListGrowDemo
 */
public class ArrayListGrowDemo {
    public static void main(String[] args) throws Exception {
        Field f = ArrayList.class.getDeclaredField("elementData");
        f.setAccessible(true);

        ArrayList<Integer> list = new ArrayList<>();
        System.out.println("刚 new 出来，capacity = " + ((Object[]) f.get(list)).length);

        int last = -1;
        for (int i = 1; i <= 200; i++) {
            list.add(i);
            int cap = ((Object[]) f.get(list)).length;
            if (cap != last) {
                System.out.printf("加第 %3d 个元素 -> capacity = %3d%n", i, cap);
                last = cap;
            }
        }
    }
}

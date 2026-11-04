import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        Map<Integer, Integer> capMap = new HashMap<>();

    }
}
class Node {
    int value;
    Node next;
    Node(int value) { this.value = value; }
}

class LinkedList {
    Node head;
    int size;

    // 1. 尾巴上追加
    void add(int value) { /* TODO */
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
        }else{
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        size++;
    }

    // 2. 按值查找，返回下标，找不到返回 -1
    int indexOf(int value) { /* TODO */
        Node temp = head;
        int index = 0;
        while (temp != null) {
            if (temp.value == value) {
                return index;
            }
            temp = temp.next;
            index++;
        }
        return -1;
    }

    // 3. 在头部插入
    void addFirst(int value) { /* TODO */
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // 4. 打印全部
    void print() { /* TODO */
        while (head != null) {
            System.out.print(head.value + " ");
            head = head.next;
        }
    }
}
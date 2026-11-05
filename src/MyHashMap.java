/**
 * 手写 HashMap（简化版：数组 + 链表，暂不含红黑树）
 *
 * 对照的 JDK 源码：java.util.HashMap（JDK 17）
 *   - table 字段      第 340 行
 *   - hash()          第 336-339 行
 *   - putVal()        第 623-664 行
 *   - resize()        第 675-757 行
 *
 * 一共 6 个 TODO。按顺序写，写完一个跑一次测试。
 */
public class MyHashMap<K, V> {

    /** 默认容量，必须是 2 的幂 */
    static final int DEFAULT_CAPACITY = 16;
    /** 负载因子 */
    static final float LOAD_FACTOR = 0.75f;

    /** 链表节点 */
    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /** 桶数组 */
    Node<K, V>[] table;
    /** 实际元素个数 */
    int size;
    /** 扩容阈值 = capacity * LOAD_FACTOR */
    int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        // TODO 1: 把下面两行补上，然后删掉 throw
           table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
           threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        //throw new UnsupportedOperationException("TODO 1: 构造方法还没写");
    }

    /** 扰动函数：让高位也参与运算，减少冲突 */
    static final int hash(Object key) {
        // TODO 2: 提示 —— key 为 null 时返回 0；
        //         否则取 h = key.hashCode()，返回 h ^ (h >>> 16)
        //         注意：h 要声明成 int 局部变量，不能用 key.hashCode() 写两遍
        //throw new UnsupportedOperationException("TODO 2: hash() 还没写");
        if (key == null) {
            return 0;
        }
        int h =key.hashCode();
        return h ^ (h >>> 16);
    }

    /** 由 hash 算桶下标 */
    private int indexOf(int hash) {
        // TODO 3: 一行搞定，用 table.length - 1 和 hash 做与运算
        //throw new UnsupportedOperationException("TODO 3: indexOf() 还没写");
        return hash & (table.length - 1);
    }

    public V put(K key, V value) {
        // TODO 4: 按这六步写
        //   ① int h = hash(key);
        //   ② int i = indexOf(h);
        //   ③ if (table[i] == null) { table[i] = new Node<>(h, key, value, null); }
        //   ④ else 遍历链表 p，用 keyEquals(key, p.key) 判断是不是同一个 key：
        //        - 是：V old = p.value; p.value = value; return old;
        //        - 否：p = p.next 继续
        //   ⑤ 走到链表末尾还没找到 → 挂到末尾（需要先记住最后一个节点）
        //   ⑥ size++，如果 size > threshold 就 resize()
        //   返回值：新增返回 null
        //throw new UnsupportedOperationException("TODO 4: put() 还没写");
        int h = hash(key);
        int i = indexOf(h);
        if (table[i] == null) {
            table[i] = new Node<>(h, key, value, null);
        }
        else {
            Node<K, V> p = table[i];
            while (true) {
                if (keyEquals(key, p.key)) {
                    V old = p.value;
                    p.value = value;
                    return old;
                }
                if (p.next == null) {
                    p.next = new Node<>(h, key, value, null);
                    break;
                }
                p = p.next;
            }
        }
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    public V get(Object key) {
        // TODO 5: 提示
        //   int h = hash(key); int i = indexOf(h);
        //   从 table[i] 开始遍历，找到 keyEquals(key, p.key) 就 return p.value
        //   遍历完还没有 → return null
        //throw new UnsupportedOperationException("TODO 5: get() 还没写");
        int h = hash(key);
        int i = indexOf(h);
        for (Node<K, V> p = table[i]; p != null; p = p.next) {
            if (keyEquals(key, p.key)) {
                return p.value;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        // TODO 6: 简化版扩容（先不学 loHead/hiHead，等写完再看 JDK 的写法）
        //   ① 记下旧数组 oldTable 和旧容量 oldCap
        //   ② 新容量 newCap = oldCap * 2
        //   ③ table = (Node<K, V>[]) new Node[newCap];
        //   ④ threshold = (int) (newCap * LOAD_FACTOR);
        //   ⑤ size = 0;   ← 因为下面要用 put 重新插入
        //   ⑥ 遍历 oldTable 的每个桶，把每条链上的节点逐个 put(p.key, p.value)
        //      注意：遍历时先存下 p.next，否则 put 之后可能乱
        //throw new UnsupportedOperationException("TODO 6: resize() 还没写");
        Node<K, V>[] oldTable = table;
        int oldCap =  oldTable.length;
        int newCap = oldCap * 2;
        table = (Node<K, V>[]) new Node[newCap];
        threshold = (int) (newCap * LOAD_FACTOR);
        size = 0;
        for (int i = 0; i < oldCap; i++) {
            Node<K, V> p = oldTable[i];
            while (true) {
                if (p == null) {
                    break;
                }
                put(p.key, p.value);
                p = p.next;
            }
        }
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    /** 某个桶上的链表长度（测试用） */
    int bucketLength(int index) {
        int n = 0;
        for (Node<K, V> p = table[index]; p != null; p = p.next) {
            n++;
        }
        return n;
    }

    /** key 比较：既要处理 null，也要处理同一个对象 */
    static boolean keyEquals(Object k1, Object k2) {
        return k1 == k2 || (k1 != null && k1.equals(k2));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Node<K, V> head : table) {
            for (Node<K, V> p = head; p != null; p = p.next) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(p);
                first = false;
            }
        }
        return sb.append('}').toString();
    }
}

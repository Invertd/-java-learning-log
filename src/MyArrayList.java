import java.util.Arrays;

/**
 * 任务 4：手写一个自己的动态数组，行为对齐 java.util.ArrayList。
 *
 * 规则：
 *   1. 不要继承 AbstractList、不要 import java.util.ArrayList
 *   2. 每个 TODO 都自己写，写不出来再看 JDK 源码
 *   3. 写完跑 MyArrayListTest，全部 PASS 才算这一项完成
 *
 * 提示：你刚才看的 grow() 就是给 ensureCapacity / grow 用的。
 */
public class MyArrayList<E> {

    /** 底层数组。第一个元素要放进去，就是它。 */
    private Object[] elementData;

    /** 装了几个元素（不是数组长度！） */
    private int size;

    /** 默认容量，和 JDK 保持一致 */
    private static final int DEFAULT_CAPACITY = 10;

    /** 空数组常量，new 出来还没 add 的时候用它 */
    private static final Object[] EMPTY = {};

    /** 无参构造：数组给空，第一次 add 才变成 10 */
    public MyArrayList() {
        // TODO 1: elementData = EMPTY;
        this.elementData = EMPTY;

    }

    /** 指定容量构造：容量 < 0 要抛 IllegalArgumentException */
    public MyArrayList(int initialCapacity) {
        // TODO 2: initialCapacity > 0 → new Object[initialCapacity]
        //         initialCapacity == 0 → EMPTY
        //         initialCapacity < 0  → throw new IllegalArgumentException(...)
        if(initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        }else if(initialCapacity == 0) {
            this.elementData = EMPTY;
        }else {
            throw new IllegalArgumentException("Illegal Capacity");
        }

    }

    public int size() {
        // TODO 3
        return this.size;
//        throw new UnsupportedOperationException("TODO 3: size() 还没写");
    }

    public boolean isEmpty() {
        // TODO 4
        return this.size == 0;
//        throw new UnsupportedOperationException("TODO 4: isEmpty() 还没写");
    }

    /** 追加到末尾 */
    public boolean add(E e) {
        // TODO 5: 先 ensureCapacity(size + 1)，再放进 elementData[size++]
        ensureCapacity(size + 1);
        elementData[size++] = e;
        return true;
//        throw new UnsupportedOperationException("TODO 5: add() 还没写");
    }

    /** 按下标取，越界抛 IndexOutOfBoundsException */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        // TODO 6: 先检查下标范围，再 return (E) elementData[index]
        if (index >= 0 && index < this.size) {
            return (E) elementData[index];
        }else{
            throw new IndexOutOfBoundsException("Illegal Index");
        }
//        throw new UnsupportedOperationException("TODO 6: get() 还没写");
    }

    /** 按下标改，返回旧值 */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        // TODO 7
        if (index >= 0 && index < this.size) {
            E old = (E) elementData[index];
            elementData[index] = element;
            return (E) old;
        }else{
            throw new IndexOutOfBoundsException("Illegal Index");
        }
//        throw new UnsupportedOperationException("TODO 7: set() 还没写");
    }

    /** 按下标删，后面的元素全部前移一位，返回被删的值 */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        // TODO 8: 记住旧值 → System.arraycopy 前移 → 把最后一位置 null（帮 GC）→ size--
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Illegal Index");
        }
        E old = (E) elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }
        elementData[--size] = null;
        return old;

//        throw new UnsupportedOperationException("TODO 8: remove() 还没写");
    }

    /** 保证至少能装 minCapacity 个元素 */
    private void ensureCapacity(int minCapacity) {
        // TODO 9: 如果 minCapacity > elementData.length 就 grow
        if (minCapacity > this.elementData.length) {
            grow(minCapacity);
        }
    }

    /** 真正的扩容：就是你在 JDK 源码里看到的那个方法 */
    private void grow(int minCapacity) {
        // TODO 10:
        //   int oldCapacity = elementData.length;
        //   int newCapacity = oldCapacity + (oldCapacity >> 1);   // 1.5 倍
        //   if (newCapacity < minCapacity) newCapacity = minCapacity;
        //   如果 oldCapacity == 0，第一次扩容要给 DEFAULT_CAPACITY
        //   elementData = Arrays.copyOf(elementData, newCapacity);
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (oldCapacity == 0){
            newCapacity = DEFAULT_CAPACITY;
        } else if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        elementData = Arrays.copyOf(elementData, newCapacity);
//        throw new UnsupportedOperationException("TODO 10: grow() 还没写");
    }

    // ================= 以下不用改，用来观察内部状态 =================

    /** 给测试用：返回当前底层数组长度 */
    @SuppressWarnings("unchecked")
    public int capacity() {
        // TODO 11（很简单，一行）
        return this.elementData.length;
//        throw new UnsupportedOperationException("TODO 11: capacity() 还没写");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elementData[i]);
        }
        return sb.append("]").toString();
    }
}

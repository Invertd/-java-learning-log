# -*- coding: utf-8 -*-
"""生成 java-learning-log 的每日/每周任务文件。

用法：
    python tools/gen_days.py          # 生成全部（已存在的不覆盖）
    python tools/gen_days.py --force  # 强制重写全部
"""
import datetime
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
DAYS_DIR = ROOT / "days"
WEEKS_DIR = ROOT / "weeks"

WD_CN = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
ROLE = {1: "新课日", 2: "新课日", 3: "轻量日", 4: "代码日", 5: "算法日", 6: "整块日", 7: "复盘日"}
HOURS = {1: 7.0, 2: 7.0, 3: 3.5, 4: 6.0, 5: 8.0, 6: 9.0, 7: 6.0}

# ---------------------------------------------------------------- 课程表
# 依据 2026-2027-1 第 11 周课表（左→右读作 周一→周五）
# 返回某天上午/下午/晚上的课（仅用于提示，不影响任务本身）
CLASSES = {
    1: ["1-2节 习近平新时代…概论（08:30-10:10）"],
    2: ["1-2节 算法分析与设计（08:30-10:10）@弘毅楼B207"],
    3: ["3-4节 习近平新时代…概论（10:30-12:10）",
        "5-6节 移动应用开发技术（14:20-16:00）",
        "7-8节 操作系统基础（16:20-18:00）@弘毅楼A203",
        "9节 移动应用开发技术（19:00-19:45）"],
    4: ["1-4节 软件设计模式（08:30-12:10）"],
    5: ["5-6节 操作系统基础（14:20-16:00）@智526"],
    6: [],
    7: [],
}

# ---------------------------------------------------------------- 课程内容
CURRICULUM = [
    {
        "id": "W1",
        "title": "集合与 HashMap",
        "goal": "能画着图讲清 ArrayList 扩容和 HashMap 的 put / resize 全过程",
        "start": "2026-11-03",
        "end": "2026-11-08",
        "days": {
            2: ("画出 HashMap 的 put 流程，手写 ArrayList",
                [("装环境：JDK 17 + IDEA + Git + LeetCode 账号，跑通 Hello World", 2.0),
                 ("建 GitHub 仓库 java-learning-log，README 写目标，推第一个 commit", 0.5),
                 ("读 ArrayList 源码：grow() 的 1.5 倍扩容、modCount、fail-fast", 1.5),
                 ("手写 MyArrayList：数组 + 自动扩容 + get/add/remove", 2.0),
                 ("算法：两数之和、字母异位词分组", 0.8)],
                ["ArrayList 扩容为什么是 1.5 倍？fail-fast 是什么，怎么触发？"]),
            3: ("读通 HashMap.putVal，能背下来",
                [("读 HashMap 源码：hash() 扰动、putVal() 的 5 个分支", 1.5),
                 ("画 HashMap 结构图：数组 + 链表 + 红黑树，标出 4 个阈值常量", 0.5),
                 ("把今天课上《操作系统基础》的概念做成 Anki 卡", 0.5),
                 ("算法：最长连续序列", 0.5)],
                ["putVal 的完整流程，从计算 hash 到插入完成，每一步在做什么？"]),
            4: ("手写 MyHashMap 第一版并能跑通",
                [("读 HashMap 源码：resize() 的 loHead/hiHead 拆分、treeifyBin()", 1.0),
                 ("手写 MyHashMap 第一版：数组 + 链表，put / get", 2.5),
                 ("算法：移动零、盛最多水的容器", 0.8),
                 ("把《软件设计模式》课上讲到的模式记进卡片（今天 4 节连堂）", 0.5)],
                ["HashMap 容量为什么必须是 2 的幂？(n-1)&hash 省掉了什么？"]),
            5: ("MyHashMap 能自动扩容并且正确",
                [("给 MyHashMap 补 remove() 和 resize() 扩容", 2.0),
                 ("算法专题：双指针 / 滑动窗口 6 题（连续刷，不换类型）", 4.0),
                 ("整理本周 commit，README 里勾掉已完成专题", 0.5)],
                ["滑动窗口的通用模板是什么？什么时候窗口右指针动、什么时候左指针动？"]),
            6: ("MyHashMap 通过 1 万条随机数据测试 + LRU 两种写法",
                [("MyHashMap 用 1 万条随机 key-value 测试，全部 get 命中", 1.5),
                 ("手写 LRU：LinkedHashMap 版（LeetCode 146）", 1.0),
                 ("手写 LRU：双向链表 + HashMap 版", 1.5),
                 ("写笔记《HashMap 十问》（为什么 2 的幂 / 0.75 / 阈值 8 / 1.7 死循环 / 1.8 改尾插 …）", 2.0),
                 ("算法：三数之和、无重复字符的最长子串、找到所有字母异位词", 2.0)],
                ["HashMap 1.7 的并发扩容为什么会死循环？1.8 是怎么改的？"]),
            7: ("本周知识能连续讲 10 分钟不卡",
                [("周复盘：对着手机录 10 分钟，讲「集合框架 + HashMap」", 1.0),
                 ("补做本周没完成的任务", 2.0),
                 ("整理 commit 记录，更新 README 的进度表", 0.5),
                 ("算法：螺旋矩阵、轮转数组", 1.0)],
                ["如果面试官问「讲讲你最熟的集合」，你会怎么组织这 10 分钟？"]),
        },
    },
    {
        "id": "W2",
        "title": "集合进阶 + 线程基础",
        "goal": "能对比 HashMap / ConcurrentHashMap，能写生产者消费者",
        "start": "2026-11-09",
        "end": "2026-11-15",
        "days": {
            1: ("讲清 ConcurrentHashMap 1.8 和 HashMap 的 3 个关键区别",
                [("LinkedHashMap 源码：双向链表维护顺序、accessOrder 实现 LRU", 1.5),
                 ("TreeMap 红黑树基本性质（只要求能讲，不求手写）", 1.0),
                 ("读 ConcurrentHashMap 1.8：CAS 插桶 + synchronized 锁桶头", 2.0),
                 ("算法：环形链表、相交链表", 1.0)],
                ["ConcurrentHashMap 1.8 为什么用 synchronized 锁桶头，而不是 Segment？"]),
            2: ("线程 6 种状态能画出来，wait/sleep 区别能讲",
                [("线程基础：6 种状态、start vs run、interrupt、join、sleep vs wait", 2.0),
                 ("写 5 个线程 demo：创建、中断、join、守护线程、异常处理", 2.0),
                 ("算法课当刷题课：把课上讲的算法手写一遍", 1.0),
                 ("算法：合并两个有序链表、删除倒数第 N 个节点", 1.0)],
                ["sleep 和 wait 的 5 个区别？为什么 wait 必须放在 synchronized 里？"]),
            3: ("wait/notify 机制讲得清",
                [("wait/notify 原理 + 为什么要在循环里判断条件（虚假唤醒）", 1.0),
                 ("把《操作系统基础》今天讲的进程/线程调度做成 Anki 卡", 0.5),
                 ("算法：两数相加", 0.5)],
                ["什么是虚假唤醒？为什么 wait 要用 while 而不是 if？"]),
            4: ("手写生产者消费者，两种实现都跑通",
                [("手写 wait/notify 生产者消费者（有界队列，不丢不重）", 2.0),
                 ("用 ReentrantLock + Condition 再实现一遍", 1.5),
                 ("笔记：两种实现的对比（锁粒度、可读性、能不能多条件）", 1.0),
                 ("把《软件设计模式》的单例、工厂写进卡片", 0.5)],
                ["synchronized 版的消费者，为什么唤醒要用 notifyAll 而不是 notify？"]),
            5: ("链表专题一次过 8 题",
                [("算法专题：链表 8 题（反转、K 个一组、回文、排序链表、合并 K 个、随机链表复制…）", 4.0),
                 ("整理 MyHashMap / LRU 的测试用例，补 README", 1.0),
                 ("更新错题本", 0.5)],
                ["链表题的通用套路有哪些？（哑结点、快慢指针、反转、哨兵）"]),
            6: ("ThreadLocal 讲得清，能说内存泄漏原因",
                [("ThreadLocal 源码：ThreadLocalMap、弱引用 key、为什么泄漏", 1.5),
                 ("手写 MyHashSet（基于 MyHashMap），过 LeetCode 705", 1.0),
                 ("算法：链表专题补 3 题", 1.5),
                 ("笔记《ThreadLocal 五问》", 1.5)],
                ["ThreadLocalMap 的 key 是弱引用，value 是强引用，为什么会导致内存泄漏？"]),
            7: ("本周知识能连续讲 10 分钟",
                [("周复盘：录音讲「集合进阶 + 线程基础」", 1.0),
                 ("补做未完成任务", 2.0),
                 ("更新 README 进度 + 整理 commit", 0.5),
                 ("算法：LRU 再来一遍（闭卷）", 0.5)],
                ["讲讲你手写的线程池和 JDK 线程池还差在哪？"]),
        },
    },
    {
        "id": "W3",
        "title": "synchronized / volatile / CAS",
        "goal": "能从对象头讲到锁升级，能讲清 volatile 的边界",
        "start": "2026-11-16",
        "end": "2026-11-22",
        "days": {
            1: ("锁升级的 4 个阶段能画图讲",
                [("对象头 Mark Word 结构（32/64 位，锁标志位）", 1.5),
                 ("Monitor（monitorenter/monitorexit）+ 锁升级：无锁→偏向→轻量级→重量级", 2.0),
                 ("JDK 15 起偏向锁默认关闭的原因", 0.5),
                 ("算法：反转链表、K 个一组翻转链表", 1.0)],
                ["偏向锁→轻量级锁→重量级锁，每次升级的触发条件是什么？"]),
            2: ("volatile 能讲清「保证什么、不保证什么」",
                [("JMM + 主内存/工作内存 + happens-before", 1.5),
                 ("volatile 的可见性、禁止重排、内存屏障（4 种）", 1.5),
                 ("写 demo：volatile 可见性演示、volatile 不保证原子性演示", 1.5),
                 ("算法：删除倒数第 N 个节点、相交链表（闭卷重写）", 0.8)],
                ["volatile 能保证原子性吗？用 i++ 举个例子。"]),
            3: ("CAS 和 ABA 讲得清",
                [("CAS 原理 + Unsafe.compareAndSwapInt + 自旋", 1.0),
                 ("ABA 问题 + AtomicStampedReference 怎么解决", 0.5),
                 ("操作系统课：把今天讲的内存管理做成 Anki 卡", 0.5),
                 ("算法：最长回文子串", 0.5)],
                ["ABA 在什么业务场景下会造成真实 bug？举一个例子。"]),
            4: ("手写双重检查单例和自旋锁",
                [("手写双重检查单例，并解释 volatile 为什么不能省", 1.0),
                 ("手写一个简易自旋锁（AtomicReference + CAS）", 1.5),
                 ("手写 AtomicInteger 的简化版（CAS 版本）", 1.5),
                 ("把《软件设计模式》的代理模式 / 观察者模式写进卡片，想清楚 Spring 里哪里用到", 0.5)],
                ["DCL 单例去掉 volatile，会出现什么具体的错误？"]),
            5: ("双指针 / 滑动窗口再刷 8 题",
                [("算法专题：滑动窗口 + 双指针 8 题", 4.0),
                 ("整理本周 3 个 demo 的 README", 1.0),
                 ("错题本更新", 0.5)],
                ["什么样的题一眼就能看出是滑动窗口？"]),
            6: ("《并发 20 问》写出前 10 问",
                [("写笔记《并发 20 问》第 1–10 问（线程、synchronized、volatile、CAS）", 2.0),
                 ("算法：字符串专题 3 题", 1.5),
                 ("把 W1–W3 的 Anki 卡片过一遍", 1.0)],
                ["synchronized 和 ReentrantLock 各自适合什么场景？"]),
            7: ("并发基础部分能自测及格",
                [("周复盘：录音讲「锁与内存可见性」", 1.0),
                 ("自测：把《并发 20 问》前 10 问自问自答并录音", 1.0),
                 ("补做未完成任务", 2.0),
                 ("更新 README 进度 + commit 整理", 0.5)],
                ["如果面试官问「你项目里哪里用到并发」，你能答什么？"]),
        },
    },
    {
        "id": "W4",
        "title": "AQS / ReentrantLock / 线程池",
        "goal": "能手写线程池，能讲清 AQS 和线程池参数怎么定",
        "start": "2026-11-23",
        "end": "2026-11-29",
        "days": {
            1: ("AQS 的 state / 队列 / 模板方法讲得清",
                [("AQS：state、CLH 变体等待队列、独占 vs 共享、tryAcquire/tryRelease 模板方法", 2.5),
                 ("ReentrantLock 公平锁 vs 非公平锁的代码差异", 1.5),
                 ("算法：二叉树前中后序遍历（递归 + 迭代）", 1.0)],
                ["AQS 的模板方法设计，和「手写一个锁」是什么关系？"]),
            2: ("Condition 和线程池 7 参数讲得清",
                [("Condition 原理（await/signal 的双队列）", 1.0),
                 ("线程池 7 个参数 + 执行流程（核心线程→队列→最大线程→拒绝）", 1.5),
                 ("4 种拒绝策略 + 为什么要自定义", 1.0),
                 ("算法：二叉树层序遍历、锯齿层序遍历", 1.0)],
                ["线程池的 7 个参数，任务进来后具体按什么顺序走？"]),
            3: ("能讲清 Executors 为什么不推荐",
                [("Executors 4 个工厂方法的坑（FixedThreadPool / SingleThread 的队列 OOM）", 1.0),
                 ("操作系统课：把页面置换算法做成卡片", 0.5),
                 ("算法：二叉树 1 题", 0.5)],
                ["newFixedThreadPool 为什么会 OOM？出在哪个参数上？"]),
            4: ("手写线程池能跑通",
                [("手写简易线程池：阻塞队列 + 核心线程 + 拒绝策略", 3.0),
                 ("线程池参数怎么定：CPU 密集型 N+1、IO 密集型 2N，以及为什么要压测", 1.0),
                 ("给手写线程池写一个 1000 任务的压测 demo", 1.0)],
                ["核心线程数设 8，队列设无界，会发生什么？"]),
            5: ("二叉树专题一次过 8 题",
                [("算法专题：二叉树 8 题（最大深度、对称、路径总和、最近公共祖先、构造、验证 BST…）", 4.0),
                 ("整理手写线程池的 README", 1.0),
                 ("错题本", 0.5)],
                ["二叉树的题，什么时候用递归、什么时候用迭代？"]),
            6: ("《并发 20 问》写完 20 问",
                [("CountDownLatch / CyclicBarrier / Semaphore 三个工具类的区别（各写一个 demo）", 1.5),
                 ("写笔记《并发 20 问》第 11–20 问（AQS、锁、线程池、工具类）", 2.0),
                 ("算法：二叉树补 3 题", 1.5)],
                ["CountDownLatch 和 CyclicBarrier 的本质区别？"]),
            7: ("第一次月度自测",
                [("**月度自测**：闭卷手写 HashMap / LRU / 线程池 / 生产者消费者（限时 2 小时）", 2.0),
                 ("自测复盘：哪些写不出来，记下来", 1.0),
                 ("补做未完成任务", 2.0),
                 ("更新 README + 更新进度表 + 和计划对照", 0.5)],
                ["一个月过去了，你现在能讲 10 分钟的技术点有几个？"]),
        },
    },
    {
        "id": "W5",
        "title": "JVM",
        "goal": "能讲清内存结构 / GC / 类加载，并会用工具排查一次问题",
        "start": "2026-11-30",
        "end": "2026-12-06",
        "days": {
            1: ("JVM 内存结构和对象创建讲得清",
                [("运行时数据区（堆/栈/方法区/程序计数器/本地方法栈）+ JDK 8 后的变化", 2.0),
                 ("对象创建过程 + 内存布局（对象头/实例数据/对齐填充）+ 逃逸分析", 1.5),
                 ("算法：验证二叉树 + 1 题", 1.0)],
                ["对象在堆里长什么样？Mark Word 里存了什么？"]),
            2: ("GC 的分代和三色标记讲得清",
                [("GC 基础：分代假设、可达性分析、四种引用", 2.0),
                 ("三色标记 + 读写屏障 + 并发漏标问题", 1.5),
                 ("CMS vs G1 vs ZGC 的取舍", 1.0)],
                ["CMS 为什么被 G1 取代？G1 的 Region 解决了什么问题？"]),
            3: ("双亲委派能讲 + 能说出 3 种打破场景",
                [("类加载过程（加载/验证/准备/解析/初始化）+ 双亲委派", 1.5),
                 ("打破双亲委派的 3 个场景：Tomcat、SPI、热部署", 0.5),
                 ("操作系统课：IO 模型做成卡片", 0.5),
                 ("算法：1 题", 0.5)],
                ["为什么要双亲委派？打破它会有什么后果？"]),
            4: ("亲手造一次 OOM 和死锁，并用工具定位",
                [("写一个 OOM: Java heap space（-Xmx 设小），用 jmap + MAT 分析", 1.5),
                 ("写一个死锁，用 jstack 定位到具体行号", 1.5),
                 ("把排查过程完整记录下来（截图 + 命令 + 结论）", 1.5),
                 ("顺带试一下 arthas 的 dashboard / thread", 0.5)],
                ["jstack 输出里，怎么看出哪两个线程死锁了？"]),
            5: ("二叉树 / 递归 8 题",
                [("算法专题：递归 + 二叉树 8 题", 4.0),
                 ("整理 JVM 实验记录成一篇笔记", 1.0),
                 ("错题本", 0.5)],
                ["递归题的「三步走」是哪三步？"]),
            6: ("《JVM 十五问》写完",
                [("写笔记《JVM 十五问》", 2.0),
                 ("jstat / jinfo / jmap 各跑一遍，记录输出含义", 1.5),
                 ("算法：二叉树补 3 题", 1.5)],
                ["线上 CPU 100% 了，你的排查步骤是什么？"]),
            7: ("JVM 能自测及格",
                [("周复盘：录音讲「JVM 内存 + GC」", 1.0),
                 ("自测《JVM 十五问》自问自答", 1.0),
                 ("补做未完成任务", 2.0),
                 ("更新 README + commit", 0.5)],
                ["OOM 排查的完整链路是什么？"]),
        },
    },
    {
        "id": "W6",
        "title": "IO / NIO / 网络",
        "goal": "能讲清 epoll 和零拷贝，能写一个 NIO server",
        "start": "2026-12-07",
        "end": "2026-12-13",
        "days": {
            1: ("BIO/NIO/AIO 的区别讲得清",
                [("BIO / NIO / AIO 的模型差异 + 各自适用场景", 1.5),
                 ("Channel / Buffer / Selector 三件套 + NIO 的 5 个核心组件", 1.5),
                 ("算法：栈 / 队列 2 题", 1.0)],
                ["NIO 的「非阻塞」到底非阻塞在哪一步？"]),
            2: ("epoll 和零拷贝讲得清",
                [("select / poll / epoll 的区别（数据结构、时间复杂度、拷贝次数）", 1.5),
                 ("Reactor 模式（单 Reactor / 主从 Reactor）", 1.0),
                 ("零拷贝：mmap 和 sendfile 各解决了什么", 1.0),
                 ("算法：有效的括号、最小栈", 1.0)],
                ["epoll 为什么比 select 高效？三个具体原因。"]),
            3: ("TCP 握手挥手能画图讲",
                [("TCP 三次握手 / 四次挥手 / 为什么是 3 次", 1.5),
                 ("TIME_WAIT 过多的原因和处理 + 粘包拆包", 0.5),
                 ("操作系统课：把 IO 多路复用做成卡片", 0.5),
                 ("算法：1 题", 0.5)],
                ["为什么要四次挥手？三次行不行？"]),
            4: ("写出 NIO Selector 版 echo server",
                [("手写 NIO 单线程 Selector echo server", 3.0),
                 ("写一个粘包问题的 demo（固定长度 / 分隔符两种解决）", 1.0),
                 ("给自己的 server 写 README", 0.5)],
                ["你这个 echo server 用一个线程能扛多少连接？为什么？"]),
            5: ("栈 / 单调栈 8 题",
                [("算法专题：栈 + 单调栈 8 题（每日温度、柱状图最大矩形、接雨水…）", 4.0),
                 ("整理本周 demo", 1.0),
                 ("错题本", 0.5)],
                ["单调栈解决的是哪一类问题？"]),
            6: ("《网络十问》写完",
                [("HTTP/1.1 vs 2 vs 3 的演进 + HTTPS 握手过程", 2.0),
                 ("写笔记《网络十问》", 1.5),
                 ("算法：栈补 3 题", 1.5)],
                ["HTTPS 握手要几次往返？为什么要对称 + 非对称两套？"]),
            7: ("网络部分能自测及格",
                [("周复盘：录音讲「IO 模型 + TCP」", 1.0),
                 ("自测《网络十问》", 1.0),
                 ("补做未完成任务", 2.0),
                 ("更新 README + 准备期末复习材料", 0.5)],
                ["一次 HTTP 请求从浏览器到服务端，中间经过了什么？"]),
        },
    },
    {
        "id": "W7",
        "title": "闭卷车轮战 + 期末复习启动",
        "goal": "把前 6 周的东西闭卷重写一遍，找出真实短板",
        "start": "2026-12-14",
        "end": "2026-12-20",
        "days": {
            1: ("闭卷重写 MyHashMap 和 LRU",
                [("**闭卷**重写 MyHashMap（不看任何资料，限时 60 分钟）", 1.5),
                 ("**闭卷**重写 LRU（两种写法，限时 60 分钟）", 1.5),
                 ("对比原版，记录差距", 0.5),
                 ("算法：1 题", 1.0)],
                ["闭卷写 MyHashMap 时，你卡在哪一步？"]),
            2: ("闭卷重写线程池和生产者消费者",
                [("**闭卷**重写线程池（限时 90 分钟）", 2.0),
                 ("**闭卷**重写生产者消费者（限时 40 分钟）", 1.0),
                 ("对比原版，记录差距", 0.5),
                 ("算法：1 题", 1.0)],
                ["手写线程池最容易被忘记的是哪个参数？"]),
            3: ("八股自测 30 题，找出薄弱项",
                [("八股自测 30 题（集合/并发/JVM/网络各 7–8 题），录音作答", 1.5),
                 ("把答不出的题目标红，列成补漏清单", 0.5),
                 ("算法：1 题", 0.5)],
                ["这 30 题里你答上了几题？哪一科最弱？"]),
            4: ("做一次完整的模拟面试",
                [("**模拟面试**：自问自答 40 分钟并录音（自我介绍 + 项目 + 八股）", 1.0),
                 ("听回放，记录卡壳的地方", 0.5),
                 ("针对卡壳的 5 个点逐个补掉", 3.0)],
                ["你的自我介绍能压到 2 分钟以内吗？"]),
            5: ("回溯专题 6 题",
                [("算法专题：回溯 6 题（全排列、子集、组合总和、N 皇后…）", 4.0),
                 ("整理 W1–W7 的笔记成目录索引", 1.0),
                 ("错题本", 0.5)],
                ["回溯的模板是什么？剪枝在哪里加？"]),
            6: ("期末复习启动",
                [("期末复习（先把最可能挂的那门过一遍）", 4.0),
                 ("八股卡片全部过一遍，删掉已经熟的", 2.0),
                 ("算法：2 题", 1.0)],
                ["期末考试和刷题，你怎么分配接下来三周？"]),
            7: ("W1–W7 总复盘，定寒假计划",
                [("**总复盘**：这 7 周做完了什么、什么没做完、为什么", 1.0),
                 ("录音讲 15 分钟，检验能不能把「集合/并发/JVM/网络」串起来", 1.0),
                 ("把寒假的 7 个周主题写进 weeks/ 目录，标注每天大概做什么", 1.5),
                 ("整理 commit 曲线 + README 总进度", 0.5)],
                ["如果明天就去面试，你觉得自己能过几轮？"]),
        },
    },
]

# ---------------------------------------------------------------- 考试周 / 寒假
BLOCKS = [
    {
        "id": "X1", "kind": "week", "title": "期末考试周 1（维持型）",
        "start": "2026-12-21", "end": "2026-12-27",
        "intro": "考试周。**不学新内容**，只维持手感。",
        "items": [("每天 Anki 20 张（八股卡）", "0.4h/天"),
                  ("每天算法 1 题（只做已做过的类型）", "0.4h/天"),
                  ("期末复习为主", "其余时间")],
        "recount": ["考试周也别断：每天 1 题，20 天不碰算法会掉得很明显。"],
    },
    {
        "id": "X2", "kind": "week", "title": "期末考试周 2（维持型）",
        "start": "2026-12-28", "end": "2027-01-03",
        "intro": "考试周。**不学新内容**，只维持手感。",
        "items": [("每天 Anki 20 张", "0.4h/天"),
                  ("每天算法 1 题", "0.4h/天"),
                  ("期末复习为主", "其余时间")],
        "recount": ["这周只做一件事：别挂科。"],
    },
    {
        "id": "X3", "kind": "week", "title": "期末考试周 3（收尾）",
        "start": "2027-01-04", "end": "2027-01-11",
        "intro": "考试收尾 + 寒假启动准备。",
        "items": [("每天 Anki 20 张 + 算法 1 题", "0.8h/天"),
                  ("考完的科目立刻开始寒假计划", "—"),
                  ("整理寒假的 MySQL / Redis 学习资料", "2h")],
        "recount": ["寒假 6.5 周决定你 3 月能不能投实习，别在寒假掉线。"],
    },
    {
        "id": "W8", "kind": "week", "title": "寒假 1：MySQL 索引",
        "start": "2027-01-12", "end": "2027-01-18",
        "intro": "寒假冲刺开始，目标 45–50h/周。",
        "items": [("B+ 树为什么不用 B 树 / 红黑树", "2h"),
                  ("聚簇索引 vs 二级索引、回表、覆盖索引", "2h"),
                  ("最左前缀 + 索引失效的 8 种情况", "2h"),
                  ("explain 每个字段能讲（type/key/rows/Extra）", "2h"),
                  ("造 100 万行数据，写慢查询并加索引优化（保存前后 explain 截图）", "6h"),
                  ("算法：每日 2 题，累计到 180 题", "—")],
        "recount": ["为什么 MySQL 用 B+ 树而不是 B 树？", "什么情况下索引会失效？说出 5 种。"],
    },
    {
        "id": "W9", "kind": "week", "title": "寒假 2：MySQL 事务 / 锁 / 优化",
        "start": "2027-01-19", "end": "2027-01-25",
        "intro": "",
        "items": [("事务 ACID、4 种隔离级别、脏读/不可重复读/幻读", "2h"),
                  ("MVCC（undo log + ReadView）", "2h"),
                  ("next-key lock / 间隙锁、select for update 加锁范围", "2h"),
                  ("redo log / undo log / binlog + 两阶段提交", "2h"),
                  ("慢查询定位、深分页优化、大表 DDL", "3h"),
                  ("算法：每日 2 题", "—")],
        "recount": ["RR 隔离级别下，幻读被完全解决了吗？", "一条 update 语句，redo log 和 binlog 怎么保证一致？"],
    },
    {
        "id": "W10", "kind": "week", "title": "寒假 3：Redis",
        "start": "2027-01-26", "end": "2027-02-01",
        "intro": "",
        "items": [("5 种数据结构的底层（SDS/ziplist/quicklist/skiplist）", "2h"),
                  ("单线程为什么快、6.0 的多线程是干什么的", "1.5h"),
                  ("RDB vs AOF、混合持久化、过期与淘汰策略", "2h"),
                  ("缓存穿透/击穿/雪崩 + 各自的解法", "2h"),
                  ("分布式锁：setnx 的坑 → Redisson 看门狗", "2h"),
                  ("主从/哨兵/Cluster", "1.5h"),
                  ("算法：每日 2 题，累计到 210 题", "—")],
        "recount": ["缓存击穿和缓存穿透的区别？", "Redisson 的看门狗解决了什么问题？"],
    },
    {
        "id": "W11", "kind": "week", "title": "寒假 4：Spring 原理",
        "start": "2027-02-02", "end": "2027-02-08",
        "intro": "注意：2027 年春节在 2 月 6 日前后，除夕到初三可以休息 3 天。",
        "items": [("IoC 容器启动流程 + Bean 生命周期（11 步）", "2.5h"),
                  ("循环依赖三级缓存（为什么需要第三级）", "2h"),
                  ("AOP：JDK 动态代理 vs CGLIB、什么时候用哪个", "2h"),
                  ("@Transactional 失效的 6 种场景", "2h"),
                  ("Spring MVC 请求流程", "1.5h"),
                  ("算法：每日 2 题", "—")],
        "recount": ["三级缓存里第三级 singletonFactories 为什么必须要？", "@Transactional 在什么情况下会失效？"],
    },
    {
        "id": "W12", "kind": "week", "title": "寒假 5：项目改造（多级缓存 + MQ）",
        "start": "2027-02-09", "end": "2027-02-15",
        "intro": "从这周开始进入项目改造，目标是让简历上出现数字。",
        "items": [("Caffeine 本地缓存 + Redis 二级缓存", "4h"),
                  ("接入 RabbitMQ / RocketMQ：发布后异步生成摘要、更新计数", "6h"),
                  ("点赞/计数用 Redis Hash + Lua，定时批量刷库", "4h"),
                  ("JMeter 500 并发压测，产出 QPS / P99 / 命中率 / 优化前后对比表", "6h"),
                  ("算法：每日 2 题，累计到 240 题", "—")],
        "recount": ["缓存一致性问题你怎么解决的？为什么用「先更新库再删缓存」？"],
    },
    {
        "id": "W13", "kind": "week", "title": "寒假 6：RAG 模块 + 评测",
        "start": "2027-02-16", "end": "2027-02-22",
        "intro": "给项目嵌一层 AI 能力，做出可评估的检索链路。",
        "items": [("文章切块 + embedding + 向量库（pgvector 或 Milvus standalone）", "6h"),
                  ("查询改写 + top-k 检索 + rerank", "6h"),
                  ("构造 30–50 条 QA 测试集，算检索命中率 / 回答准确率 / 幻觉率", "5h"),
                  ("记录优化前后的三个指标（这是简历上最值钱的一句话）", "2h"),
                  ("算法：每日 2 题", "—")],
        "recount": ["你的 RAG 检索命中率从多少提到多少？怎么测的？"],
    },
    {
        "id": "W14", "kind": "week", "title": "寒假 7：开源化 + 简历重写 + 投递准备",
        "start": "2027-02-23", "end": "2027-02-28",
        "intro": "把前 13 周的成果变成一个「可以被验证」的仓库和一份简历。",
        "items": [("项目仓库整理：README + 架构图 + 压测报告 + commit 历史", "6h"),
                  ("写 2 篇技术博客（HashMap 手写、压测优化），发掘金/CSDN", "5h"),
                  ("简历重写：删「Java（熟练）」，3 条带数字的项目 bullet", "3h"),
                  ("整理目标公司清单（深圳 A/B 层 + 广州 A/B 层）到一份表", "2h"),
                  ("算法：累计 250 题，开始做 Hot100 二刷", "—")],
        "recount": ["你的项目一句话介绍是什么？30 秒版本。"],
    },
]


def render_day(day_no, date, week, weekday, title_goal, tasks, recount):
    goal = title_goal
    lines = []
    lines.append(f"# Day {day_no} · {date} · {WD_CN[weekday - 1]}")
    lines.append("")
    lines.append(f"**{week['id']} {week['title']}** ｜ **{ROLE[weekday]}** ｜ **可投入约 {HOURS[weekday]}h**")
    lines.append("")
    cls = CLASSES.get(weekday) or []
    if cls:
        lines.append("> 今天有课：" + "；".join(cls))
        lines.append("> 课内任务：把课上讲的概念当场做成 Anki 卡片")
    else:
        lines.append("> 今天没课，整块时间，别浪费。")
    lines.append("")
    lines.append("## 今日目标")
    lines.append("")
    lines.append(goal)
    lines.append("")
    lines.append("## 任务清单")
    lines.append("")
    total = 0.0
    for i, (text, h) in enumerate(tasks, 1):
        total += h
        lines.append(f"- [ ] **{i}.** {text}（{h}h）")
    lines.append("")
    lines.append(f"合计约 **{total:g}h**")
    lines.append("")
    lines.append("## 每日固定动作（雷打不动，不计入上面）")
    lines.append("")
    lines.append("- [ ] 07:00–08:20 晨间：Anki 20 张 + 1 题算法")
    lines.append("- [ ] 22:00–22:40 写笔记 + **口述录音 3 分钟**")
    lines.append("- [ ] 22:40–23:20 再 1 题 + 记错题本")
    lines.append("")
    lines.append("## 今晚口述题（对着手机录 3 分钟）")
    lines.append("")
    for q in recount:
        lines.append(f"- {q}")
    lines.append("")
    lines.append("## 收尾")
    lines.append("")
    lines.append("- [ ] 未完成的任务已顺延到明天，并在 logs 里写了原因")
    lines.append("")
    return "\n".join(lines)


def render_week(block):
    lines = []
    lines.append(f"# {block['id']} · {block['title']}")
    lines.append("")
    lines.append(f"**{block['start']} → {block['end']}**")
    lines.append("")
    if block.get("intro"):
        lines.append(block["intro"])
        lines.append("")
    lines.append("## 本周任务")
    lines.append("")
    for i, (text, h) in enumerate(block["items"], 1):
        lines.append(f"- [ ] **{i}.** {text}（{h}）")
    lines.append("")
    lines.append("## 本周口述题")
    lines.append("")
    for q in block.get("recount", []):
        lines.append(f"- {q}")
    lines.append("")
    lines.append("## 本周每日检查")
    lines.append("")
    for d in ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]:
        lines.append(f"- [ ] {d}：Anki 20 张 + 算法 1 题 + commit")
    lines.append("")
    return "\n".join(lines)


def main():
    force = "--force" in sys.argv
    DAYS_DIR.mkdir(exist_ok=True)
    WEEKS_DIR.mkdir(exist_ok=True)

    total = 0
    for week in CURRICULUM:
        start = datetime.date.fromisoformat(week["start"])
        end = datetime.date.fromisoformat(week["end"])
        d = start
        while d <= end:
            wd = d.isoweekday()
            data = week["days"].get(wd)
            if not data:
                d += datetime.timedelta(days=1)
                continue
            total += 1
            fname = DAYS_DIR / f"{total:02d}-{d.isoformat()}.md"
            if fname.exists() and not force:
                pass
            else:
                fname.write_text(render_day(total, d.isoformat(), week, wd, data[0], data[1], data[2]),
                                 encoding="utf-8")
            d += datetime.timedelta(days=1)

    for block in BLOCKS:
        fname = WEEKS_DIR / f"{block['id']}-{block['start']}.md"
        if fname.exists() and not force:
            continue
        fname.write_text(render_week(block), encoding="utf-8")

    print(f"生成完成：{total} 个每日文件，{len(BLOCKS)} 个每周文件")


if __name__ == "__main__":
    main()

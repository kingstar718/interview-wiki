# JVM

## 三、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 运行时内存 | 堆、虚拟机栈、方法区、直接内存 | 每个区域可能出现什么 OOM |
| 对象创建 | 类检查、分配、零值、对象头、构造 | 指针碰撞、TLAB、逃逸分析 |
| 对象布局 | 对象头、实例数据、对齐填充 | Mark Word 如何保存锁和 GC 信息 |
| 可达性分析 | GC Roots、引用链 | 四种引用、finalize 为什么废弃 |
| GC 算法 | 标记清除、复制、标记整理 | 分代假说、跨代引用 |
| 并发标记 | 三色标记、漏标两条件 | 增量更新 vs SATB、CMS 与 G1 为何选择不同 |
| 收集器 | 吞吐量、延迟、堆规模 | G1 Region/RSet/Mixed GC、ZGC 染色指针、如何选型 |
| STW | 安全点、主动式中断 | 大循环拖长 STW、安全区域 |
| 类加载 | 加载、验证、准备、解析、初始化 | 双亲委派、SPI、类冲突 |
| JVM 调优 | 目标、指标、证据链 | GC 日志怎么看、为什么不能只调参数 |
| 内存泄漏 | 对象存活但不再需要 | MAT 支配树、ThreadLocal、类加载器泄漏 |
| OOM | 报错信息定位区域 | 堆 / 元空间 / 直接内存 / 线程，各自排查工具 |

回答调优题先说目标和现象，再说工具、证据和修改，不能从背 JVM 参数开始。

---

[← 返回知识点](知识点索引.md)

---

## 一、JVM 虚拟机

### 核心概念速查

| 概念 | 说明 |
|------|------|
| **堆（Heap）** | 对象分配的内存区域，GC 管理 |
| **栈（Stack）** | 方法执行、局部变量的内存区域 |
| **方法区** | 类、常量、静态变量等 |
| **GC** | 自动垃圾回收，回收无引用对象 |
| **GC Root** | 引用链的起点：栈变量、静态变量等 |
| **Young Generation** | 新生代，GC 频繁 |
| **Old Generation** | 老年代，GC 不频繁 |

### 高频面试题

#### 1. JVM 垃圾回收机制和调优？（难度：Hard）

**快答**
- GC 通过可达性分析（reference chain）判断对象是否存活
- 新生代用 Eden + Survivor，老年代单独分配
- GC 调优：选择合适收集器、调整堆大小、减少 full GC

**深答**（本节省略细节，详见 JVM 专题文档）

---

### 最佳实践

**❌ 常见误区：**
1. 频繁创建大对象（增加 GC 压力）
2. 不释放资源（try-with-resources 自动关闭）
3. 盲目优化堆大小（基于监控数据调整）

**✅ 正确做法：**
```java
// 1. 使用对象池避免频繁创建
ObjectPool<Resource> pool = new ObjectPool<>();
Resource res = pool.borrow();
try {
    // use
} finally {
    pool.release(res);
}

// 2. 自动资源管理
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 自动关闭
}

// 3. 监控 GC，基于数据调优
// 查看 GC 日志，分析 full GC 频率和暂停时间
```

---

## 二、JVM 补充

### 1. 对象创建过程

1. **类加载检查**：检查类是否已加载、解析、初始化
2. **分配内存**：根据类信息计算对象大小，从堆中分配（指针碰撞或空闲列表）
3. **初始化零值**：将内存空间初始化为零值（保证字段默认值）
4. **设置对象头**：存储类元数据信息、哈希码、GC 分代年龄、锁状态等
5. **执行 `<init>` 方法**：按程序员意图初始化对象

### 2. 对象在内存中的布局

对象头（Header） + 实例数据（Instance Data） + 对齐填充（Padding）

- **对象头**：Mark Word（存储哈希码、GC 年龄、锁状态） + 类指针（指向 Class 对象）
- **实例数据**：对象中定义的各种字段
- **对齐填充**：JVM 要求对象大小是 8 字节的整数倍

### 3. GC Roots 有哪些？

- 虚拟机栈（栈帧中的本地变量表）中引用的对象
- 方法区中类静态属性引用的对象
- 方法区中常量引用的对象
- 本地方法栈中 JNI 引用的对象
- 活跃线程对象

### 4. 对象什么时候会从新生代晋升到老年代？

- **年龄阈值**：对象在 Survivor 区熬过一次 Minor GC 年龄 +1，达到 `MaxTenuringThreshold`（默认 15）时晋升
- **大对象直接进入**：超过 `-XX:PretenureSizeThreshold` 的大对象直接分配到老年代
- **动态年龄判定**：Survivor 区中同一年龄的对象大小总和超过 Survivor 区的一半时，大于等于该年龄的对象直接晋升

### 5. 双亲委派的破坏场景

以下场景需要打破双亲委派：
- **SPI 机制**：如 JDBC 驱动加载，父加载器（Bootstrap）需要加载子加载器（Application）路径下的类，通过 `Thread.currentThread().getContextClassLoader()` 解决
- **Tomcat**：每个 Web 应用有自己的 ClassLoader，优先加载自己的类（WebappClassLoader），实现应用隔离
- **热部署/热替换**：自定义 ClassLoader 每次加载新版本 class 文件

### 6. 如何排查内存泄漏？

**步骤**：
1. 添加 JVM 参数：`-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof`
2. 发生 OOM 时自动生成堆快照
3. 用 MAT / JProfiler 分析，看 **支配树（Dominator Tree）** 找出占用最大的对象
4. 查看 **引用链（GC Root Path）** 确定是谁持有了泄漏对象

**常见内存泄漏原因**：
- 静态集合无意识缓存大量对象
- 未关闭资源（连接、流等）
- ThreadLocal 未 remove
- 事件监听器未注销

### 7. 类卸载的苛刻条件

需要 **同时满足** 三点：
1. 该类所有实例都已被回收
2. 加载该类的 ClassLoader 已被回收
3. 该类的 Class 对象无任何引用

### 8. 什么时候该用什么 GC 收集器？

| 收集器 | 适用场景 |
|--------|---------|
| Parallel GC | 后台计算，追求高吞吐 |
| G1（默认） | 大堆内存（数 GB+），兼顾吞吐和延迟 |
| ZGC | 超大堆（TB 级），停顿 < 1ms |
| Shenandoah | 大堆低延迟，类似 ZGC |
| Serial GC | 小内存 / 客户端应用 |

> CMS 和 ParNew 已在 JDK 14 被移除，不再适用于现代环境。

### 9. JVM 常用调优参数

| 参数 | 作用 |
|------|------|
| `-Xms` / `-Xmx` | 堆初始 / 最大大小 |
| `-Xss` | 单线程栈大小 |
| `-XX:+HeapDumpOnOutOfMemoryError` | OOM 时生成堆快照 |
| `-XX:MaxGCPauseMillis` | G1 最大停顿时间目标 |
| `-XX:+UseZGC` | 启用 ZGC 收集器 |
| `-XX:+PrintGCDetails` | 打印 GC 详细日志 |

### 10. 三色标记：并发标记为什么会漏标？怎么解决？

**是什么**：并发标记阶段给对象染三种颜色描述扫描进度。

| 颜色 | 含义 |
|------|------|
| 白 | 未被扫描到；标记结束仍是白色 → 回收 |
| 灰 | 自身已扫描，但它引用的对象还没扫完 |
| 黑 | 自身和引用都扫描完毕，不会再重新扫描 |

**为什么会出问题**：标记和用户线程**并发**执行，对象引用关系在标记过程中会变。

- **多标（浮动垃圾）**：已标记存活的对象随后变成垃圾 → 本轮不回收，留到下轮，无害。
- **漏标（致命）**：同时满足两个条件时，存活对象会被当垃圾回收：
  1. 用户线程把一个**白色对象**挂到**黑色对象**下（黑色不再扫描）
  2. 同时删除了所有**灰色对象**到该白色对象的引用（灰色扫不到它了)

**怎么解决**：破坏两个条件之一即可，两大流派对应两种屏障：

| 方案 | 破坏的条件 | 做法 | 使用者 |
|------|-----------|------|--------|
| **增量更新**（Incremental Update） | 条件 1 | **写屏障**记录"黑→白"的新增引用，重新标记阶段把黑色对象重扫一遍 | CMS |
| **原始快照**（SATB, Snapshot At The Beginning） | 条件 2 | **写屏障**记录"被删除的旧引用"，按标记开始那一刻的快照视角，被删对象本轮仍算存活 | G1、Shenandoah |

**常见追问**
- 为什么 G1 选 SATB 而 CMS 选增量更新？→ SATB 重新标记只需扫屏障记录的旧引用，停顿更短更可控（G1 的卖点是可预测停顿）；代价是快照视角产生更多浮动垃圾。增量更新要重扫黑色对象，重新标记停顿更长。
- ZGC 为什么不用这套？→ ZGC 用**读屏障 + 染色指针**（把标记位存在 64 位指针的高位上），加载引用时通过读屏障自愈修正，标记/转移全程并发，停顿与堆大小无关。

### 11. G1 收集器原理

**是什么**：JDK 9+ 默认收集器。把堆划分为约 2048 个大小相等的 **Region**（1~32MB），Eden/Survivor/Old 不再物理连续，而是 Region 的逻辑集合；超过 Region 一半的大对象放 **Humongous** 区。

**为什么这么设计**：
- **可预测停顿**：`-XX:MaxGCPauseMillis`（默认 200ms）是目标而非命令。G1 跟踪每个 Region 的回收价值（能回收多少空间/要花多少时间），每次只挑**收益最高的一部分 Region** 回收（Garbage First 名字的由来），用停顿预测模型控制单次 GC 时长。
- **Region 化解决碎片**：整体是标记-整理，Region 之间是复制算法，不产生 CMS 式的碎片。

**回收过程**：
1. **Young GC**：Eden 满触发，存活对象复制到 Survivor/Old Region，STW 但很短
2. **并发标记周期**：老年代占比超过 `-XX:InitiatingHeapOccupancyPercent`（默认 45%）触发，含初始标记（STW，借 Young GC 顺带做）→ 并发标记 → 最终标记（STW，处理 SATB 记录）→ 清理
3. **Mixed GC**：并发标记结束后的若干次 Young GC 顺带回收部分高收益 Old Region

**跨 Region 引用怎么处理**：每个 Region 有 **RSet（Remembered Set）**，记录"谁引用了我"（points-into），配合卡表实现。Young GC 时只需扫 RSet 而不用扫全堆。代价：RSet 占用约 10~20% 额外内存，写屏障维护有开销。

**常见追问**
- G1 什么时候会退化成 Full GC？→ Mixed GC 回收速度赶不上分配速度（并发模式失败）、Humongous 分配失败、转移时 Survivor/Old 装不下（to-space exhausted）。退化后是单线程/多线程的整堆压缩（JDK 10+ 并行化），停顿秒级。排查方向：增大堆、调低 IHOP 让并发标记提早、排查大对象。
- 为什么大对象是"半个 Region"为界？→ 连续 Region 分配 Humongous 开销大且回收时机特殊（Young GC / Full GC / 并发清理才回收），大量短命大对象是 G1 的典型性能杀手。

### 12. 强软弱虚四种引用

| 引用 | 回收时机 | 典型场景 |
|------|---------|---------|
| **强引用** `Object o = new Object()` | 永不回收（可达时） | 默认引用 |
| **软引用** `SoftReference` | **内存不足（OOM 前）** 才回收 | 内存敏感缓存（图片缓存） |
| **弱引用** `WeakReference` | **下一次 GC** 就回收 | `ThreadLocalMap` 的 key、`WeakHashMap` |
| **虚引用** `PhantomReference` | 随时，无法通过它拿到对象 | 配合 `ReferenceQueue` 跟踪回收时机，管理堆外内存（`DirectByteBuffer` 的 Cleaner） |

**常见追问**
- ThreadLocal 为什么用弱引用还会泄漏？→ key（ThreadLocal 对象）是弱引用会被回收，但 **value 是强引用**，线程不死（线程池）value 就一直挂在 ThreadLocalMap 里 → 必须手动 `remove()`。弱引用只解决了 key 的泄漏，还留下 key 为 null 的 stale entry。
- 软引用适合做缓存吗？→ 谨慎。回收时机由 JVM 决定，接近 OOM 时集中清空导致缓存雪崩 + Full GC 变长；生产缓存更推荐 Caffeine 这类带容量/过期策略的库。

### 13. 安全点与安全区域

**是什么**：GC 需要 STW 时，线程不能停在任意位置——**安全点（Safepoint）** 是线程状态确定、栈上引用关系明确的位置（方法调用、循环回跳、异常跳转处）。

**为什么需要**：可达性分析要求引用关系冻结。JVM 用**主动式中断**：设置一个全局标志，线程执行到安全点时轮询该标志，发现置位就挂起。所以 STW 时间 = 等所有线程走到安全点 + GC 本身。

**常见追问**
- 大循环为什么会拖长 STW？→ 可数循环（int 计数）默认**不在循环内插安全点轮询**，一个千万次的纯计算循环会让其他所有线程干等它跑完才能开始 GC。JDK 10+ 的 Loop Strip Mining 缓解；排查用 `-XX:+PrintSafepointStatistics` 看 sync 时间。
- 线程 sleep/blocked 了走不到安全点怎么办？→ **安全区域（Safe Region）**：进入该区域前声明"这段代码不改引用关系"，GC 不必等它；线程离开安全区域时检查 GC 是否完成，未完成则挂起。

### 14. OOM 有哪几种？分别怎么排查？

| OOM 类型 | 报错信息 | 常见原因 | 排查 |
|---------|---------|---------|------|
| 堆溢出 | `Java heap space` | 内存泄漏 / 堆太小 / 大查询加载过多数据 | HeapDump + MAT 支配树（见第 6 节） |
| 元空间 | `Metaspace` | 动态生成类失控（CGLIB 代理、Groovy 脚本、反射滥用） | `-XX:MaxMetaspaceSize` 限制 + jcmd 看类加载数量 |
| 直接内存 | `Direct buffer memory` | NIO/Netty 堆外内存未释放，`-XX:MaxDirectMemorySize` 不足 | NMT（Native Memory Tracking）、Netty 泄漏检测 |
| 无法创建线程 | `unable to create new native thread` | 线程数超系统限制（ulimit）、线程栈总量耗尽 | `jstack` 数线程 + 检查线程池是否无界 |
| 栈溢出 | `StackOverflowError` | 递归过深、`-Xss` 太小 | 看栈轨迹的重复帧 |
| GC 开销超限 | `GC overhead limit exceeded` | 98% 时间在 GC 但只回收 2% 内存，堆接近满 | 同堆溢出 |

**答题要点**：先说 OOM ≠ 只有堆，报错信息直接指向区域；再按区域给排查工具链。可衔接[生产排障](生产排障.md)的内存上涨排查流程。

[← 返回知识点](知识点索引.md)

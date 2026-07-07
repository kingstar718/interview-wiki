# JVM

## 三、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 运行时内存 | 堆、虚拟机栈、方法区、直接内存 | 每个区域可能出现什么 OOM |
| 对象创建 | 类检查、分配、零值、对象头、构造 | 指针碰撞、TLAB、逃逸分析 |
| 对象布局 | 对象头、实例数据、对齐填充 | Mark Word 如何保存锁和 GC 信息 |
| 可达性分析 | GC Roots、引用链 | 四种引用、finalize 为什么废弃 |
| GC 算法 | 标记清除、复制、标记整理 | 分代假说、跨代引用 |
| 收集器 | 吞吐量、延迟、堆规模 | G1 Region、ZGC 染色指针、如何选型 |
| 类加载 | 加载、验证、准备、解析、初始化 | 双亲委派、SPI、类冲突 |
| JVM 调优 | 目标、指标、证据链 | GC 日志怎么看、为什么不能只调参数 |
| 内存泄漏 | 对象存活但不再需要 | MAT 支配树、ThreadLocal、类加载器泄漏 |

回答调优题先说目标和现象，再说工具、证据和修改，不能从背 JVM 参数开始。

---

[← 返回知识点](../社招问题知识点.md)

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

[← 返回知识点](../社招问题知识点.md)

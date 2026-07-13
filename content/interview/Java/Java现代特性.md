# Java现代特性

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| JDK 升级 | 版本收益、依赖兼容、灰度验证 | 反射强封装、Jakarta 迁移 |
| JDK 24/26 | Stream Gatherers、Class-File API、HTTP/3、G1 同步优化 | Virtual Thread pinning 修复、Final 字段限制信号 |
| Record | 数据载体、自动方法、浅不可变 | 与 Lombok、实体类的区别 |
| sealed class | 封闭类型层次 | 与 enum、普通接口如何选择 |
| 模式匹配 | 类型判断、绑定、穷尽检查 | null 分支、可读性边界 |
| 虚拟线程 | M:N、阻塞卸载、IO 场景 | pinning、ThreadLocal、连接池 |
| Scoped Value | 只读上下文、作用域 | 与 ThreadLocal 的生命周期区别 |
| Stream | 惰性、无副作用、并行池 | parallelStream 为什么可能变慢 |
| JPMS | 模块依赖和强封装 | 与 Maven 多模块的区别 |

现代 Java 特性要回答“是否已正式发布、项目是否实际采用、迁移成本是什么”，不要把预览特性当成稳定能力。

---

[← 返回知识点](知识点索引.md)

---

## 一、版本演进

### Java 8、17、21、25 应重点关注什么？

难度 🟡

| 版本 | 定位 | 面试重点 |
|------|------|----------|
| Java 8 | 长期使用基线 | Lambda、Stream、Optional、CompletableFuture |
| Java 17 | LTS | Record、密封类、模式匹配、强封装 |
| Java 21 | LTS | 虚拟线程、Record Pattern、switch 模式匹配 |
| Java 24 | 非 LTS | Stream Gatherers、Class-File API、Flexible Constructor Bodies 转正、抗量子加密 |
| Java 25 | LTS | 模块导入声明、紧凑源文件、Scoped Values、Flexible Constructor Bodies、Compact Object Headers 等 18 个 JEP |
| Java 26 | 最新非 LTS | HTTP/3、Final Mean Final、G1 同步优化、AOT 对象缓存、结构化并发第 6 预览 |

社招回答应优先说明项目实际版本、升级收益和兼容风险，不需要机械背诵每个 JEP。

### 从 Java 8 升级到 17/21 要注意什么？

- JDK 内部 API 强封装，依赖反射访问内部类的库可能失效。
- `javax.*` 到 `jakarta.*` 主要是 Jakarta EE/Spring Boot 3 生态迁移，不是 JDK 自动完成。
- GC、容器感知和默认参数发生变化。
- 字节码增强、代理、序列化和监控 Agent 需要验证兼容性。
- 先升级依赖和构建工具，再升级运行时，并做完整压测和灰度。

### JDK 24、26 有什么关键变化？

难度 🟡

JDK 24（2025-03-18）和 JDK 26（2026-03-17）是两个非 LTS 特性版本，但引入了多个对日常开发影响深远的正式特性。JDK 25 是 LTS 版本（2025-09-16），是 JDK 21 之后的下一个长期支持基线。

**JDK 24 正式特性（选讲）：**

| JEP | 特性 | 面试价值 |
|-----|------|----------|
| 485 | **Stream Gatherers** — `Stream` 终于有了自定义中间操作的官方 API：`stream.gather(myGatherer)`。之前自定义 Stream 操作只能自己写 Spliterator，现在可以用官方框架组合。 | ★★★★ 面试高频：这是 Stream 自 Java 8 以来最大的扩展，面试官可能会追问 Gatherer 与自定义 Collector 的区别，以及能否替代现有 window/takeWhile 等操作。 |
| 484 | **Class-File API** — 操作 `.class` 字节码的官方标准 API，替代 ASM（第三方库）。Spring、Hibernate 等框架的字节码增强可以逐步脱离 ASM 依赖。 | ★★★ 中间件方向：框架开发者必知，业务开发了解即可。 |
| 492 | **Flexible Constructor Bodies（转正）** — 构造函数中可以在 `super()` 之前执行逻辑（校验、初始化等），不用再靠静态工厂变通。这是 Java 语言层面少有的构造能力增强。 | ★★★ 面试可能考：与原来 `super()` 必须在第一行的设计意图对比。 |
| 491 | **Synchronize Virtual Threads without Pinning（转正）** — `synchronized` 不再导致虚拟线程 pinning，JDK 21 时代最大的虚拟线程坑被填平。 | ★★★★★ 必考：虚拟线程面试的版本口径分水岭。 |
| 496/497 | **抗量子加密** — ML-KEM（密钥封装）和 ML-DSA（数字签名）正式纳入 JDK，为后量子时代做准备。 | ★★ 安全方向：知道 JDK 在做此准备即可。 |

**JDK 25 正式特性（选讲，LTS 版本）：**

JDK 25 是 JDK 21 之后的下一个 LTS，共 18 个 JEP，其中 9 个聚焦性能和运行时。以下是面试价值最高的几个：

| JEP | 特性 | 面试价值 |
|-----|------|----------|
| 511 | **Module Import Declarations（转正）** — `import module java.base;` 一条语句导入整个模块的所有导出包，写 demo 和教学代码方便很多。 | ★★★ 知道这是语法糖，不会替代实际项目中的精确 import。 |
| 513 | **Flexible Constructor Bodies（转正）** — 与 JDK 24 一致，两个版本确认定型。 | ★★ 参照 JDK 24 条目。 |
| 506 | **Scoped Values（转正）** — 只读上下文传递（Trace ID、租户），替代 ThreadLocal 用于虚拟线程场景。 | ★★★★ 高频：结合虚拟线程问 ThreadLocal 与 Scoped Value 的区别。 |
| 519 | **Compact Object Headers（转正）** — 64 位 JVM 对象头从 12-16 字节压缩到约 8-10 字节，降低内存占用。 | ★★★ 性能方向：知道它是通过移除无用对象头位来实现的。 |
| 521 | **Generational Shenandoah（转正）** — Shenandoah GC 也引入了分代回收（类似 ZGC 的分代演进路线）。 | ★★ GC 方向：JVM 调优面可能会被问到。 |
| 512 | **Compact Source Files + Instance Main Methods（转正）** — 无 `class` 包装的脚本式 Java 文件，`main` 方法不用 `public static void` 签名，降低入门门槛。 | ★★ 知道即可，面试极少考。 |
| 509 | **JFR CPU-Time Profiling（实验性）** — JFR 直接采样 CPU 调用栈，定位热点方法不再依赖 async-profiler。 | ★★ 运维方向：线上问题排查能力提升。 |

**JDK 26 正式特性（选讲）：**

| JEP | 特性 | 面试价值 |
|-----|------|----------|
| 500 | **Prepare to Make Final Mean Final** — 深度反射修改 `final` 字段会发出警告。这是 Java“完整性优先”原则的关键落地——保护敏感数据不被恶意或意外修改。JDK 26 只发警告，后续版本将完全禁止。 | ★★★★ 必考：Spring/MyBatis 等框架大量用反射设 final 字段，面试官会追问影响范围。 |
| 517 | **HTTP/3 for the HTTP Client API（转正）** — `HttpClient` 原生支持 HTTP/3（QUIC 协议），微服务间通信延迟更低。 | ★★★ 微服务方向：知道 JDK HTTP Client 补齐了 HTTP/3 支持。 |
| 516 | **Ahead-of-Time Object Caching with Any GC（转正）** — Project Leyden 成果：预初始化对象缓存，启动时顺序加载，支持 ZGC 等任意 GC。 | ★★★ 性能方向：Java 启动加速进入 Leyden 阶段。 |
| 522 | **G1 GC: Improve Throughput by Reducing Synchronization（转正）** — 减少应用线程与 GC 线程的同步开销。 | ★★ GC 方向：同等硬件跑更多请求，顺带引出 G1 的 region 和 SATB 原理。 |
| 525 | **Structured Concurrency（第六预览）** — 结构化并发仍在预览，但已到第 6 版，定型可期。 | ★★★ 并发方向：结合虚拟线程一起问。 |
| 530 | **Primitive Types in Patterns, instanceof, and switch（第四预览）** — `switch` 和 `instanceof` 中直接匹配 `int`、`long` 等基本类型，消除装箱拆箱。 | ★★ 语言特性：知道模式匹配正在统一基本类型和引用类型。 |

---

## 二、Record 与不可变数据

### Record 是什么？

难度 🟡

Record 用于声明透明的数据载体，编译器自动生成：

- `private final` 组件字段。
- 访问器。
- 全参数构造器。
- `equals`、`hashCode`、`toString`。

```java
public record UserSummary(long id, String name) {}
```

Record 是浅不可变：字段引用不能更换，但字段指向的可变对象仍可被修改。

### Record 适合和不适合什么场景？

适合：

- DTO、查询结果、事件消息。
- 值对象和模式匹配。

不适合：

- 需要无参构造和可变属性的老旧框架。
- 需要继承具体父类。
- 内部包含大量可变状态的领域实体。

---

## 三、密封类与模式匹配

### sealed class 解决什么问题？

密封类显式限制允许继承或实现它的类型：

```java
public sealed interface Result
        permits Success, Failure {}
```

它适合表达封闭的领域类型集合，让编译器帮助检查分支是否完整。

### switch 模式匹配有什么价值？

可以同时完成类型判断、变量绑定和分支处理：

```java
return switch (result) {
    case Success(var data) -> handle(data);
    case Failure(var code, var message) -> fail(code, message);
};
```

配合 sealed 类型时，编译器可以检查是否穷尽所有合法子类型。

### Record Pattern 是什么？

Record Pattern 支持直接解构 Record：

```java
if (obj instanceof Point(int x, int y)) {
    return x + y;
}
```

它减少手动类型转换和访问器调用，但嵌套模式过深会降低可读性。

---

## 四、虚拟线程

### 虚拟线程是什么？

难度 🔴

虚拟线程是 JVM 调度的轻量级线程，大量虚拟线程复用少量平台线程。

当虚拟线程执行支持的阻塞 IO 时，JVM 可以卸载虚拟线程，让载体线程继续执行其他任务。因此它适合“每请求一线程”的高并发 IO 模型。

### 虚拟线程适合哪些场景？

适合：

- HTTP/RPC 请求。
- 数据库和网络 IO。
- 大量彼此独立的阻塞任务。

不适合直接提升：

- CPU 密集计算。
- 受数据库连接池等外部资源限制的吞吐量。
- 依赖大量 ThreadLocal 缓存的设计。

### 虚拟线程是否还需要线程池？

通常不需要为了限制线程数量而池化虚拟线程，可以按任务创建。

真正有限的资源应通过 Semaphore、连接池或限流器限制，例如数据库连接数，而不是用固定大小的虚拟线程池间接限制。

### 什么是虚拟线程 pinning？

虚拟线程执行某些操作时可能无法从载体线程卸载，导致载体线程被占用。

**版本口径要答对**：JDK 21 时代最典型的 pinning 场景是在 `synchronized` 临界区内阻塞；**JDK 24 起（JEP 491）synchronized 已不再导致 pinning**，剩余场景主要是 native 方法 / 外部函数调用中阻塞。面试说"synchronized 会 pinning"必须带上版本前提，否则暴露知识停在 21。

治理重点：

- 项目还在 21：缩短临界区、不在锁内做慢 IO，热点锁可换 `ReentrantLock`。
- 用 JFR 的 `jdk.VirtualThreadPinned` 事件观察 pinned 情况。
- 不把平台线程时代的线程池模式原样迁移。

---

## 五、Scoped Value 与上下文传递

### Scoped Value 解决什么问题？

Scoped Value 用于在线程及其子任务范围内安全传递只读上下文，例如 Trace ID、租户信息。

与 ThreadLocal 相比，它强调：

- 值不可变。
- 生命周期有明确词法范围。
- 更适合虚拟线程和结构化任务。

版本状态：Scoped Values 在 **JDK 25 定稿（JEP 506）**；JDK 21~24 上是预览特性。生产使用以项目 JDK 版本为准，21 上不要把预览能力当稳定 API 依赖。

---

## 六、现代 Java 工程实践

### Optional 应该如何正确使用？

- 适合作为“可能没有结果”的返回值。
- 不建议作为实体字段、方法参数或序列化模型的默认选择。
- 避免无条件调用 `get()`。
- `orElse` 会立即计算默认值，昂贵操作使用 `orElseGet`。

### Stream 并行执行要注意什么？

- 默认使用公共 ForkJoinPool，可能与其他任务互相影响。
- 数据量小或操作简单时并行开销可能更大。
- 避免共享可变状态和阻塞 IO。
- 必须通过基准测试验证，不应仅凭“并行”判断更快。

### 模块系统 JPMS 解决什么问题？

JPMS 通过 `module-info.java` 声明依赖和导出包，增强封装并减少运行时缺失依赖。

大型既有项目迁移成本较高，很多 Spring 项目仍主要使用 classpath。回答时应区分 JDK 模块和 Maven 模块。

---

## 参考资料

- [JDK 26](https://openjdk.org/projects/jdk/26/)
- [JDK 25](https://openjdk.org/projects/jdk/25/)
- [JDK 24](https://openjdk.org/projects/jdk/24/)
- [Java Language Changes (JDK 26)](https://docs.oracle.com/en/java/javase/26/language/java-language-changes.html)
- [The Arrival of Java 26 (Oracle Blog)](https://blogs.oracle.com/java/the-arrival-of-java-26)

---

[← 返回知识点](知识点索引.md)

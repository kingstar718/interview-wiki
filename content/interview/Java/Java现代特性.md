# Java现代特性

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| JDK 升级 | 版本收益、依赖兼容、灰度验证 | 反射强封装、Jakarta 迁移 |
| Record | 数据载体、自动方法、浅不可变 | 与 Lombok、实体类的区别 |
| sealed class | 封闭类型层次 | 与 enum、普通接口如何选择 |
| 模式匹配 | 类型判断、绑定、穷尽检查 | null 分支、可读性边界 |
| 虚拟线程 | M:N、阻塞卸载、IO 场景 | pinning、ThreadLocal、连接池 |
| Scoped Value | 只读上下文、作用域 | 与 ThreadLocal 的生命周期区别 |
| [结构化并发](#结构化并发structuredtaskscope解决什么问题) | 作用域绑定、异常短路、父子任务 | 与 CF/ES 对比、与虚拟线程关系 |
| Stream | 惰性、无副作用、并行池 | parallelStream 为什么可能变慢 |
| JPMS | 模块依赖和强封装 | 与 Maven 多模块的区别 |

现代 Java 特性要回答“是否已正式发布、项目是否实际采用、迁移成本是什么”，不要把预览特性当成稳定能力。

---

## 一、版本演进

### Java 8、17、21、25 应重点关注什么？

难度 🟡

| 版本 | 定位 | 面试重点 |
|------|------|----------|
| Java 8 | 长期使用基线 | Lambda、Stream、Optional、CompletableFuture |
| Java 17 | LTS | Record、密封类、模式匹配、强封装 |
| Java 21 | LTS | 虚拟线程、Record Pattern、switch 模式匹配 |
| Java 25 | LTS | 模块导入声明、紧凑源文件、Scoped Values 等演进 |

社招回答应优先说明项目实际版本、升级收益和兼容风险，不需要机械背诵每个 JEP。

### 从 Java 8 升级到 17/21 要注意什么？

- JDK 内部 API 强封装，依赖反射访问内部类的库可能失效。
- `javax.*` 到 `jakarta.*` 主要是 Jakarta EE/Spring Boot 3 生态迁移，不是 JDK 自动完成。
- GC、容器感知和默认参数发生变化。
- 字节码增强、代理、序列化和监控 Agent 需要验证兼容性。
- 先升级依赖和构建工具，再升级运行时，并做完整压测和灰度。

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

## 七、结构化并发

### 结构化并发（StructuredTaskScope）解决什么问题？

频次 ★★★★ · 难度 🟡

**是什么**：结构化并发（JDK 21 正式发布，JEP 453）让并发任务的生命周期绑定到一个**词法作用域**——子任务要么全部完成，要么全部取消，不会出现"提交了任务但忘了 join"导致的资源泄漏。

**核心用法：**

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> user = scope.fork(() -> fetchUser(id));
    Subtask<List<Order>> orders = scope.fork(() -> fetchOrders(id));

    scope.join();               // 等待所有子任务完成
    scope.throwIfFailed();      // 有失败则抛出第一个异常

    return new Result(user.get(), orders.get());
}
```

**三个核心设计：**
1. **作用域绑定**：`try-with-resources` 控制子任务生命周期，`scope` 关闭时未完成的子任务自动取消
2. **结构化父子关系**：子任务继承父任务的 `StructuredTaskScope` 和 `ScopedValue`，形成可追踪的调用树
3. **异常短路**：`ShutdownOnFailure` 任一子任务失败就取消其余；`ShutdownOnSuccess` 拿到第一个成功结果就取消其余

**与 CompletableFuture / ExecutorService 的对比：**

| 维度 | CompletableFuture / ExecutorService | StructuredTaskScope |
|------|-----------------------------------|-------------------|
| 任务生命周期 | 提交后不受控，忘记 join 会泄漏 | try-with-resources 自动关闭，离开作用域未完成自动 cancel |
| 父子关系 | 无，这是最根本的缺失 | 显式父子作用域，cancel 向上传播 |
| 异常处理 | 每个 CF 各自处理，或 get() 时逐个处理 | `throwIfFailed()` 统一短路 |
| 上下文传递 | ThreadLocal 需手动传递 | Scoped Value 自动继承 |
| 取消 | `future.cancel()` 只取消自己 | scope 关闭取消所有未完成子任务 |

**常见追问：**
- 与虚拟线程是什么关系？→ 互补关系：结构化并发解决"任务编排与生命周期管理"，虚拟线程解决"高效承载大量任务"；两者常一起使用——虚拟线程做载体，StructuredTaskScope 做编排
- ShutdownOnFailure 和 ShutdownOnSuccess 各适用什么场景？→ 前者适合"全部成功才有意义"（如查多个源聚合），后者适合"抢答式"（如多个镜像地址取最快返回）
- 能替代 CompletableFuture 吗？→ 不能完全替代。结构化并发适合**扇出（fan-out）**场景，CF 的优势在**链式编排**（thenApply/thenCompose）和回调。复杂流水线场景两者互补

---

## 参考资料

- [JDK 25](https://openjdk.org/projects/jdk/25/)
- [Java Language Changes](https://docs.oracle.com/en/java/javase/25/language/java-language-changes.html)


# Java基础

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 基本类型与包装类型 | 存储、默认值、泛型、自动装箱 | Integer 缓存、拆箱 NPE、比较陷阱 |
| String 不可变 | final、字符存储、哈希缓存、安全性 | 字符串常量池、intern、拼接优化 |
| equals/hashCode | 相等契约、哈希容器定位 | 只重写 equals 会怎样、可变 Key 风险 |
| 抽象类与接口 | 单继承、多实现、状态与行为 | default 方法冲突、如何选型 |
| 反射与注解 | Class 元数据、运行期解析 | 性能开销、框架如何扫描、代理关系 |
| SPI | META-INF/services、ServiceLoader 懒加载 | 上下文类加载器为何破坏双亲委派、Dubbo SPI 改进点 |
| 泛型 | 类型擦除、编译期约束 | PECS、桥接方法、为何不能 new T |
| Stream | 惰性求值、中间/终止操作 | 并行流线程池、副作用、性能边界 |
| CompletableFuture | 任务编排、异常传播、线程池 | thenApply/thenCompose、超时和取消 |
| IO/NIO | 阻塞模型、Channel/Buffer/Selector | 零拷贝、半包粘包、Netty 如何使用 |
| 序列化 | 对象到字节、版本兼容 | serialVersionUID、安全风险、替代协议 |

回答基础题时不要停留在语法定义，至少补充一个运行时行为或常见错误。

---

[← 返回知识点](知识点索引.md)

---

## 一、IO 流

### 核心概念速查

| 类型 | 说明 | 适用场景 |
|------|------|---------|
| **BIO** | 阻塞 IO，同步阻塞 | 连接数少 |
| **NIO** | 非阻塞 IO，选择器 | 连接数多 |
| **AIO** | 异步 IO，不常用 | 极端场景 |

### 高频面试题

#### BIO、NIO、AIO 的区别？

难度 🟡

**快答**
- BIO：线程阻塞在 read，一个连接一个线程
- NIO：一个线程处理多个连接，非阻塞
- AIO：异步，操作系统通知结果

---

## 二、异常处理

### 高频面试题

#### 受检异常 vs 非受检异常？

难度 🟢

**快答**
- **受检异常（Checked Exception）**：编译器检查，必须处理，如 IOException
- **非受检异常（Unchecked Exception）**：继承 RuntimeException，可不处理

**建议：**
- 自定义异常：如果调用者能恢复，用受检异常；如果无法恢复，用非受检异常
- 不要捕获后什么都不做（empty catch）
- 使用特定的异常类，便于定位问题

---

## 三、Java 基础高频补充

### JVM、JDK、JRE 三者关系？

难度 🟢

**快答**
- JDK > JRE > JVM，层层包含
- JDK = 开发工具包（javac、jdb 等）+ JRE
- JRE = 运行时环境 + JVM
- JVM = 虚拟机，负责字节码解释/编译执行

**深答**
- 跨平台的是 Java 程序（字节码），不是 JVM。JVM 是用 C/C++ 写的平台相关程序
- JVM 不只跑 Java，Kotlin、Scala 等语言编译后也能在 JVM 上运行
- Java 是编译 + 解释混合模式：先编译为字节码，JVM 中解释器 + JIT 编译器混合执行

---

### int 和 Integer 的区别？

难度 🟡

**快答**
- int 是基本类型，Integer 是包装类（引用类型）
- Integer 支持自动装箱/拆箱，有缓存机制（-128 ~ 127）
- int 默认值 0，Integer 默认值 null

**深答**

**为什么需要 Integer？**
- 泛型只能用引用类型：`List<Integer>` 而非 `List<int>`
- 集合只能存对象，不能存基本类型
- 提供了 parseInt()、toString() 等工具方法

**Integer 缓存机制：**
```java
Integer a = 127;
Integer b = 127;
a == b;  // true（缓存复用）

Integer c = 128;
Integer d = 128;
c == d;  // false（超出缓存范围，新建对象）
```
默认缓存范围 -128 ~ 127，通过 `Integer.valueOf()` 创建时生效。

**为什么还保留 int？**
- int 读写效率更高，不需要对象分配
- 64 位 JVM 开启引用压缩后，一个 Integer 对象占 16 字节，int 只占 4 字节
- 自动装箱在循环中会创建大量无用对象，增加 GC 压力

---

### String、StringBuilder、StringBuffer 区别？

难度 🟡

| 特性 | String | StringBuilder | StringBuffer |
|------|--------|--------------|-------------|
| 可变性 | 不可变 | 可变 | 可变 |
| 线程安全 | 是（因不可变） | 否 | 是（synchronized） |
| 性能 | 低（频繁修改时） | 高 | 中 |
| 适用场景 | 静态字符串 | 单线程动态操作 | 多线程动态操作 |

**String 不可变的原因：**
- 内部用 `private final char[] value` 存储
- 字符串常量池的需要（多个引用指向同一对象）
- 安全性（类加载器、网络连接等场景）
- 线程安全（不可变天然线程安全）

---

### == 和 equals 的区别？

难度 🟢

**快答**
- `==` 比较基本类型的值，比较引用类型的地址
- `equals` 默认等价于 `==`，但可以被重写为比较内容
- String、Integer 等类重写了 equals 比较内容

**hashCode 和 equals 的关系：**
- 如果 `a.equals(b)` 为 true，则 `a.hashCode() == b.hashCode()` 必须为 true
- 如果 hashCode 相同，equals 不一定为 true（哈希冲突）
- 重写 equals 必须重写 hashCode，否则在 HashMap/HashSet 中会出问题

---

### Java 创建对象有哪几种方式？

难度 🟡

| 方式 | 是否调用构造器 | 特点 |
|------|--------------|------|
| `new` 关键字 | 是 | 最常用，紧密耦合 |
| 反射（Constructor.newInstance） | 是 | 灵活，用于框架 |
| clone() | 否 | 需实现 Cloneable，浅拷贝 |
| 反序列化 | 否 | 需实现 Serializable |
| 工厂模式 | 是（在方法内） | 解耦，隐藏创建逻辑 |

---

### 深拷贝和浅拷贝的区别？

难度 🟡

- **浅拷贝**：只复制对象本身和值类型字段，引用类型字段复制的是引用地址（新旧对象共享同一个引用对象）
- **深拷贝**：递归复制对象及其所有引用类型字段，生成完全独立的新对象

**实现深拷贝的三种方式：**
1. 实现 Cloneable 接口并递归 clone 引用字段
2. 序列化 + 反序列化（需实现 Serializable）
3. 手动递归复制

---

### 反射机制及应用场景？

难度 🟡

**快答**
- 反射是在运行状态中动态获取类信息（属性、方法、构造器）并调用/修改的能力
- 核心类：`Class`、`Method`、`Field`、`Constructor`

**应用场景：**
- Spring IOC 容器：根据配置文件动态加载和创建 Bean
- JDBC 驱动加载：`Class.forName("com.mysql.cj.jdbc.Driver")`
- 动态代理、ORM 框架（Hibernate、MyBatis）

**获取私有字段：**
```java
Field field = clazz.getDeclaredField("privateField");
field.setAccessible(true);
Object value = field.get(obj);
```

---

### SPI 机制：ServiceLoader 是怎么找到实现类的？

频次 ★★★ · 难度 🟡

**是什么**：SPI（Service Provider Interface）是"接口在框架、实现在第三方"的服务发现机制：框架只定义接口，实现方在自己 jar 的 `META-INF/services/<接口全限定名>` 文件里登记实现类，框架用 `ServiceLoader.load(接口.class)` 在运行时发现并实例化。典型：JDBC 驱动（`java.sql.Driver`）、SLF4J 绑定、Dubbo 扩展点。

**为什么这么设计**：解决"框架代码不能 import 实现类"的依赖倒置问题——JDK 的 DriverManager 不可能 import MySQL 驱动。没有 SPI 就得硬编码 `Class.forName("com.mysql...")`，换实现要改代码；SPI 把"配置文件登记 + 反射加载"这套约定标准化，是开闭原则在类加载层面的落地。

**源码**（JDK 8 `java.util.ServiceLoader`，主干）：

```java
public static <S> ServiceLoader<S> load(Class<S> service) {
    // 取线程上下文类加载器，而不是 ServiceLoader 自己的加载器（见下文双亲委派衔接）
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return ServiceLoader.load(service, cl);
}

private class LazyIterator implements Iterator<S> {
    Enumeration<URL> configs;  // 各 jar 中 META-INF/services/ 下的同名文件

    public boolean hasNextService() {
        if (configs == null)   // 第一次 hasNext 才去扫描配置文件 —— 懒加载
            configs = loader.getResources(PREFIX + service.getName());
        // 逐文件逐行读出实现类全限定名
        ...
    }

    public S nextService() {
        Class<?> c = Class.forName(cn, false, loader);  // 只加载不初始化
        S p = service.cast(c.newInstance());            // 实例化并做类型检查
        providers.put(cn, p);                           // LinkedHashMap 缓存已创建实例
        return p;
    }
}
```

两个关键点：①**懒加载**——`load()` 只创建迭代器，不做任何 IO，遍历到哪个才加载/实例化哪个；②**只能全量顺序迭代**——想要特定实现也得从头逐个实例化再自己挑（DriverManager 就是全部实例化后靠 URL 前缀匹配）。

**与双亲委派的衔接**：DriverManager 在 `java.sql` 包、由 Bootstrap 加载，按双亲委派它"看不见"应用 classpath 下的驱动实现；所以 `load()` 取**线程上下文类加载器**（默认 AppClassLoader）来加载实现类——父加载器借子加载器干活，方向反了，这是双亲委派的经典破坏场景，见[JVM](JVM.md)"双亲委派的破坏场景"。

**对比 Dubbo SPI 为什么重写**：

| 维度 | JDK ServiceLoader | Dubbo ExtensionLoader |
|------|-------------------|------------------------|
| 配置格式 | 一行一个类名 | KV：`dubbo=com.xxx.DubboProtocol` |
| 获取方式 | 只能全量迭代 | 按名取 `getExtension("dubbo")`，**按需实例化** |
| IoC | 无 | 扩展点之间可 setter 注入（自适应扩展） |
| AOP | 无 | Wrapper 类自动层层包装（如 ProtocolFilterWrapper） |
| 失败表现 | 某个实现类加载失败，整个迭代抛异常 | 单个扩展失败不影响其他，报错点名扩展名 |

**常见追问**：
- JDBC 4.0 之后为什么不用写 `Class.forName` 了？→ DriverManager 静态初始化时用 ServiceLoader 自动发现驱动；之前的 Class.forName 是靠驱动类静态块里 `registerDriver` 完成注册
- Spring Boot 自动配置和 SPI 是什么关系？→ 思想同源："配置文件登记 + 反射加载"，只是文件换成 `spring.factories`（2.7+ 为 `AutoConfiguration.imports`），加载器换成 SpringFactoriesLoader，还叠加了条件注解按需生效，见[SpringBoot](SpringBoot.md)自动配置原理
- ServiceLoader 线程安全吗？→ 不安全（Javadoc 明确标注），providers 缓存无同步，多线程共享要外部加锁

**通用概念**：SPI 是**控制反转在"发现实现"环节的形态**——使用方不 new 具体实现，由约定/容器反向提供。同一模式：Spring IoC（见[Spring](Spring.md)）、SLF4J 日志门面找绑定、K8s 的 CNI/CSI 插件机制。

---

### Java 注解的原理？

难度 🟡

- 注解本质是继承 `Annotation` 接口的特殊接口
- 运行时注解通过反射获取时，返回的是动态代理对象（`AnnotationInvocationHandler`）
- 注解信息存储在 class 文件的属性表中（`RuntimeVisibleAnnotations`）
- `@Retention` 控制保留策略：SOURCE（仅源码）、CLASS（class 文件）、RUNTIME（运行时可反射）
- `@Target` 控制作用位置：TYPE、FIELD、METHOD、PARAMETER 等

---

### 面向对象六大设计原则？

难度 🟡

| 原则 | 缩写 | 含义 |
|------|------|------|
| 单一职责 | SRP | 一个类只负责一项职责 |
| 开闭原则 | OCP | 对扩展开放，对修改封闭 |
| 里氏替换 | LSP | 子类对象能替换父类对象 |
| 接口隔离 | ISP | 接口应该小而专 |
| 依赖倒置 | DIP | 依赖抽象而非具体实现 |
| 最少知识 | LoD | 只与直接朋友交互 |

**多态的体现：** 方法重载（编译时）、方法重写（运行时）、接口实现、向上/向下转型

---

### 抽象类和接口的区别？

难度 🟡

| 特性 | 抽象类 | 接口 |
|------|--------|------|
| 关键字 | extends | implements |
| 继承数量 | 单继承 | 多实现 |
| 成员变量 | 可有实例变量 | 只能有常量（public static final） |
| 方法 | 可有具体实现 | Java 8 前只能有抽象方法，Java 8+ 可有 default/static，Java 9+ 可有 private |
| 构造器 | 有 | 无 |
| 设计意图 | is-a 关系，代码复用 | has-a/can-do 能力，定义规范 |

### 泛型是如何实现的？为什么说是"伪泛型"？

**是什么**：Java 泛型只存在于**编译期**，编译器做完类型检查后会**擦除**成原始类型（Type Erasure），运行时字节码里根本没有泛型信息——这就是常说的"伪泛型"，区别于 C++ 模板（真的为每个类型生成一份代码）。

**擦除规则**：

```java
public class Box<T> { T value; }              // 编译后 T 被擦成 Object
public class NumBox<T extends Number> { T v; } // 编译后 T 被擦成 Number(擦成上界)

List<String> list = new ArrayList<>();
List<Integer> list2 = new ArrayList<>();
System.out.println(list.getClass() == list2.getClass()); // true —— 运行时都是 ArrayList,没有 <String>/<Integer> 之分
```

**桥接方法（源码验证擦除的证据）**：

```java
class MyComparator implements Comparator<String> {
    public int compare(String a, String b) { return a.length() - b.length(); }
}
```
用 `javap -p MyComparator` 反编译能看到编译器**额外生成**了一个方法：
```java
// 编译器生成的桥接方法(bridge method),字节码层面才存在
public int compare(Object a, Object b) {
    return compare((String) a, (String) b);   // 强转后调用真正的实现
}
```
接口 `Comparator<T>` 擦除后方法签名是 `compare(Object, Object)`，但子类写的是 `compare(String, String)`——两者签名不同，**不构成重写**。编译器靠生成桥接方法伪造出一个 `compare(Object,Object)` 覆盖接口方法，内部再强转调用真正实现，才让擦除后的多态继续成立。

**常见追问**
- 为什么不能 `new T[10]`？→ 擦除后 `T` 变成 `Object`，`new T[10]` 实际会创建 `Object[]`；但调用方赋值给 `String[]` 之类的具体数组类型引用时，运行时**数组是有类型信息的**（不像泛型集合），会在别的地方触发 `ClassCastException`。所以 JDK 禁止直接写这行代码，要用 `(T[]) new Object[10]` 强转（不安全但能过编译，本质是绕过检查）或 `Array.newInstance(clazz, 10)`。
- 泛型擦除会带来什么运行时开销问题？→ 基本类型泛型会被迫**自动装箱**（`List<Integer>` 存的是 `Integer` 对象不是 `int`），大量数据场景有装箱拆箱和内存开销，这也是 JDK 一直没有 `List<int>` 的根因；Java 21 的 Value Types（Project Valhalla）目标之一就是解决这个问题。
- 通配符 `? extends T` 和 `? super T` 怎么记？→ **PECS 原则**（Producer Extends, Consumer Super）：只读取（生产数据给你用）就用 `extends`，如 `List<? extends Number> src` 你能读出 Number 但不能往里加；只写入（消费你给的数据）就用 `super`，如 `List<? super Integer> dest` 你能加 Integer 但读出来只能当 Object 用。

**通用概念**：类型擦除是**编译期多态、运行期单态**的一种权衡——在保证向后兼容（Java 5 引入泛型时，老代码用 `List` 不用 `List<T>` 也能和新代码互相调用）和不修改 JVM 字节码规范的前提下实现类型安全检查。C# 的泛型是运行时具体化（reified），没有这个问题，但代价是不能像 Java 一样直接对老字节码保持兼容。

---

## 四、Java 8+ 新特性

Java 17/21/25 的 Record、密封类、模式匹配和虚拟线程见 [Java现代特性](Java现代特性.md)。

### Lambda 表达式和函数式接口

难度 🟡

**Lambda 语法：**
```java
// 单表达式
(parameters) -> expression

// 多语句
(parameters) -> { statements; return value; }
```

**常见函数式接口：**
| 接口 | 方法签名 | 用途 |
|------|---------|------|
| `Predicate<T>` | `boolean test(T)` | 条件判断 |
| `Function<T,R>` | `R apply(T)` | 转换 |
| `Consumer<T>` | `void accept(T)` | 消费 |
| `Supplier<T>` | `T get()` | 供给 |

---

### Stream API

难度 🟡

**常用操作：**
```java
List<Integer> result = list.stream()
    .filter(n -> n > 0)       // 过滤
    .map(n -> n * 2)          // 映射
    .sorted()                 // 排序
    .distinct()               // 去重
    .collect(Collectors.toList()); // 收集
```

**聚合操作：**
```java
long count = list.stream().count();
int sum = list.stream().mapToInt(Integer::intValue).sum();
Optional<Integer> max = list.stream().max(Integer::compareTo);
```

**并行流：** `list.parallelStream()` — 基于 ForkJoinPool，适合 CPU 密集型任务，I/O 密集型不推荐

---

### Optional 类

难度 🟢

```java
Optional<String> opt = Optional.ofNullable(value);
opt.orElse("default");          // 为空时返回默认值
opt.orElseGet(() -> "computed"); // 为空时执行函数
opt.ifPresent(v -> println(v));  // 有值时执行
opt.map(String::toUpperCase).orElse("N/A"); // 链式调用
```

---

### CompletableFuture 异步编程

难度 🔴

```java
CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "result1", executor);
CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "result2");

// 组合两个结果
cf1.thenCombine(cf2, (r1, r2) -> r1 + r2)
   .thenAccept(System.out::println);
```

相比 Future 的优势：支持回调、组合编排、不需要阻塞等待结果

---

## 五、设计模式

### 单例模式（双重检查锁定）

难度 🟡

```java
public class Singleton {
    private static volatile Singleton instance = null;
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**为什么需要 volatile？**
- 保证可见性
- 禁止指令重排序（`instance = new Singleton()` 分为：分配内存 → 初始化 → 赋值给引用，重排序后其他线程可能拿到未初始化的对象）

---

### 策略模式 vs 责任链模式

难度 🟡

**策略模式**：封装一组可互换的算法，运行时选择
- 场景：支付方式选择（支付宝/微信/银行卡）、排序算法切换

**责任链模式**：请求沿处理者链传递，直到被处理
- 场景：请求校验链（登录 → 权限 → 频率限制）、审批流程

两者共同目的：消除复杂的 if-else，提高扩展性

---

### 代理模式 vs 适配器模式

难度 🟢

- **代理模式**：控制对对象的访问，添加额外功能（如 Spring AOP）
- **适配器模式**：转换接口，让不兼容的类协同工作

---

## 六、IO 与网络编程

### NIO 三大核心组件

难度 🟡

| 组件 | 说明 |
|------|------|
| Channel（通道） | 双向数据传输，类似流但更强大 |
| Buffer（缓冲区） | 数据容器，读写切换通过 flip() |
| Selector（选择器） | I/O 多路复用，一个线程监听多个 Channel |

**NIO 工作流程：**
```
Channel 注册到 Selector → Selector 轮询就绪事件 → 处理就绪的 Channel
```

**实际应用：** Netty 底层基于 NIO Selector + epoll 实现高并发网络通信

---

### 序列化和反序列化

难度 🟡

**Java 原生序列化：**
```java
// 序列化
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("obj.ser"));
oos.writeObject(obj);

// 反序列化
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("obj.ser"));
Object obj = ois.readObject();
```

**原生序列化的问题：**
- 不跨语言（只适用于 Java）
- 安全性差（反序列化可执行任意代码）
- 性能差（序列化后的字节流大）

**替代方案：** Protobuf（高性能、跨语言）、JSON（FastJSON、Jackson）

---

## 七、其他高频问题

### BigDecimal 为什么比 double 更适合金额计算？

难度 🟢

double 使用二进制浮点运算，无法精确表示某些十进制小数（如 0.1），导致精度丢失：
```java
System.out.println(0.05 + 0.01); // 0.060000000000000005
```

**正确做法：**
```java
BigDecimal a = new BigDecimal("0.05");  // 用字符串构造
BigDecimal b = new BigDecimal("0.01");
System.out.println(a.add(b)); // 0.06（精确）
```

---

### 值传递 vs 引用传递

难度 🟡

**Java 只有值传递！**
- 基本类型：传递值的副本，修改不影响原值
- 引用类型：传递引用的副本，通过副本可修改对象内容，但修改引用指向不影响原引用

---

### static 关键字的四种用法

难度 🟢

| 用法 | 说明 |
|------|------|
| 静态变量 | 类级别共享，所有实例共用 |
| 静态方法 | 不依赖实例，不能访问非静态成员 |
| 静态代码块 | 类加载时执行一次，初始化静态资源 |
| 静态内部类 | 不依赖外部类实例，避免内存泄漏 |

---

[← 返回知识点](知识点索引.md)

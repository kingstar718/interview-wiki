# Java基础

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 基本类型与包装类型 | 存储、默认值、泛型、自动装箱 | Integer 缓存、拆箱 NPE、比较陷阱 |
| String 不可变 | final、字符存储、哈希缓存、安全性 | 字符串常量池、intern、拼接优化 |
| equals/hashCode | 相等契约、哈希容器定位 | 只重写 equals 会怎样、可变 Key 风险 |
| 抽象类与接口 | 单继承、多实现、状态与行为 | default 方法冲突、如何选型 |
| 内部类 | 成员/静态/局部/匿名四类 | 内存泄漏、final 变量捕获 |
| 枚举 | 类型安全、单例、携带数据 | 策略枚举、与常量对比 |
| 不可变类 | final 字段、防御性拷贝 | 与 Record 的关系 |
| 组合 vs 继承 | is-a vs has-a、耦合度 | "组合优于继承"原则 |
| 反射与注解 | Class 元数据、运行期解析 | 性能开销、框架如何扫描、代理关系 |
| SPI | META-INF/services、ServiceLoader 懒加载 | 上下文类加载器为何破坏双亲委派、Dubbo SPI 改进点 |
| 泛型 | 类型擦除、编译期约束 | PECS、桥接方法、为何不能 new T |
| 异常 | 受检 vs 非受检、常见异常类 | 自定义异常、finally 执行时机 |
| Stream | 惰性求值、中间/终止操作 | 方法引用、并行流陷阱、collector |
| CompletableFuture | 任务编排、异常传播、线程池 | thenApply/thenCompose、超时和取消 |
| IO/NIO | 阻塞模型、Channel/Buffer/Selector | 零拷贝、半包粘包、Netty 如何使用 |
| 序列化 | 对象到字节、版本兼容 | serialVersionUID、安全风险、替代协议 |

回答基础题时不要停留在语法定义，至少补充一个运行时行为或常见错误。

---

[← 返回知识点](知识点索引.md)

---

## 一、语言基础

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
- 现代口径：**JDK 9+ 官方不再单独发行 JRE**，模块化后用 `jlink` 按需裁剪定制运行时，"JDK 包含 JRE"的三层说法只适用于 JDK 8 时代

---

### int 和 Integer 的区别？

频次 ★★★★ · 难度 🟡

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

频次 ★★★★ · 难度 🟡

| 特性 | String | StringBuilder | StringBuffer |
|------|--------|--------------|-------------|
| 可变性 | 不可变 | 可变 | 可变 |
| 线程安全 | 是（因不可变） | 否 | 是（synchronized） |
| 性能 | 低（频繁修改时） | 高 | 中 |
| 适用场景 | 静态字符串 | 单线程动态操作 | 多线程动态操作 |

**String 不可变的原因：**
- 内部数组 `private final` 且不暴露修改方法——JDK 8 是 `char[] value`，**JDK 9+ 改为 `byte[] value` + `coder` 标记**（Compact Strings：纯 Latin-1 内容每字符 1 字节，比 UTF-16 省一半内存，"为什么改 byte[]"本身就是高频追问）
- 字符串常量池的需要（多个引用指向同一对象）
- 安全性（类加载器、网络连接等场景）
- 线程安全（不可变天然线程安全）

---

### == 和 equals 的区别？

频次 ★★★★ · 难度 🟢

**快答**
- `==` 比较基本类型的值，比较引用类型的地址
- `equals` 默认等价于 `==`，但可以被重写为比较内容
- String、Integer 等类重写了 equals 比较内容

**hashCode 和 equals 的关系：**
- 如果 `a.equals(b)` 为 true，则 `a.hashCode() == b.hashCode()` 必须为 true
- 如果 hashCode 相同，equals 不一定为 true（哈希冲突）
- 重写 equals 必须重写 hashCode，否则在 HashMap/HashSet 中会出问题

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

**常见追问**
- 为什么不能 `new BigDecimal(0.1)`？→ double 传进构造器时精度已经丢了（实际是 0.1000000000000000055…），要用字符串构造或 `BigDecimal.valueOf()`（内部走 `Double.toString`）
- `equals` 和 `compareTo` 的区别？→ `equals` 连标度一起比（`0.1` 与 `0.10` 不等），`compareTo` 只比数值；金额判等要用 `compareTo() == 0`，用 HashSet/HashMap 对 BigDecimal 去重是经典坑
- 除法为什么会抛异常？→ 除不尽（如 1/3）时不指定精度直接抛 `ArithmeticException`，必须 `divide(b, scale, RoundingMode.HALF_UP)` 显式给舍入模式

---

## 二、面向对象与设计模式

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

### 单例模式（双重检查锁定）

频次 ★★★★ · 难度 🟡

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

另一种线程安全且惰性的写法是**静态内部类单例**——线程安全由 `<clinit>` 的加锁单次语义兜底，见[JVM](JVM.md)"类加载过程"一节。

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

## 三、泛型、反射与 SPI

### 泛型是如何实现的？为什么说是"伪泛型"？

频次 ★★★ · 难度 🟡

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
- 泛型擦除会带来什么运行时开销问题？→ 基本类型泛型会被迫**自动装箱**（`List<Integer>` 存的是 `Integer` 对象不是 `int`），大量数据场景有装箱拆箱和内存开销，这也是 JDK 一直没有 `List<int>` 的根因；Project Valhalla 的值类型提案目标之一就是解决这个问题（**截至 JDK 25 仍未正式落地**，面试别把它当已发布特性讲）。
- 通配符 `? extends T` 和 `? super T` 怎么记？→ **PECS 原则**（Producer Extends, Consumer Super）：只读取（生产数据给你用）就用 `extends`，如 `List<? extends Number> src` 你能读出 Number 但不能往里加；只写入（消费你给的数据）就用 `super`，如 `List<? super Integer> dest` 你能加 Integer 但读出来只能当 Object 用。

**通用概念**：类型擦除是**编译期多态、运行期单态**的一种权衡——在保证向后兼容（Java 5 引入泛型时，老代码用 `List` 不用 `List<T>` 也能和新代码互相调用）和不修改 JVM 字节码规范的前提下实现类型安全检查。C# 的泛型是运行时具体化（reified），没有这个问题，但代价是不能像 Java 一样直接对老字节码保持兼容。

---

### 反射机制及应用场景？

频次 ★★★ · 难度 🟡

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

### Java 注解的原理？

难度 🟡

- 注解本质是继承 `Annotation` 接口的特殊接口
- 运行时注解通过反射获取时，返回的是动态代理对象（`AnnotationInvocationHandler`）
- 注解信息存储在 class 文件的属性表中（`RuntimeVisibleAnnotations`）
- `@Retention` 控制保留策略：SOURCE（仅源码）、CLASS（class 文件）、RUNTIME（运行时可反射）
- `@Target` 控制作用位置：TYPE、FIELD、METHOD、PARAMETER 等

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

## 四、异常处理

### 受检异常 vs 非受检异常？

难度 🟢

**快答**
- **受检异常（Checked Exception）**：编译器检查，必须处理，如 IOException
- **非受检异常（Unchecked Exception）**：继承 RuntimeException，可不处理

**为什么这么设计**：受检异常想把"可预期、可恢复的失败"（文件不存在、网络中断）编码进方法签名，强迫调用方表态；运行时异常代表编程错误（空指针、越界），当场恢复没有意义所以不强制。但强制处理催生了大量 catch 后吞掉的反模式，Kotlin/C# 都放弃了受检异常，Spring 把 `SQLException` 包装成非受检的 `DataAccessException` 也是同一判断。

**建议：**
- 自定义异常：如果调用者能恢复，用受检异常；如果无法恢复，用非受检异常
- 不要捕获后什么都不做（empty catch）
- 使用特定的异常类，便于定位问题

**常见追问**
- finally 一定会执行吗？→ 除 `System.exit()`、JVM 崩溃、所在线程被杀外都执行；但 **finally 里写 return 会吞掉 try 的返回值和异常**，属于禁手
- try-with-resources 的原理？→ 编译器语法糖，自动生成 finally 调 `close()`；close 抛出的异常会通过 `addSuppressed` 挂在主异常上而不是覆盖它——手写 finally close 恰好相反（close 异常覆盖业务异常），这是它的核心优势
- 异常的性能成本在哪？→ 构造异常时 `fillInStackTrace` 抓取整个调用栈最贵；用异常做正常流程控制是反模式

---

## 五、Java 8+ 函数式编程

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

## 六、IO 与序列化

### BIO、NIO、AIO 的区别？

频次 ★★★★ · 难度 🟡

**是什么**：

| 模型 | 本质 | 线程模型 | 适用场景 |
|------|------|---------|---------|
| **BIO** | 同步阻塞 | 一连接一线程，线程阻塞在 read | 连接数少且固定 |
| **NIO** | 同步非阻塞 + IO 多路复用 | 一个 Selector 线程管理成千上万连接 | 高并发网关、中间件 |
| **AIO** | 异步（内核完成后回调） | 无需自己等待就绪 | 业界极少用（见追问） |

**为什么 NIO 能撑高并发（C10K 问题）**：BIO 一万个连接就要一万个线程——仅线程栈就吃掉约 10GB（`-Xss` 默认 1MB），加上上下文切换，机器先于业务被拖死。NIO 把"等数据到达"交给内核（select/epoll），应用线程只处理**就绪**的连接，阻塞点从 N 个线程收敛到 1 个 `select()` 调用。

**常见追问**
- NIO 的"非阻塞"到底哪里非阻塞？→ read 不再等数据：没数据立即返回 0；"等就绪"这件事由 Selector 统一阻塞在 `select()` 上完成。所以 NIO 是"读写非阻塞 + 等待集中化"，不是没有阻塞。
- 为什么 Java AIO 没流行？→ Linux 上的实现用 epoll 模拟、并非内核真异步（io_uring 才是），相比 Netty 式 NIO 没有实际收益；Windows 的 IOCP 是真异步但服务器不跑 Windows。
- select/poll/epoll 的区别？→ 属于操作系统考点，见[操作系统](操作系统.md)"select/poll/epoll 区别"。

---

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

相关深挖：零拷贝（`FileChannel.transferTo` / sendfile）见[操作系统](操作系统.md)"零拷贝"；半包粘包与拆包器见[Netty与RPC](Netty与RPC.md)"TCP 粘包/拆包如何解决？"。

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

**替代方案：** Protobuf（高性能、跨语言）、JSON（Jackson、fastjson2）。注意别再推荐 fastjson 1.x——反序列化漏洞史太多，面试提它有减分风险。

**常见追问**
- serialVersionUID 有什么用？→ 反序列化时的版本校验凭据：不显式声明时由编译器按类结构哈希自动生成，类一改动它就变 → 老数据反序列化直接抛 `InvalidClassException`；显式声明后增删字段可以兼容（新增字段读出默认值，删掉的字段被忽略）
- 为什么说反序列化有安全风险？→ `readObject` 会执行对象图里各类的反序列化逻辑，攻击者用 gadget chain（如 Apache Commons Collections 链）可以达成远程代码执行；JDK 9（JEP 290，后移植到 8u121）引入反序列化过滤器，原则是**永远不反序列化不可信数据**
- transient 和 static 字段会被序列化吗？→ 都不会。transient 是显式排除；static 属于类不属于对象实例

---

## 七、面向对象深入

### 什么是内部类？内部类有哪些类型？使用场景是什么？

频次 ★★ · 难度 🟡

**是什么**：定义在另一个类内部的类。Java 支持四种内部类：

| 类型 | 定义位置 | 特点 | 典型场景 |
|------|---------|------|---------|
| **成员内部类** | 类体内、方法外 | 可访问外部类所有成员；外部类实例存在后才能创建 | 紧密关联外部类逻辑的辅助类 |
| **静态内部类** | 类体内，`static` 修饰 | 不依赖外部类实例，只能访问外部类静态成员 | 与外部类工具性关联（如 Builder 模式） |
| **局部内部类** | 方法体内 | 作用域局限于方法内，可访问局部变量（需 final/effectively final） | 临时封装方法内逻辑 |
| **匿名内部类** | 表达式位置（`new 接口(){}`） | 隐式继承类或实现接口，无类名，最简洁 | 回调、事件监听、一次性实现 |

**为什么用静态内部类更安全**：非静态内部类隐式持有外部类 `this` 引用，如果内部类生命周期长于外部类（如被提交到线程池），会导致**外部类无法被 GC 回收**（内存泄漏）。静态内部类不持有外部引用，更安全。

**常见追问**：匿名内部类为什么只能访问 final 局部变量？→ Java 通过**值拷贝**将局部变量复制到匿名内部类对象中，如果变量可变，内外不一致会产生语义歧义——所以强制 final/effectively final，保证内外看到的值一致。

### this 和 super 关键字在 Java 中的作用和区别是什么？

**是什么**：`this` 引用当前对象实例，`super` 引用父类部分。`this()` 调用本类其他构造器，`super()` 调用父类构造器（必须放构造器首行，两者不能同时出现）。

**常见用法**：`this.field`（区分同名参数和实例变量）、`super.method()`（调用被重写的父类方法）、`super()`（子类构造器默认隐式调用父类无参构造器，父类没有无参构造器时必须显式调用）。

### Java 中的枚举类型是如何定义的？枚举相比常量有什么优势？

频次 ★★ · 难度 🟡

**是什么**：`enum` 关键字定义枚举类型，每个枚举值是该类型的一个 `public static final` 实例。`enum` 默认继承 `java.lang.Enum`，不可再继承其他类，但可实现接口。

**相比常量（`public static final int`）的优势**：
- **类型安全**：编译器检查类型，不会把 `Color.RED` 误传成其他 int 值
- **命名空间**：枚举值自带所属类型，不会像 int 常量那样全局污染
- **可携带数据和行为**：枚举可以有字段、构造器、方法
- **switch 友好**：Java 7+ switch 支持枚举，IDE 补全所有 case
- **单例天然保证**：每个枚举值在 JVM 中只有一个实例，是实现单例的最安全方式

**高级用法**：枚举构造器定义字段（如 `RED(0xFF0000)`），每个枚举值实现接口的不同行为（策略枚举），`values()` 遍历所有值。

### 如何设计一个不可变类？

频次 ★★ · 难度 🟡

**是什么**：不可变类创建后其状态（字段值）不可改变。Java 的 `String`、`Integer`、`BigDecimal`、`Record` 都是不可变类。

**设计规则**：
1. 类声明为 `final`，防止子类破坏不可变性
2. 所有字段 `private final`
3. 不提供 setter 方法
4. 如果字段是可变对象引用，**防御性拷贝**：构造器拷贝传入对象，getter 返回拷贝而非原始引用
5. 可变操作返回新对象而非修改当前对象（`String.substring()` 返回新 `String`）

**为什么不可变类线程安全**：状态不可变 → 不存在竞态条件 → 多线程随意共享，不需要同步。这是 `String` 作为 HashMap key 的原因——hashCode 可以缓存，不怕被改。

**常见追问**：`final` 修饰引用类型字段，引用不能变但对象内容能变 → 需要防御性拷贝，或使用 `List.copyOf()` / `Collections.unmodifiableList()` 包装。

### 组合与继承各有什么优缺点？什么情况下选择组合而不是继承？

频次 ★★ · 难度 🟡

**是什么**：继承用 `extends` 复用父类代码，组合在类内部持有另一个类的实例引用。

| 维度 | 继承 | 组合 |
|------|------|------|
| 关系 | `is-a`（子类是父类） | `has-a`（包含关系） |
| 耦合度 | 高（子类依赖父类实现细节） | 低（只依赖接口） |
| 灵活性 | 编译时确定，不可变 | 运行时动态替换 |
| 封装性 | 破坏封装（子类访问父类 protected 成员） | 不破坏封装 |
| 扩展性 | 只能单继承 | 可组合多个行为 |

**"组合优于继承"原则**：继承是强耦合——父类改实现子类可能跟着出问题。组合通过接口 + 委托实现更灵活的复用。只有当确实存在"is-a"关系且父类设计为继承而设计时（如模板方法模式），才用继承。

### 泛型擦除是什么？它如何影响泛型运行时的行为？

频次 ★★ · 难度 🟡

**是什么**：Java 泛型通过**编译期类型擦除**实现。编译器将泛型类型参数替换为边界类型（默认 `Object`），在需要时插入强制类型转换。编译后字节码中不保留泛型信息。

**影响**：
- `List<String>` 和 `List<Integer>` 的 Class 对象相同（都是 `List.class`）——无法通过 `instanceof` 区分泛型参数类型
- 不能 `new T()` 或 `new T[]`（运行时不知道 T 是什么）
- 静态字段不能使用泛型类型参数（类级共享，与泛型实例化矛盾）
- bridge method：编译器为保持多态自动生成桥接方法

**常见追问**：泛型擦除为什么要保留？→ 兼容 Java 5 之前的原始类型（raw type），让旧代码不做任何修改就能在新 JVM 跑。C# 的泛型是运行时保留的（reified generics），各有取舍。

---

## 八、异常与函数式补充

### Java 常见的异常类有哪些？

频次 ★★ · 难度 🟡

**运行时异常（RuntimeException，非受检）**：
- `NullPointerException`：对象为 null 时调用方法/访问字段
- `IndexOutOfBoundsException`：数组/集合索引越界（`ArrayIndexOutOfBoundsException`、`StringIndexOutOfBoundsException`）
- `IllegalArgumentException`：方法参数不合法（含 `NumberFormatException`）
- `IllegalStateException`：对象状态不满足方法调用条件
- `ClassCastException`：类型转换错误
- `ConcurrentModificationException`：迭代集合时被结构修改（fail-fast）
- `ArithmeticException`：算术异常（如除零）

**受检异常（Checked Exception，编译期强制处理）**：
- `IOException`：I/O 操作失败（含 `FileNotFoundException`）
- `SQLException`：数据库操作失败
- `ClassNotFoundException`：`Class.forName()` 找不到类
- `InterruptedException`：线程被中断

**Error（不要求处理，通常无法恢复）**：
- `OutOfMemoryError`：堆内存耗尽
- `StackOverflowError`：递归过深
- `NoClassDefFoundError`：编译时存在但运行时找不到的类

### 如何在 Java 中自定义异常？

**是什么**：继承 `Exception`（受检异常）或 `RuntimeException`（非受检异常），提供构造器，可选添加错误码等额外信息。`throw` 抛出异常实例，`throws` 声明方法可能抛出的异常类型。

### Java 中的方法引用（Method References）是什么？如何使用？

频次 ★★ · 难度 🟡

**是什么**：当 Lambda 表达式只是调用一个已存在的方法时，可用方法引用作为更简洁的替代。`::` 语法。

| 类型 | 语法 | Lambda 等价 |
|------|------|------------|
| 静态方法引用 | `ClassName::staticMethod` | `(args) -> ClassName.staticMethod(args)` |
| 实例方法引用（特定对象） | `instance::method` | `(args) -> instance.method(args)` |
| 实例方法引用（任意对象） | `ClassName::instanceMethod` | `(obj, args) -> obj.instanceMethod(args)` |
| 构造器引用 | `ClassName::new` | `(args) -> new ClassName(args)` |

**实例**：`list.forEach(System.out::println)`、`stream.map(String::toUpperCase)`、`stream.collect(Collectors.toCollection(ArrayList::new))`。

---

[← 返回知识点](知识点索引.md)

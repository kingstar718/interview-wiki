# Java基础

## 面试追问地图

- [基本类型与包装类型](#int-和-integer-的区别) — 存储、默认值、泛型、自动装箱 → Integer 缓存、拆箱 NPE、比较陷阱
- [String 不可变](#stringstringbuilderstringbuffer-区别) — final、字符存储、哈希缓存、安全性 → 字符串常量池、intern、拼接优化
- [equals/hashCode](#和-equals-的区别) — 相等契约、哈希容器定位 → 只重写 equals 会怎样、可变 Key 风险
- [抽象类与接口](#抽象类和接口的区别) — 单继承、多实现、状态与行为 → default 方法冲突、如何选型
- [反射与注解](#反射机制及应用场景) — Class 元数据、运行期解析 → 性能开销、框架如何扫描、代理关系
- [SPI](#spi-机制serviceloader-是怎么找到实现类的) — META-INF/services、ServiceLoader 懒加载 → 上下文类加载器为何破坏双亲委派、Dubbo SPI 改进点
- [泛型](#泛型是如何实现的为什么说是伪泛型) — 类型擦除、编译期约束 → PECS、桥接方法、为何不能 new T
- [Stream](#stream-api) — 惰性求值、中间/终止操作 → 并行流线程池、副作用、性能边界
- [CompletableFuture](#completablefuture-异步编程) — 任务编排、异常传播、线程池 → thenApply/thenCompose、超时和取消
- [IO/NIO](#bionioaio-的区别) — 阻塞模型、Channel/Buffer/Selector → 零拷贝、半包粘包、Netty 如何使用
- [序列化](#序列化和反序列化) — 对象到字节、版本兼容 → serialVersionUID、安全风险、替代协议
- [面向对象与设计模式](#面向对象六大设计原则) — 六大原则、单例、策略、代理 → DCL volatile、设计原则落地
- [内部类](#内部类) — 四种内部类、语法糖、外部类引用 → 内存泄漏、final 变量拷贝、静态内部类单例
- [Enum 枚举](#enum-枚举) — 本质是 final 类、ordinal、values() → EnumMap/EnumSet、反射限制、单例场景
- [异常处理](#受检异常-vs-非受检异常) — 受检/非受检、try-with-resources → 异常链、性能开销、最佳实践
- [不可变类](#如何设计一个不可变类) — final 字段、防御性拷贝 → 与 Record 的关系
- [组合 vs 继承](#组合与继承各有什么优缺点什么情况下选择组合而不是继承) — is-a vs has-a、耦合度 → "组合优于继承"原则

回答基础题时不要停留在语法定义，至少补充一个运行时行为或常见错误。

---

## 一、语言基础

### JVM、JDK、JRE 三者关系？

频次 ★★★ · 难度 🟢

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

### Object 类有哪些重要方法？

频次 ★★★★ · 难度 🟡

**是什么**：`java.lang.Object` 是所有 Java 类的根，定义了 9 个核心方法（JDK 8 口径）：

- `equals(Object)` — 判断对象内容相等。需要自定义相等逻辑时重写（如 HashMap key）
- `hashCode()` — 返回哈希码。重写 equals 时必须重写
- `toString()` — 返回字符串表示。日志/调试时方便查看对象状态
- `clone()` — 创建并返回副本。需要支持克隆时重写（浅拷贝，需实现 Cloneable）
- `getClass()` — 返回运行时 Class 对象。一般不重写（final）
- `notify()` / `notifyAll()` / `wait()` — 线程间通信。并发编程中使用（final，不可重写）
- `finalize()` — GC 前回调。**已废弃**（JDK 9 deprecated，JDK 18 标记移除）

**为什么这么设计**：Object 的方法是所有对象的"公共协议"——hashCode/equals 支撑哈希容器，wait/notify 支撑线程协作，toString 支撑调试。这些方法放在 Object 层，保证了所有 Java 对象都有这些基础能力，不用每个类自己声明。

**常见追问**
- equals 和 hashCode 的契约是什么？→ ①equals 为 true 则 hashCode 必须相等（反向不成立）；②同一个对象的 hashCode 多次调用必须一致（前提是 equals 用到的字段没变）。违反契约会导致 HashMap/HashSet 行为异常
- finalize 为什么被废弃？→ ①执行时机不确定（全靠 GC 心情），不能指望它释放资源；②有 finalize 的对象 GC 要多一轮才能回收，严重拖慢 GC；③finalize 中抛异常会被忽略，静默失败。替代方案：try-with-resources、Cleaner（JDK 9+）
- clone 的浅拷贝问题？→ `Object.clone()` 默认是浅拷贝，引用字段只复制引用不复制对象。实现深拷贝需要递归 clone 引用字段，或走序列化深拷贝，见[深拷贝和浅拷贝的区别](#深拷贝和浅拷贝的区别)

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

**一句话**：单线程用 StringBuilder，多线程用 StringBuffer，不改动用 String。

- **String**：不可变，线程安全（因不可变），适合静态字符串。频繁修改性能低
- **StringBuilder**：可变，非线程安全，性能最高。适合单线程动态操作
- **StringBuffer**：可变，synchronized 保证线程安全，性能中等。适合多线程动态操作

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

频次 ★★★★ · 难度 🟡

**Java 只有值传递！**
- 基本类型：传递值的副本，修改不影响原值
- 引用类型：传递引用的副本，通过副本可修改对象内容，但修改引用指向不影响原引用

**常见追问**
- String 传参能改吗？→ String 是不可变对象，方法内 `str = "new"` 只是改了局部引用的指向，外部的引用不受影响。但如果是 `StringBuilder` 传参，方法内 `sb.append("x")` 会改变外部对象的内容——因为改的是对象本身，不是引用
- 数组传参能改吗？→ 能改元素内容（`arr[0] = 1`），但不能改引用指向（`arr = new int[10]`）——与对象引用传参的规则一致

---

### static 关键字的四种用法

频次 ★★★ · 难度 🟢

- **静态变量**：类级别共享，所有实例共用
- **静态方法**：不依赖实例，不能访问非静态成员
- **静态代码块**：类加载时执行一次，初始化静态资源
- **静态内部类**：不依赖外部类实例，避免内存泄漏

**常见追问**
- 静态代码块和构造代码块谁先执行？→ 静态代码块 > 构造代码块（`{}`）> 构造器。静态代码块只在类加载时执行一次，构造代码块每次 new 都执行
- 子类父类静态代码块执行顺序？→ 父类静态 → 子类静态 → 父类构造代码块 → 父类构造器 → 子类构造代码块 → 子类构造器
- static 方法能重写吗？→ 不能。static 方法属于类，不是实例方法，没有多态。子类可以定义同名 static 方法隐藏父类方法（叫"隐藏"不叫"重写"），调用哪个版本取决于引用类型而非实际对象类型

---

### final 关键字的三种用法

频次 ★★★★ · 难度 🟢

**是什么**：`final` 修饰类/方法/变量，分别表示"不可继承/不可重写/不可修改"。

- **final 类**：不可被继承。如 `String`、`Integer`、安全框架的核心类
- **final 方法**：不可被子类重写。如模板方法模式中锁定算法骨架、防止子类破坏不变量
- **final 变量**：赋值后不可改。常量（`static final`）、局部变量传递给匿名内部类

**为什么这么设计**：三者共同点是"锁死"，但设计意图不同——final 类防继承破坏（安全），final 方法防重写破坏（正确性），final 变量防修改（线程安全+JMM 保证）。

**常见追问**
- final 变量和普通变量的 JMM 区别？→ `final` 字段在构造函数中正确初始化后，JMM 保证其他线程不需要同步就能看到正确的值（final 字段语义）。这是不可变对象线程安全的基础——前提是构造函数中 this 没有逸出
- blank final 是什么？→ 声明时未赋值的 final 字段，必须在构造函数中赋值（每个构造器都要覆盖所有 blank final）。这允许 final 字段的值依赖构造器参数，同时保持不可变性
- final 方法能提高性能吗？→ 早期 JVM 会对 final 方法做内联优化，但现代 JIT 通过类层次分析（CHA）能自动识别"事实上 final"的方法，手动加 final 的收益很小。所以 final 的主要价值是语义，不是性能

---

### BigDecimal 为什么比 double 更适合金额计算？

频次 ★★★ · 难度 🟢

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

### Enum 枚举

频次 ★★★★★ · 难度 🟢

**是什么**：enum 是 Java 5 引入的语法糖，编译后生成继承 `java.lang.Enum` 的 final 类。枚举常量实质是类的静态 final 实例。

**核心特性：**
- 构造器默认 private，在 static 块中按声明顺序初始化所有常量
- `ordinal()` 返回声明顺序（0-based），依赖 ordinal 的代码在增删中间常量时静默出错
- `values()` 是编译器生成的静态方法（反射不可见），返回所有常量数组
- `valueOf(String)` 通过 `name()` 匹配枚举常量
- enum 可以实现接口、定义抽象方法让每个常量提供不同实现
- 编译器禁止显式继承 Enum，但允许 implements 接口

```java
public enum Status {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款");

    private final int code;
    private final String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Status fromCode(int code) {
        for (Status s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
```

**EnumMap / EnumSet：**
- `EnumMap`：内部用数组（ordinal 做下标），比 HashMap 更快更省内存。key 必须同类型枚举
- `EnumSet`：内部用位向量（64 位 long 或 long[]），比 HashSet 快几个数量级。适合枚举组合判断
- 两者迭代顺序都是枚举声明顺序

**常见追问：**
- enum 可以被反射创建吗？→ 不能。`Constructor.newInstance()` 对 enum 类型抛 `IllegalArgumentException`
- enum 线程安全吗？→ 枚举常量在 static 块初始化，由 JVM 类加载保证线程安全
- 单例模式为什么要用 enum？→ 天然防反射攻击、防序列化破坏（Enum 的 readObject 返回同一实例）
- switch 可以用 enum 吗？→ 可以且推荐，编译器检查是否覆盖所有分支
- 枚举能序列化吗？→ 写的是 name，读时 `valueOf(name)`，保证单例；即使 serialVersionUID 不同也能反序列化

---

## 二、面向对象与设计模式

### 面向对象六大设计原则？

频次 ★★★ · 难度 🟡

- **单一职责（SRP）**：一个类只负责一项职责
- **开闭原则（OCP）**：对扩展开放，对修改封闭
- **里氏替换（LSP）**：子类对象能替换父类对象
- **接口隔离（ISP）**：接口应该小而专
- **依赖倒置（DIP）**：依赖抽象而非具体实现
- **最少知识（LoD）**：只与直接朋友交互

**多态的体现：** 方法重载（编译时）、方法重写（运行时）、接口实现、向上/向下转型

**常见追问**
- 里氏替换的经典违反案例？→ 正方形继承长方形——正方形设宽时高也变，违反父类"宽高独立修改"的预期。根本原因是"正方形 is-a 长方形"只在数学上成立，在可变对象的行为语义上不成立
- 依赖倒置怎么落地？→ 关键不是"用接口"，而是"接口的归属权"——接口由使用方定义，实现方去适配。比如 Controller 需要的 `UserService` 接口由业务层定义，不是由 DAO 层定义

---

### 抽象类和接口的区别？

频次 ★★★★ · 难度 🟡

**抽象类**
- 关键字：`extends`，单继承
- 成员变量：可有实例变量
- 方法：可有具体实现
- 构造器：有
- 设计意图：is-a 关系，代码复用

**接口**
- 关键字：`implements`，多实现
- 成员变量：只能有常量（`public static final`）
- 方法：Java 8 前只能有抽象方法，Java 8+ 可有 default/static，Java 9+ 可有 private
- 构造器：无
- 设计意图：has-a/can-do 能力，定义规范

**常见追问**
- default 方法冲突怎么解决？→ 实现两个接口的同名 default 方法，编译期强制子类重写；子类可通过 `接口名.super.method()` 选择调用哪一方的实现
- 什么时候用抽象类而不是接口？→ 需要共享状态（实例变量）和构造器逻辑时用抽象类（模板方法模式是典型场景）；只定义行为契约用接口。Java 8+ 接口有了 default 方法后，抽象类的存在空间被压缩，但"状态共享"仍是抽象类不可替代的
- 接口里能定义常量吗？→ 能（`public static final`），但不推荐用接口做常量池——接口是用来定义行为的，用接口暴露常量是反模式（Effective Java 第 22 条）

---

### 方法重载和方法重写的区别？

频次 ★★★★ · 难度 🟡

**是什么**：重载（Overload）是编译期多态——同一个类中方法名相同、参数列表不同；重写（Override）是运行期多态——子类重新定义父类的方法。

**重载（Overload）**
- 发生在同一个类（或父子类），参数列表必须不同（类型/个数/顺序）
- 返回值可以不同，访问修饰符和异常可以不同
- 编译期决定调用哪个版本

**重写（Override）**
- 发生在子类重写父类方法，参数列表必须相同
- 返回值相同或协变（子类型），访问修饰符不能更严格，异常不能抛出更宽泛的受检异常
- 运行期决定调用哪个版本，推荐加 `@Override` 注解

**常见追问**
- 重载能只靠返回值类型区分吗？→ 不能。JVM 的方法签名不包括返回值类型，编译期无法区分 `int f()` 和 `String f()`——调用方不强制接收返回值时，编译器不知道调用哪个
- `@Override` 注解有什么用？→ ①编译期检查：确保方法确实重写了父类方法（如果拼错或参数不对，编译器报错）；②文档作用：明确告诉读者这是重写。不加也能重写，但容易因拼写错误导致"以为重写了其实没有"
- 静态方法能被重写吗？→ 不能。static 方法属于类而非实例，子类定义同名 static 方法叫"隐藏"（hiding），调用哪个版本取决于**引用类型**，不是实际对象类型——所以没有多态

---

### 内部类

频次 ★★★★★ · 难度 🟢

**是什么**：定义在另一个类内部的类。Java 有四种内部类，本质是编译器语法糖——编译后全部提升为独立的顶级 class 文件（`Outer$Inner.class`）。

**成员内部类**
- 定义在类成员位置，必须通过 `outer.new Inner()` 创建
- 依赖外部类实例 · 不可定义静态成员（Java 16 前）
- 典型用途：专属于外部类的逻辑，如集合的 Iterator

**静态内部类**
- 定义在类成员位置 + `static`，可独立创建 `new Outer.StaticInner()`
- 不依赖外部类实例 · 可定义静态成员
- 典型用途：单例持有者、Builder、分组配置

**局部内部类**
- 定义在方法/代码块内，需在外部类方法中创建
- 依赖外部类实例 · 不可定义静态成员
- 典型用途：极少用，仅在该方法内复用

**匿名内部类**
- 方法内 `new` 接口/类时直接定义，需在外部类方法中创建
- 依赖外部类实例 · 不可定义静态成员
- 典型用途：事件回调、Runnable、Comparator

**关键特性：**
- 成员内部类持有外部类 `this` 引用，可以访问外部类 private 成员；这也意味着外部类无法被 GC（内部类对象存活时），是内存泄漏的常见来源
- 匿名内部类引用的局部变量必须是 `final` 或 effectively final（JDK 8+）
- 静态内部类不持有外部类引用，不会导致内存泄漏——Android/Handler 泄漏的修复方式就是改成静态内部类
- 所有内部类编译后独立成 class 文件：`Outer$Inner.class`

**常见追问：**
- 为什么内部类访问的局部变量必须是 final？→ 内部类对象可能在方法返回后才执行（如回调），那时局部变量已出栈。Java 的解决方案是在内部类中拷贝一份，用 final 保证拷贝和原值始终一致
- 内部类会导致内存泄漏吗？→ 会。成员内部类隐式持有外部类引用，如果内部类生命周期比外部类长（如匿名 Runnable 提交到线程池），外部类就无法被 GC。修复：改用静态内部类 + 弱引用
- 静态内部类单例为什么线程安全？→ 见[单例模式](#单例模式双重检查锁定)

### this 和 super 关键字在 Java 中的作用和区别是什么？

**是什么**：`this` 引用当前对象实例，`super` 引用父类部分。`this()` 调用本类其他构造器，`super()` 调用父类构造器（必须放构造器首行，两者不能同时出现）。

**常见用法**：`this.field`（区分同名参数和实例变量）、`super.method()`（调用被重写的父类方法）、`super()`（子类构造器默认隐式调用父类无参构造器，父类没有无参构造器时必须显式调用）。

**常见追问**：`this()` 和 `super()` 为什么必须放构造器首行？→ Java 规定子类构造器必须先完成父类初始化才能执行自己的逻辑——语法层面的"父类优先"原则。这也解释了为什么两者不能同时出现：一个构造器只能有一个"首行"。

---

### Java 创建对象有哪几种方式？

频次 ★★★ · 难度 🟡

- **new 关键字**：调用构造器，最常用，紧密耦合
- **反射（Constructor.newInstance）**：调用构造器，灵活，用于框架
- **clone()**：不调用构造器，需实现 Cloneable，浅拷贝
- **反序列化**：不调用构造器，需实现 Serializable
- **工厂模式**：在方法内调用构造器，解耦，隐藏创建逻辑

**常见追问**
- 反序列化为什么不调构造器？→ 对象流在 native 层直接分配内存并逐字段赋值，走的是 `ObjectInputStream` 内部机制，不是 `Constructor.newInstance()`；这也是为什么反序列化能绕过构造器里的校验逻辑，有安全风险
- clone() 和反序列化谁更快？→ clone 更快（native 内存拷贝），但两者都是浅拷贝且不调构造器，都需要实现标记接口
- 有没有不调构造器就能创建对象的方式？→ `Unsafe.allocateInstance()`（JDK 内部 API，不推荐）和反序列化都可以——这也说明了构造器不是创建对象的唯一路径

---

### 深拷贝和浅拷贝的区别？

频次 ★★★ · 难度 🟡

- **浅拷贝**：只复制对象本身和值类型字段，引用类型字段复制的是引用地址（新旧对象共享同一个引用对象）
- **深拷贝**：递归复制对象及其所有引用类型字段，生成完全独立的新对象

**实现深拷贝的三种方式：**
1. 实现 Cloneable 接口并递归 clone 引用字段
2. 序列化 + 反序列化（需实现 Serializable）
3. 手动递归复制

**常见追问**
- 序列化深拷贝的缺点？→ ①慢（IO + 反射）；②所有对象必须实现 Serializable；③transient 字段会丢失；④循环引用可能栈溢出。适合复杂对象图的深度拷贝，简单对象推荐手动递归复制
- clone() 为什么被诟病？→ Cloneable 是标记接口但不声明 clone 方法（设计缺陷），clone() 是 protected 方法，且默认为浅拷贝——几乎所有行为都靠"约定"而非"契约"

---

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

---

### 组合与继承各有什么优缺点？什么情况下选择组合而不是继承？

频次 ★★ · 难度 🟡

**是什么**：继承用 `extends` 复用父类代码，组合在类内部持有另一个类的实例引用。

**继承**
- 关系：`is-a`（子类是父类）
- 耦合度：高（子类依赖父类实现细节）
- 灵活性：编译时确定，不可变
- 封装性：破坏封装（子类访问父类 protected 成员）
- 扩展性：只能单继承

**组合**
- 关系：`has-a`（包含关系）
- 耦合度：低（只依赖接口）
- 灵活性：运行时动态替换
- 封装性：不破坏封装
- 扩展性：可组合多个行为

**"组合优于继承"原则**：继承是强耦合——父类改实现子类可能跟着出问题。组合通过接口 + 委托实现更灵活的复用。只有当确实存在"is-a"关系且父类设计为继承而设计时（如模板方法模式），才用继承。

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

频次 ★★★ · 难度 🟡

**策略模式**：封装一组可互换的算法，运行时选择
- 场景：支付方式选择（支付宝/微信/银行卡）、排序算法切换

**责任链模式**：请求沿处理者链传递，直到被处理
- 场景：请求校验链（登录 → 权限 → 频率限制）、审批流程

两者共同目的：消除复杂的 if-else，提高扩展性

**常见追问**
- 策略和责任链怎么区分？→ 策略是"选一个执行"，责任链是"逐个试直到有人处理"。策略只有一个实现被调用，责任链可能多个处理器都参与
- 策略模式不就用一个 Map 存实现类吗？→ 核心不是怎么存，而是"策略的互换性对调用方透明"——调用方不关心具体是哪个算法，只依赖策略接口。Map 只是存储方式，策略模式的价值在于封装变化

---

### 代理模式 vs 适配器模式

频次 ★★ · 难度 🟢

- **代理模式**：控制对对象的访问，添加额外功能（如 Spring AOP）
- **适配器模式**：转换接口，让不兼容的类协同工作

**常见追问**
- 代理和适配器的根本区别？→ 代理和被代理对象实现**同一个接口**，对调用方透明；适配器实现的是**目标接口**（和被适配对象不同），目的是把不兼容的接口转成调用方需要的接口
- 装饰器模式和代理模式怎么分？→ 装饰器是"增强"（层层包装，如 Java I/O 的 `BufferedInputStream`），代理是"控制访问"（如延迟加载、权限检查）。代理通常在编译期/启动期生成（动态代理），装饰器在运行时手动组合

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

**常见追问**
- 反射的性能开销有多大？→ 比直接调用慢 1~2 个数量级（setAccessible 检查、类型包装、数组遍历参数）；优化三板斧：①缓存 Method/Field 对象不重复查；②`setAccessible(true)` 跳过安全检查；③批量操作时用 MethodHandle（Java 7+）或 VarHandle（Java 9+）比反射快
- `setAccessible(true)` 在模块系统下还能用吗？→ JDK 9+ 强封装下反射访问 `java.*` 内部 API 会抛 `InaccessibleObjectException`；JVM 参数 `--add-opens` 可临时放开，但 JDK 26 起 Final Mean Final 将进一步收紧
- 反射和动态代理的关系？→ 动态代理底层就是反射——`Proxy.newProxyInstance()` 生成的代理类，在 `InvocationHandler.invoke()` 里通过 `Method.invoke()` 调用目标方法

---

### Java 注解的原理？

频次 ★★★ · 难度 🟡

- 注解本质是继承 `Annotation` 接口的特殊接口
- 运行时注解通过反射获取时，返回的是动态代理对象（`AnnotationInvocationHandler`）
- 注解信息存储在 class 文件的属性表中（`RuntimeVisibleAnnotations`）
- `@Retention` 控制保留策略：SOURCE（仅源码）、CLASS（class 文件）、RUNTIME（运行时可反射）
- `@Target` 控制作用位置：TYPE、FIELD、METHOD、PARAMETER 等

**常见追问**
- 注解能继承吗？→ `@Inherited` 元注解只能让子类继承父类上的注解，接口实现和重写方法不会继承注解——这是 Spring `@Transactional` 在自调用时失效的底层原因之一
- 重复注解怎么实现？→ Java 8 引入 `@Repeatable`，把多个同类型注解包装进一个容器注解；`getAnnotationsByType()` 能直接拿到数组
- 注解处理器（APT）和运行时反射有什么区别？→ APT 在编译期处理（如 Lombok、MapStruct），生成新代码，运行时零开销；运行时反射在 JVM 中动态获取，有性能开销但更灵活，是两个完全不同的阶段

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

**JDK ServiceLoader**
- 配置格式：一行一个类名
- 获取方式：只能全量迭代
- IoC：无
- AOP：无
- 失败表现：某个实现类加载失败，整个迭代抛异常

**Dubbo ExtensionLoader**
- 配置格式：KV（`dubbo=com.xxx.DubboProtocol`）
- 获取方式：按名取 `getExtension("dubbo")`，按需实例化
- IoC：扩展点之间可 setter 注入（自适应扩展）
- AOP：Wrapper 类自动层层包装
- 失败表现：单个扩展失败不影响其他，报错点名扩展名

**常见追问**：
- JDBC 4.0 之后为什么不用写 `Class.forName` 了？→ DriverManager 静态初始化时用 ServiceLoader 自动发现驱动；之前的 Class.forName 是靠驱动类静态块里 `registerDriver` 完成注册
- Spring Boot 自动配置和 SPI 是什么关系？→ 思想同源："配置文件登记 + 反射加载"，只是文件换成 `spring.factories`（2.7+ 为 `AutoConfiguration.imports`），加载器换成 SpringFactoriesLoader，还叠加了条件注解按需生效，见[SpringBoot](SpringBoot.md)自动配置原理
- ServiceLoader 线程安全吗？→ 不安全（Javadoc 明确标注），providers 缓存无同步，多线程共享要外部加锁

**通用概念**：SPI 是**控制反转在"发现实现"环节的形态**——使用方不 new 具体实现，由约定/容器反向提供。同一模式：Spring IoC（见[Spring](Spring.md)）、SLF4J 日志门面找绑定、K8s 的 CNI/CSI 插件机制。

---

## 四、异常处理

### 受检异常 vs 非受检异常？

频次 ★★★★ · 难度 🟢

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

### try-with-resources 的原理和最佳实践

频次 ★★★★ · 难度 🟡

**是什么**：Java 7 引入的语法糖，实现了 `AutoCloseable` 接口的资源可以在 try 块结束时自动关闭，无需手动 finally。

```java
// 传统写法：close() 异常会覆盖业务异常
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    String line = br.readLine();
} finally {
    if (br != null) br.close();
}

// try-with-resources（JDK 7+）：close 异常挂在主异常上
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line = br.readLine();
}
```

**为什么这么设计**：解决两个痛点：①忘记 close 导致资源泄漏；②手写 finally close 时，close 抛出的异常会**覆盖** try 块中的业务异常，导致排查只能看到 close 失败而看不到真正的业务问题。try-with-resources 用 suppressed 异常机制解决了后者。

**常见追问**
- 多个资源关闭顺序？→ 后声明的先关闭（栈序，逆序关闭）。`try (a; b; c) { }` 关闭顺序是 c → b → a
- 什么类可以用？→ 实现了 `AutoCloseable`（JDK 7+）的类。常见：InputStream/OutputStream、Reader/Writer、Connection/Statement/ResultSet
- 和手动 finally close 的核心区别？→ close 异常不覆盖业务异常，而是通过 `addSuppressed` 挂在主异常上，排查时既能看到业务异常也能看到 close 异常

---

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

**常见追问**：受检 vs 非受检怎么选？→ 调用方能恢复的用受检（如文件不存在可提示用户重试），无法恢复的用非受检（如参数校验失败）。受检异常强制表态是好事也是坏事——太多受检异常会让调用方 catch 后吞掉，反而降低代码质量

---

## 五、Java 8+ 函数式编程

Java 17/21/25 的 Record、密封类、模式匹配和虚拟线程见 [Java现代特性](Java现代特性.md)。

### Lambda 表达式和函数式接口

频次 ★★★★ · 难度 🟡

**Lambda 语法：**
```java
// 单表达式
(parameters) -> expression

// 多语句
(parameters) -> { statements; return value; }
```

**常见函数式接口：**
- `Predicate<T>` — `boolean test(T)` — 条件判断
- `Function<T,R>` — `R apply(T)` — 转换
- `Consumer<T>` — `void accept(T)` — 消费
- `Supplier<T>` — `T get()` — 供给

**常见追问**
- 为什么 Lambda 只能用 effectively final 的局部变量？→ Lambda 表达式本质是匿名内部类的语法糖，变量通过值拷贝传递；如果变量可变，内外不一致会导致语义歧义——所以强制 final/effectively final
- Lambda 序列化问题？→ Lambda 表达式默认不序列化，除非用 `(Function & Serializable) obj -> ...` 交叉类型写法；但序列化 Lambda 依赖 JVM 实现细节，跨版本不稳定
- 方法引用和 Lambda 有性能差异吗？→ 几乎无差异——两者编译后都是 `invokedynamic` 指令，由 JVM 在运行时生成相同的实现类

### Java 中的方法引用（Method References）是什么？如何使用？

频次 ★★ · 难度 🟡

**是什么**：当 Lambda 表达式只是调用一个已存在的方法时，可用方法引用作为更简洁的替代。`::` 语法。

- **静态方法引用**：`ClassName::staticMethod` → `(args) -> ClassName.staticMethod(args)`
- **实例方法引用（特定对象）**：`instance::method` → `(args) -> instance.method(args)`
- **实例方法引用（任意对象）**：`ClassName::instanceMethod` → `(obj, args) -> obj.instanceMethod(args)`
- **构造器引用**：`ClassName::new` → `(args) -> new ClassName(args)`

**实例**：`list.forEach(System.out::println)`、`stream.map(String::toUpperCase)`、`stream.collect(Collectors.toCollection(ArrayList::new))`。

---

### Stream API

频次 ★★★★ · 难度 🟡

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

**常见追问**
- parallelStream 为什么可能更慢？→ ①线程切换和结果合并的开销可能超过计算本身；②默认使用公共 ForkJoinPool，其他并行任务会互相影响；③数据量小或操作链短时并行没有收益——必须用 JMH 实测，不能凭直觉
- findFirst 和 findAny 有什么区别？→ 串行流两者行为一样；并行流中 `findFirst` 强制按顺序找第一个（性能受限），`findAny` 返回任意一个（更自由更快）。需要顺序确定性用 `findFirst`，只关心有没有用 `findAny`
- 短路操作有哪些？→ `limit()`、`findFirst()`、`findAny()`、`anyMatch()`、`allMatch()`、`noneMatch()`——它们不消费整个流，一旦条件满足立即终止

---

### Optional 类

频次 ★★★ · 难度 🟢

```java
Optional<String> opt = Optional.ofNullable(value);
opt.orElse("default");          // 为空时返回默认值
opt.orElseGet(() -> "computed"); // 为空时执行函数
opt.ifPresent(v -> println(v));  // 有值时执行
opt.map(String::toUpperCase).orElse("N/A"); // 链式调用
```

**常见追问**
- orElse 和 orElseGet 的陷阱？→ `orElse()` 参数**总是会执行**（即使 Optional 有值），`orElseGet()` 只在为空时执行。所以昂贵操作（如查数据库）必须用 `orElseGet()`，否则白白浪费性能
- Optional 为什么不应该做字段？→ ①Optional 没实现 Serializable；②有额外对象创建开销；③字段为 null 本身就是合法的"未设置"语义，包装一层 Optional 只是把 null 藏起来——Optional 的设计意图是**返回值类型**，不是字段或参数

---

### CompletableFuture 异步编程

频次 ★★★★ · 难度 🔴

```java
CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "result1", executor);
CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "result2");

// 组合两个结果
cf1.thenCombine(cf2, (r1, r2) -> r1 + r2)
   .thenAccept(System.out::println);
```

相比 Future 的优势：支持回调、组合编排、不需要阻塞等待结果

**常见追问**
- thenApply 和 thenCompose 的区别？→ `thenApply` 是 `T → U` 的普通映射（类似 `map`），`thenCompose` 是 `T → CompletableFuture<U>` 的扁平化（类似 `flatMap`）。前者两个异步任务没有依赖，后者第二个任务依赖第一个的结果
- 不指定线程池会怎样？→ `supplyAsync` 无参版本使用公共 ForkJoinPool.commonPool()（JDK 8 默认并行度 = CPU 核数 - 1）。生产环境必须显式传自定义线程池，否则多个业务共享公共池互相影响，且核心线程不会被 GC
- 如何实现超时取消？→ `orTimeout(timeout, unit)` 超时抛 `TimeoutException`；`completeOnTimeout(defaultValue, timeout, unit)` 超时返回默认值（Java 9+）
- 如何统一处理异常？→ `exceptionally()` 处理单个 CF 的异常返回默认值；`handle()` 无论正常/异常都执行（类似 finally）；多 CF 组合用 `allOf().whenComplete()` 统一处理

---

## 六、IO 与序列化

### BIO、NIO、AIO 的区别？

频次 ★★★★ · 难度 🟡

**是什么**：

- **BIO（同步阻塞）**：一连接一线程，线程阻塞在 read。适合连接数少且固定的场景
- **NIO（同步非阻塞 + IO 多路复用）**：一个 Selector 线程管理成千上万连接。适合高并发网关、中间件
- **AIO（异步，内核完成后回调）**：无需自己等待就绪。业界极少用（见追问）

**为什么 NIO 能撑高并发（C10K 问题）**：BIO 一万个连接就要一万个线程——仅线程栈就吃掉约 10GB（`-Xss` 默认 1MB），加上上下文切换，机器先于业务被拖死。NIO 把"等数据到达"交给内核（select/epoll），应用线程只处理**就绪**的连接，阻塞点从 N 个线程收敛到 1 个 `select()` 调用。

**常见追问**
- NIO 的"非阻塞"到底哪里非阻塞？→ read 不再等数据：没数据立即返回 0；"等就绪"这件事由 Selector 统一阻塞在 `select()` 上完成。所以 NIO 是"读写非阻塞 + 等待集中化"，不是没有阻塞。
- 为什么 Java AIO 没流行？→ Linux 上的实现用 epoll 模拟、并非内核真异步（io_uring 才是），相比 Netty 式 NIO 没有实际收益；Windows 的 IOCP 是真异步但服务器不跑 Windows。
- select/poll/epoll 的区别？→ 属于操作系统考点，见[操作系统](操作系统.md)"select/poll/epoll 区别"。

---

### NIO 三大核心组件

频次 ★★★ · 难度 🟡

- **Channel（通道）**：双向数据传输，类似流但更强大
- **Buffer（缓冲区）**：数据容器，读写切换通过 flip()
- **Selector（选择器）**：I/O 多路复用，一个线程监听多个 Channel

**NIO 工作流程：**
```
Channel 注册到 Selector → Selector 轮询就绪事件 → 处理就绪的 Channel
```

**实际应用：** Netty 底层基于 NIO Selector + epoll 实现高并发网络通信

相关深挖：零拷贝（`FileChannel.transferTo` / sendfile）见[操作系统](操作系统.md)"零拷贝"；半包粘包与拆包器见[Netty与RPC](Netty与RPC.md)"TCP 粘包/拆包如何解决？"。

**常见追问**
- Buffer 的 flip() 做了什么？→ 写模式切换到读模式：`limit = position`（标记数据截止位置），`position = 0`（从头读），`mark = -1`。配套的 `clear()` 是读切回写（position=0，limit=capacity），`compact()` 是把未读数据移到开头继续写
- DirectBuffer 和 HeapBuffer 的区别？→ HeapBuffer 在 JVM 堆上，受 GC 管理但多一次拷贝（堆→堆外→内核）；DirectBuffer 在堆外内存，零拷贝直接操作，但分配/回收成本高，适合长期复用的网络 IO 场景
- mmap 和 sendfile 各自适用场景？→ mmap 适合小文件随机读写（数据先到用户态），sendfile 适合大文件网络传输（数据不经过用户态，DMA 直接到 socket 缓冲区，真零拷贝）。Kafka 就是靠 sendfile 实现高效日志消费

---

### 序列化和反序列化

频次 ★★★ · 难度 🟡

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


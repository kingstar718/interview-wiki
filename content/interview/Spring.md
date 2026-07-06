# Spring

## 十、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| IoC/DI | BeanDefinition、容器创建、依赖注入 | 构造器 vs 字段注入、FactoryBean |
| Bean 生命周期 | 实例化、属性填充、Aware、前后处理器 | BeanPostProcessor 执行时机、代理何时生成 |
| AOP | 切点、通知、代理调用 | JDK/CGLIB 选择、自调用为何失效 |
| 事务 | 代理、传播、隔离、回滚规则 | checked 异常、this 调用、事务边界 |
| 循环依赖 | 三级缓存、提前暴露引用 | 构造器循环、prototype、代理对象一致性 |
| 作用域 | singleton/prototype/request | 单例 Bean 线程安全吗 |
| 扩展点 | BPP、BFPP、ImportSelector | starter 和自动配置如何使用 |
| SpringMVC | DispatcherServlet 调度链 | 参数解析、返回值处理、异常处理 |

Spring 问题通常会沿“容器启动 → Bean 创建 → 代理生成 → 方法调用”连续追问，应保持时间顺序清晰。

---

[[社招问题知识点|← 返回知识点]]

---

## 一、Spring IoC 和 DI

### 高频面试题

#### 1. Spring Bean 的生命周期？（难度：Hard）

**快答**

Bean 生命周期共 10 步：

```
1. 实例化（Instantiation）→ createInstance
2. 属性赋值（Populate）→ 依赖注入（DI）
3. BeanNameAware.setBeanName()
4. BeanClassLoaderAware.setBeanClassLoader()
5. BeanFactoryAware.setBeanFactory()
6. 前置处理（@PostConstruct、InitializingBean.afterPropertiesSet）
7. 初始化回调（init-method）
8. 后置处理（BeanPostProcessor.postProcessAfterInitialization）
9. 可用状态（ready to use）
10. 销毁前处理（@PreDestroy、DisposableBean.destroy）
11. 销毁回调（destroy-method）
```

---

#### 2. Spring 事务什么时候会失效？（难度：Hard）

**快答**

7 种常见场景：

```java
// 场景 1：非 public 方法
@Transactional
private void privateMethod() {}  // ❌ 不生效

// 场景 2：同类方法调用
public void methodA() {
    methodB();  // ❌ methodB 的 @Transactional 不生效
}

@Transactional
public void methodB() {}

// 场景 3：异常被捕获
@Transactional
public void method() {
    try {
        // ...
    } catch (Exception e) {
        // ❌ 如果异常被捕获，事务不会回滚
    }
}

// 场景 4：异常类型不匹配
@Transactional(rollbackFor = RuntimeException.class)
public void method() {
    throw new Exception();  // ❌ 非 RuntimeException，不回滚
}

// 场景 5：数据库不支持事务
// MyISAM → ❌，InnoDB → ✅

// 场景 6：在 static 方法上
@Transactional
public static void staticMethod() {}  // ❌ 不生效

// 场景 7：多线程场景
@Transactional
public void method() {
    new Thread(() -> {
        // ❌ 新线程没有继承事务上下文
    }).start();
}
```

---

## 二、IoC 深入

### 1. 控制反转、依赖注入、依赖倒置的区别？

| 概念 | 含义 |
|------|------|
| **控制反转（IoC）** | 程序执行流程的控制权从程序员反转到框架。对象的创建、初始化、销毁由容器管理 |
| **依赖注入（DI）** | 一种编码技巧，不通过 new 在类内部创建依赖对象，而是外部创建后通过构造器/Setter/字段注入 |
| **依赖倒置原则** | 高层模块不依赖低层模块，共同依赖抽象；抽象不依赖具体实现，具体实现依赖抽象 |

### 2. 三种依赖注入方式的区别？

- **构造器注入**：通过构造函数传递依赖，保证初始化时依赖已就绪，推荐方式
- **Setter 注入**：通过 Setter 设置依赖，灵活性高但依赖可能未完全初始化
- **字段注入**：`@Autowired` 直接注字段，代码简洁但隐藏依赖关系，不推荐生产使用

### 3. IoC 的实现机制？

- **反射**：运行时动态加载类、创建实例、调用方法
- **工厂模式**：BeanFactory/ApplicationContext 作为工厂管理 Bean 创建和生命周期
- **依赖注入**：容器负责管理组件间依赖关系

---

## 三、AOP 面向切面编程

### 1. AOP 的实现方式（JDK 动态代理 vs CGLIB）？（难度：Hard）

**快答**

| 特性         | JDK 代理        | CGLIB 代理 |
| ---------- | ------------- | -------- |
| 原理         | 动态生成 $Proxy 类 | 继承目标类    |
| 条件         | 必须实现接口        | 任何类      |
| 性能         | 创建快，调用快       | 创建慢，调用快  |
| 能代理 static | ❌             | ❌        |
| 能代理 final  | ❌             | ❌        |
| Spring 选择  | 优先用           | 没接口才用    |

---

## 四、@Transactional 详解

```java
@Transactional(
    value = "transactionManager",  // 事务管理器
    transactionManager = "txManager",
    propagation = Propagation.REQUIRED,  // 事务传播
    isolation = Isolation.DEFAULT,       // 隔离级别
    timeout = -1,                        // 超时时间（秒）
    readOnly = false,                    // 只读优化
    rollbackFor = Exception.class,       // 回滚异常
    noRollbackFor = IOException.class    // 不回滚异常
)
public void method() {}
```

**传播行为：**
- REQUIRED（默认）：有事务就加入，没有就新建
- REQUIRES_NEW：总是新建事务
- NESTED：嵌套事务，支持 savepoint
- NOT_SUPPORTED：非事务执行
- NEVER：必须非事务，否则报错

---

## 五、循环依赖与三级缓存

### 1. Spring 如何解决循环依赖？（难度：Hard）

**快答**：仅解决 **单例 + Setter/字段注入** 的循环依赖。通过三级缓存 + 提前暴露未完全初始化的引用。

三级缓存（`DefaultSingletonBeanRegistry`）：

| 缓存 | 类型 | 作用 |
|------|------|------|
| `singletonObjects`（一级） | `Map<String, Object>` | 完全初始化好的 Bean |
| `earlySingletonObjects`（二级） | `Map<String, Object>` | 早期引用（可能是代理对象） |
| `singletonFactories`（三级） | `Map<String, ObjectFactory<?>>` | 工厂对象，按需生成早期引用 |

解决过程（A 依赖 B，B 依赖 A）：
1. 创建 A 实例 → 放入三级缓存（ObjectFactory）
2. A 属性注入时发现需要 B → 创建 B 实例 → 放入三级缓存
3. B 属性注入时发现需要 A → 从三级缓存获取 A 的工厂 → 调用 getObject() 生成早期引用 → 放入二级缓存 → 注入 B
4. B 完成初始化 → 升一级缓存
5. A 继续注入 B → 完成初始化 → 升一级缓存

### 2. 为什么必须用三级缓存？二级不够？

核心是为了 **正确处理 AOP 代理的 Bean**。

如果只有二级缓存，B 注入 A 时拿到的是原始对象，但 A 最终生成的是代理对象 → 同一个 Bean 出现两个实例，违反单例。

三级缓存中的 `ObjectFactory` 可以智能判断：需要代理就提前生成代理放入二级缓存，不需要就返回原始对象。本质是 **"按需延迟生成正确引用"**。

### 3. 哪些情况 Spring 无法解决循环依赖？

- 构造器注入的循环依赖（实例化前就需要依赖，死锁）
- 原型（prototype）模式下的循环依赖
- 非单例模式的循环依赖

---

## 六、Bean 作用域与生命周期

### 1. Spring Bean 的作用域？

| 作用域 | 说明 |
|--------|------|
| singleton | 默认，容器内唯一实例 |
| prototype | 每次请求创建新实例 |
| request | 每个 HTTP 请求一个实例（Web） |
| session | 每个用户会话一个实例（Web） |
| application | ServletContext 内唯一（Web） |
| websocket | WebSocket 会话内唯一 |

### 2. Singleton vs Prototype 生命周期区别？

| 阶段 | Singleton | Prototype |
|------|-----------|-----------|
| 创建时机 | 容器启动时（或首次） | 每次请求时 |
| 初始化 | 完整执行生命周期 | 完整执行到初始化 |
| 销毁 | 容器关闭时自动销毁 | **容器不管理销毁**，需手动释放 |

### 3. Bean 初始化/销毁前后的扩展方式？

- `@PostConstruct` / `@PreDestroy` 注解
- 实现 `InitializingBean` / `DisposableBean` 接口
- XML 配置 `init-method` / `destroy-method`
- `@Bean(initMethod="init", destroyMethod="destroy")`

---

## 七、Spring 扩展点

常用扩展点：

| 扩展点 | 作用 |
|--------|------|
| `BeanFactoryPostProcessor` | 容器实例化 Bean **之前**修改 Bean 定义 |
| `BeanPostProcessor` | Bean 实例化、初始化 **前后**额外处理（AOP 代理的核心） |
| `ImportSelector` | 根据条件动态注册 Bean 定义（自动装配核心） |
| `ImportBeanDefinitionRegistrar` | 动态注册 Bean 定义 |
| `HandlerInterceptor` | 拦截 MVC 请求 |
| `ControllerAdvice` | 全局异常处理、数据绑定 |

---

## 八、设计模式

### Spring 中用到的设计模式

| 模式 | 应用 |
|------|------|
| 工厂模式 | BeanFactory、ApplicationContext |
| 代理模式 | AOP（JDK/CGLIB） |
| 单例模式 | Bean 默认单例 |
| 模板方法 | JdbcTemplate、RestTemplate |
| 观察者模式 | 事件驱动（ApplicationEvent/Listener） |
| 适配器模式 | HandlerAdapter 适配不同 Controller |
| 策略模式 | AOP 选择 JDK/CGLIB 代理策略 |
| 装饰器模式 | TransactionAwareCacheDecorator |

---

## 九、SpringMVC

Servlet、Tomcat、Nginx 和完整 Web 请求链见 [[Web容器与Nginx]]。

### 1. SpringMVC 处理流程？

```
用户请求
    ↓
DispatcherServlet（前端控制器）
    ↓
HandlerMapping → 找到 Controller + 拦截器 → HandlerExecutionChain
    ↓
HandlerAdapter → 参数绑定、数据验证
    ↓
Controller（Handler）执行 → 返回 ModelAndView
    ↓
ViewResolver（视图解析器）→ 解析 View
    ↓
渲染视图 → 响应
```

### 2. HandlerMapping 和 HandlerAdapter 的作用？

- **HandlerMapping**：将 URL 请求映射到对应的 Controller（根据 URL、参数等找到处理器）
- **HandlerAdapter**：适配不同类型的 Controller 并调用（适配器模式），因为 Controller 可能有不同接口类型

---

## 最佳实践

**✅ 正确做法：**

```java
// 1. @Transactional 必须是 public
@Transactional
public void publicMethod() {}

// 2. 避免同类调用
@Service
public class UserService {
    @Transactional
    public void methodA() {
        methodB();  // ❌ 错误
    }
    
    @Transactional
    public void methodB() {}
}

// 正确做法：注入自己
@Service
public class UserService {
    @Autowired
    private UserService self;
    
    public void methodA() {
        self.methodB();  // ✅ 通过代理对象调用
    }
    
    @Transactional
    public void methodB() {}
}

// 3. 异常类型要对
@Transactional(rollbackFor = Exception.class)  // 所有异常都回滚
public void method() {}

// 4. 数据库要用 InnoDB
```

[[社招问题知识点|← 返回知识点]]

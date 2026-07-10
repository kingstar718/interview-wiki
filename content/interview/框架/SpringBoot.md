# SpringBoot

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [自动配置](#spring-boot-自动配置的原理) | ImportSelector、候选配置、条件注解 | AutoConfiguration.imports、条件报告 |
| [条件注解](#conditional-条件注解有哪些) | @ConditionalOnClass、@ConditionalOnMissingBean | @ConditionalOnProperty、条件评估报告 |
| [Starter](#如何自定义-springboot-starter) | 依赖聚合 + 自动配置 | 自定义 starter、配置元数据 |
| [启动流程](#spring-boot-启动流程) | Environment、Context、refresh、WebServer | Runner 时机、启动事件、失败分析 |
| [内嵌 Tomcat](#springboot-内嵌-tomcat-是怎么启动的) | TomcatServletWebServerFactory、getWebServer、Connector | onRefresh 时机、与独立 Tomcat 对比 |
| [配置管理](#spring-boot-配置的常见优先级) | 属性源优先级、类型绑定、Profile | 动态刷新、密钥管理、配置回滚 |
| [Filter/Interceptor/AOP](#filterinterceptoraop-如何选择) | 所属层次和调用时机 | 异常链、静态资源、Bean 注入 |
| [参数校验](#如何做参数校验) | Bean Validation、分组、自定义约束 | Controller 外的方法校验 |
| [Actuator](#spring-boot-actuator-有什么作用) | 健康、指标、管理端点 | 暴露风险、自定义 HealthIndicator |
| [优雅停机](#spring-boot-如何实现优雅停机) | 摘流量、等待请求、释放资源 | MQ 消费、线程池、K8s terminationGracePeriod |

回答 Spring Boot 题要区分版本，特别是 2.7、3.x 的自动配置注册方式和 Jakarta 包迁移。

---

## 一、Spring Boot 自动配置

### Spring Boot 自动配置的原理？

难度 🟡

**快答**

```
@SpringBootApplication
    ↓ 包含 @EnableAutoConfiguration
@EnableAutoConfiguration
    ↓ 导入 AutoConfigurationImportSelector
AutoConfigurationImportSelector
    ↓ 读取 spring.factories
/META-INF/spring.factories 中的 org.springframework.boot.autoconfigure.EnableAutoConfiguration
    ↓ 加载所有 autoconfiguration 类
    ↓ 按 @Conditional 条件判断是否需要配置
    ↓ 注入 Bean 到容器中
```

---

## 二、SpringBoot 进阶

### SpringBoot 过滤器 vs 拦截器？

| 特性 | 过滤器（Filter） | 拦截器（Interceptor） |
|------|-----------------|----------------------|
| 规范 | Servlet 规范 | Spring MVC 框架 |
| 作用范围 | 全局（所有请求+静态资源） | 仅 Controller 层 |
| 执行时机 | Servlet 之前 | DispatcherServlet 之后、Controller 前后 |
| Spring Bean 注入 | 不直接支持 | 支持 |
| 方法 | `doFilter()` | `preHandle` / `postHandle` / `afterCompletion` |
| 场景 | 编码、全局日志、安全 | 权限校验、参数校验 |

### SpringBoot 如何做到导入即可用？

- **起步依赖**：一个 starter 包含所有相关依赖
- **自动配置**：扫描 `AutoConfiguration.imports`（2.7+）或 `spring.factories`（2.7-），加载自动配置类
- **条件注解**：`@ConditionalOnClass`、`@ConditionalOnMissingBean` 等按需加载

### SpringBoot 2.7+ 自动配置变更

- 从 `META-INF/spring.factories` 迁移到 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Spring Boot 3.0 已完全移除对 `spring.factories` 自动配置的支持

### @Conditional 条件注解有哪些？

频次 ★★★★ · 难度 🟡

**是什么**：Spring Boot 自动配置类上散布着大量 `@Conditional` 注解，它们决定某段配置在当前环境下是否生效——**满足条件才创建 Bean，不满足就跳过**。

**条件注解全家桶**（按判断维度归类）：

| 维度 | 注解 | 用途 |
|------|------|------|
| 类存在性 | `@ConditionalOnClass` / `@ConditionalOnMissingClass` | 检查类路径上是否有某个类（Starter 最常用——引入了 `redis-starter` 才有 `RedisTemplate` 的自动配置） |
| Bean 存在性 | `@ConditionalOnBean` / `@ConditionalOnMissingBean` | 检查容器是否有/没有某个 Bean（避免重复注册，用户自定义了 `DataSource` 就不走默认的） |
| 属性值 | `@ConditionalOnProperty` | 检查配置项是否等于指定值（如 `server.ssl.enabled=true` 才开启 SSL 自动配置） |
| 资源存在性 | `@ConditionalOnResource` | 检查类路径下是否有某个资源文件（如 `logback.xml` 才加载 Logback 配置） |
| Web 环境 | `@ConditionalOnWebApplication` / `@ConditionalOnNotWebApplication` | 判断当前是否是 Web 应用（`WebMvcAutoConfiguration` 靠它仅在 Web 环境生效） |
| SpEL 表达式 | `@ConditionalOnExpression` | 按 Spring EL 表达式的真假决定（终极灵活手段，如 `#{${server.port} > 1024}`） |
| Java 版本 | `@ConditionalOnJava` | 按 JVM 版本匹配（兼容性配置） |
| Jndi 资源 | `@ConditionalOnJndi` | 检查 JNDI 中是否存在某个资源 |

**工作原理**：`ConditionEvaluator`（在 `AutoConfigurationImportSelector` 加载完候选类后被调用）逐个检查自动配置类上的所有 `@Conditional` 子注解——每条注解由对应的 `Condition` 实现（如 `OnClassCondition`）调用 `matches()` 方法返回 true/false，全部通过才注册该配置类。条件评估结果会写入 `ConditionEvaluationReport`（即自动配置报告，actuator 中可通过 `/actuator/conditions` 查看）。

**常见追问**
- `@ConditionalOnClass` 和 `@ConditionalOnMissingBean` 哪个更常用？→ `@ConditionalOnClass` 是自动配置的核心开关——依赖不在类路径上，自动配置根本不应加载；`@ConditionalOnMissingBean` 用于支持用户自定义覆盖，常见于 `DataSource`、`RestTemplate` 等
- 自定义条件注解怎么做？→ 实现 `Condition` 接口的 `matches()` 方法，标注 `@Conditional(MyCondition.class)` 即可

### 如何自定义 SpringBoot Starter？

1. 创建 Maven 项目，引入 `spring-boot-starter`
2. 创建自动配置类 `@Configuration` + `@EnableConfigurationProperties`
3. 创建配置属性类 `@ConfigurationProperties(prefix="xxx")`
4. 注册自动配置（`AutoConfiguration.imports` 文件中写类全限定名）
5. 发布到 Maven 仓库

### SpringBoot 约定大于配置

- 自动化配置：引入 starter 后自动配置相关组件
- 默认配置：日志、数据源、Web 服务器等都有合理默认值
- 约定项目结构：main 类在根包，controller/service/dao 在子包

---

## 三、启动流程

### Spring Boot 启动流程？

难度 🔴

```text
SpringApplication.run
  ↓
准备 Environment 与监听器
  ↓
创建 ApplicationContext
  ↓
加载 BeanDefinition
  ↓
refresh 刷新容器
  ↓
启动内嵌 Web Server
  ↓
执行 Runner
```

关键步骤：

1. 推断应用类型和主配置类。
2. 加载配置文件、环境变量和命令行参数。
3. 发布启动阶段事件。
4. 创建并刷新 Spring 容器（refresh() 十二步骨架见[Spring](Spring.md)容器启动一节，内嵌 Tomcat 在其中 `onRefresh()` 一步创建）。
5. 执行自动配置和 Bean 生命周期。
6. 启动内嵌服务器。
7. 执行 `ApplicationRunner`、`CommandLineRunner`。

### `ApplicationRunner` 和 `CommandLineRunner` 有什么区别？

两者都在容器启动完成后执行：

- `ApplicationRunner` 接收结构化的 `ApplicationArguments`。
- `CommandLineRunner` 接收原始字符串数组。

可通过 `@Order` 控制顺序。不要放置无法控制时长的重任务，否则会拖慢启动或导致启动失败。

### SpringBoot 内嵌 Tomcat 是怎么启动的？

频次 ★★★★ · 难度 🔴

**是什么**：传统 Java Web 应用部署时先打 war 包放到 Tomcat webapps 目录，SpringBoot 内嵌 Tomcat 把 Tomcat 当成一个**普通的 Java 对象**在 `main` 方法里创建和启动。

**启动链路**（结合 refresh() 十二步理解）：

```
SpringApplication.run()
  ↓
AbstractApplicationContext.refresh()
  ↓
第 9 步 onRefresh()
  → ServletWebServerApplicationContext.onRefresh()
    → createWebServer()
      → 从容器中获取 TomcatServletWebServerFactory
      → factory.getWebServer()
```

`TomcatServletWebServerFactory.getWebServer()` 做什么：

1. **创建 Tomcat 实例**：`new Tomcat()` —— 这就是为什么说 SpringBoot 启动了一个 Tomcat 对象而非 Tomcat 进程
2. **设置基目录和临时目录**：`Tomcat.setBaseDir()`（默认 `java.io.tmpdir`）
3. **创建 Connector**：调用 `getEmbeddedServletContainerFactory()` 的 `createConnector()` → 通过 `Connector(protocol)` 创建 HTTP Connector（默认 NIO），设置端口、压缩、SSL 等
4. **创建 Host 和 Engine**：添加 `StandardEngine` → `StandardHost`
5. **注册 Context**：`addContext("/", baseDir)` —— 没有 web.xml，靠 `TomcatStarter` 触发 ServletContainerInitializer
6. **组装 Pipeline**：把 `TomcatServletWebServerFactory` 中配置的 Valve（访问日志、错误页面等）加到 Context/Engine 的 Pipeline 中
7. **调用 `Tomcat.start()`**：启动 Connector 开始监听端口

**Connector 什么时候真正开始接收请求？**

不立即——`Tomcat.start()` 只是初始化组件，**Connector 完成端口绑定之前会触发 WebServerInitializedEvent**，然后 `finishRefresh()` 中 `WebServerStartStopLifecycle.start()` 才会将 Connector 的 `start()` 调用到底层 NIO 通道绑定——此时流量才进来。这保证了**所有单例 Bean 已实例化完成（第 11 步）后才对外开放服务**。

**与 war 包部署的对比**：

| | 传统 Tomcat 部署 | SpringBoot 内嵌 |
|---|---|---|
| Tomcat 来源 | 系统安装/独立进程 | Maven 依赖 `spring-boot-starter-tomcat` |
| 启动方式 | `startup.sh` → 启动进程 → 扫描 webapps 下 war | `java -jar app.jar` → `main()` → refresh → onRefresh → getWebServer |
| 配置 | `server.xml` / `context.xml` | `application.properties`（`server.tomcat.*`）或 `WebServerFactoryCustomizer` |
| 部署单位 | war 包 | fat jar（含内嵌 Tomcat 类和资源） |
| 多实例 | 多个 war 运行在同一个 Tomcat 进程 | 每个 jar 独立进程，互不影响 |

**常见追问**
- 内嵌 Tomcat 怎么改端口？→ `server.port` 属性 → `TomcatServletWebServerFactory` 创建 `Connector` 时设置 `setPort()`
- 想自定义内嵌 Tomcat 配置（如 AccessLogValve）怎么做？→ 实现 `WebServerFactoryCustomizer<TomcatServletWebServerFactory>`，在 `customize()` 中调用 `factory.addContextValves()`
- 内嵌 Tomcat 和独立 Tomcat 性能有差异吗？→ 几乎没有，因为底层 NIO 通道和线程模型完全一样，差异在运维侧（进程隔离 vs 部署管理便利性）

## 四、配置管理

### Spring Boot 配置的常见优先级？

通常外部配置会覆盖包内配置，命令行参数和环境变量优先级较高。面试回答应抓住原则：

- 同一个属性存在多个来源时，优先级高的覆盖低的。
- Profile 用于区分环境，不用于保存秘密。
- 密码和令牌应由环境变量或密钥管理系统注入。

### `@Value` 和 `@ConfigurationProperties` 的区别？

| | `@Value` | `@ConfigurationProperties` |
|---|---|---|
| 场景 | 少量独立属性 | 一组结构化配置 |
| 类型绑定 | 较弱 | 支持批量类型安全绑定 |
| 校验 | 不方便 | 可结合 Bean Validation |
| 可维护性 | 属性多时较差 | 更适合配置类 |

### 配置刷新要注意什么？

- 配置变更需要审计和权限控制。
- 明确哪些配置支持动态刷新。
- 多个实例应关注生效顺序和短时不一致。
- 高风险配置需要灰度、校验和快速回滚。

---

## 五、Web 工程实践

### 如何做统一异常处理？

使用 `@RestControllerAdvice` 配合 `@ExceptionHandler`：

- 业务异常转换为稳定的业务错误码。
- 参数异常返回明确字段和原因。
- 未知异常记录完整日志，对外隐藏内部栈信息。
- 统一响应中携带 Trace ID，方便排查。

### 如何做参数校验？

- 请求对象使用 `@Valid` 或 `@Validated`。
- 字段使用 `@NotNull`、`@Size`、`@Pattern` 等约束。
- 复杂跨字段规则使用自定义校验器。
- Service 层仍需维护关键业务校验，不能完全依赖 Controller。

### Filter、Interceptor、AOP 如何选择？

- Filter：Servlet 层全局处理，如编码、跨域、基础安全。
- Interceptor：Spring MVC 请求前后处理，如鉴权、日志。
- AOP：方法级横切逻辑，如审计、事务和指标。

---

## 六、监控与生产能力

### Spring Boot Actuator 有什么作用？

Actuator 提供健康检查、指标、环境和线程等管理端点，可与监控系统集成。

生产环境需要：

- 只暴露必要端点。
- 对管理端点鉴权或使用独立管理端口。
- 禁止泄露配置、密钥和内部信息。
- 自定义健康检查时区分存活和就绪。

### Spring Boot 如何实现优雅停机？

优雅停机的目标是停止接收新请求，并等待正在处理的请求完成：

1. 实例先从流量入口摘除。
2. 停止接收新任务。
3. 等待请求、线程池和消息消费在超时内完成。
4. 关闭连接池和容器。

还需结合网关、注册中心、容器编排和负载均衡器共同验证。

### Spring Boot 应用启动失败怎么排查？

- 查看最底层 `Caused by`，不要只看顶部异常。
- 检查端口占用、配置绑定、Bean 冲突和循环依赖。
- 检查数据库、缓存和配置中心等启动依赖。
- 开启条件评估报告，确认自动配置为何生效或未生效。


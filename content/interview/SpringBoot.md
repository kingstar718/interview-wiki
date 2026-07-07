# SpringBoot

[← 返回知识点](../社招问题知识点.md)

---

## 一、Spring Boot 自动配置

### 1. Spring Boot 自动配置的原理？（难度：Medium）

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

### 1. SpringBoot 过滤器 vs 拦截器？

| 特性 | 过滤器（Filter） | 拦截器（Interceptor） |
|------|-----------------|----------------------|
| 规范 | Servlet 规范 | Spring MVC 框架 |
| 作用范围 | 全局（所有请求+静态资源） | 仅 Controller 层 |
| 执行时机 | Servlet 之前 | DispatcherServlet 之后、Controller 前后 |
| Spring Bean 注入 | 不直接支持 | 支持 |
| 方法 | `doFilter()` | `preHandle` / `postHandle` / `afterCompletion` |
| 场景 | 编码、全局日志、安全 | 权限校验、参数校验 |

### 2. SpringBoot 如何做到导入即可用？

- **起步依赖**：一个 starter 包含所有相关依赖
- **自动配置**：扫描 `AutoConfiguration.imports`（2.7+）或 `spring.factories`（2.7-），加载自动配置类
- **条件注解**：`@ConditionalOnClass`、`@ConditionalOnMissingBean` 等按需加载

### 3. SpringBoot 2.7+ 自动配置变更

- 从 `META-INF/spring.factories` 迁移到 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Spring Boot 3.0 已完全移除对 `spring.factories` 自动配置的支持

### 4. 如何自定义 SpringBoot Starter？

1. 创建 Maven 项目，引入 `spring-boot-starter`
2. 创建自动配置类 `@Configuration` + `@EnableConfigurationProperties`
3. 创建配置属性类 `@ConfigurationProperties(prefix="xxx")`
4. 注册自动配置（`AutoConfiguration.imports` 文件中写类全限定名）
5. 发布到 Maven 仓库

### 5. SpringBoot 约定大于配置

- 自动化配置：引入 starter 后自动配置相关组件
- 默认配置：日志、数据源、Web 服务器等都有合理默认值
- 约定项目结构：main 类在根包，controller/service/dao 在子包

---

## 三、启动流程

### 1. Spring Boot 启动流程？（难度：Hard）

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
4. 创建并刷新 Spring 容器。
5. 执行自动配置和 Bean 生命周期。
6. 启动内嵌服务器。
7. 执行 `ApplicationRunner`、`CommandLineRunner`。

### 2. `ApplicationRunner` 和 `CommandLineRunner` 有什么区别？

两者都在容器启动完成后执行：

- `ApplicationRunner` 接收结构化的 `ApplicationArguments`。
- `CommandLineRunner` 接收原始字符串数组。

可通过 `@Order` 控制顺序。不要放置无法控制时长的重任务，否则会拖慢启动或导致启动失败。

---

## 四、配置管理

### 1. Spring Boot 配置的常见优先级？

通常外部配置会覆盖包内配置，命令行参数和环境变量优先级较高。面试回答应抓住原则：

- 同一个属性存在多个来源时，优先级高的覆盖低的。
- Profile 用于区分环境，不用于保存秘密。
- 密码和令牌应由环境变量或密钥管理系统注入。

### 2. `@Value` 和 `@ConfigurationProperties` 的区别？

| | `@Value` | `@ConfigurationProperties` |
|---|---|---|
| 场景 | 少量独立属性 | 一组结构化配置 |
| 类型绑定 | 较弱 | 支持批量类型安全绑定 |
| 校验 | 不方便 | 可结合 Bean Validation |
| 可维护性 | 属性多时较差 | 更适合配置类 |

### 3. 配置刷新要注意什么？

- 配置变更需要审计和权限控制。
- 明确哪些配置支持动态刷新。
- 多个实例应关注生效顺序和短时不一致。
- 高风险配置需要灰度、校验和快速回滚。

---

## 五、Web 工程实践

### 1. 如何做统一异常处理？

使用 `@RestControllerAdvice` 配合 `@ExceptionHandler`：

- 业务异常转换为稳定的业务错误码。
- 参数异常返回明确字段和原因。
- 未知异常记录完整日志，对外隐藏内部栈信息。
- 统一响应中携带 Trace ID，方便排查。

### 2. 如何做参数校验？

- 请求对象使用 `@Valid` 或 `@Validated`。
- 字段使用 `@NotNull`、`@Size`、`@Pattern` 等约束。
- 复杂跨字段规则使用自定义校验器。
- Service 层仍需维护关键业务校验，不能完全依赖 Controller。

### 3. Filter、Interceptor、AOP 如何选择？

- Filter：Servlet 层全局处理，如编码、跨域、基础安全。
- Interceptor：Spring MVC 请求前后处理，如鉴权、日志。
- AOP：方法级横切逻辑，如审计、事务和指标。

---

## 六、监控与生产能力

### 1. Spring Boot Actuator 有什么作用？

Actuator 提供健康检查、指标、环境和线程等管理端点，可与监控系统集成。

生产环境需要：

- 只暴露必要端点。
- 对管理端点鉴权或使用独立管理端口。
- 禁止泄露配置、密钥和内部信息。
- 自定义健康检查时区分存活和就绪。

### 2. Spring Boot 如何实现优雅停机？

优雅停机的目标是停止接收新请求，并等待正在处理的请求完成：

1. 实例先从流量入口摘除。
2. 停止接收新任务。
3. 等待请求、线程池和消息消费在超时内完成。
4. 关闭连接池和容器。

还需结合网关、注册中心、容器编排和负载均衡器共同验证。

### 3. Spring Boot 应用启动失败怎么排查？

- 查看最底层 `Caused by`，不要只看顶部异常。
- 检查端口占用、配置绑定、Bean 冲突和循环依赖。
- 检查数据库、缓存和配置中心等启动依赖。
- 开启条件评估报告，确认自动配置为何生效或未生效。

## 七、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 自动配置 | ImportSelector、候选配置、条件注解 | AutoConfiguration.imports、条件报告 |
| Starter | 依赖聚合 + 自动配置 | 自定义 starter、配置元数据 |
| 启动流程 | Environment、Context、refresh、WebServer | Runner 时机、启动事件、失败分析 |
| 配置管理 | 属性源优先级、类型绑定、Profile | 动态刷新、密钥管理、配置回滚 |
| Filter/Interceptor/AOP | 所属层次和调用时机 | 异常链、静态资源、Bean 注入 |
| 参数校验 | Bean Validation、分组、自定义约束 | Controller 外的方法校验 |
| Actuator | 健康、指标、管理端点 | 暴露风险、自定义 HealthIndicator |
| 优雅停机 | 摘流量、等待请求、释放资源 | MQ 消费、线程池、K8s terminationGracePeriod |

回答 Spring Boot 题要区分版本，特别是 2.7、3.x 的自动配置注册方式和 Jakarta 包迁移。

---

[← 返回知识点](../社招问题知识点.md)

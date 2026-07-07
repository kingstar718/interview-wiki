# Web容器与Nginx

[← 返回知识点](知识点索引.md)

---

## 一、Servlet 与 Web 容器

### 1. Servlet 的生命周期是什么？（难度：Medium）

1. 容器加载 Servlet 类并创建实例。
2. 调用一次 `init()` 完成初始化。
3. 每个请求调用 `service()`，再分派到 `doGet()`、`doPost()` 等方法。
4. 应用停止或 Servlet 卸载时调用一次 `destroy()`。

Servlet 通常是单例，多线程共享，因此不要把请求级可变状态保存在实例字段中。

### 2. Servlet 容器负责什么？

- 监听端口和管理连接。
- 解析 HTTP 请求和生成响应。
- 管理 Servlet、Filter、Listener 生命周期。
- 将请求映射到目标 Servlet。
- 提供 Session、安全、异步请求等规范能力。

Spring MVC 的 `DispatcherServlet` 本质上也是 Servlet，后续流程见 [Spring](Spring.md)。

### 3. Filter、Servlet、Interceptor 的执行顺序？

```text
客户端
  ↓
Filter Chain
  ↓
DispatcherServlet
  ↓
HandlerInterceptor.preHandle
  ↓
Controller
  ↓
postHandle / afterCompletion
  ↓
Filter 返回链
```

Filter 位于 Servlet 规范层，Interceptor 位于 Spring MVC 层。

---

## 二、Tomcat 架构

### 1. Tomcat 的核心组件有哪些？（难度：Hard）

```text
Server
 └─ Service
     ├─ Connector
     └─ Engine
         └─ Host
             └─ Context
                 └─ Wrapper
```

- Connector：接收连接、解析协议并生成 Request/Response。
- Engine：请求处理引擎。
- Host：一个虚拟主机。
- Context：一个 Web 应用。
- Wrapper：一个 Servlet。

### 2. Tomcat Connector 的请求流程？

```text
Socket → Endpoint → Processor → Adapter
→ Container Pipeline → Servlet
```

Endpoint 管理网络 IO，Processor 解析 HTTP，Adapter 将连接器请求适配到容器请求。

### 3. Tomcat 的线程池如何工作？

连接建立后，请求由工作线程处理。重点参数包括：

- `maxThreads`：最大工作线程数。
- `maxConnections`：最大连接数。
- `acceptCount`：工作线程和连接达到上限后的等待队列。
- `connectionTimeout`：连接等待超时。

线程数不是越大越好。大量线程会增加内存、上下文切换，并可能把压力转移到数据库和下游。

### 4. Tomcat 出现大量请求排队如何排查？

- 当前线程数和忙线程数。
- accept 队列与连接数。
- 线程栈是否阻塞在数据库、锁或下游。
- 数据库和 HTTP 连接池是否耗尽。
- 请求超时、慢请求和流量变化。

只扩大 `maxThreads` 可能加剧下游过载。

### 5. Tomcat 类加载机制有什么特点？

每个 Web 应用使用独立的 WebAppClassLoader，以实现应用隔离。Web 应用通常优先加载自身类，但 Java 核心类和容器关键类仍由父加载器负责。

常见问题：

- 同一依赖在容器和应用中存在不同版本。
- JDBC Driver、ThreadLocal、线程未释放导致应用重载后 ClassLoader 泄漏。

---

## 三、同步与异步请求

### 1. Servlet 异步处理解决什么问题？

Servlet 线程可以启动异步上下文并释放容器线程，待异步任务完成后再提交响应。

它适合长轮询或等待异步结果，但不会让业务自动变快。异步任务仍需受控线程池、超时和取消机制。

### 2. Spring MVC 与 WebFlux 如何选择？

- Spring MVC：Servlet 模型，生态成熟，适合普通阻塞式业务。
- WebFlux：响应式非阻塞模型，适合端到端非阻塞、高并发 IO 和流式场景。

如果数据库驱动和下游仍是阻塞式，盲目切换 WebFlux 的收益有限，反而增加调试和上下文传播复杂度。

---

## 四、Nginx 基础

### 1. Nginx 为什么性能高？（难度：Hard）

- 事件驱动和 IO 多路复用。
- Worker 进程处理大量连接。
- 避免每连接一线程。
- 高效内存管理和零拷贝能力。
- Master/Worker 架构支持平滑重载。

Nginx 擅长网络和代理处理，不适合承载复杂业务逻辑。

### 2. 正向代理和反向代理的区别？

- 正向代理代表客户端访问外部服务，服务端通常不知道真实客户端。
- 反向代理代表服务端集群接收客户端请求，客户端通常不知道真实后端。

Nginx 常用于反向代理、负载均衡、TLS 终止和静态资源服务。

### 3. Nginx 的负载均衡算法有哪些？

- 轮询。
- 加权轮询。
- 最少连接。
- IP Hash。
- 一致性哈希等扩展策略。

选择时考虑请求耗时差异、会话状态、扩缩容和热点问题。能无状态化时不要依赖粘性会话。

### 4. Nginx 如何传递真实客户端 IP？

代理通常设置：

```text
X-Forwarded-For
X-Real-IP
Forwarded
```

应用只能信任来自受控代理的这些 Header，否则客户端可以伪造。多层代理需要明确可信代理链。

---

## 五、Nginx 故障与调优

### 1. 502 和 504 有什么区别？

- 502 Bad Gateway：代理收到无效上游响应，常见于连接拒绝、进程崩溃或协议错误。
- 504 Gateway Timeout：上游在代理超时时间内未完成响应。

排查要结合 Nginx error log、upstream 地址、连接/读取超时和后端日志。

### 2. 长连接如何配置和排查？

需要区分：

- 客户端到 Nginx。
- Nginx 到上游服务。

关注 keepalive 超时、最大请求数、上游连接池和双方超时关系。频繁主动断开可能产生大量 TIME_WAIT。

### 3. Nginx 如何实现平滑重载？

Master 读取新配置并启动新 Worker；旧 Worker 停止接收新连接，处理完已有请求后退出。

重载前应执行配置检查，避免错误配置导致服务不可用。

### 4. Nginx 限流有哪些思路？

- 按请求速率限流。
- 按并发连接数限流。
- 按 IP、用户或业务 Key 分区。
- 设置突发容量和排队/拒绝策略。

入口限流应与应用、数据库和下游容量匹配，不能只设置一个经验数字。

---

## 六、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| Servlet 生命周期 | init/service/destroy、单例并发 | 实例字段线程安全、何时初始化 |
| Tomcat 架构 | Connector、Engine、Host、Context | 请求如何从 Socket 到 Servlet |
| Tomcat 线程池 | 线程、连接、accept 队列 | 参数关系、扩大线程为何更慢 |
| 类加载 | WebApp 隔离、父加载边界 | 热部署泄漏、依赖冲突 |
| Nginx 高性能 | 多进程、事件驱动、多路复用 | Worker 数、惊群、阻塞操作 |
| 反向代理 | 路由、TLS、真实 IP | 多层代理 Header 信任 |
| 负载均衡 | 算法、健康检查、无状态 | 粘性会话和扩容问题 |
| 502/504 | 无效响应 vs 上游超时 | 如何从代理日志定位后端 |
| 长连接 | 两段连接、超时、连接池 | TIME_WAIT、连接复用 |
| 平滑重载 | 新旧 Worker 交接 | 长连接何时退出、配置回滚 |

Web 链路题应能完整描述“客户端 → Nginx → Tomcat → Filter → DispatcherServlet → Controller”。

---

## 参考资料

- [Apache Tomcat Architecture](https://tomcat.apache.org/tomcat-10.1-doc/architecture/overview.html)
- [Nginx Documentation](https://nginx.org/en/docs/)

---

[← 返回知识点](知识点索引.md)

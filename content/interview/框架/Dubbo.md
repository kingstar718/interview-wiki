# Dubbo

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [Dubbo 核心概念](#dubbo-核心概念) | 服务提供者/消费者、注册中心、监控中心 | 服务治理、RPC 与 REST 区别 |
| [调用流程](#dubbo-调用流程) | 代理 → 负载均衡 → 集群容错 → 网络传输 | 谁先谁后、Filter 链扩展 |
| [架构演进](#dubbo-架构演进从-2x-到-3x) | 2.x 中心化、3.x 云原生 | Triple 协议、Proxyless、K8s 适配 |
| [负载均衡策略](#负载均衡策略) | 加权随机/最小活跃/一致性哈希/加权轮询 | 一致性哈希的虚拟节点、dubbo 默认策略 |
| [集群容错](#集群容错) | Failover/Failfast/Failsafe/Forking 等 | 重试次数、幂等性要求 |
| [SPI 机制](#spi-与-dubbo-的扩展点) | 自适应扩展点、IoC/AOP | 与 Java SPI 区别、为什么不用 Spring Factory |
| [服务暴露与引用](#五dubbo-服务暴露和引用流程) | ServiceConfig → Proxy → Protocol → Exporter | 本地暴露 vs 远程暴露 |
| [协议](#协议对比dubbo-vs-triple-vs-http) | Dubbo 协议、Triple(gRPC)、HTTP/REST | 协议选型、序列化对比 |
| [与 Spring Cloud 对比](#六dubbo-与-spring-cloud-对比) | 服务治理 vs 生态整合 | 选型建议、云原生适配 |

Dubbo 题要区分"RPC 框架"和"服务治理"两个维度，先讲清楚 Dubbo 是什么、解决什么问题，再说具体机制。

---

## 一、Dubbo 基础

### Dubbo 核心概念

频次 ★★★★ · 难度 🟢

**是什么**：Dubbo 是阿里巴巴开源的高性能 RPC 框架，提供**服务治理**能力（服务发现、负载均衡、流量管控、可观测性），核心是让远程调用像本地调用一样透明。

**五大角色**：

| 角色 | 说明 |
|------|------|
| **服务提供者（Provider）** | 暴露服务的一方，启动时向注册中心注册 |
| **服务消费者（Consumer）** | 调用服务的一方，启动时从注册中心订阅 |
| **注册中心（Registry）** | 服务地址的注册与发现，如 Nacos/ZK |
| **监控中心（Monitor）** | 统计调用次数和耗时，可选 |
| **配置中心（Config Center）** | 动态配置路由规则和治理参数，如 Nacos/Apollo |

**为什么需要 Dubbo（相比于直接 HTTP 调用）**：
1. **服务发现**：HTTP 调用需要硬编码地址，Dubbo 通过注册中心自动发现
2. **负载均衡**：多节点自动分发，支持加权随机/最小活跃等策略
3. **集群容错**：调用失败自动重试、降级、熔断
4. **协议可拔插**：可换 Dubbo/triple/gRPC/HTTP 等协议，根据场景选最优
5. **流量管控**：路由规则、灰度发布、限流降级

### Dubbo 调用流程

频次 ★★★★ · 难度 🟡

**是什么**：一次完整的 Dubbo 调用经过以下步骤：

```text
Consumer                      Provider
┌────────────┐                ┌────────────┐
│  业务代码   │                │  业务实现   │
│     ↓      │                │     ↑      │
│ Proxy(代理) │── 网络 ──────→│ Protocol   │
│     ↓      │                │     ↑      │
│ Cluster    │                │  Exporter  │
│     ↓      │                │     ↑      │
│ LoadBalance│                │  Filter链  │
│     ↓      │                │     ↑      │
│ Filter链   │←──── 网络 ─────│  Proxy     │
│     ↓      │                │     ↑      │
│  Network   │                │  业务实现   │
└────────────┘                └────────────┘
```

1. **Proxy**：消费方持有接口的代理对象，拦截方法调用
2. **Cluster**：选择集群容错策略（如 Failover 重试），决定调用哪个 Provider
3. **LoadBalance**：从多个 Provider 中选一个（默认加权随机）
4. **Filter 链**：执行上下文传递、监控统计、日志等切面逻辑
5. **Protocol**：序列化请求 → 网络传输 → 反序列化响应
6. **Provider 端**：反向经过 Filter 链 → 调用真实实现

---

## 二、架构演进

### Dubbo 架构演进：从 2.x 到 3.x

频次 ★★★ · 难度 🟡

| 特性 | Dubbo 2.x | Dubbo 3.x |
|------|-----------|-----------|
| 服务发现模型 | **接口粒度**（注册中心存接口名→地址列表） | **应用粒度**（实例级注册，兼容 K8s Native Service） |
| 协议 | Dubbo 协议（默认） | Triple（基于 gRPC，支持 HTTP/2 + Streaming） |
| 云原生 | 不支持 | 支持 K8s、Proxyless 模式（接入 Istio/Envoy） |
| 新特性 | — | 应用级服务发现、响应式编程、模块化、跨语言互通 |
| 适配 | Spring Boot / 传统部署 | Spring Cloud / K8s / Service Mesh |

**应用级服务发现**（3.x 最大变化）：
- 2.x 在注册中心注册的 key 是 `interface:version`，一个服务有几十个接口就注册几十条，Nacos/ZK 数据量膨胀
- 3.x 改为注册**应用实例**（IP:Port），接口元数据通过 `MetadataService` 延迟获取，大幅减少注册中心存储压力（从 O(接口数) 降到 O(实例数)），这是 Dubbo 3.x 适配大规模集群的核心设计
- 兼容性：2.x 消费方可以调用 3.x 提供方（协议兼容，但需要额外配置元数据中心）

**Triple 协议**：基于 gRPC 的 HTTP/2 协议，支持 Streaming（服务端流/客户端流/双向流），天然跨语言（protobuf IDL），与 K8s 网关兼容（Envoy 原生支持 HTTP/2）。

---

## 三、集群与负载均衡

### 负载均衡策略

频次 ★★★★ · 难度 🟡

Dubbo 内置 5 种负载均衡策略，默认**加权随机**（`weightedRandom`）：

| 策略 | 说明 | 适用场景 |
|------|------|---------|
| **加权随机（Random）** | 按权重随机分配，权重越大概率越高 | 通用，默认策略 |
| **加权轮询（RoundRobin）** | 按权重平滑轮询，兼顾权重和公平 | 请求处理时间相近 |
| **最小活跃数（LeastActive）** | 选活跃请求数最少的节点（处理得快的节点会被优先调用） | 请求处理时间差异大 |
| **一致性哈希（ConsistentHash）** | 相同参数哈希到同一节点，天然支持缓存 | 有状态服务（如本地缓存） |
| **最短响应时间（ShortestResponse）** | 选响应时间最短的节点 | 对延迟敏感 |

**一致性哈希的虚拟节点**：Dubbo 默认每个 Provider 对应 160 个虚拟节点，均匀分布在哈希环上，使节点增删时影响范围最小化。与[分布式系统](分布式系统.md#一致性哈希为什么需要虚拟节点)的一致性哈希原理一致。

### 集群容错

频次 ★★★★ · 难度 🟡

| 策略 | 说明 | 适用场景 |
|------|------|---------|
| **Failover**（默认） | 调用失败自动重试其他节点，`retries=2`（默认重试 2 次） | 读操作或幂等写操作 |
| **Failfast** | 立即失败，不重试，抛异常 | 非幂等写操作（如扣库存） |
| **Failsafe** | 失败后静默忽略（不抛异常），返回空结果 | 日志上报、非核心链路 |
| **Failback** | 失败后定时重试（后台线程），返回空结果 | 消息通知，允许异步补偿 |
| **Forking** | 同时调用多个节点，谁先返回用谁 | 对延迟要求极高的读场景 |
| **Broadcast** | 广播给所有节点，一个失败就抛异常 | 缓存更新、状态同步 |

**常见追问**：
- 重试会引发什么问题？→ 非幂等操作重复执行（如重复扣款）。所以写操作必须用 Failfast 或自己保证幂等
- 重试和超时怎么配合？→ `timeout` 是单次调用超时，`retries` 是重试次数，总耗时 = timeout × (retries + 1)，必须设合理（如 `timeout=500ms, retries=2` 最坏 1.5s）

**FailoverClusterInvoker 源码**（Dubbo 3.x，简化）：

```java
public class FailoverClusterInvoker<T> extends AbstractClusterInvoker<T> {
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) {
        int len = getUrl().getMethodParameter(invocation.getMethodName(), RETRIES_KEY, DEFAULT_RETRIES) + 1;
        // 重试次数 = retries + 1（第一次调用 + retries 次重试）
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                // 重试前检查是否有已调用过的 invoker，避免重复调用
                checkInvokers(invokers, invocation);
                // 重新选一个（排除已失败的）
                Invoker<T> invoker = select(loadbalance, invocation, invokers, selected);
                Result result = invoker.invoke(invocation);
                if (result != null) return result;
            }
        }
        // 所有重试都失败，抛异常
        throw new RpcException("Failed to invoke ...");
    }
}
```

---

## 四、协议与 SPI

### 协议对比：Dubbo vs Triple vs HTTP

频次 ★★★ · 难度 🟢

| 协议 | 传输 | 序列化 | 适用场景 |
|------|------|--------|---------|
| **Dubbo** | TCP | Hessian2 | 内部服务间高性能调用，Java ↔ Java |
| **Triple** | HTTP/2 | Protobuf | 跨语言、云原生、Streaming、gRPC 兼容 |
| **HTTP/REST** | HTTP | JSON/XML | 对外开放 API、异构系统集成 |
| **gRPC** | HTTP/2 | Protobuf | 跨语言、双向流，Triple 的底层基础 |

**Dubbo 协议为什么快**：单一长连接 + NIO 异步通信，避免 HTTP 每次请求的握手开销；Hessian2 序列化体积小速度快。

### SPI 与 Dubbo 的扩展点

频次 ★★★ · 难度 🔴

**是什么**：Dubbo 的**微内核 + 插件化架构**——内核只定义接口，所有功能（协议、序列化、负载均衡、过滤器等）都是 SPI 扩展点，可插拔。

**与 Java SPI 的区别**：

| 特性 | Java SPI | Dubbo SPI |
|------|---------|-----------|
| 加载方式 | 一次性加载全部实现 | **按需加载**，只加载配置文件中指定的 |
| 扩展名 | 无 | 每个实现有唯一名称（如 `dubbo`、`triple`） |
| 自适应扩展 | 不支持 | `@Adaptive` 根据运行时参数动态选择实现 |
| IoC | 不支持 | 扩展点之间可以自动注入依赖 |
| AOP | 不支持 | `@Activate` 按条件自动包装，实现 Filter 链自动装配 |

```java
// Dubbo SPI 配置文件示例
// META-INF/dubbo/org.apache.dubbo.rpc.Protocol
dubbo=org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol
triple=org.apache.dubbo.rpc.protocol.tri.TripleProtocol
rest=org.apache.dubbo.rpc.protocol.rest.RestProtocol

// 使用：@SPI 注解标记扩展点接口
@SPI("dubbo")  // 默认用 dubbo 协议
public interface Protocol {
    <T> Exporter<T> export(Invoker<T> invoker);
    <T> Invoker<T> refer(Class<T> type, URL url);
}
```

**ExtensionLoader 加载源码**（Dubbo 3.x，简化）：

```java
// ExtensionLoader.getExtension("dubbo") 的核心流程
public T getExtension(String name) {
    // 1. 从缓存取，没有则创建
    Holder<Object> holder = cachedInstances.get(name);
    if (holder == null) {
        cachedInstances.putIfAbsent(name, new Holder<>());
        holder = cachedInstances.get(name);
    }
    // 2. 双重检查锁 + 创建实例
    Object instance = holder.get();
    if (instance == null) {
        synchronized (holder) {
            instance = createExtension(name);
            holder.set(instance);
        }
    }
    return (T) instance;
}

private T createExtension(String name) {
    // 1. 加载 SPI 配置文件，获取实现类 Class
    Class<?> clazz = getExtensionClasses().get(name);
    // 2. 反射创建实例
    T instance = (T) clazz.newInstance();
    // 3. IoC：注入依赖的扩展点（递归调用 getExtension）
    injectExtension(instance);
    // 4. AOP：用 @Activate 包装类生成代理
    if (instance instanceof WrapperClass) {
        instance = ((WrapperClass)instance).wrapInstance(instance);
    }
    return instance;
}
```

---

## 五、DUBBO 服务暴露和引用流程

### 服务暴露流程

频次 ★★★ · 难度 🔴

**是什么**：Provider 端启动时，Dubbo 将服务暴露到网络的完整过程。

```text
ServiceConfig（用户配置）
  → ProxyFactory.getInvoker（生成 Invoker）
  → Protocol.export（暴露服务）
      → 本地暴露（Injvm 协议，供本地 JVM 调用）
      → 远程暴露（Dubbo/Triple 协议，供远程调用）
  → Exporter（保存 Invoker 和绑定的端口）
  → Registry.register（向注册中心注册服务地址）
```

**本地暴露 vs 远程暴露**：
- **本地暴露**：同 JVM 内调用不走网络，用 `InjvmProtocol`，通过 `injvm://` URL 标识。消费方和服务方在同一 JVM 时自动走本地
- **远程暴露**：通过配置的协议（Dubbo/Triple）暴露到网络，注册到注册中心

**RegistryProtocol.export 源码**（Dubbo 3.x，简化）：

```java
// RegistryProtocol.export() 的核心流程
public <T> Exporter<T> export(final Invoker<T> originInvoker) {
    // 1. 获取注册中心 URL 和导出 URL
    URL registryUrl = getRegistryUrl(originInvoker);
    URL providerUrl = getProviderUrl(originInvoker);

    // 2. 委托具体协议（Dubbo/Triple）暴露服务，绑定端口
    final Exporter<T> exporter = doLocalExport(originInvoker);

    // 3. 向注册中心注册服务地址
    final Registry registry = getRegistry(registryUrl);
    registry.register(providerUrl);

    // 4. 注册成功后，订阅覆盖规则（配置中心动态路由）
    registry.subscribe(overrideSubscribeUrl, overrideListener);

    return exporter;
}

// 实际暴露服务的核心：打开 Netty 端口
private <T> Exporter<T> doLocalExport(Invoker<T> invoker) {
    URL url = invoker.getUrl();
    // 协议适配（DubboProtocol / TripleProtocol 等）
    return protocolSPI.export(invoker);
}
```

### 服务引用流程

频次 ★★★ · 难度 🔴

```text
ReferenceConfig（用户配置）
  → RegistryProtocol.refer（从注册中心订阅）
  → Cluster.getProxy（生成代理对象，屏蔽 Cluster + LoadBalance）
  → ProxyFactory.getProxy（JDK 动态代理 / Javassist 字节码代理）
  → 返回代理对象给业务代码使用
```

---

## 六、Dubbo 与 Spring Cloud 对比

频次 ★★★★ · 难度 🟢

| 维度 | Dubbo | Spring Cloud |
|------|-------|-------------|
| 定位 | RPC 框架 + 服务治理 | 微服务全栈生态 |
| 通信协议 | Dubbo/Triple（TCP，性能高） | HTTP REST（通用，生态好） |
| 服务发现 | Nacos/ZK | Nacos/Eureka/Consul |
| 负载均衡 | 客户端（5 种策略） | 客户端（Ribbon/LoadBalancer） |
| 容错 | 6 种集群容错策略 | 重试 + 熔断（Sentinel/Hystrix/Resilience4j） |
| 配置管理 | 配置中心插件（Nacos/Apollo） | Spring Cloud Config + Bus |
| 网关 | 无内置，可对接 | Zuul/Gateway |
| 云原生 | 3.x 支持 K8s/Proxyless | Spring Cloud + K8s 原生 |
| 学习成本 | 较低（专注 RPC 治理） | 较高（组件多，生态大） |
| 跨语言 | Triple 协议支持 | HTTP 天然跨语言 |

**选型建议**：
- 内部服务间通信性能要求高、Java 技术栈统一 → **Dubbo**（更轻量，治理能力更强）
- 异构系统多、需要 API 网关、团队更熟 Spring 生态 → **Spring Cloud**（HTTP 更通用，组件生态更完整）
- 两者可以共存：Dubbo 做内部高性能 RPC，Spring Cloud Gateway 做对外 API 网关

---

## 七、常见面试题

### Dubbo 支持哪些序列化方式？

Hessian2（默认）、JSON、Protobuf、Kryo、FST、Java 原生序列化。推荐 Hessian2（跨语言 + 体积小）或 Protobuf（跨语言 + 性能高）。

### Dubbo 的 Filter 链如何工作？

Dubbo 的 Filter 链基于 `@Activate` SPI 机制——每个 Filter 通过 `@Activate(group = "provider")` 或 `@Activate(group = "consumer")` 声明在 Provider 端还是 Consumer 端生效。Filter 链通过**责任链模式**串联，每次调用依次经过 Consumer 端 Filter → Provider 端 Filter。

内置 Filter 举例：`ExceptionFilter`（异常统一处理）、`AccessLogFilter`（访问日志）、`TimeoutFilter`（超时监控）、`ContextFilter`（隐式参数传递）。

### Dubbo 如何实现本地调用？

同 JVM 内自动走 `injvm://` 协议，不会经过网络。配置 `<dubbo:consumer check=false scope=local />` 或设置 `@DubboReference(scope = Scope.LOCAL)`。

### Dubbo 的隐式参数传递怎么实现？

通过 `RpcContext`：Consumer 端 `RpcContext.getContext().setAttachment("key", "value")`，Provider 端 `RpcContext.getContext().getAttachment("key")` 获取。注意仅限当前调用，不能被跨线程传递（`RpcContext` 是 `ThreadLocal` 实现）。

### 怎么确定 Dubbo 的线程模型？

Dubbo 2.x 默认 IO 线程（Boss/Worker）和业务线程共用同一池，IO 线程处理完请求后直接在业务线程池执行。Dubbo 3.x 支持**线程池隔离**，可以为不同服务设置不同的线程池。`<dubbo:provider threadpool="cached" threads="200" />`。

---

**关联篇目**：[Netty与RPC](Netty与RPC.md)（Dubbo 底层网络传输依赖 Netty）、[分布式系统](分布式系统.md)（一致性哈希、服务发现）、[SpringCloud微服务](SpringCloud微服务.md)（服务治理对比）
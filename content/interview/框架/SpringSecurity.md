# Spring Security

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [过滤器链](#spring-security-的过滤器链如何工作) | DelegatingFilterProxy、FilterChainProxy、SecurityFilterChain | 多条链如何匹配、过滤器顺序 |
| [认证流程](#用户名密码认证流程是什么) | Authentication、AuthenticationManager、ProviderManager | 多种认证方式如何扩展 |
| [安全上下文](#securitycontext-为什么通常存在线程本地变量中) | SecurityContextHolder、ThreadLocal、请求结束清理 | 异步线程如何传递身份 |
| [401/403](#authenticationentrypoint-和-accessdeniedhandler-分别处理什么) | 未认证 vs 权限不足 | 为什么业务异常处理器接不到 |
| [密码编码](#passwordencoder-为什么使用委托模式) | 慢哈希、DelegatingPasswordEncoder、算法迁移 | `{id}` 前缀、何时升级哈希 |
| [请求授权](#请求级授权和方法级授权如何配合) | RequestMatcher、GrantedAuthority、`@PreAuthorize` | URL 放行后为何仍需对象级鉴权 |
| [JWT 资源服务器](#无状态-jwt-api-如何配置) | Bearer Token、签名和声明校验、无服务端会话 | Token 撤销、密钥轮换 |
| [CSRF/CORS](#前后端分离项目能直接关闭-csrf-吗) | 凭证是否自动携带、预检请求、配置顺序 | Cookie JWT 是否仍有 CSRF |

Spring Security 解决的是应用安全链路的编排；认证协议、JWT 和 OAuth 2.0 的安全边界见[安全认证](../分布式与架构/安全认证.md)。

---

## 一、整体架构

### Spring Security 的过滤器链如何工作？

频次 ★★★★★ · 难度 🔴 · 高频：全厂

Servlet 容器只认识标准 `Filter`，Spring Bean 则由容器管理，两者通过以下链路衔接：

```text
Servlet FilterChain
  -> DelegatingFilterProxy
    -> FilterChainProxy
      -> 第一个匹配请求的 SecurityFilterChain
        -> 认证、异常转换、授权等 Security Filter
          -> DispatcherServlet
```

- `DelegatingFilterProxy`：在 Servlet 生命周期与 Spring 容器之间做桥接，把请求委托给 Spring Bean。
- `FilterChainProxy`：Spring Security 的总入口，选择安全链、应用防火墙并在请求结束时清理安全上下文。
- `SecurityFilterChain`：一组带 `RequestMatcher` 的安全过滤器。可以为 `/api/**`、管理端和静态资源配置不同链。

多条链只执行**第一个匹配项**，所以必须把范围更窄的链放在前面，并配置兜底链。把 `/api/**` 链排在 `/**` 后面，会导致前者永远无法命中。

```java
@Bean
@Order(1)
SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/api/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .build();
}
```

**常见追问**：
- 自定义 JWT Filter 应该放在哪？→ 放在授权过滤器之前，并保证只执行一次；更推荐直接使用 Resource Server 的 Bearer Token 支持，而不是重复实现协议解析。
- 为什么过滤器顺序重要？→ 必须先加载上下文、再认证、最后授权；顺序颠倒时授权阶段看不到已认证身份。

### 用户名密码认证流程是什么？

频次 ★★★★★ · 难度 🔴 · 高频：全厂

1. 认证过滤器从请求提取凭证，构造尚未认证的 `Authentication`。
2. 过滤器调用 `AuthenticationManager.authenticate()`。
3. 常用实现 `ProviderManager` 遍历支持该 Token 类型的 `AuthenticationProvider`。
4. Provider 读取用户、校验密码，返回已认证且带权限的 `Authentication`。
5. 成功结果写入 `SecurityContext`；失败交给失败处理器。

`ProviderManager` 负责路由，`AuthenticationProvider` 负责一种认证机制。用户名密码、短信验证码、LDAP 可以各自实现 Provider，而不是在一个过滤器里堆条件分支。

**常见追问**：
- `UserDetailsService` 会校验密码吗？→ 不会，它只加载用户；密码匹配由 `DaoAuthenticationProvider` 配合 `PasswordEncoder` 完成。
- `Authentication` 中有什么？→ `principal`、`credentials`、`authorities` 和认证状态；成功后通常清除凭证，减少密码残留。

### SecurityContext 为什么通常存在线程本地变量中？

频次 ★★★★ · 难度 🟡

`SecurityContextHolder` 默认用 `ThreadLocal` 保存当前请求的 `SecurityContext`，业务代码无需层层传递身份参数，同一请求线程内都能读取 `Authentication`。请求结束必须清理，否则线程池复用会把上一个用户身份泄漏给下一个请求；`FilterChainProxy` 会负责清理。

异步任务切换线程后，普通 ThreadLocal 不会自动传递。可使用 `DelegatingSecurityContextExecutor`、`DelegatingSecurityContextRunnable`，或在提交任务时显式捕获并在执行后清理上下文。不要仅切换为 `MODE_INHERITABLETHREADLOCAL` 就交给线程池：线程池线程通常早已创建，不会为每个任务重新继承。

**通用概念**：这与 MDC、事务上下文的传播问题相同，都是“捕获 → 传递 → 恢复 → 清理”的上下文生命周期，底层原理见[并发编程](../Java/并发编程.md)的 ThreadLocal。

---

## 二、异常与密码

### AuthenticationEntryPoint 和 AccessDeniedHandler 分别处理什么？

频次 ★★★★ · 难度 🟡

| 场景 | 处理组件 | 常见响应 |
|------|----------|----------|
| 用户未认证，需要提供凭证 | `AuthenticationEntryPoint` | 401 或跳转登录页 |
| 用户已认证，但权限不足 | `AccessDeniedHandler` | 403 |

`ExceptionTranslationFilter` 把过滤器链中的认证/授权异常转换为 HTTP 响应。异常发生在 `DispatcherServlet` 之前，因此普通 `@ControllerAdvice` 通常接不到；API 项目应分别配置 JSON 格式的 EntryPoint 和 Handler，不能把所有失败都返回 200。

### PasswordEncoder 为什么使用委托模式？

频次 ★★★★ · 难度 🟡

密码哈希参数会随硬件和安全要求演进。`DelegatingPasswordEncoder` 用 `{id}encodedPassword` 保存算法标识，例如 `{bcrypt}...`，校验旧密码时按 id 路由到旧算法，新密码按当前默认算法生成，从而支持渐进迁移。

密码应使用 BCrypt、PBKDF2、scrypt 或 Argon2 等带盐慢哈希，不能使用 MD5、SHA-256 等快速摘要。登录成功后若 `upgradeEncoding()` 判断参数已落后，可在本次认证后重新编码并更新存储。

---

## 三、授权

### 请求级授权和方法级授权如何配合？

频次 ★★★★★ · 难度 🟡 · 高频：全厂

- 请求级授权：在 `authorizeHttpRequests` 中按路径、HTTP 方法和身份做入口拦截。
- 方法级授权：启用 `@EnableMethodSecurity`，用 `@PreAuthorize` 等表达式保护 Service 方法。
- 数据级授权：在业务查询中限制资源归属、租户或数据范围，不能靠角色字符串代替。

```java
@PreAuthorize("hasAuthority('order:refund') and #operatorId == authentication.name")
public void refund(Long orderId, String operatorId) {
    // 仍需在业务层校验订单归属和状态
}
```

URL 权限只能说明“能否进入接口”，不能证明用户有权操作某个订单。水平越权和对象归属校验见[安全认证](../分布式与架构/安全认证.md)。

### `hasRole` 和 `hasAuthority` 有什么区别？

`hasRole("ADMIN")` 默认检查 `ROLE_ADMIN`，会自动加 `ROLE_` 前缀；`hasAuthority("order:read")` 按原字符串比较。项目应统一角色和细粒度权限的命名，避免数据库存 `ADMIN`、代码又混用 `ROLE_ADMIN` 导致误判。

---

## 四、无状态 API

### 无状态 JWT API 如何配置？

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里

资源服务器接收 `Authorization: Bearer <token>`，验证签名、有效期、发行方和受众，再把声明转换为 `GrantedAuthority`。无状态表示服务端不保存登录 Session，不表示 Token 不需要撤销、轮换和审计。

```java
http
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
```

生产配置至少要限制允许的算法、校验 `iss`/`aud`、支持 `kid` 密钥轮换，并控制时钟偏差。Access Token 应短期有效；强制下线可结合短有效期、Refresh Token 轮换和高风险场景黑名单，详见[安全认证](../分布式与架构/安全认证.md)。

### 前后端分离项目能直接关闭 CSRF 吗？

频次 ★★★★ · 难度 🟡

不能只凭“前后端分离”判断，应看**浏览器是否会自动携带认证凭证**：

- Bearer Token 只放在自定义 `Authorization` 头，由前端代码主动添加，通常可关闭 CSRF 防护。
- Session Cookie 或把 JWT 放进 Cookie，浏览器会自动携带，仍需 CSRF Token、SameSite 等防护。
- CORS 解决的是跨域读取权限，不是 CSRF；必须限制可信 Origin，不能在携带凭证时使用通配来源。

**常见追问**：预检请求为什么被 401？→ `OPTIONS` 在认证过滤器前未放行，或 CORS Filter 顺序错误；应先处理 CORS，再进入认证授权链。

---

## 参考资料

- [Spring Security Servlet Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)
- [Spring Security Authentication Architecture](https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html)
- [安全认证](../分布式与架构/安全认证.md)

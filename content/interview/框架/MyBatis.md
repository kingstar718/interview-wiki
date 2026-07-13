# MyBatis

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [`#{}` vs `${}`](#和-的区别) | PreparedStatement、字符串替换 | 动态列名如何防注入 |
| [@Mapper vs @Repository](#mapper-和-repository-的区别) | 代理生成、异常转换、@MapperScan 原理 | 如何免标 @Mapper、PersistenceExceptionTranslationPostProcessor |
| [执行流程](#mybatis-执行一条-sql-的流程) | MapperProxy、SqlSession、Executor、Handler | 插件在哪一层拦截 |
| [一级缓存](#一级缓存和二级缓存的区别) | SqlSession 级、本地 Map | 更新后何时清空、跨会话为何无效 |
| [二级缓存](#一级缓存和二级缓存的区别) | namespace 级、序列化 | 多表和多实例一致性 |
| [动态 SQL](#常用动态-sql-标签有哪些) | if/where/foreach | 超大 IN、空集合、SQL 可读性 |
| [resultMap](#resulttype-和-resultmap-的区别) | 字段映射、关联和集合 | N+1、JOIN 结果去重 |
| [插件](#mybatis-插件原理) | 四大对象、动态代理 | 多插件顺序、BoundSql 修改风险 |
| [分页](#分页插件的基本原理) | count + 方言改写 | 深分页、游标分页 |
| [批处理](#mybatis-执行一条-sql-的流程) | BatchExecutor、flushStatements | 事务大小、部分失败如何处理 |
| [MyBatis-Spring 集成](#mybatis-是如何整合进-spring-的) | SqlSessionFactoryBean、MapperFactoryBean、SqlSessionTemplate | @MapperScan 源码、线程安全的 SqlSession |

回答 ORM 题要回到底层 JDBC，说明 MyBatis 帮你封装了什么、没有替你解决什么。

---

## 一、MyBatis 核心

### `#` 和 `$` 的区别？

| | `#{}` | `${}` |
|---|---|---|
| SQL 类型 | 预编译 SQL（? 占位符） | 普通 SQL 字符串拼接 |
| 安全性 | 防止 SQL 注入 | 不防 SQL 注入 |
| 性能 | 预编译，执行效率高 | 每次编译 |
| 用途 | 参数值 | 表名、列名等动态 SQL |

### MyBatis 的优势

- SQL 与代码解耦，写在 XML 中便于管理和优化
- 动态 SQL 支持（`<if>`、`<foreach>` 等标签）
- 自动映射 + 自定义 resultMap
- 插件扩展机制（分页、SQL 改写等）

### JDBC 连接步骤

加载驱动 → 建立连接 → 创建 Statement → 执行 SQL → 处理 ResultSet → 关闭资源（ResultSet → Statement → Connection）

### @Mapper 和 @Repository 的区别？

频次 ★★★★ · 难度 🟡

| | `@Mapper` | `@Repository` |
|---|---|---|
| 来源 | MyBatis 自有注解 | Spring 注解（stereotype 体系，同 `@Service`/`@Controller`） |
| 作用 | 标识映射器接口，配合 `@MapperScan` 生成代理 | 标记 DAO 层，让 Spring 自动扫描并注册 Bean |
| 代理生成 | `MapperScannerConfigurer` 扫描 `@Mapper` 后通过 `MapperFactoryBean` 创建 JDK 动态代理 | `ClassPathBeanDefinitionScanner` 扫到后注册普通 BeanDefinition，真正代理由 `MapperFactoryBean` 创建（需额外依赖 MyBatis-Spring） |
| 异常转换 | 不处理 | 配合 `PersistenceExceptionTranslationPostProcessor` 将 MyBatis 异常转为 Spring 的 `DataAccessException` 体系 |
| 是否必须 | 必须（框架要求） | 可选（Spring 管理的辅助标注） |

**为什么常一起用**？只用 `@Mapper` 时 MyBatis 的 `org.apache.ibatis.binding.BindingException` 等异常直接抛到上层；加上 `@Repository` 后 Spring 的 `PersistenceExceptionTranslationPostProcessor` 会拦截 DAO 层的异常并翻译为 `DataAccessException` 体系，便于和 JPA/JDBC 等其他数据访问技术统一异常处理。所以常见写法是 `@Repository` + `@Mapper` 同时标注。

**常见追问**
- `@MapperScan` 是什么原理？→ 注册 `MapperScannerConfigurer`，它实现 `BeanDefinitionRegistryPostProcessor`，在容器启动时扫描指定包下带 `@Mapper` 的接口，逐个注册为 `MapperFactoryBean` 类型的 BeanDefinition
- 不加 `@Mapper` 能用 `@MapperScan` 吗？→ 可以。`@MapperScan` 的 `annotationClass` 默认是 `Mapper.class`，但可以改为 `@Repository.class` 或留空扫所有接口；SpringBoot 中也可以只在启动类上加 `@MapperScan` 扫整个 Mapper 包，类上免标 `@Mapper`

---

## 二、执行流程

### MyBatis 执行一条 SQL 的流程？

难度 🔴

```text
Mapper 接口代理
  ↓
MapperMethod
  ↓
SqlSession
  ↓
Executor
  ↓
StatementHandler
  ↓
ParameterHandler / ResultSetHandler
  ↓
JDBC
```

1. `SqlSessionFactory` 根据配置创建 `SqlSession`。
2. `MapperProxy` 将 Mapper 接口方法转换为 `MappedStatement` 调用。
3. `Executor` 负责缓存、事务和 SQL 执行调度。
4. `StatementHandler` 创建并执行 JDBC Statement。
5. `ParameterHandler` 设置参数。
6. `ResultSetHandler` 将结果集映射为 Java 对象。

### MyBatis 中 Executor 有哪些类型？

| 类型 | 特点 |
|------|------|
| `SimpleExecutor` | 每次执行都创建新的 Statement |
| `ReuseExecutor` | 复用相同 SQL 的 Statement |
| `BatchExecutor` | 批量执行更新语句，统一刷新 |
| `CachingExecutor` | 装饰器，负责二级缓存 |

批量执行需要关注错误定位、事务大小和 JDBC 驱动配置，不能只看代码调用次数。

---

## 三、缓存机制

### 一级缓存和二级缓存的区别？

难度 🟡

| | 一级缓存 | 二级缓存 |
|---|---|---|
| 范围 | `SqlSession` | Mapper namespace |
| 默认状态 | 默认开启 | 需要显式开启 |
| 生命周期 | 会话结束后失效 | 可跨 SqlSession |
| 数据结构 | 本地 Map | 可扩展缓存实现 |

一级缓存可能导致同一会话内读到旧值；执行更新、提交、回滚或清空缓存会使相关缓存失效。

二级缓存使用要谨慎：

- 多表关联查询很难精确失效。
- 分布式环境下本地二级缓存无法天然保持一致。
- 对一致性敏感的数据更适合显式缓存方案。

### 为什么生产项目经常不用 MyBatis 二级缓存？

因为它按 namespace 管理，缓存失效粒度较粗。复杂业务中一张表可能被多个 Mapper 修改，容易出现一致性问题。Redis 等独立缓存虽然也需要一致性治理，但可观测性、容量和跨实例共享能力更强。

---

## 四、动态 SQL 与结果映射

### 常用动态 SQL 标签有哪些？

- `<if>`：按条件拼接。
- `<choose>/<when>/<otherwise>`：多分支选择。
- `<where>`：自动处理 `WHERE` 和前导 `AND/OR`。
- `<set>`：更新语句中自动处理逗号。
- `<foreach>`：批量参数和 `IN` 查询。
- `<sql>/<include>`：复用 SQL 片段。

大集合使用 `IN` 时要关注数据库参数上限、SQL 长度和执行计划，必要时分批或使用临时表。

### `resultType` 和 `resultMap` 的区别？

- `resultType`：适合字段和属性可直接映射的简单结果。
- `resultMap`：适合字段改名、嵌套对象、一对一和一对多映射。

复杂关联应警惕 N+1 查询。可以使用 JOIN 一次查询、批量查询后组装，或明确控制延迟加载。

---

## 五、插件与工程实践

### MyBatis 插件原理？

难度 🔴

MyBatis 通过 JDK 动态代理拦截四类核心对象：

- `Executor`
- `StatementHandler`
- `ParameterHandler`
- `ResultSetHandler`

插件使用 `@Intercepts` 和 `@Signature` 声明拦截目标，可以实现分页、SQL 改写、审计和性能监控。插件会进入执行主链，必须控制耗时并避免修改错误的 BoundSql。

### 分页插件的基本原理？

拦截查询流程，根据数据库方言：

1. 生成统计总数 SQL。
2. 给原 SQL 增加 `LIMIT/OFFSET` 等分页语句。
3. 重新绑定参数并执行。

深分页仍然可能扫描和丢弃大量记录，不能仅依赖分页插件，应考虑游标或基于索引的 seek 分页。

### MyBatis-Plus 和 MyBatis 的关系？

MyBatis-Plus 是 MyBatis 的增强工具，提供通用 CRUD、条件构造器、分页和代码生成等能力，不替代 MyBatis。

使用时需要注意：

- 复杂 SQL 仍应明确编写和评审。
- 条件构造器不能代替索引和执行计划分析。
- 自动填充、逻辑删除和租户插件会隐式改写 SQL。

### Mapper 方法传多个参数为什么常用 `@Param`？

`@Param` 为参数提供稳定名称，便于 XML 和注解 SQL 引用。否则参数名可能依赖编译选项，或只能通过 `param1`、`arg0` 等名称访问，降低可读性和稳定性。

---

## 六、MyBatis-Spring 集成

### MyBatis 是如何整合进 Spring 的？

频次 ★★★★ · 难度 🔴

MyBatis-Spring 是 MyBatis 和 Spring 之间的桥梁，核心靠三个 SPI 对象把 MyBatis 的组件挂接到 Spring 容器中：

**1. SqlSessionFactoryBean** — 创建 SqlSessionFactory

实现 `FactoryBean<SqlSessionFactory>`，在 `getObject()` 中完成：
1. 读取 `Configuration`（XML 或 Java 配置类）
2. 扫描 Mapper XML 文件路径注册 `MappedStatement`
3. 注册类型别名和类型处理器
4. 设置插件（分页、审计等 Interceptor）
5. 返回配置好的 `SqlSessionFactory`

**2. MapperFactoryBean** — 将 Mapper 接口转为 Spring Bean

每个 Mapper 接口对应一个 `MapperFactoryBean`，它同样实现 `FactoryBean` 接口，`getObject()` 返回 JDK 动态代理（`MapperProxy`）。代理对象注入到所有需要该 Mapper 的 Spring Bean 中——这就是为什么 `@Autowired private UserMapper userMapper` 拿到的是 MyBatis 代理。

**3. SqlSessionTemplate** — 线程安全的 SqlSession 替代品

原生 MyBatis 的 `SqlSession` 不是线程安全的（它与当前连接绑定在 ThreadLocal）。`SqlSessionTemplate` 封装了每次从 `SqlSessionFactory` 获取新 `SqlSession`、使用后关闭的逻辑，保证每个 DAO 方法都在自己的 SqlSession 中执行。它也是 **Spring 事务管理的关键**——事务开启时，`TransactionSynchronizationManager` 会把当前 SqlSession 绑定到当前线程，`SqlSessionTemplate` 检查到线程已绑定就直接复用，实现事务传播。

**三者的装配链路**：
```text
@MapperScan
  ↓ 注册
MapperScannerConfigurer
  ↓ 扫描 @Mapper 接口
ClassPathMapperScanner
  ↓ 为每个接口创建 BeanDefinition（beanClass = MapperFactoryBean）
MapperFactoryBean.getObject()
  ↓
MapperProxy（JDK 动态代理）
  ↓ 方法调用时委托给
SqlSessionTemplate
  ↓
SqlSession（线程安全使用）
```

**常见追问**
- 没有 MyBatis-Spring 能用 Mapper 吗？→ 可以，但每个 Mapper 方法前要手动 `sqlSessionFactory.openSession()` → `session.getMapper()` → 用完 `session.close()`，且没有事务管理
- `SqlSessionTemplate` 和 `SqlSessionManager` 的区别？→ `SqlSessionTemplate` 是 MyBatis-Spring 提供的，必须配 Spring 事务管理器；`SqlSessionManager` 是 MyBatis 原生提供的，只能在单线程中串行使用
- 为什么 MyBatis-Spring 是专门一个 jar 包而不是 MyBatis 内置功能？→ 因为 MyBatis 框架本身不假设任何 IoC 容器，集成 Spring 需要的 `FactoryBean`、`BeanDefinitionRegistryPostProcessor` 都是 Spring SPI，**是 MyBatis 适配 Spring，而不是 Spring 适配 MyBatis**——同一思路看 [RocketMQ](消息队列.md) 和 [Redis](Redis.md) 的 Spring Boot Starter


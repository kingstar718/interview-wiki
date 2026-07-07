# MyBatis

[← 返回知识点](../社招问题知识点.md)

---

## 一、MyBatis 核心

### 1. `#` 和 `$` 的区别？

| | `#{}` | `${}` |
|---|---|---|
| SQL 类型 | 预编译 SQL（? 占位符） | 普通 SQL 字符串拼接 |
| 安全性 | 防止 SQL 注入 | 不防 SQL 注入 |
| 性能 | 预编译，执行效率高 | 每次编译 |
| 用途 | 参数值 | 表名、列名等动态 SQL |

### 2. MyBatis 的优势

- SQL 与代码解耦，写在 XML 中便于管理和优化
- 动态 SQL 支持（`<if>`、`<foreach>` 等标签）
- 自动映射 + 自定义 resultMap
- 插件扩展机制（分页、SQL 改写等）

### 3. JDBC 连接步骤

加载驱动 → 建立连接 → 创建 Statement → 执行 SQL → 处理 ResultSet → 关闭资源（ResultSet → Statement → Connection）

---

## 二、执行流程

### 1. MyBatis 执行一条 SQL 的流程？（难度：Hard）

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

### 2. MyBatis 中 Executor 有哪些类型？

| 类型 | 特点 |
|------|------|
| `SimpleExecutor` | 每次执行都创建新的 Statement |
| `ReuseExecutor` | 复用相同 SQL 的 Statement |
| `BatchExecutor` | 批量执行更新语句，统一刷新 |
| `CachingExecutor` | 装饰器，负责二级缓存 |

批量执行需要关注错误定位、事务大小和 JDBC 驱动配置，不能只看代码调用次数。

---

## 三、缓存机制

### 1. 一级缓存和二级缓存的区别？（难度：Medium）

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

### 2. 为什么生产项目经常不用 MyBatis 二级缓存？

因为它按 namespace 管理，缓存失效粒度较粗。复杂业务中一张表可能被多个 Mapper 修改，容易出现一致性问题。Redis 等独立缓存虽然也需要一致性治理，但可观测性、容量和跨实例共享能力更强。

---

## 四、动态 SQL 与结果映射

### 1. 常用动态 SQL 标签有哪些？

- `<if>`：按条件拼接。
- `<choose>/<when>/<otherwise>`：多分支选择。
- `<where>`：自动处理 `WHERE` 和前导 `AND/OR`。
- `<set>`：更新语句中自动处理逗号。
- `<foreach>`：批量参数和 `IN` 查询。
- `<sql>/<include>`：复用 SQL 片段。

大集合使用 `IN` 时要关注数据库参数上限、SQL 长度和执行计划，必要时分批或使用临时表。

### 2. `resultType` 和 `resultMap` 的区别？

- `resultType`：适合字段和属性可直接映射的简单结果。
- `resultMap`：适合字段改名、嵌套对象、一对一和一对多映射。

复杂关联应警惕 N+1 查询。可以使用 JOIN 一次查询、批量查询后组装，或明确控制延迟加载。

---

## 五、插件与工程实践

### 1. MyBatis 插件原理？（难度：Hard）

MyBatis 通过 JDK 动态代理拦截四类核心对象：

- `Executor`
- `StatementHandler`
- `ParameterHandler`
- `ResultSetHandler`

插件使用 `@Intercepts` 和 `@Signature` 声明拦截目标，可以实现分页、SQL 改写、审计和性能监控。插件会进入执行主链，必须控制耗时并避免修改错误的 BoundSql。

### 2. 分页插件的基本原理？

拦截查询流程，根据数据库方言：

1. 生成统计总数 SQL。
2. 给原 SQL 增加 `LIMIT/OFFSET` 等分页语句。
3. 重新绑定参数并执行。

深分页仍然可能扫描和丢弃大量记录，不能仅依赖分页插件，应考虑游标或基于索引的 seek 分页。

### 3. MyBatis-Plus 和 MyBatis 的关系？

MyBatis-Plus 是 MyBatis 的增强工具，提供通用 CRUD、条件构造器、分页和代码生成等能力，不替代 MyBatis。

使用时需要注意：

- 复杂 SQL 仍应明确编写和评审。
- 条件构造器不能代替索引和执行计划分析。
- 自动填充、逻辑删除和租户插件会隐式改写 SQL。

### 4. Mapper 方法传多个参数为什么常用 `@Param`？

`@Param` 为参数提供稳定名称，便于 XML 和注解 SQL 引用。否则参数名可能依赖编译选项，或只能通过 `param1`、`arg0` 等名称访问，降低可读性和稳定性。

---

## 六、面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| `#{}` vs `${}` | PreparedStatement、字符串替换 | 动态列名如何防注入 |
| 执行流程 | MapperProxy、SqlSession、Executor、Handler | 插件在哪一层拦截 |
| 一级缓存 | SqlSession 级、本地 Map | 更新后何时清空、跨会话为何无效 |
| 二级缓存 | namespace 级、序列化 | 多表和多实例一致性 |
| 动态 SQL | if/where/foreach | 超大 IN、空集合、SQL 可读性 |
| resultMap | 字段映射、关联和集合 | N+1、JOIN 结果去重 |
| 插件 | 四大对象、动态代理 | 多插件顺序、BoundSql 修改风险 |
| 分页 | count + 方言改写 | 深分页、游标分页 |
| 批处理 | BatchExecutor、flushStatements | 事务大小、部分失败如何处理 |

回答 ORM 题要回到底层 JDBC，说明 MyBatis 帮你封装了什么、没有替你解决什么。

---

[← 返回知识点](../社招问题知识点.md)

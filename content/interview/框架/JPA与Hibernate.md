# JPA 与 Hibernate

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [JPA 与 Hibernate](#jpa-和-hibernate-是什么关系) | 规范与实现、ORM、持久化上下文 | 与 MyBatis 如何选择 |
| [实体生命周期](#实体有哪些生命周期状态) | transient、managed、detached、removed | merge 与 persist 的区别 |
| [一级缓存](#一级缓存和持久化上下文是什么关系) | EntityManager 作用域、身份映射 | clear、detach、批处理内存 |
| [脏检查](#脏检查为什么不需要显式-update) | 快照、flush、事务提交 | flush 不等于 commit |
| [懒加载](#懒加载为什么经常抛-lazyinitializationexception) | 代理、会话边界、事务 | OSIV 的利弊 |
| [N+1](#n1-问题如何发现和解决) | 访问关联触发额外 SQL | fetch join、EntityGraph、批量抓取 |

---

## 一、基础与生命周期

### JPA 和 Hibernate 是什么关系？

JPA（Jakarta Persistence）定义 ORM 的接口、注解和语义，Hibernate ORM 是常用实现之一。Spring Data JPA 又在 JPA 之上提供 Repository 抽象和查询派生；它不是新的 ORM 实现。

与 [MyBatis](MyBatis.md) 相比，JPA 更适合以实体关系和聚合修改为中心的业务，能减少通用 CRUD；MyBatis 对 SQL、执行计划和复杂报表的控制更直接。选型不应只看代码量，还要看团队对 SQL 的控制需求、模型复杂度和性能可预测性。

### 实体有哪些生命周期状态？

- **瞬时（transient/new）**：刚 `new`，尚未交给持久化上下文。
- **托管（managed）**：被当前 `EntityManager` 跟踪，修改可在 flush 时同步数据库。
- **游离（detached）**：曾被托管，但上下文已关闭或实体被 `detach/clear`。
- **删除（removed）**：已标记删除，将在 flush 时执行 `DELETE`。

`persist` 使新实体进入托管状态；`merge` 把传入对象的状态复制到一个托管实例并返回它，原对象仍可能是游离对象，所以应使用返回值。

## 二、持久化上下文与写入

### 一级缓存和持久化上下文是什么关系？

一级缓存是持久化上下文的身份映射：同一上下文中按同一实体类型和主键查询，通常得到同一 Java 对象。它保证工作单元内的对象一致性，但不是跨请求、跨节点的业务缓存。

批量写入时要周期性 `flush()` 后 `clear()`，否则大量托管实体和快照会持续占内存。二级缓存跨持久化上下文，需单独配置，并要评估失效、一致性和命中率。

### 脏检查为什么不需要显式 UPDATE？

Hibernate 加载托管实体时保存快照，flush 时比较当前状态和快照，生成必要的 `UPDATE`。触发 flush 的常见时机是事务提交、显式 `flush()`，以及某些查询执行前。

`flush` 只把内存状态同步为数据库语句，不代表事务已提交；后续回滚仍会撤销。过早 flush 会增加往返和约束检查，过晚则可能让约束错误推迟暴露。

## 三、关联加载与性能

### 懒加载为什么经常抛 LazyInitializationException？

懒加载通常依赖代理或字节码增强；访问未初始化关联时，需要仍然可用的持久化上下文。实体离开事务后再访问关联，就可能抛出 `LazyInitializationException`。

解决重点是为用例显式定义查询边界：在事务内用 fetch join、EntityGraph 或 DTO 投影一次取齐所需数据。把所有关联改为 EAGER，或依赖 OSIV 把会话延长到 Web 层，容易产生不可见 SQL、连接占用和序列化递归。

### N+1 问题如何发现和解决？

先查一批父实体（1 条 SQL），遍历时每个父实体又触发一次关联查询（N 条），即 N+1。它既可能来自懒加载，也可能因 EAGER 未被当前 JPQL 一次抓取而发生。

常见手段：

1. 对当前用例使用 `join fetch` 或 `EntityGraph`。
2. 列表接口使用 DTO 投影，只取展示字段。
3. 对适合的关联启用批量抓取，减少往返，但它只是把 N 次压缩成若干批次。
4. 开启 SQL/慢查询统计，在集成测试中断言查询次数。

集合 fetch join 与分页组合要谨慎：行数会因一对多关联膨胀，可能导致内存分页或结果错误。可先分页查主键，再按主键批量抓取详情。

## 参考资料

- [Hibernate ORM User Guide](https://docs.hibernate.org/orm/current/userguide/html_single/)
- [Jakarta Persistence Specification](https://jakarta.ee/specifications/persistence/)


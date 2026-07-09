# MongoDB

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| 文档模型 | JSON/BSON、集合、库 | 无 Schema 设计、与关系型对比 |
| 复制集 | 主从、Oplog、自动故障转移 | 选举机制、写关注、读偏好 |
| 分片 | 片键选择、数据分布、平衡 | 片键选择不当后果、hashed/range 分片 |
| 索引 | 单字段/复合/TTL/稀疏/地理 | 索引下推到存储引擎、explain |
| 事务 | 4.0 多文档事务、2PC 模拟 | RDB 事务 vs MongoDB 事务差异 |
| 聚合管道 | \$match/\$group/\$lookup | 内存限制、$lookup 性能、pipeline 优化 |
| 存储引擎 | WiredTiger、MVCC、压缩 | 快照隔离、journal、cache 淘汰 |
| ObjectId | 12 字节、时间戳+机器+进程+自增 | 单调递增与分布式趋势 |

MongoDB 面试重点在于「无 Schema 带来的设计灵活性」以及「复制集/分片的分布式架构」。

---

[← 返回知识点](知识点索引.md)

---

## 一、基础概念

### MongoDB 适合什么场景？

频次 ★★★ · 难度 🟢

- **灵活的文档模型**：字段可变，适合数据结构频繁变更的业务（如电商 SKU 属性不统一）。
- **快速迭代**：不需要 DDL 迁移，代码改字段直接写。
- **高写入吞吐**：WiredTiger 引擎写入性能优异，适合日志、监控等 write-heavy 场景。
- **地理空间查询**：内置 2dsphere 索引，适合 LBS 应用。
- **二级索引 + 聚合**：比 Redis 功能丰富，比 MySQL 开发效率高。

不适合：强事务依赖（银行转账）、多表关联复杂查询（Join）、强 Schema 约束的领域模型。

### 文档、集合、数据库

| 概念 | 类比 RDB | 说明 |
|------|---------|------|
| Document | Row | BSON 格式，字段可动态增减 |
| Collection | Table | 自动建 collection，可设置 validator |
| Database | Database | 独立权限与 namespace |
| ObjectId | 自增 ID | 12 字节：4s 时间戳 + 5s 随机 + 3s 自增 |

### ObjectId 的结构

```text
ObjectId = [4 字节 unix 时间戳][5 字节随机机器/进程][3 字节自增计数器]
```

- 前 4 字节：秒级时间戳，隐含插入顺序（可按 `_id` 排序 ≈ 按时间排序）
- 中间 5 字节：机器标识 + 进程 ID，保证分布式不重复
- 后 3 字节：同一进程同一秒内的自增计数器
- 单调递增但不连续（分布式趋势，不是严格自增）

### MongoDB 与关系型数据库的映射

| 场景 | RDB 方式 | MongoDB 方式 |
|------|---------|-------------|
| 1:1 关系 | 外键 + JOIN | 内嵌文档 |
| 1:N 关系 | 外键表 | 数组内嵌 或 引用 |
| M:N 关系 | 中间表 | 数组引用 或 反向引用 |
| 频繁变动的字段 | DDL 修改表 | 直接写不同结构的文档 |

**内嵌 vs 引用的选择原则：**
- 内嵌：子数据规模小、总是随父文档一起读、不独立变更
- 引用：子数据规模大、需独立查询、频繁被多个父文档共用

---

## 二、CRUD 操作

### 基本写入与查询

```js
// 插入
db.users.insertOne({ name: "Alice", age: 30, tags: ["admin"] });
db.users.insertMany([...]);

// 查询
db.users.find({ age: { $gte: 18 } }).sort({ name: 1 }).limit(10);
db.users.findOne({ _id: ObjectId("...") });

// 更新
db.users.updateOne({ _id: ... }, { $set: { name: "Bob" }, $push: { tags: "vip" } });
db.users.replaceOne({ _id: ... }, { name: "Bob", age: 25 });  // 完全替换文档

// 删除
db.users.deleteMany({ status: "inactive" });
```

**更新操作符：** `$set`、`$unset`、`$inc`、`$push`、`$pull`、`$addToSet`、`$rename`

### 原子性与 upsert

- 单个文档的写操作是**原子**的（即使更新了多个字段）
- `updateOne` 带 `upsert: true`：不存在则插入，存在则更新（避免先查后改的竞态）

---

## 三、索引

### 索引类型

| 索引类型 | 说明 | 使用场景 |
|---------|------|---------|
| 单字段 | 普通 B-Tree 索引 | 等值/范围/排序 |
| 复合索引 | 多字段索引，支持**最左前缀** | 多条件组合查询 |
| 多键索引 | 数组字段的索引，每个元素一个索引项 | 数组字段查询 |
| TTL 索引 | 文档过期自动删除 | 会话/日志过期清理 |
| 稀疏索引 | 只为有该字段的文档建索引 | 字段缺失场景 |
| 地理索引 | 2dsphere（GeoJSON）/ 2d（坐标） | LBS 附近的人 |
| 哈希索引 | 字段哈希值做索引 | 分片片键均衡 |
| 文本索引 | 分词后全文索引 | 简单文本搜索 |

### 复合索引的最左前缀原则

与 MySQL 一致：`{a:1, b:1, c:1}` 能支持：
- `{a}` ✅ / `{a,b}` ✅ / `{a,b,c}` ✅
- `{b}` ❌ / `{c}` ❌ / `{b,c}` ❌

### explain 与慢查询

```js
db.users.find({ age: { $gt: 25 } }).explain("executionStats");
```

关注字段：`COLLSCAN`（集合扫描）→ 应加索引、`nReturned`  vs `totalDocsExamined`、`executionTimeMillis`。

### 索引是查询层还是存储引擎层的能力？

索引本身由**存储引擎**（WiredTiger）维护和物理存储——索引结构也是一棵 B-Tree，与数据一样走 WiredTiger 的 MVCC、压缩和缓存机制；查询层（Query Planner）只负责根据查询条件**选择**用哪个索引、生成执行计划。这也是为什么不同存储引擎（历史上的 MMAPv1）对同一份索引定义可能有不同的物理表现和性能特征，但索引的**逻辑语义**（最左前缀、多键等）由 Server 层统一定义，与引擎无关。

---

## 四、复制集

### 复制集架构

复制集 = 一组 mongod 实例，一主多从：

- **Primary**：处理所有写请求
- **Secondary**：通过同步 Oplog 异步复制，可配置读偏好
- **Arbiter**：只参与选举，不存数据（投票节点）

**Oplog（操作日志）**：
- `local.oplog.rs` 集合，Capped Collection（固定大小，滚动覆盖）
- Secondary 拉取 Oplog 重放 → 保持与 Primary 一致
- Oplog 大小不足 → Secondary 落后太多 → 无法追上时需重新同步

### 自动故障转移

1. Secondary 检测到 Primary 心跳超时（默认 10s）
2. 触发选举：节点**获得大多数投票**（含仲裁节点）→ 成为新 Primary
3. 旧 Primary 恢复后降级为 Secondary

**写关注（Write Concern）：**
| 级别 | 说明 |
|------|------|
| `w:1` | 主节点写入即确认（默认，故障切换可能丢数据） |
| `w:"majority"` | 大多数节点写入后才确认（强安全） |
| `w:3` | 指定 3 个节点确认 |

**读偏好（Read Preference）：**
| 模式 | 说明 |
|------|------|
| `primary` | 只读主（默认，强一致） |
| `primaryPreferred` | 主优先，主不可用时读从 |
| `secondary` | 只读从 |
| `nearest` | 读最近节点（低延迟） |

关联知识点：Redis 哨兵的 SDOWN→ODOWN 选举与 MongoDB 选举都是**多数派选主**，但 MongoDB 用 Oplog 同步更接近 MySQL 半同步复制。

---

## 五、分片

### 分片集群组成

- **mongos**：路由层，应用连接 mongos 即可，对分片透明
- **Config Server**：存集群元数据（片键范围、数据分布）
- **Shard**：每个分片是一个复制集，存实际数据

### 片键选择

**片键是决定分片性能的最关键因素**，一旦选定后很难修改。

| 片键策略 | 写入分布 | 查询性能 | 说明 |
|---------|---------|---------|------|
| **Hashed 片键** | 均匀 | 等值查询单分片 | 范围查询广播 |
| **Range 片键** | 可能倾斜 | 范围查询可定位 chunk | 热点写问题 |
| **自增 ID 片键** | ❌ 全写最后一个 chunk | 范围查询好 | 写入严重倾斜 |

**最佳实践：**
- 高写入场景 → hashed 片键
- 范围查询为主 → range 片键
- 避免单调递增的片键（时间戳、自增 ID）

**片键无法修改**——设计时需要充分考虑未来数据分布。

---

## 六、事务与一致性

### MongoDB 的多文档事务

- 4.0 起支持复制集内的多文档事务（ACID）
- 4.2 起支持分片集群上的多文档事务
- 事务在 WiredTiger 的快照隔离下执行

**事务与 RDB 事务的差异：**
| 维度 | RDB | MongoDB |
|------|-----|---------|
| 事务范围 | 表间 | 文档间（跨集合、跨库需要显式开启） |
| 隔离级别 | 可重复读/读已提交 | 快照隔离（SI） |
| 性能 | 高 | 比单文档写入慢（因 WiredTiger 事务开销） |
| 使用频率 | 高频 | 低频（推荐用单文档原子操作替代） |

### 写一致性

- **单文档写是原子的**：即使不开启事务，对单个文档的 `$set`、`$inc` 等操作原子完成
- **大多数场景不需要事务**：通过内嵌文档 + 原子操作就能保证一致性

---

## 七、聚合管道

### 聚合管道常用阶段

```js
db.orders.aggregate([
  { $match: { status: "completed" } },       // 过滤
  { $group: { _id: "$category", total: { $sum: "$amount" } } },  // 分组
  { $sort: { total: -1 } },
  { $limit: 10 },
  { $lookup: {                         // LEFT JOIN 等价
      from: "users",
      localField: "userId",
      foreignField: "_id",
      as: "user"
  }}
]);
```

**性能注意事项：**
- `$match` 和 `$sort` 尽量靠前，利用索引
- `$lookup` 性能低于 RDB 的 JOIN（MongoDB 不是为关联设计的）
- 聚合结果超 100MB 会写磁盘，大结果集用 `allowDiskUse: true`（但会慢）
- 能用 `$project` 提前减字段，减少内存开销

---

## 八、存储引擎 WiredTiger

### WiredTiger 核心特性

- **文档级并发控制**：MVCC + 行锁（文档级），写入并发高
- **压缩**：默认 snappy 压缩索引和数据，存储空间省
- **快照隔离**：读操作在快照上执行，写不阻塞读
- **Cache 淘汰**：LRU 策略，缓存不足时触发淘汰
- **Journal**：宕机恢复时通过 journal 回放未刷盘的写入

**Cache 大小**：默认 = max(50% × (物理内存 - 1GB), 256MB)——内存越大，缓存拿到的份额越大，256MB 只是小内存机器的下限保底，写密集场景建议上调。

### Journal 与 Checkpoint

- 写入先刷 Journal（WAL），再定期 Checkpoint 写入磁盘
- Checkpoint 间隔 60s 或 2GB journal 数据
- 宕机恢复：从最近 checkpoint 加载，回放 checkpoint 后的 journal
- 与 RocksDB 的 WAL / MySQL 的 Redo Log 原理相同

---

## 九、性能优化

| 优化手段 | 说明 |
|---------|------|
| 慢查询分析 | `explain("executionStats")` + `db.currentOp()` |
| 索引覆盖查询 | 只查索引包含的字段，避免文档读取 |
| 批量写入 | `insertMany`、`bulkWrite` |
| 连接池 | 合理大小（默认 100），避免短连接 |
| 选择合适片键 | hashed 片键防热点 |
| 控制集合大小 | 使用 Capped Collection 固定大小 |
| 监控内存 | WiredTiger cache 命中率 < 80% 需扩容或优化 |
| Oplog 大小 | 默认 5% 空闲磁盘，高写入场景应提前规划 |

### 常见慢查询场景

- 无索引的 COLLSCAN
- `$lookup` 在分片集群上执行
- `$regex` 前缀不固定（无法走索引）
- 未带片键的跨分片查询（广播到所有分片）

# 内容待办 — interview-wiki

> 规则：动手前先 grep 验证考点是否已覆盖；完成一项就移到「已完成」并附 commit 短哈希。
> 写法模板与同步清单见 [CONTRIBUTING.md](./CONTRIBUTING.md)。发现新缺口先登记到这里再做。

## 待办

### 高优先级（★★★★★/★★★★ 高频且明显缺失）

（暂无，2026-07-07 已清空，见已完成）

### 中优先级

- [ ] Redis：缓存预热 — 0 覆盖。冷启动雪崩、定时任务/双缓存/灰度放量方案，与缓存三大问题衔接
- [ ] 并发编程：ThreadLocal 源码补深 — 现有原理够用，可补 `ThreadLocalMap` 开放寻址、魔数 0x61c88647、`expungeStaleEntry` 清理时机（评估性价比后再做）
- [ ] 网络：TLS 1.3 握手 — 追问地图提到但正文只有 TLS 1.2；1-RTT/0-RTT、与 QUIC 衔接
- [ ] 操作系统：页表/TLB — 多级页表为什么省内存、TLB miss 代价、大页（HugePage）与数据库/JVM 的关系
- [ ] Spring：容器启动 refresh() 源码 — 0 覆盖。十二步骨架、`onRefresh` 与 SpringBoot 内嵌 Tomcat 启动的衔接点、`finishBeanFactoryInitialization` 与 Bean 生命周期呼应
- [ ] Spring：@Autowired 注入原理 — 0 覆盖。`AutowiredAnnotationBeanPostProcessor` 时机、byType/byName 与 @Resource 区别、字段注入为什么不推荐
- [ ] Java基础：SPI 源码 — `ServiceLoader` 懒加载迭代器源码、与双亲委派破坏的衔接（JVM 篇已有）、Dubbo SPI 为什么重写（按需加载/IoC/AOP）
- [ ] Redis：Cluster 深挖 — MOVED/ASK 仅在追问地图提及。gossip 协议、槽迁移完整流程、为什么是 16384 槽（CRC16 + 心跳包大小）
- [ ] 消息队列：Kafka 日志存储 — 0 覆盖。分段（segment）、稀疏索引 .index/.timeindex、按 offset 二分查找流程，可与 MySQL B+树对比"为什么 MQ 不需要稠密索引"
- [ ] Redis：pipeline vs mget vs Lua — 0 覆盖。往返次数、原子性差异、pipeline 太长的风险
- [ ] 分布式系统：一致性哈希虚拟节点 — 概念散在各篇但虚拟节点 0 覆盖。数据倾斜问题、TreeMap 实现示例代码、与哈希槽（Redis Cluster）对比
- [ ] 系统设计：限流算法实现 — 算法名已有但实现 0。滑动窗口计数代码、Redis+Lua 分布式限流脚本、令牌桶 Guava RateLimiter 预热
- [ ] 微服务：链路追踪原理 — traceId/spanId 仅零星提及。生成与跨进程传播（HTTP header/MQ）、与 MDC 日志串联、采样率取舍
- [ ] 网络：HTTPS 证书校验细节 — 证书链 0 覆盖。信任链逐级验签、为什么中间人拿不到有效证书、自签名/双向 TLS（mTLS）场景
- [ ] 网络：服务端推送方案选型 — SSE 0 覆盖。长轮询 vs SSE vs WebSocket 对比表、各自适用场景与网关/LB 配合的坑
- [ ] 操作系统：page cache 与 OOM killer — 0 覆盖。脏页回写参数、direct IO 适用场景、cgroup 内存限制与容器里 JVM 被杀的关系（衔接 Linux与工程化的容器内存）

### 低优先级 / 待评估

- [ ] 集合框架：HashMap resize 源码检查 — 已有 8 处 resize/putVal 命中，先评估现有深度是否够，不够再逐行补
- [ ] MongoDB：是否值得新增专题（部分公司考，非主流必考，需要用户确认）
- [ ] Elasticsearch：段合并与 forcemerge 对写入性能的影响（现有 12 节已较全，补充性）
- [ ] 并发编程：ForkJoinPool 工作窃取 — 工作窃取 0 覆盖。双端队列偷任务、`CompletableFuture` 默认用 commonPool 的坑（IO 任务饿死）
- [ ] 集合框架：LinkedHashMap 实现 LRU — 0 覆盖。accessOrder、`removeEldestEntry` 钩子、手写 LRU 题衔接（算法题已有则互链）
- [ ] Elasticsearch：选主机制 — 0 覆盖。7.x 前 minimum_master_nodes 脑裂参数、7.x 后基于 quorum 的选举演变
- [ ] 系统设计：Seata AT 模式深挖 — 仅一处提及。undo log 反向补偿、全局锁与脏写、AT vs TCC 的侵入性对比
- [ ] 安全认证：水平/垂直越权 — 越权仅一笔带过。资源归属校验的通用做法、IDOR 案例
- [ ] 锁升级细节评估 — 偏向锁已有命中，确认是否覆盖"JDK 15 废弃偏向锁及原因"，缺则补

### 站点侧（非内容）

- [ ] 暗色模式手动切换按钮（当前仅跟随系统）
- [ ] jsdelivr 国内访问慢的备选：换 fastly 镜像或资源本地化进仓库
- [ ] vue.css 首行 Google Fonts `@import` 国内阻塞：本地化 vue.css 并删除该行

## 已完成

- [x] Netty与RPC：内存池 — PooledByteBufAllocator 分级(Arena/Chunk/Subpage)、伙伴算法满二叉树、线程绑定 Arena 减少竞争（待 commit）
- [x] Java基础：泛型擦除 — 擦除到上界、桥接方法源码(javap)、为什么不能 new T[]、PECS 原则（待 commit）
- [x] 系统设计：Feed 流 — 推/拉/推拉结合三方案对比、大 V 阈值、收件箱只存 ID（待 commit）
- [x] 系统设计：库存扣减防超卖 — DB 乐观锁/Redis Lua 预扣减/分段库存三方案、超卖 vs 少卖优先级（待 commit）
- [x] 工程实践：数据库连接池原理 — HikariCP ConcurrentBag/FastList/字节码代理、连接数公式、maxLifetime 与 wait_timeout 陷阱（待 commit，写入 MySQL.md）
- [x] 消息队列：RocketMQ 存储模型 — CommitLog/ConsumeQueue/IndexFile、mmap、刷盘策略、与 Kafka 分区存储对比（待 commit）
- [x] MySQL：JOIN 算法 — NLJ/BNL/8.0 Hash Join、小表驱动大表的真实含义（待 commit）
- [x] 并发编程：线程池源码 — execute 三步提交/ctl 位运算(状态+线程数压一个 int)/Worker 继承 AQS 实现不可重入锁（待 commit）
- [x] 并发编程：AQS 源码级拆解 — state/CLH 变体队列/acquire-release 主干源码/独占-共享区别/Condition 队列转移（待 commit）
- [x] JVM：三色标记/G1 原理/四种引用/安全点/OOM 盘点（dd617d0）
- [x] 消息队列：Kafka ISR-HW-LEO/rebalance/exactly-once/读写分离与分区数（dd617d0）
- [x] 网络：TCP 重传/流量 vs 拥塞控制/keepalive；数据结构：时间轮（dd617d0）
- [x] 重复考点整合互链 10 处：CAP、分布式 ID、熔断、会话/攻击、跳表、死锁、零拷贝（dd617d0）
- [x] MySQL：Buffer Pool 改进 LRU/当前读 vs 快照读/大表 Online DDL 与 gh-ost（d0e552a）
- [x] 分布式系统：Redisson 看门狗/RedLock 争议（d0e552a）
- [x] Redis：PSYNC 源码判定/哨兵 SDOWN-ODOWN 选主/主从延迟丢失窗口（d0e552a）
- [x] Spring：事务传播七种补全/REQUIRES_NEW vs NESTED 场景/getTransaction 源码链（d0e552a）

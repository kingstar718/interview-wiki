# 内容待办 — interview-wiki

> 规则：动手前先 grep 验证考点是否已覆盖；完成一项就移到「已完成」并附 commit 短哈希。
> 写法模板与同步清单见 [CONTRIBUTING.md](./CONTRIBUTING.md)。发现新缺口先登记到这里再做。

## 待办

### 高优先级（★★★★★/★★★★ 高频且明显缺失）

存量整改（规范见 CONTRIBUTING.md「存量内容整改规范」，2026-07-09 登记）：

- [x] 整改:JVM.md 试点 — 拆 docsify 外壳/删自引用"详见 JVM 专题文档"/补运行时内存区、GC 三算法、类加载五阶段三节（e894fdb）
- [x] 整改:Java基础.md 事实修正 — String byte[]、Valhalla 表述、fastjson、JDK9+ 无独立 JRE、BigDecimal 追问（e894fdb）
- [x] 整改:Java现代特性.md 事实更新 — pinning 补 JDK 24 JEP 491、Scoped Values 补 JDK 25 定稿状态（e894fdb）
- [x] 整改:Java基础.md 结构 — 六个主题章重组、拆三件套外壳、升级 BIO/NIO/AIO 与异常处理、序列化补 serialVersionUID/安全追问（e894fdb）
- [x] 整改:集合框架.md 结构 — 五主题章重组、新增 CopyOnWriteArrayList/TreeMap/HashMap 并发问题三节、fail-fast 升级（3831ed7）
- [x] 整改:并发编程.md 结构 — 六主题章重组、拆外壳、锁升级与公平锁去重改互链、CF/虚拟线程补跨篇指路（3831ed7）
- [x] JVM:调优方法论小节 — 目标/证据链三步、GC 日志三类信息、症状→方向速查表、"为什么不能只调参数"（e894fdb）
- [x] 整改:Spring.md 结构（框架分类首批试点）— 移出"高频面试题"外壳内的生命周期/事务失效两题归位；补追问地图承诺的"自调用为何失效"小节；合并事务失效 7 场景与最佳实践为归因表；修正 Bean 生命周期"10 步"计数错误（实为 11 步）并拆清 @PostConstruct 与 afterPropertiesSet 的顺序；删除文末孤立"最佳实践"章（4e84f29）
- [x] 整改:框架分类扫描结论 — MyBatis/SpringBoot/Netty与RPC/SpringCloud微服务 四篇已核查，结构良好、无 docsify 外壳残留，暂不需要整改；SpringCloud微服务.md 顺手给 Hystrix 标注维护模式现状（4e84f29）
- [x] 整改:数据库/中间件分类扫描 — MySQL/Redis/Elasticsearch/MongoDB/ZooKeeper与注册中心/消息队列 六篇结构层面均已在此前批次整改完毕，无 docsify 外壳残留；本批聚焦事实核查与地图缺口：
  MySQL 补"为什么不用红黑树/跳表"追问；Redis 补追问地图承诺却全篇缺失的"分布式锁"专节（高频题目索引早已声称覆盖但实际是空头承诺）；
  MongoDB 修正 WiredTiger Cache 大小公式 min→max 的计算错误、补索引存储层归属追问；ZooKeeper与注册中心 补 Nacos/ZK/Eureka 一致性模型(CP/AP)对比并标注 Eureka 2.0 已停止开发；
  Elasticsearch/消息队列两篇核查无遗留问题，未改动（4e84f29）
- [x] 整改:计算机基础/分布式与架构/工程实践/面试分类扫描（十五篇，存量整改规范全站铺完）—
  结构层面全部良好、无 docsify 外壳残留，本批聚焦重复考点收编与事实核查：
  网络.md 合并"TCP 粘包解决"与"TCP 粘包/拆包"两个近乎重复的小节为一节；
  系统设计.md 收编三处与其他篇目重复而更浅的内容(可重入分布式锁→链接分布式系统.md Redisson 看门狗、
  API 超时排查→链接生产排障.md、RPC 框架设计→链接 Netty与RPC.md)，改为一句话+链接；
  操作系统/数据结构与算法/分布式系统/安全认证/生产排障/API设计与接口治理/Linux与工程化/Web容器与Nginx/
  构建与依赖管理/测试与代码质量/面试三篇 核查后确认内容扎实、无遗留问题，未改动（5e81afd）

### 中优先级

- [ ] 算法:techniques 细粒度标签(方案见 [RFC-算法题标签方案.md](./RFC-算法题标签方案.md),A~H 已拍板,词表 35 条)
  - [x] P1 13 个套路页写入 techniques 白名单 + 补 `## 已解题目` H2;gen_topics.py 支持分组与迁移期平铺回退
  - [x] P2 试点回填 双指针与滑动窗口(11)+ 栈与队列(12),去重 21 篇;暴露并修正三条词表问题(见 RFC 3.2.4)
  - [x] P3 6 个 subagent 并行回填 139 篇 techniques;校验 S 上线(严格模式);校验 T 废除改审计输出;词表 35→44(位运算补 2 词、新增 9 词、2 处改名);13 个套路页全部切换分组显示
  - [x] P4 剑指四篇纳入校验(SOLUTION_RE/ALGO_H1_RE 扩展 + 补足 offer14/40/45 缺失小节 + 修 J 项把「剑指 Offer 40.」误判为引用 LC40);outline.py --tech 按技术词检索;同步 CLAUDE/CONTRIBUTING
  - [x] 内容:720 词典中最长的单词 —— 补 Trie+DFS 主解法与五个空小节,techniques 改为 [Trie前缀树, 哈希查表]
- [ ] 算法:19 篇题解有空小节(校验 L 只查小节名与顺序,不查内容,九节结构给了虚假的安全感)
  边界条件/变式/易错点/面试追问 各空 19 处,复杂度 空 10 处。清单:1094 / 1109 / 1206 / 211 / 212 / 260 / 303 / 304 / 338 / 371 / 380 / 381 / 432 / 460 / 461 / 648 / 677 / 802 / 919
  - [ ] 逐篇补齐内容(可分批,按套路走)
  - [ ] 补齐后给 check_index.py 加校验:题解九节不得为空(现在加会直接红)
  - [ ] P4 修剑指三篇绕过校验 L 的漏洞(SOLUTION_RE 不匹配 `offerNN-`,补足缺失小节);outline.py --tech;同步 CLAUDE/CONTRIBUTING
- [ ] 结构:概念/ 收口为八股概念层(分域原则已写入 CLAUDE.md)
  - [ ] 校验 P 的内容域只按 interview 分类计,algorithms 不计域
  - [ ] 拆 概念/树.md:「二叉搜索树」节移进 algorithms/二叉树.md,树.md 只留介质定价与索引结构选型
- [x] 算法:题单 21 专题全量补齐(约 160 篇), 同步 README 与索引链接(3cb81d2)

### 低优先级 / 待评估

- [x] 集合框架：HashMap resize 源码检查 — 已有 putVal/resize 源码逐行解析，深度足够 ✅
- [x] 系统设计：Seata AT 模式深挖 — 已补充 undo log/全局锁/脏写/AT vs TCC 对比
- [x] 集合框架：LinkedHashMap 实现 LRU — 已补充 accessOrder/removeEldestEntry 源码 + 算法题互链
- [x] 并发编程：ForkJoinPool 工作窃取 — 已补充原理/代码示例/commonPool 坑
- [x] 安全认证：水平/垂直越权 — 已补充 IDOR 定义/防御方案/注解示例
- [x] Elasticsearch：选主机制 — 已补充 7.x 前后选主流程/脑裂关联
- [x] Elasticsearch：段合并与 forcemerge — 已补充合并过程/对写入的影响
- [x] 锁升级细节评估 — 偏向锁已有"JDK 15 起已默认禁用"覆盖 ✅
- [ ] MongoDB：是否值得新增专题（部分公司考，非主流必考，需要用户确认）

### 站点侧（非内容）

- [ ] 暗色模式手动切换按钮（当前仅跟随系统）
- [ ] jsdelivr 国内访问慢的备选：换 fastly 镜像或资源本地化进仓库
- [ ] vue.css 首行 Google Fonts `@import` 国内阻塞：本地化 vue.css 并删除该行

## 已完成

- [x] 索引:知识点索引自动化 — 新增 scripts/slug.py(纯标准库复刻 github-slugger v2)+ gen_index.py(扫 interview/ 篇目真实 H3,按 quartz.ts ORDER 生成「真实标题+锚点」索引,点击直达小节,带 --check 防漂移);check_index.py 加 M 项锚点死链校验(归一化匹配);重写 content/indexes/知识点索引.md,同步 CLAUDE/CONTRIBUTING 文档(546495a)
- [x] 算法:关联题回填三批全量(链表 13 + 数组 19 + 栈与队列 8)— 每篇按同套路/进阶/易混/知识点四类补齐,与 interview 篇目互链(caaa180)
- [x] 算法:check_index.py 校验 L「关联题」转必填(九节固定)(caaa180)

- [x] 算法:滑动窗口 5 篇全量补齐(3/76/239/438/567), 同步 README 与索引链接(2abd4ee)
- [x] 算法:二分查找 7 篇全量补齐(4/33/34/69/74/153/剑指53), 同步 README 与索引链接(2abd4ee)
- [x] 算法:二叉树 12 篇全量补齐(98/102/104/105/113/124/199/226/230/236/297/543), 同步 README 与索引链接(b409cec)
- [x] 算法:图 8 篇全量补齐(133/200/207/210/417/787/797/994), 同步 README 与索引链接(c6b098d)
- [x] 算法:回溯 13 篇全量补齐(17/22/37/39/40/46/47/51/78/79/90/93/131), 同步 README 与索引链接(2aed504)
- [x] 算法:动态规划 22 篇全量补齐(5/10/32/62/63/64/70/72/121/122/139/152/188/198/213/300/309/312/322/416/714/1143), 同步 README 与索引链接(bfe9e59)
- [x] 算法:贪心 8 篇全量补齐(45/55/53/134/406/435/621/763), 同步 README 与索引链接(d37df72)
- [x] 算法:排序 6 篇全量补齐(56/252/253/912/剑指45), 同步 README 与索引链接(225df9d)
- [x] 算法:堆 6 篇全量补齐(215/295/347/703/剑指40), 同步 README 与索引链接(9166c24)
- [x] 算法:单调栈 6 篇全量补齐(496/503), 同步 README 与索引链接(8c9c74b)
- [x] 算法:并查集 5 篇全量补齐(547/684/990/1319), 同步 README 与索引链接(b11f574)
- [x] 算法:17~21 专题全部补齐(23新篇), 算法题解 21 专题全量完成(3cb81d2)

- [x] 并发编程：ThreadLocal 源码补深 — ThreadLocalMap 开放寻址、魔数 0x61c88647 斐波那契散列、expungeStaleEntry 顺手清理、TTL 传递（9886056）
- [x] 系统设计：限流算法实现 — 固定窗口临界问题、滑动窗口环形分桶代码、Redis+Lua ZSET 脚本、RateLimiter 预热（9886056）
- [x] 微服务：链路追踪原理 — traceId/spanId 生成与 HTTP/MQ 传播、MDC 串联、头部 vs 尾部采样（9886056）
- [x] 网络：HTTPS 证书校验 — 证书链逐级验签、中间人三条路逐条堵死、OCSP Stapling、mTLS 场景（9886056）
- [x] 网络：服务端推送选型 — 长轮询/SSE/WebSocket 对比表、SSE 内建续传、网关缓冲与超时的坑（9886056）
- [x] 操作系统：page cache 与 OOM killer — 脏页回写参数、direct IO 与双重缓存、cgroup OOM 与容器 137 排查（9886056）

- [x] 分布式系统：一致性哈希虚拟节点 — 哈希环、虚拟节点治倾斜与级联压垮、TreeMap 实现、与哈希槽取舍对比（e6f4fab）
- [x] 消息队列：Kafka 日志存储 — segment 三件套、稀疏索引三步查找、与 MySQL B+树"索引密度匹配访问模式"对比（e6f4fab）
- [x] Redis：Cluster 深挖 — gossip 扩散/PFAIL→FAIL、16384 槽两笔账、槽迁移五步与 MOVED vs ASK 对比（e6f4fab）
- [x] Java基础：SPI 源码 — ServiceLoader LazyIterator 懒加载、上下文类加载器破坏双亲委派衔接、Dubbo ExtensionLoader 对比（e6f4fab）
- [x] 操作系统：页表/TLB — 四级页表按需分配省内存、TLB miss 五次访存代价、HugePage/THP 对比与 Redis 关 THP 互链（e6f4fab）
- [x] Redis：缓存预热 — 冷启动=全量雪崩、定时任务/双缓存/灰度放量对比、预热刷满内存追问（bd82b57）
- [x] Redis：pipeline vs mget vs Lua — RTT/原子性/Cluster 跨槽三维对比、pipeline 过长风险、批处理摊薄固定开销通用概念（bd82b57）
- [x] Spring：容器启动 refresh() 源码 — 十二步骨架注释版、onRefresh 与内嵌 Tomcat、BFPP/BPP/getBean 三锚点（bd82b57）
- [x] Spring：@Autowired 注入原理 — AutowiredAnnotationBPP 两段式、@Resource 对比表、字段注入四宗罪（bd82b57）
- [x] 网络：TLS 1.3 握手 — 1-RTT 三个配套设计、0-RTT 重放风险、与 QUIC 建连衔接（bd82b57）
- [x] Netty与RPC：内存池 — PooledByteBufAllocator 分级(Arena/Chunk/Subpage)、伙伴算法满二叉树、线程绑定 Arena 减少竞争（2eef022）
- [x] Java基础：泛型擦除 — 擦除到上界、桥接方法源码(javap)、为什么不能 new T[]、PECS 原则（2eef022）
- [x] 系统设计：Feed 流 — 推/拉/推拉结合三方案对比、大 V 阈值、收件箱只存 ID（2eef022）
- [x] 系统设计：库存扣减防超卖 — DB 乐观锁/Redis Lua 预扣减/分段库存三方案、超卖 vs 少卖优先级（2eef022）
- [x] 工程实践：数据库连接池原理 — HikariCP ConcurrentBag/FastList/字节码代理、连接数公式、maxLifetime 与 wait_timeout 陷阱（2eef022，写入 MySQL.md）
- [x] 消息队列：RocketMQ 存储模型 — CommitLog/ConsumeQueue/IndexFile、mmap、刷盘策略、与 Kafka 分区存储对比（2eef022）
- [x] MySQL：JOIN 算法 — NLJ/BNL/8.0 Hash Join、小表驱动大表的真实含义（2eef022）
- [x] 并发编程：线程池源码 — execute 三步提交/ctl 位运算(状态+线程数压一个 int)/Worker 继承 AQS 实现不可重入锁（2eef022）
- [x] 并发编程：AQS 源码级拆解 — state/CLH 变体队列/acquire-release 主干源码/独占-共享区别/Condition 队列转移（2eef022）
- [x] JVM：三色标记/G1 原理/四种引用/安全点/OOM 盘点（dd617d0）
- [x] 消息队列：Kafka ISR-HW-LEO/rebalance/exactly-once/读写分离与分区数（dd617d0）
- [x] 网络：TCP 重传/流量 vs 拥塞控制/keepalive；数据结构：时间轮（dd617d0）
- [x] 重复考点整合互链 10 处：CAP、分布式 ID、熔断、会话/攻击、跳表、死锁、零拷贝（dd617d0）
- [x] MySQL：Buffer Pool 改进 LRU/当前读 vs 快照读/大表 Online DDL 与 gh-ost（d0e552a）
- [x] 分布式系统：Redisson 看门狗/RedLock 争议（d0e552a）
- [x] Redis：PSYNC 源码判定/哨兵 SDOWN-ODOWN 选主/主从延迟丢失窗口（d0e552a）
- [x] Spring：事务传播七种补全/REQUIRES_NEW vs NESTED 场景/getTransaction 源码链（d0e552a）

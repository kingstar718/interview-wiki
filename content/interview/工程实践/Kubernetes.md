# Kubernetes

## 面试追问地图

| 主问题 | 必讲关键点 | 下一层追问 |
|--------|------------|------------|
| [集群架构](#kubernetes-控制面和节点组件如何协作) | API Server、etcd、Scheduler、Controller、kubelet | 声明式与调谐循环 |
| [工作负载](#deploymentstatefulsetdaemonsetjob-如何选择) | 无状态、有状态、每节点、批任务 | Headless Service、滚动更新 |
| [Pod 生命周期](#pod-从创建到运行经历什么) | 调度、拉镜像、容器启动、探针、终止 | Pending/CrashLoopBackOff 排查 |
| [探针](#startupreadinessliveness-探针如何配合) | 启动、流量、存活三种语义 | 重启风暴、优雅摘流量 |
| [调度](#scheduler-如何为-pod-选择节点) | 过滤、打分、亲和性、污点容忍 | 拓扑分散、优先级抢占 |
| [资源](#requestslimits-和-qos-如何影响调度与驱逐) | 调度依据、cgroup 限制、QoS | CPU throttling、OOMKilled |
| [网络](#serviceingress-和-gateway-api-分别解决什么) | ClusterIP、服务发现、七层入口 | kube-proxy、EndpointSlice、NetworkPolicy |
| [存储](#pvpvcstorageclass-如何解耦存储) | 供给、声明、动态制备、访问模式 | StatefulSet、回收策略 |
| [弹性](#hpa-如何决定扩缩容) | 指标、期望副本数、稳定窗口 | 冷启动、突发流量、VPA 冲突 |
| [发布](#如何做到-kubernetes-应用无损发布) | readiness、preStop、优雅停机、PDB | 滚动参数、数据库兼容 |
| [排障](#pod-异常时如何建立排障顺序) | Events、状态、日志、资源、网络 | Pending、OOMKilled、流量不通 |

容器镜像、JVM 容器内存和 CI/CD 入门见[Linux与工程化](Linux与工程化.md)；服务网格见[系统设计](系统设计.md)。

---

## 一、架构与工作负载

### Kubernetes 控制面和节点组件如何协作？

频次 ★★★★★ · 难度 🔴 · 高频：字节/阿里

| 组件 | 核心职责 |
|------|----------|
| kube-apiserver | 所有状态读写入口，负责认证、鉴权、准入和 API |
| etcd | 保存集群期望状态和元数据 |
| kube-scheduler | 为未绑定节点的 Pod 选择 Node |
| kube-controller-manager | 运行 Deployment、Node 等控制器的调谐循环 |
| kubelet | 让本节点 Pod 的实际状态逼近期望状态 |
| 容器运行时 | 通过 CRI 创建和管理容器 |
| kube-proxy/网络实现 | 实现 Service 转发；部分 eBPF 方案可替代 kube-proxy 数据面 |

创建 Deployment 不是 API Server 直接启动容器：API Server 持久化期望状态，控制器创建 ReplicaSet 和 Pod，Scheduler 绑定节点，目标节点 kubelet 再调用运行时启动容器。每个控制器持续比较 `spec` 与实际状态并调谐，这就是声明式系统的核心。

**常见追问**：etcd 为什么重要？→ 它保存控制面状态；业务容器数据通常不在 etcd。etcd 故障会影响控制面变更，但已运行 Pod 不会因此立即停止。

### Deployment、StatefulSet、DaemonSet、Job 如何选择？

频次 ★★★★★ · 难度 🟡 · 高频：全厂

| 工作负载 | 适用场景 | 关键能力 |
|----------|----------|----------|
| Deployment | 无状态 Web/API | 副本、滚动更新、回滚 |
| StatefulSet | 数据库、注册中心等有状态服务 | 稳定序号、网络身份、持久卷绑定、顺序操作 |
| DaemonSet | 日志/监控/网络 Agent | 每个匹配节点运行一个 Pod |
| Job | 有完成条件的一次性任务 | 失败重试、完成计数 |
| CronJob | 周期性创建 Job | Cron 调度、并发策略、历史保留 |

StatefulSet 只提供稳定身份和编排顺序，不自动让数据库具备复制、一致性和备份能力。把 MySQL 放进 StatefulSet 不等于获得高可用。

### Pod 从创建到运行经历什么？

1. API Server 校验并保存 Pod。
2. Scheduler 选择节点并写入绑定结果。
3. 节点 kubelet 观察到 Pod，准备 Volume 和网络。
4. 运行时拉取镜像，依次运行 init container，再启动应用容器。
5. startup/readiness/liveness 探针开始工作。
6. Pod 删除时进入 Terminating，执行 `preStop`，发送终止信号并等待宽限期。

Pod 是可替换对象，不应把本地文件系统当作可靠持久化。容器重启发生在原 Pod 内；Pod 被重建后 UID 和 IP 都可能变化。

---

## 二、健康检查与资源

### startup、readiness、liveness 探针如何配合？

频次 ★★★★★ · 难度 🟡 · 高频：全厂

| 探针 | 回答的问题 | 失败动作 |
|------|------------|----------|
| startup | 应用是否已经完成启动 | 启动成功前不运行另外两类探针，持续失败则重启 |
| readiness | 现在是否适合接流量 | 从 Service 后端移除，不重启 |
| liveness | 进程是否陷入无法自愈状态 | 重启容器 |

下游数据库暂时不可用通常只应影响 readiness，不能直接让 liveness 失败；否则所有实例会同时重启，进一步放大故障。探针接口应轻量，不做深度全链路调用。

**常见追问**：只配置 liveness 有什么问题？→ 新实例可能尚未预热就接流量；发布摘流量也没有 readiness 状态控制。

### requests、limits 和 QoS 如何影响调度与驱逐？

频次 ★★★★★ · 难度 🔴 · 高频：字节/美团

- `requests`：调度器预留和选择节点的依据，也是 CPU 份额竞争的重要基础。
- `limits`：容器运行上限；CPU 超限通常被节流，内存超限可能触发 cgroup OOM kill。
- `Guaranteed`：每个容器 CPU/内存 request 等于 limit，节点压力下最不易被驱逐。
- `Burstable`：至少配置了某项 request/limit，但不满足 Guaranteed。
- `BestEffort`：未配置 request/limit，最先被驱逐。

JVM 的 `-Xmx` 不能等于容器 memory limit，还要给 Metaspace、线程栈、直接内存和 native 库留空间。退出码 137 和 OOMKilled 的排查见[操作系统](操作系统.md)。

### CPU limit 为什么会让延迟抖动？

CPU 是可压缩资源，超过 limit 后进程被 CFS/cgroup 节流而非杀死。平均 CPU 看似不高，短时间突发仍可能耗完配额并等待下个周期，表现为 P99 延迟尖刺。排查应同时看 CPU 使用率与 throttled seconds/periods，不能只看平均值。

---

## 三、调度与弹性

### Scheduler 如何为 Pod 选择节点？

频次 ★★★★ · 难度 🟡

调度分为过滤和打分：先排除资源不足、污点不容忍、亲和性不满足的节点，再按资源、亲和性和拓扑等策略打分，选出目标节点。

- nodeSelector/NodeAffinity：把 Pod 放到具有指定标签的节点。
- PodAffinity/AntiAffinity：按其他 Pod 的位置聚合或打散。
- taint/toleration：节点拒绝普通 Pod，只有容忍该污点的 Pod 才有资格调度；容忍不代表一定调度过去。
- topology spread constraints：把副本按可用区/节点均匀分散，降低单故障域风险。

硬反亲和规则过强会让扩容 Pod 长期 Pending。生产中通常优先用拓扑分散和软偏好，再为真正隔离需求使用硬约束。

### HPA 如何决定扩缩容？

频次 ★★★★ · 难度 🟡

HPA 周期读取资源或自定义指标，按“当前指标 / 目标指标”估算期望副本数，并通过容忍区间和稳定窗口降低抖动。CPU 利用率型指标以 request 为基准，request 未配置时可能无法得到有效利用率。

扩容不是即时容量：新 Pod 还要调度、拉镜像、启动和预热。突发流量需要预留容量、快速指标和限流兜底；只依赖 HPA 会在冷启动窗口内过载。HPA 调副本数，Cluster Autoscaler/节点自动扩缩调节点容量，两者解决不同层次。

---

## 四、网络与配置

### Service、Ingress 和 Gateway API 分别解决什么？

频次 ★★★★★ · 难度 🟡 · 高频：全厂

| 对象 | 作用 |
|------|------|
| Service | 为一组动态 Pod 提供稳定虚拟 IP/DNS 和四层访问入口 |
| Ingress | 用规则描述 HTTP/HTTPS 外部入口，需 Ingress Controller 实现 |
| Gateway API | 用 Gateway、HTTPRoute 等角色化资源表达更丰富的流量治理 |

Service 通过 label selector 关联 Pod，并由 EndpointSlice 保存实际后端。请求不通时应沿链路检查：DNS → Service → EndpointSlice → Pod IP/端口 → readiness → NetworkPolicy，而不是先重启 Pod。

### ClusterIP、NodePort、LoadBalancer、Headless Service 如何选择？

- ClusterIP：集群内部访问的默认类型。
- NodePort：每个节点开放端口，常作为外部负载均衡的底层能力，不宜直接作为复杂生产入口。
- LoadBalancer：请求云平台或实现提供外部负载均衡器。
- Headless(`clusterIP: None`)：不提供虚拟 IP，DNS 直接返回 Pod 地址，常与 StatefulSet 做节点发现。

### ConfigMap 和 Secret 有什么边界？

ConfigMap 保存非敏感配置，Secret 表达敏感数据，但默认 base64 只是编码，不是加密。生产应结合 etcd 静态加密、RBAC 最小权限、外部密钥系统和轮换策略。环境变量注入后不会自动刷新；Volume 投影可更新文件，但应用仍要支持重载。

---

## 五、存储与发布

### PV、PVC、StorageClass 如何解耦存储？

频次 ★★★★ · 难度 🟡

- PV：集群中的一块存储资源。
- PVC：工作负载对容量、访问模式和 StorageClass 的声明。
- StorageClass：描述动态制备器和参数，PVC 可据此自动创建 PV。
- CSI：存储插件标准接口，把具体存储实现与 Kubernetes 解耦。

删除 PVC 后数据是否保留取决于 PV 回收策略。数据库扩容、快照和跨可用区恢复必须按具体存储能力设计，不能只看 PVC 处于 Bound。

### 如何做到 Kubernetes 应用无损发布？

频次 ★★★★★ · 难度 🔴 · 高频：全厂

1. 新 Pod 完成 startup 和 readiness 后才进入后端。
2. 旧 Pod 终止时先变为 NotReady，并给 EndpointSlice/负载均衡传播留时间。
3. `preStop` 触发应用摘流量或短暂等待，进程收到 SIGTERM 后停止接新请求。
4. 在 `terminationGracePeriodSeconds` 内等待在途请求、MQ 消费和线程池结束。
5. 超时后 kubelet 发送 SIGKILL，因此宽限期必须覆盖真实最长收尾时间。
6. 用 PDB 限制自愿中断时同时不可用的副本数，并设置合理滚动更新参数。

PDB 不能阻止节点宕机，也不保证应用健康；它只约束驱逐等自愿中断。数据库变更还需遵循“先兼容旧代码、再发布新代码、最后清理旧结构”的扩展/收缩模式。

---

## 六、排障

### Pod 异常时如何建立排障顺序？

频次 ★★★★★ · 难度 🔴 · 高频：全厂

1. `kubectl get pod -o wide`：看阶段、重启次数、节点和 IP。
2. `kubectl describe pod`：看 Conditions 和 Events，判断调度、拉镜像、挂载、探针问题。
3. `kubectl logs --previous`：容器重启时查看上一次日志。
4. 核对 Deployment/Pod spec、镜像、环境变量、Volume 和 Service EndpointSlice。
5. 查节点资源、驱逐、CNI/CSI 和控制面事件。

| 现象 | 优先方向 |
|------|----------|
| Pending | 资源不足、污点、亲和性、PVC 未绑定 |
| ImagePullBackOff | 镜像名、凭证、仓库网络、拉取策略 |
| CrashLoopBackOff | 上一次日志、启动命令、配置、探针、退避重启 |
| OOMKilled/137 | limit、JVM 总内存、泄漏、节点压力 |
| Running 但无流量 | readiness、Service selector、EndpointSlice、端口、NetworkPolicy |
| Terminating 卡住 | finalizer、Volume 卸载、preStop 和进程不响应 SIGTERM |

`CrashLoopBackOff` 是重启退避状态，不是根因；根因通常在容器退出码、上一次日志或 Events 中。

---

## 参考资料

- [Kubernetes Components](https://kubernetes.io/docs/concepts/overview/components/)
- [Kubernetes Workloads](https://kubernetes.io/docs/concepts/workloads/)
- [Services, Load Balancing, and Networking](https://kubernetes.io/docs/concepts/services-networking/)
- [Storage](https://kubernetes.io/docs/concepts/storage/)

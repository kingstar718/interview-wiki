import { loadQuartzConfig, loadQuartzLayout } from "./quartz/plugins/loader/config-loader"
import { Explorer } from "./.quartz/plugins"

// Explorer 排序表:YAML 放不下函数,在此覆盖(quartz.config.yaml 里的 explorer 注释指向这里)。
// 注意:sortFn/mapFn 会被 toString() 序列化到页面、在浏览器端 eval,
// 因此必须自包含 —— 排序表/改名表只能写在函数体内,不能引用外部变量。
Explorer({
  // 顶层目录显示中文名(仅影响侧栏,不改真实路径/面包屑)
  mapFn: (node) => {
    const NAMES: Record<string, string> = {
      indexes: "索引",
      interview: "社招八股",
      algorithms: "算法刷题",
    }
    if (node.isFolder && NAMES[node.displayName]) {
      node.displayName = NAMES[node.displayName]
    }
    return node
  },
  // 学习顺序排序表:命中表内名称按表序,未命中回退「目录在前 + 数字感知排序」
  // (算法专题 01~21 与题解题号天然有序)。新增篇目在此登记位置。
  sortFn: (a, b) => {
    const ORDER = [
      // 顶层(mapFn 改名后的显示名;「概念」目录本就是中文,不经 mapFn)
      "索引",
      "概念",
      "社招八股",
      "算法刷题",
      // 概念/(跨域知识节点,显示名取页面 H1;新增概念在此登记)
      "摊还",
      "局部性",
      "树",
      "写时复制",
      "间接层",
      "上下文传递",
      "背压",
      "幂等",
      "多数派",
      // indexes/(显示名取页面 H1,知识点索引的 H1 是「社招面试问题知识点」)
      "社招面试问题知识点",
      "算法题索引",
      "高频题目索引",
      // interview/ 分类(学习顺序)
      "Java",
      "框架",
      "数据库",
      "中间件",
      "计算机基础",
      "分布式与架构",
      "工程实践",
      "面试",
      // Java/
      "Java基础",
      "集合框架",
      "并发编程",
      "JVM",
      "Java现代特性",
      // 框架/
      "Spring",
      "SpringBoot",
      "SpringCloud微服务",
      "MyBatis",
      "Netty与RPC",
      // 数据库/
      "MySQL",
      "Redis",
      // 中间件/
      "消息队列",
      "Elasticsearch",
      "MongoDB",
      "ZooKeeper与注册中心",
      // 计算机基础/
      "网络",
      "操作系统",
      "数据结构与算法",
      // 分布式与架构/
      "分布式系统",
      "系统设计",
      "安全认证",
      "生产排障",
      // 工程实践/
      "Linux与工程化",
      "Web容器与Nginx",
      "构建与依赖管理",
      "测试与代码质量",
      "API设计与接口治理",
      // 面试/
      "面试问题深挖指南",
      "项目经历与场景题",
      "领域建模与代码设计",
      // algorithms/ 专题目录按编号回退排序,无需在此登记
    ]
    const ra = ORDER.indexOf(a.displayName)
    const rb = ORDER.indexOf(b.displayName)
    if (ra !== -1 || rb !== -1) {
      if (ra === -1) return 1
      if (rb === -1) return -1
      return ra - rb
    }
    if (a.isFolder !== b.isFolder) {
      return a.isFolder ? -1 : 1
    }
    return (a.displayName || "").localeCompare(b.displayName || "", "zh-CN", {
      numeric: true,
      sensitivity: "base",
    })
  },
})

const config = await loadQuartzConfig()
export default config
export const layout = await loadQuartzLayout()

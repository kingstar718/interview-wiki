# Interview Wiki · 后端工程师面大厂

Java 系后端面大厂知识库,用 [Quartz 4](https://quartz.jzhao.xyz/) 静态生成,支持 [[双链]]、反向链接与关系图谱。

## 内容

- **面试专题**(`content/interview/`):JVM / MySQL / Redis / Spring / 并发 / 操作系统 / 网络 / 分布式 / 系统设计 …
- **算法题**(`content/algorithms/`):套路节点 + `problems/` 题目池,题解含固定九节结构,归属由 `topics:`(粗套路)与 `techniques:`(细技术词)frontmatter 声明;`content/算法题索引.md`(按套路分组、技术词以行内标签标注的题目视图)由 `gen_topics.py` 生成
- **三大索引**(`content/` 根目录):知识点索引(八股总览)、算法题索引(按专题)、高频题目索引(按热度)
- **英语学习**(`content/英语学习/`):学习方案与资源、基础/技术词汇、技术读写听说、职场沟通与面试英语
- **软考系统架构师**(`content/软考系统架构师/`):软考高级备考——考试信息、重点分布、综合知识知识域、案例分析、论文写作

## 本地预览

```bash
npm ci                     # 首次,Node ≥22
npx quartz build --serve   # http://localhost:8080
```

## 部署

push `main` 后 Cloudflare Pages 自动构建发布。详见 [DEPLOY.md](DEPLOY.md)。

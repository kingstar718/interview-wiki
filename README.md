# Interview Wiki · 后端工程师面大厂

Java 系后端面大厂知识库,用 [Quartz 4](https://quartz.jzhao.xyz/) 静态生成,支持 [[双链]]、反向链接与关系图谱。

## 内容

- **面试专题**(`content/interview/`):JVM / MySQL / Redis / Spring / 并发 / 操作系统 / 网络 / 分布式 / 系统设计 …
- **算法题**(`content/algorithms/`):13 个套路节点 + `problems/` 题目池,题解含固定九节结构,归属由 `topics:`(粗套路)与 `techniques:`(细技术词)frontmatter 声明;套路页「已解题目」与 `content/算法题索引.md` 都由 `gen_topics.py` 生成
- **三大索引**(`content/` 根目录):知识点索引(八股总览)、算法题索引(按专题)、高频题目索引(按热度)

## 本地预览

```bash
npm ci                     # 首次,Node ≥22
npx quartz build --serve   # http://localhost:8080
```

## 部署

push `main` 后 GitHub Actions 自动构建发布(Pages Source = GitHub Actions)。详见 [DEPLOY.md](DEPLOY.md)。

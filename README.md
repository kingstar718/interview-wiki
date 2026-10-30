# Interview Wiki · 后端工程师面大厂

Java 系后端学习知识库，使用 [Zensical](https://zensical.org/) 将 `content/` 中的标准 Markdown 构建为静态站点。

## 内容

- **面试专题**（`content/interview/`）：JVM、MySQL、Redis、Spring、并发、操作系统、网络、分布式与系统设计等
- **算法题**（`content/algorithms/`）：套路节点与扁平题目池，索引由题解 frontmatter 自动生成
- **英语学习**（`content/英语学习/`）
- **AI 提效**（`content/AI提效/`）
- **软考系统架构师**（`content/软考系统架构师/`）

站内链接均为相对当前文件的标准 Markdown 链接，不使用双链、反向链接、悬停预览或关系图谱。

## 本地预览

```bash
python -m venv .venv
.venv\Scripts\python -m pip install -r requirements.txt
.venv\Scripts\python -m zensical serve
```

访问 <http://127.0.0.1:8000>。Linux/macOS 将 `.venv\Scripts\python` 换成 `.venv/bin/python`。

## 构建与部署

```bash
python -m zensical build
```

构建产物位于 `public/`。push `main` 后由 Cloudflare Pages 自动发布，配置见 [DEPLOY.md](DEPLOY.md)。

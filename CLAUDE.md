# CLAUDE.md — interview-wiki

本项目是「后端工程师面大厂」知识库,使用 [Docsify 4](https://docsify.js.org/) 渲染。
Docsify 是运行时渲染(CDN 加载,无构建步骤),Markdown 即内容。

## 目录结构

```text
interview-wiki/
├── index.html         # Docsify 入口(CDN + 侧栏 + 搜索,basePath: content/)
├── .nojekyll          # 让 GitHub Pages 不走 Jekyll(不忽略 _sidebar.md)
├── content/           # 笔记源码(Docsify 渲染此目录)
│   ├── index.md         # 站点首页
│   ├── _sidebar.md      # 侧栏导航
│   ├── 社招问题知识点.md  # 个人八股总枢纽
│   ├── indexes/         # 两大索引:算法题/高频题目
│   ├── interview/       # 社招八股 30 篇
│   └── algorithms/      # 算法刷题(数组已填,其余专题待补)
├── CLAUDE.md           # 本文件(Claude 项目指引)
└── DEPLOY.md           # 部署指引
```

## 常用命令

```bash
# 本地预览(任选其一,改完 md 刷新即生效,无构建步骤)
npx docsify-cli serve .     # http://localhost:3000
python -m http.server 8000  # http://localhost:8000
```

## 内容约定

- 频次:★★★★★ 必考 / ★★★★ 高频 / ★★★ 常见 / ★★ 偶考
- 难度:🟢 易 / 🟡 中 / 🔴 难
- 公司:阿里 / 腾讯 / 字节 / 美团 / 百度 / 京东 / 拼多多 / 滴滴 / 网易 / 快手
- 详解模板:
  - 算法题:思路 → 复杂度 → Java 代码 → 边界/变式 → 易错点
  - 知识点:是什么 → 为什么这么设计 → 对比同类 → 常见追问 → 代码/图示

## 链接约定

用**标准 Markdown 相对链接** `[文本](相对路径.md)`(已从 `[[双链]]` 批量转换)。

- 同目录:`[JVM](JVM.md)`
- 上级:`[社招问题知识点](../社招问题知识点.md)`
- 下级:`[算法题索引](indexes/01-算法题索引.md)`

侧栏在 `content/_sidebar.md`,链接相对 `content/`(basePath)。

## 部署

见 [DEPLOY.md](./DEPLOY.md)。GitHub Pages 直接 serve `main` 分支根,无需 Actions。

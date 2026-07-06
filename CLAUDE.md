# CLAUDE.md — interview-wiki

本项目是「后端工程师面大厂」知识库,使用 [Quartz v5](https://quartz.jzhao.xyz/) 渲染为静态站点。
Quartz 基于 Obsidian 风格 Markdown,支持 `[[双链]]`、悬停预览(popover)、关系图谱、全文搜索。

## 目录结构

```text
interview-wiki/
├── content/            # 笔记源码(Quartz 只渲染此目录)
│   ├── index.md          # 站点首页
│   ├── 社招问题知识点.md    # 个人八股总枢纽([[双链]])
│   ├── indexes/          # 两大索引:算法题/高频题目
│   ├── interview/        # 社招八股 30 篇
│   └── algorithms/       # 算法刷题(数组已填,其余专题待补)
├── quartz/             # Quartz 引擎(勿手改)
├── quartz.config.yaml          # 站点配置(pageTitle、baseUrl、插件、布局)
├── quartz.config.default.yaml  # 默认配置(参考)
├── quartz.lock.json            # 插件版本锁
├── package.json
├── CLAUDE.md           # 本文件(Claude 项目指引)
└── DEPLOY.md           # 部署指引(GitHub Pages / Cloudflare Pages / 自建)
```

## 常用命令

```bash
npm i                        # 安装依赖(需 Node ≥22,engine-strict)
npx quartz plugin install    # 从 quartz.lock.json 安装插件(构建/预览前先跑一次)
npx quartz build             # 构建到 public/
npx quartz build --serve     # 本地预览(http://localhost:8080,带 HMR)
npx quartz sync              # 提交并推送(触发 CI 部署)
```

## 内容约定

- 频次:★★★★★ 必考 / ★★★★ 高频 / ★★★ 常见 / ★★ 偶考
- 难度:🟢 易 / 🟡 中 / 🔴 难
- 公司:阿里 / 腾讯 / 字节 / 美团 / 百度 / 京东 / 拼多多 / 滴滴 / 网易 / 快手
- 详解模板:
  - 算法题:思路 → 复杂度 → Java 代码 → 边界/变式 → 易错点
  - 知识点:是什么 → 为什么这么设计 → 对比同类 → 常见追问 → 代码/图示

## 链接约定(重要)

当前用**标准 Markdown 相对链接** `[文本](path.md)`,可正常导航,但**无悬停预览**。

要启用 Quartz 的 popover 悬停预览,需:
1. 把各专题目录里的 `README.md` 重命名为**唯一文件名**(如 `01-集合.md`),避免所有 stem 都叫 "README" 造成 wikilink 歧义;
2. 把 `[文本](path)` 改写为 `[[笔记名]]` 双链。

此为待办,**未完成前悬停预览不可用**(标准链接导航正常)。

## 部署

见 [DEPLOY.md](./DEPLOY.md)。`quartz.config.yaml` 的 `baseUrl` 待 GitHub 仓库确定后再填(项目页形如 `https://<user>.github.io/<repo>/`)。

## Bootstrap(若 quartz/ 引擎不存在)

```bash
# 在 interview-wiki 根目录
git clone --depth 1 https://github.com/jackyzha0/quartz.git /tmp/quartz
cp -r /tmp/quartz/quartz .
cp /tmp/quartz/{package.json,package-lock.json,tsconfig.json,globals.d.ts,index.d.ts,quartz.lock.json,quartz.config.default.yaml,.gitignore} .
sed 's|pageTitle: Quartz 5|pageTitle: Interview Wiki|; s|baseUrl: quartz.jzhao.xyz|baseUrl: ""|' /tmp/quartz/quartz.config.default.yaml > quartz.config.yaml
npm i
npx quartz plugin install
npx quartz build --serve
```

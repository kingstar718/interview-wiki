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
├── README.md           # 仓库说明(GitHub 展示用)
└── DEPLOY.md           # 部署指引
```

## 常用命令

```bash
# 本地预览(任选其一,改完 md 刷新即生效,无构建步骤)
npx docsify-cli serve .     # http://localhost:3000
python -m http.server 8000  # http://localhost:8000

# 索引自检(改完目录/索引/分类后必跑,纯标准库,退出码非 0 即有问题)
python3 scripts/check_index.py
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
- 下级:`[算法题索引](indexes/算法题索引.md)`

侧栏在 `content/_sidebar.md`,链接相对 `content/`(basePath)。

## 命名与索引约定(AI 快速定位/校验/修改)

- **权威源**:`content/_sidebar.md` 是分类的唯一权威源;`社招问题知识点.md`、`indexes/高频题目索引.md` 等是它的「视图」,改分类先改侧栏,再同步视图。
- **文件名 = 稳定语义 ID**:`interview/`、`indexes/` 下用语义名(`MySQL.md`、`算法题索引.md`),**禁止位置型数字前缀**(`01-`);顺序只在 `_sidebar.md`/索引表里表达,不编进文件名,以免重排断链。
- **例外**:`algorithms/` 下的题号(`1-two-sum.md`)与固定专题序号(`01-数组与字符串/`)是稳定 ID,允许保留;新增专题往后加号(22、23…),不重编中间。
- 改完跑 `python3 scripts/check_index.py`,校验死链/命名/文件集/分类一致/无孤儿题解。
- **专题 README 是本地导航入口**:`algorithms/<专题>/` 下新增题解后,必须在本专题 README 补上链接(校验 E 会拦截漏链)。

## 部署

见 [DEPLOY.md](./DEPLOY.md)。GitHub Pages 直接 serve `main` 分支根,无需 Actions。

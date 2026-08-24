# CLAUDE.md — interview-wiki

本项目是「后端工程师面大厂」知识库,使用 [Quartz 5](https://quartz.jzhao.xyz/) 静态生成。
Markdown 即内容(`content/`),`npx quartz build` 产出 `public/` 静态站点。

## 目录结构

```text
interview-wiki/
├── quartz/              # Quartz 框架源码(vendor 自 jackyzha0/quartz v4 分支,勿随意改)
├── quartz.config.yaml   # 站点配置(插件/主题/baseUrl,中文站点设置在此)
├── quartz.ts            # Explorer sortFn/mapFn 覆盖(分类排序表在此,新增篇目要登记;
│                        #   函数会序列化到浏览器执行,排序表必须写在函数体内;
│                        #   覆盖必须走 componentRegistry,不能 import .quartz/plugins,原因见文件内注释)
├── content/             # 笔记源码(Quartz 渲染此目录)
│   ├── index.md           # 站点首页(不写篇数,手写计数必漂移)
│   ├── 知识点索引.md      # 六大索引:知识点/算法题/英语学习/AI提效/软考系统架构师(均脚本生成)+高频题目(手写)
│   ├── 算法题索引.md
│   ├── 高频题目索引.md
│   ├── 英语学习索引.md
│   ├── AI提效索引.md
│   ├── 软考系统架构师索引.md
│   ├── interview/         # 面试专题,按分类分目录(Java/框架/数据库/中间件/
│   │                      #   计算机基础/分布式与架构/工程实践/面试)
│   ├── algorithms/        # 算法题,14 个套路节点 + problems/ 扁平题目池
│   ├── 英语学习/          # 学习方案/资源 + 词汇 + 技术读写听说 + 职场沟通/面试英语
│   ├── AI提效/            # 第四域:用 AI 进开发流程 + 后端把 LLM 接进业务系统
│   └── 软考系统架构师/    # 第五个域:软考高级备考(考试信息/重点分布/知识域/案例/论文)
├── CLAUDE.md            # 本文件(Claude 项目指引)
├── CONTRIBUTING.md      # 内容规范(小节模板/整合规范/同步清单)
├── TODO.md              # 内容待办(领任务/登记缺口/完成归档)
├── README.md            # 仓库说明(GitHub 展示用)
└── DEPLOY.md            # 部署指引
```

## 常用命令

```bash
# 本地预览(改完 md 自动热重载)
npm ci                      # 首次装依赖(Node ≥22)
npx quartz build --serve    # http://localhost:8080

# 内容定位(改前先跑,不用通读千行文件)
python3 scripts/outline.py Redis           # 打印专题标题树+行号
python3 scripts/outline.py --grep 缓存预热  # 全库定位考点(标题+正文,正文命中标注所在小节)
python3 scripts/outline.py --tech 单调栈    # 按算法技术词检索题解(不带参数则列出 44 词+覆盖数)

# 索引刷新(生成物,勿手编)
python3 scripts/gen_index.py      # 改完 interview 篇目 H3 后跑(知识点索引)
python3 scripts/gen_topics.py     # 改完题解 topics/techniques/元数据行后跑(套路页+算法题索引)
python3 scripts/gen_english.py    # 改完英语篇元数据行后跑(英语学习索引)
python3 scripts/gen_ai.py         # 改完 AI 篇元数据行后跑(AI提效索引)
python3 scripts/gen_rk.py         # 改完软考篇元数据行后跑(软考系统架构师索引)

# 校验(CI 也会跑,本地先过一遍)
python3 scripts/check_index.py    # 18 项校验;Windows GBK 终端需加环境变量 PYTHONIOENCODING=utf-8
```

**改什么跑什么**（改完立即跑，不等收尾）：

| 改了什么 | 跑什么 |
|---------|--------|
| interview/ 篇目的 H3 标题 | `gen_index.py` |
| 题解的 `topics:` / `techniques:` | `gen_topics.py` |
| 题解的元数据行 | `gen_topics.py` + 手工同步 `高频题目索引.md` |
| 英语篇的元数据行(等级/场景) | `gen_english.py` |
| AI 篇的元数据行(等级/场景) | `gen_ai.py` |
| 软考篇的元数据行(科目/考频/场景) | `gen_rk.py` |
| 文件分类归属 | 移动文件 → 同步索引底部专题清单 |
| 任何改动 | `check_index.py`（最后一道关）

## 内容工作流(新增/修改必读)

> 检索/新增/修改的分任务操作手册在 [CONTRIBUTING.md](./CONTRIBUTING.md) 顶部「操作手册」一节(新增分四条路:八股小节/八股篇目/算法题解/技术词)。下面是骨架。

1. **先领任务**:内容任务统一记录在 [TODO.md](./TODO.md)。动手前从「待办」领取;发现新缺口**先登记再做**,不要直接写。
2. **先定位再写**:
   - `outline.py --grep <考点>` 验证是否已覆盖（算法用 `--tech`）
   - `outline.py <文件>` 看标题树+行号，决定插入位置
   - 做查漏补缺前先 Web 搜索多来源高频题单（JavaGuide/小林coding/掘金），确认考点确实高频后再动手
   - 小节结构、写作模板、整合规范见 [CONTRIBUTING.md](./CONTRIBUTING.md)（是什么 → 为什么 → 源码⭕ → 对比⭕ → 常见追问 → 通用概念⭕）
3. **同步**:正文写完后同步追问地图行、相关篇目互链;`知识点索引.md` 由 `scripts/gen_index.py` 从各篇目 H3 自动生成(真实标题 + github-slugger 锚点),改完跑脚本刷新,勿手编。新增篇目还要在 `quartz.ts` 的 Explorer 排序表(ORDER)登记位置,注意表里登记的是**页面 H1 标题**而非文件名。
4. **收尾**:跑 `python3 scripts/gen_index.py` 刷新知识点索引 → `python3 scripts/gen_topics.py` 刷新算法题索引 → `python3 scripts/check_index.py`;完成项移到 TODO.md「已完成」并附 commit 短哈希。

### 操作技巧

**编辑**：
- Edit 工具是 exact string match，`old_string` 必须与文件 byte-by-byte 一致（换行符、缩进、标点一个不差）
- 匹配失败第一反应：**重新 Read 目标区域，复制原文**，不要凭记忆写
- 大段删除时 Edit 容易因换行符（LF）匹配失败，兜底用 PowerShell 的 `IndexOf` 定位 + `Substring` 截断
- 本仓库文件使用 **LF 换行**（`\n`），不是 Windows 默认的 CRLF（`\r\n`）

**读取**：
- 改前用 Grep 找行号，再用 `Read` 的 `offset`/`limit` 精准截取 10-30 行，**不要从头读到尾**
- 跨文件修改时并行 Read 多个目标段，互不阻塞

**Windows 编码**：`outline.py`、`check_index.py` 输出含 Unicode（✓✗），GBK 终端会崩，加 `PYTHONIOENCODING=utf-8` 环境变量即可。

## 分域原则(结构层面的硬约束)

**算法是算法,八股是八股。**

- `algorithms/` 与 `interview/` 是两个独立的域
- 算法的概念层是 `algorithms/` 下的套路页(原子=题解 → 抽共性;题解按套路归类的反向视图在 `算法题索引.md`,由 `gen_topics.py` 生成)
- 算法侧做细粒度归类用 `techniques:` 标签(见 [RFC-算法题标签方案.md](./RFC-算法题标签方案.md))。

## 内容约定

- 频次:★★★★★ 必考 / ★★★★ 高频 / ★★★ 常见 / ★★ 偶考
- 难度:🟢 易 / 🟡 中 / 🔴 难
- 公司:阿里 / 腾讯 / 字节 / 美团 / 百度 / 京东 / 拼多多 / 滴滴 / 网易 / 快手
- 元数据行(可选,标题下一行):`频次 ★★★★ · 难度 🟡 · 高频:字节/美团`,出现即校验格式(校验 I)
- **小节标题 = 稳定语义 ID**:问法式、禁止数字编号开头、发布后不轻改;追问地图不带章号且固定置顶(校验 G/H,细则见 CONTRIBUTING.md)
- **AI提效域**:`content/AI提效/` 是第四个域,结构与英语域同构(元数据行 `等级 L2 · 每天 … · 场景:…` → `gen_ai.py` → 双视图索引;首个 H2 是 `## 学习地图`;校验 C/G/H/U 覆盖,D/E 不覆盖)。定位是**用 AI 提效 + 后端把 LLM 接进系统**,不是八股也不是算法,**不进知识点索引**。
- **软考系统架构师域**:`content/软考系统架构师/` 是第五个域,结构与英语/AI 域同构(元数据行 `科目 … · 考频 ★… · 场景:…` → `gen_rk.py` → 双视图索引;首个 H2 是 `## 学习地图`;校验 C/G/H/V 覆盖,D/E 不覆盖)。定位是**软考高级系统架构设计师备考**(考试信息/重点分布/知识域/案例分析/论文),不是大厂面试八股,**不进知识点索引**。
- **英语学习域**:`content/英语学习/` 是独立于 interview/algorithms 的第三个域。首个 H2 是 `## 学习地图`(不是「面试追问地图」——它不是面试专题),其余同 interview 体例(中文数字章 + 常见追问 + 相关)。校验 C/G/H 覆盖它;D/E 不覆盖(那两项绑 `知识点索引.md` 的专题文件清单,八股专属,**英语篇目不进知识点索引**)
- 详解模板:
  - 算法题(固定小节,顺序不变):题目 → 思路 → 代码 → 复杂度 → 边界条件 → 变式 → 易错点 → 面试追问 → 关联题;H1 为`题号. 中文题名(English Title)`,元数据行必填且为难度/频次/公司的权威源(细则见 CONTRIBUTING.md)
  - 知识点:是什么 → 为什么这么设计 → 源码⭕ → 对比同类⭕ → 常见追问 → 通用概念⭕(细则见 CONTRIBUTING.md)

## 链接约定(Quartz shortest 语义)

Quartz 的 `CrawlLinks` 配置为 `markdownLinkResolution: "shortest"`(Obsidian 同款),
链接**按文件名全库唯一匹配**,不按相对路径:

- **首选纯文件名**:`[JVM](JVM.md)`、`[MySQL](MySQL.md#索引)` —— 无论源文件在哪个目录都能解析;带 `#锚点` 跳到具体小节,锚点由 github-slugger 规则生成(小写+删标点+空格转-,见 `scripts/slug.py`),`check_index.py` 校验项 M 拦锚点死链
- **双链可用**:`[[JVM]]`、`[[MySQL#索引|MySQL 索引]]`(ObsidianFlavoredMarkdown 已启用),与标准链接等价,都计入反链/图谱
- **文件名不唯一时写 content 根全路径**:目前全库文件名唯一(`index.md` 除外),没有需要写全路径的页面;将来若出现同名文件,链接必须写 `[x](interview/Java/JVM.md)` 这样的 content 根全路径
- **禁止相对路径多段链接**(`../interview/JVM.md`、`problems/1-two-sum.md`):Quartz 会把多段路径当作从 content 根出发解析,相对写法必死链。`check_index.py` 检查项 A 会拦截
- 代码块/行内代码里的 `[[...]]` 不会被转换,不算链接
- **URL 只小写 ASCII,中文原样保留**:`AI提效/LLM应用开发.md` → `/ai提效/llm应用开发`。写 md 链接时不用管大小写(按文件名匹配),但**手写外链/分享链接要用小写形式**;`public/` 下同时存在 `AI提效/` 目录是 Quartz 自动生成的 289 字节跳转桩(`<meta http-equiv="refresh">`),不是重复内容,别手删
- **侧栏中文目录名 ≠ 真实路径**:`interview`/`algorithms`/`problems` 是真实英文目录名,侧栏显示的「面试专题/算法题/题库」来自 `quartz.ts` 的 Explorer `mapFn` 改名表,只影响显示,链接和 URL 仍用英文名

## 命名与索引约定(AI 快速定位/校验/修改)

- **权威源**:`interview/<分类>/` 目录结构是分类的唯一权威源(Explorer 侧栏直接反映目录树);`知识点索引.md`(由 `scripts/gen_index.py` 从各篇目真实 H3 + github-slugger 锚点自动生成,勿手编)底部「专题文件清单」等是它的「视图」,改分类先移动文件,再同步视图。
 - **文件名 = 稳定语义 ID**:`interview/` 下用语义名(`MySQL.md`、`集合框架.md`),**禁止位置型数字前缀**(`01-`);顺序在 `quartz.ts` 的 Explorer 排序表里表达,不编进文件名。
- **文件名全库唯一**(README.md/index.md 除外):纯文件名链接方案的前提,新文件重名会被校验 B 拦截。
- **例外**:`algorithms/problems/` 下的题号(`1-two-sum.md`)是稳定 ID,允许保留;`algorithms/` 下的套路页(`双指针与滑动窗口.md`)是语义名,同 `interview/` 命名规则,新增套路直接语义命名,不用数字序号。
- **算法套路节点在 `algorithms/`**:题解用两级 frontmatter 声明归属 —— `topics:`(粗套路,决定归入哪个套路,校验 F)+ `techniques:`(细技术词,决定落到 `算法题索引.md` 里哪个分组,校验 S)。词表权威源是**套路页自己的 frontmatter `techniques:`**,各页并集即全局词表,同一个词可被多页声明。题解按套路→技术词的反向分组视图整篇生成在 `算法题索引.md`(`gen_topics.py`)。细则见 CONTRIBUTING.md。
- 改完跑 `python3 scripts/check_index.py`,校验死链/文件名唯一/命名/文件集/分类一致/题解归属/关系类型/技术词表。

## 部署

见 [DEPLOY.md](./DEPLOY.md)。push `main` 后 Cloudflare Pages 自动构建发布;
改完在本地跑 `python3 scripts/check_index.py` 做索引/死链校验(见上)。

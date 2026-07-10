# CLAUDE.md — interview-wiki

本项目是「后端工程师面大厂」知识库,使用 [Quartz 5](https://quartz.jzhao.xyz/) 静态生成。
Markdown 即内容(`content/`),`npx quartz build` 产出 `public/` 静态站点。

## 目录结构

```text
interview-wiki/
├── quartz/              # Quartz 框架源码(vendor 自 jackyzha0/quartz v4 分支,勿随意改)
├── quartz.config.yaml   # 站点配置(插件/主题/baseUrl,中文站点设置在此)
├── quartz.ts            # Explorer sortFn/mapFn 覆盖(分类排序表在此,新增篇目要登记;
│                        #   函数会序列化到浏览器执行,排序表必须写在函数体内)
├── .github/workflows/deploy.yml  # CI:校验 → 构建 → 发布 GitHub Pages
├── content/             # 笔记源码(Quartz 渲染此目录)
│   ├── index.md           # 站点首页(不写篇数,手写计数必漂移)
│   ├── indexes/           # 三大索引:知识点/算法题(均脚本生成)/高频题目(手写)
│   ├── 概念/              # 跨 interview 分类的知识节点(不伸进算法侧,见「分域原则」)
│   ├── interview/         # 社招八股,按分类分目录(Java/框架/数据库/中间件/
│   │                      #   计算机基础/分布式与架构/工程实践/面试)
│   └── algorithms/        # 算法题,13 个套路节点 + problems/ 扁平题目池
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

# 索引自检(改完目录/索引/分类后必跑,纯标准库,退出码非 0 即有问题)
python3 scripts/check_index.py

# 知识点索引自动生成(改完 interview 篇目 H3 后跑,从真实标题+锚点刷新,勿手编)
python3 scripts/gen_index.py

# 概念页「出现在哪里」自动生成(改完任何指向 概念/ 的链接后跑;--check 只检测漂移)
python3 scripts/gen_concepts.py

# 套路页「已解题目」+ 算法题索引 自动生成(改完题解 topics:/techniques:/元数据行后跑)
python3 scripts/gen_topics.py

# 内容定位(改前先跑,不用通读千行文件)
python3 scripts/outline.py Redis           # 打印专题标题树+行号
python3 scripts/outline.py --grep 缓存预热  # 全库定位考点(标题+正文,正文命中标注所在小节)
python3 scripts/outline.py --tech 单调栈    # 按算法技术词检索题解(不带参数则列出 44 词+覆盖数)
```

## 内容工作流(新增/修改必读)

> 检索/新增/修改的分任务操作手册在 [CONTRIBUTING.md](./CONTRIBUTING.md) 顶部「操作手册」一节(新增分五条路:八股小节/八股篇目/算法题解/技术词/概念)。下面是骨架。

1. **先领任务**:内容任务统一记录在 [TODO.md](./TODO.md)。动手前从「待办」领取;发现新缺口**先登记再做**,不要直接写。
2. **先定位再写**:`outline.py --grep` 验证考点是否已覆盖(算法用 `--tech`),`outline.py <文件>` 看结构定插入位置;小节结构、修改整合规范、写作要求见 [CONTRIBUTING.md](./CONTRIBUTING.md)(是什么 → 为什么 → 源码⭕ → 对比⭕ → 常见追问 → 通用概念⭕)。
3. **同步**:正文写完后同步追问地图行、相关篇目互链;`indexes/知识点索引.md` 由 `scripts/gen_index.py` 从各篇目 H3 自动生成(真实标题 + github-slugger 锚点),改完跑脚本刷新,勿手编。新增篇目还要在 `quartz.ts` 的 Explorer 排序表(ORDER)登记位置,注意表里登记的是**页面 H1 标题**而非文件名。
4. **收尾**:跑 `python3 scripts/gen_index.py` 刷新知识点索引 → `python3 scripts/gen_concepts.py` 刷新概念页 → `python3 scripts/gen_topics.py` 刷新套路页 → `python3 scripts/check_index.py`;完成项移到 TODO.md「已完成」并附 commit 短哈希。

## 分域原则(结构层面的硬约束)

**算法是算法,八股是八股。抽取概念时不要把两者混为一谈。**

- `algorithms/` 与 `interview/` 是两个独立的域,各自有自己的概念层:
  - 八股的概念层是 `概念/`(原子=考点 → 抽共性 → `## 出现在哪里` 反向视图)
  - **算法的概念层是 `algorithms/` 下的 13 个套路页**(原子=题解 → 抽共性 → `## 已解题目` 反向视图),两者同构
- 所以 `概念/` **不覆盖算法**。让它伸进算法侧等于在已有概念层上再盖一层。
- 校验 P 的内容域只按 interview 分类计,`algorithms` 不计域。
- 算法 → 概念 的单向链接可以保留作延伸阅读,但**不计入概念的入场券**——「可以链」和「必须链」是两回事。
- 算法侧要做概念层,就在套路页上做:`techniques:` 细粒度标签(进行中,见 [RFC-算法题标签方案.md](./RFC-算法题标签方案.md))。

## 内容约定

- 频次:★★★★★ 必考 / ★★★★ 高频 / ★★★ 常见 / ★★ 偶考
- 难度:🟢 易 / 🟡 中 / 🔴 难
- 公司:阿里 / 腾讯 / 字节 / 美团 / 百度 / 京东 / 拼多多 / 滴滴 / 网易 / 快手
- 元数据行(可选,标题下一行):`频次 ★★★★ · 难度 🟡 · 高频:字节/美团`,出现即校验格式(校验 I)
- **小节标题 = 稳定语义 ID**:问法式、禁止数字编号开头、发布后不轻改;追问地图不带章号且固定置顶(校验 G/H,细则见 CONTRIBUTING.md)
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

## 命名与索引约定(AI 快速定位/校验/修改)

- **权威源**:`interview/<分类>/` 目录结构是分类的唯一权威源(Explorer 侧栏直接反映目录树);`indexes/知识点索引.md`(由 `scripts/gen_index.py` 从各篇目真实 H3 + github-slugger 锚点自动生成,勿手编)底部「专题文件清单」等是它的「视图」,改分类先移动文件,再同步视图。
- **文件名 = 稳定语义 ID**:`interview/`、`indexes/` 下用语义名(`MySQL.md`、`算法题索引.md`),**禁止位置型数字前缀**(`01-`);顺序在 `quartz.ts` 的 Explorer 排序表里表达,不编进文件名。
- **文件名全库唯一**(README.md/index.md 除外):纯文件名链接方案的前提,新文件重名会被校验 B 拦截。
- **例外**:`algorithms/problems/` 下的题号(`1-two-sum.md`)是稳定 ID,允许保留;`algorithms/` 下的套路页(`双指针与滑动窗口.md`)是语义名,同 `interview/` 命名规则,新增套路直接语义命名,不用数字序号。
- **跨分类概念在 `概念/`**:入场券是「入链覆盖 ≥2 个内容域」(校验 P,导航链接不算);`## 出现在哪里` 由 `gen_concepts.py` 生成(校验 Q);题解 `## 关联题` 每条须带白名单类型前缀(校验 O:同套路/进阶/基础/易混/知识点)。细则见 CONTRIBUTING.md。
- **算法套路节点在 `algorithms/`**:题解用两级 frontmatter 声明归属 —— `topics:`(13 个粗套路,决定出现在哪页,校验 F)+ `techniques:`(44 个细技术词,决定落到页内哪个分组,校验 S)。词表权威源是**套路页自己的 frontmatter `techniques:`**,13 页并集即全局词表,同一个词可被多页声明。套路页 `## 已解题目` 由 `gen_topics.py` 反向分组生成(校验 R)。细则见 CONTRIBUTING.md。
- 改完跑 `python3 scripts/check_index.py`,校验死链/文件名唯一/命名/文件集/分类一致/题解归属/关系类型/概念成色/套路视图。

## 部署

见 [DEPLOY.md](./DEPLOY.md)。push `main` 后 GitHub Actions 自动构建发布(Pages Source = GitHub Actions)。

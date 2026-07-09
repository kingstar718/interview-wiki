#!/usr/bin/env python3
"""校验 interview-wiki 索引一致性 —— 纯标准库，无需构建。

用法:
    python3 scripts/check_index.py      # 在任意目录运行均可，脚本自定位仓库根

设计前提: 站点由 Quartz 渲染，`interview/<分类>/<篇目>.md` 的目录结构是分类的
唯一权威源(Explorer 侧栏直接反映目录树)，`indexes/知识点索引.md` 底部的
「专题文件清单」是它的「视图」。视图与权威源漂移(分类错位、少收录、死链、
文件名歧义)人工肉眼难查，故用本脚本一键校验。

链接语义(与 Quartz CrawlLinks 的 markdownLinkResolution: "shortest" 一致):
    - 纯文件名链接 `[JVM](JVM.md)` / 双链 `[[JVM]]`: 按文件名全库唯一匹配
    - 多段路径链接 `[x](algorithms/README.md)`: 视为从
      content/ 根出发的路径(不是相对当前文件!)

检查项:
    A. 死链      —— 所有 .md 链接与 [[双链]] 都能按上述语义 resolve
    B. 文件名唯一 —— 纯文件名链接方案的前提: 除 README.md/index.md 外,
                    全库 .md 文件名不得重复(重复会导致链接歧义)
    C. 命名规范  —— interview/ 与 indexes/ 下禁止位置型数字前缀 (^数字-)
                    (algorithms/problems/ 例外: 题号是稳定 ID，允许)
    D. 文件集    —— interview/ 实际文件 == 枢纽「专题文件清单」收录
    E. 分类一致  —— interview/<分类>/ 目录名 == 枢纽清单中该文件的分类名
    F. 题解归属  —— algorithms/problems/ 下每个题解必须有非空 topics: frontmatter,
                    且每个值都对应 algorithms/ 下存在的套路页(防止拼写错误指向
                    不存在的套路、防止题解游离在任何套路视图之外)
    G. 地图置顶  —— interview/ 专题第一个 H2 必须是无章号的「## 面试追问地图」
                    (面试问题深挖指南豁免)
    H. 标题无编号 —— interview/ 的 H3-H6 小节标题禁止数字编号开头(^数字[.、]):
                    小节标题是稳定语义 ID,同 C 项原则(401/502 等状态码开头合法)
    I. 元数据行  —— 「频次 ★ · 难度 🟡 · 高频：公司」行出现即校验(interview + 算法题解):
                    ★ 1~5 个、难度限 🟢🟡🔴、公司限约定清单(算法侧可用「全厂」)、段序固定
    J. 关联题欠链 —— 题解正文提到已收录题号(如「148. 排序链表」)却全文无链接 -> 报警,
                    保证关联网络随收录自动趋于完整(行首序号列表不算提及)
    K. 元数据一致 —— 题解元数据行是难度/频次/公司的权威源,算法题索引题单表是视图:
                    已解题必须带链接、逐行比对难度/频次/公司
    L. 题解结构  —— H1 为「# 题号. 中文题名（English Title）」+ 元数据行必填 +
                    固定小节顺序(题目→…→面试追问→关联题,九节必填)
    M. 锚点死链  —— 链接的 #锚点 经 slugify 后须 ∈ 目标文件标题 slug 集合(归一化
                    匹配,镜像 Quartz 宽松行为,放过大小写笔误,抓真死链);页内锚点
                    校验当前文件,跨文件/双链校验目标文件。锚点 slug 由 slug.py(与
                    Quartz 同源的 github-slugger 规则)生成,A 项只校验文件名不校验锚点
    N. 高频表一致 —— 高频题目索引 A 表(高频算法题 Top N)同为题解元数据行的视图:
                    已解题必须带链接且指向该题解、逐行比对难度/热度/公司(同 K 项口径,
                    公司只比集合不比顺序 —— 表里按主考公司在前书写)
    O. 关系类型   —— 题解「## 关联题」每条目必须以白名单类型前缀开头(同套路/进阶/
                    基础/易混/知识点)。关系区是图的边,类型必须可机器解析;条目里
                    是否带链接由 J 项管(未收录的题允许纯文本,收录后 J 会催回补)
    P. 概念成色   —— 概念/<x>.md 的入链必须覆盖 ≥2 个内容域(interview + algorithms)。
                    只在一个域出现的不算跨域概念,应回收进正文。索引页/首页的导航
                    链接不计入,否则自己链一下就能凑够两个域,判据失效
    Q. 概念视图   —— 概念页「出现在哪里」由 gen_concepts.py 生成,内容须与全库真实
                    入链一致(手写的成员列表最终都会漂移,同 K/N 项的教训)
    R. 套路视图   —— 套路页「已解题目」由 gen_topics.py 从题解 topics: frontmatter
                    生成,内容须与真实 frontmatter 一致(漂移检测,镜像 Q 项之于 P 项)

任一检查失败 -> 退出码 1，可直接接入 CI / pre-commit / AI 改完自检。
"""
import os
import re
import sys
from collections import defaultdict

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import slugify, slugify_headings  # 与 Quartz 同源的 github-slugger 规则
from outline import parse_headings  # 跳过围栏代码块提取标题
import gen_concepts  # 概念页反链的唯一实现,P/Q 项复用它,避免两套解析漂移
import gen_topics  # 套路页题目分组的唯一实现,F/R 项复用它,避免两套解析漂移

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
HUB = os.path.join(CONTENT, "indexes", "知识点索引.md")
INTERVIEW = os.path.join(CONTENT, "interview")
ALGORITHMS = os.path.join(CONTENT, "algorithms")
PROBLEMS = os.path.join(ALGORITHMS, "problems")

LINK_RE = re.compile(r"\[[^\]]*\]\(([^)]+)\)")
WIKILINK_RE = re.compile(r"(?<!!)\[\[([^\]|#]+)[^\]]*\]\]")
NUM_PREFIX_RE = re.compile(r"^\d+[-_]")
# 文件名可重复的「目录级」文件,链接它们必须写全路径
NON_UNIQUE_OK = {"README.md", "index.md"}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def all_md_files():
    for root, _dirs, files in os.walk(CONTENT):
        for f in files:
            if f.endswith(".md"):
                yield os.path.join(root, f)


FENCE_RE = re.compile(r"^(```|~~~)")
CODE_SPAN_RE = re.compile(r"`[^`]*`")


def strip_code(text):
    """剥离围栏代码块与行内代码(保留行数,链接扫描不受代码干扰)。
    Quartz 同样不会转换代码里的链接/双链。"""
    out = []
    in_fence = False
    for line in text.splitlines():
        if FENCE_RE.match(line.strip()):
            in_fence = not in_fence
            out.append("")
            continue
        out.append("" if in_fence else CODE_SPAN_RE.sub("", line))
    return out


def build_name_map():
    """basename -> [content 根相对路径, ...]"""
    by_name = defaultdict(list)
    for path in all_md_files():
        rel = os.path.relpath(path, CONTENT).replace(os.sep, "/")
        by_name[os.path.basename(path)].append(rel)
    return by_name


def check_dead_links(by_name):
    """A. 链接按 Quartz shortest 语义必须可解析。"""
    errors = []
    for path in all_md_files():
        rel = os.path.relpath(path, ROOT)
        for lineno, line in enumerate(strip_code(read(path)), 1):
            targets = []
            for m in LINK_RE.finditer(line):
                t = m.group(1).split("#")[0].strip()
                if not t or t.startswith(("http://", "https://", "mailto:")):
                    continue
                if t.endswith(".md"):
                    targets.append(t)
            for m in WIKILINK_RE.finditer(line):
                targets.append(m.group(1).strip() + ".md")
            for target in targets:
                base = os.path.basename(target)
                if "/" not in target:
                    hits = by_name.get(base, [])
                    if len(hits) == 0:
                        errors.append(f"{rel}:{lineno} -> {target} (目标不存在)")
                    elif len(hits) > 1:
                        errors.append(
                            f"{rel}:{lineno} -> {target} (文件名歧义: {', '.join(hits)}，请写 content 根全路径)"
                        )
                else:
                    # 多段路径 = 从 content/ 根出发(Quartz 语义,非相对当前文件)
                    resolved = os.path.normpath(os.path.join(CONTENT, target.lstrip("/")))
                    if not os.path.isfile(resolved):
                        errors.append(
                            f"{rel}:{lineno} -> {target} (按 content 根解析不存在;"
                            f" Quartz 不支持相对当前文件的多段路径)"
                        )
    return errors


def check_unique_names(by_name):
    """B. 除 README/index 外,文件名全库唯一(纯文件名链接方案的前提)。"""
    errors = []
    for name, paths in sorted(by_name.items()):
        if name in NON_UNIQUE_OK or len(paths) == 1:
            continue
        errors.append(f"{name} 重复: {', '.join(paths)} (文件名是稳定语义 ID,必须全库唯一)")
    return errors


def check_naming():
    """C. interview/、indexes/、概念/ 下禁止位置型数字前缀(含分类子目录)。"""
    errors = []
    for folder in ("interview", "indexes", "概念"):
        d = os.path.join(CONTENT, folder)
        if not os.path.isdir(d):
            continue
        for root, dirs, files in os.walk(d):
            for name in sorted(dirs) + sorted(f for f in files if f.endswith(".md")):
                if NUM_PREFIX_RE.match(name):
                    rel = os.path.relpath(os.path.join(root, name), ROOT)
                    errors.append(f"{rel} (位置型数字前缀，应改用语义名)")
    return errors


def actual_categories():
    """目录结构 -> {interview 文件名: 分类目录名}。权威源。"""
    mapping = {}
    for root, _dirs, files in os.walk(INTERVIEW):
        for f in files:
            if f.endswith(".md"):
                cat = os.path.relpath(root, INTERVIEW).replace(os.sep, "/")
                mapping[f] = None if cat == "." else cat.split("/")[0]
    return mapping


def parse_hub_table():
    """知识点索引.md 底部「专题文件清单」表 -> {interview 文件名: 分类名}。"""
    mapping = {}
    in_table = False
    for line in read(HUB).splitlines():
        s = line.strip()
        if s.startswith("## "):
            in_table = s == "## 专题文件清单"
            continue
        if not in_table or not s.startswith("|"):
            continue
        cells = [c.strip() for c in s.strip("|").split("|")]
        if len(cells) < 2 or cells[0] in ("分类", "") or set(cells[0]) <= {"-"}:
            continue
        cat = cells[0]
        for m in LINK_RE.finditer(s):
            target = m.group(1).split("#")[0].strip()
            if target.endswith(".md"):
                mapping[os.path.basename(target)] = cat
    return mapping


def check_file_set(actual, hub):
    """D. interview/ 实际文件 == 枢纽「专题文件清单」收录。"""
    errors = []
    for f in sorted(set(actual) - set(hub)):
        errors.append(f"interview/{actual[f]}/{f} 存在但未被「枢纽专题文件清单」收录")
    for f in sorted(set(hub) - set(actual)):
        errors.append(f"「枢纽专题文件清单」收录了 {f}，但 interview/ 下不存在")
    return errors


def check_category(actual, hub):
    """E. 分类目录名与枢纽清单分类名必须一致。"""
    errors = []
    for f in sorted(set(actual) & set(hub)):
        if actual[f] is None:
            errors.append(f"interview/{f} 未归入任何分类目录(应移入 interview/<分类>/)")
        elif actual[f] != hub[f]:
            errors.append(f"{f} 分类漂移: 目录=「{actual[f]}」 vs 枢纽=「{hub[f]}」")
    return errors


def check_orphan_solutions():
    """F. algorithms/problems/ 下的题解必须有非空 topics: frontmatter,
    且每个值都对应 algorithms/ 下存在的套路页(复用 gen_topics,避免两套解析漂移)。"""
    errors = []
    pattern_names = {os.path.splitext(os.path.basename(p))[0] for p in gen_topics.pattern_files()}
    for path in gen_topics.problem_files():
        rel = os.path.relpath(path, ROOT)
        topics = gen_topics.parse_topics(read(path))
        if not topics:
            errors.append(f"{rel} 缺少 topics: frontmatter(题解必须归属至少一个套路)")
            continue
        for t in topics:
            if t not in pattern_names:
                errors.append(f"{rel} topics 里的「{t}」不对应任何 algorithms/ 套路页(拼写错误?)")
    return errors


MAP_HEADING = "## 面试追问地图"
MAP_EXEMPT = {"面试问题深挖指南.md"}
H2_RE = re.compile(r"^##\s")
NUM_SECTION_RE = re.compile(r"^#{3,6}\s+\d+[.、．]\s")
META_TRIGGER_RE = re.compile(r"^(频次 |难度 )")
META_PARTS = [
    ("频次", re.compile(r"^频次 ★{1,5}$")),
    ("难度", re.compile(r"^难度 [🟢🟡🔴]$")),
    ("高频", None),  # 公司清单单独校验
]
COMPANIES = {"阿里", "腾讯", "字节", "美团", "百度", "京东", "拼多多", "滴滴", "网易", "快手", "全厂"}
SOLUTION_RE = re.compile(r"^(\d+)-.+\.md$")
ALGO_H1_RE = re.compile(r"^# \d+\. .+（.+）$")
ALGO_SECTIONS = ["题目", "思路", "代码", "复杂度", "边界条件", "变式", "易错点", "面试追问", "关联题"]
# 题号提及: 非行首的「数字. 」或「数字、」后跟中英文(行首是有序列表序号,不算)
MENTION_RE = re.compile(r"(\d{1,4})[.、]\s?[A-Za-z一-龥]")
ALGO_INDEX = os.path.join(CONTENT, "indexes", "算法题索引.md")
HOT_INDEX = os.path.join(CONTENT, "indexes", "高频题目索引.md")


def interview_files():
    for root, _dirs, files in os.walk(INTERVIEW):
        for f in sorted(files):
            if f.endswith(".md"):
                yield os.path.join(root, f)


def solution_files():
    """(path, 题号, basename) —— algorithms/problems/<题号>-slug.md"""
    if not os.path.isdir(PROBLEMS):
        return
    for f in sorted(os.listdir(PROBLEMS)):
        m = SOLUTION_RE.match(f)
        if m:
            yield os.path.join(PROBLEMS, f), m.group(1), f


def meta_files():
    yield from interview_files()
    for path, _num, _base in solution_files():
        yield path


def skip_frontmatter(lines):
    """去掉开头的 --- frontmatter 块(题解的 topics: 声明)及其后的空行,
    不影响无 frontmatter 的文件。"""
    if lines and lines[0].strip() == "---":
        for i, line in enumerate(lines[1:], 1):
            if line.strip() == "---":
                rest = lines[i + 1 :]
                while rest and rest[0].strip() == "":
                    rest = rest[1:]
                return rest
    return lines


def parse_solution_meta(path):
    """题解元数据行 -> (频次, 难度, 公司集合) 或 None。取 H1 后前几行。"""
    for line in skip_frontmatter(strip_code(read(path)))[:6]:
        s = line.strip()
        if META_TRIGGER_RE.match(s):
            stars = re.search(r"频次 (★{1,5})", s)
            diff = re.search(r"难度 ([🟢🟡🔴])", s)
            comp = re.search(r"高频[：:]([^ ·]+)", s)
            return (
                stars.group(1) if stars else None,
                diff.group(1) if diff else None,
                set(comp.group(1).split("/")) if comp else set(),
            )
    return None


def check_related_links():
    """J. 题解正文提到已收录题号却全文无链接 -> 欠链。"""
    solved = {num: base for _p, num, base in solution_files()}
    errors = []
    for path, own_num, _base in solution_files():
        rel = os.path.relpath(path, ROOT)
        text_lines = strip_code(read(path))
        linked = set()
        for line in text_lines:
            for m in LINK_RE.finditer(line):
                lm = SOLUTION_RE.match(os.path.basename(m.group(1).split("#")[0]))
                if lm:
                    linked.add(lm.group(1))
        for lineno, line in enumerate(text_lines, 1):
            bare = LINK_RE.sub("", line)
            for m in MENTION_RE.finditer(bare):
                num = m.group(1)
                if bare[: m.start()].strip() == "":
                    continue  # 行首序号(有序列表),不算题号提及
                if num == own_num or num not in solved or num in linked:
                    continue
                errors.append(
                    f"{rel}:{lineno} 提到「{num}.」但未链接 {solved[num]}(已收录题必须带链接)"
                )
    return errors


def check_algo_meta_sync():
    """K. 题解元数据行(权威源) vs 算法题索引题单表(视图) 逐行比对。"""
    errors = []
    actual = {base: (path, num) for path, num, base in solution_files()}
    # 索引里带链接的行: basename -> [(难度, 频次, 公司集合, 行号)]
    index_rows = defaultdict(list)
    for lineno, line in enumerate(read(ALGO_INDEX).splitlines(), 1):
        if not line.startswith("|"):
            continue
        cells = [c.strip() for c in line.strip("|").split("|")]
        if len(cells) < 5:
            continue
        m = LINK_RE.search(cells[1])
        if m:
            base = os.path.basename(m.group(1).split("#")[0])
            index_rows[base].append((cells[2], cells[3], set(cells[4].split()), lineno))
        elif cells[0].isdigit() and f"{cells[0]}-" in " ".join(actual):
            hit = next((b for b in actual if b.startswith(cells[0] + "-")), None)
            if hit:
                errors.append(f"索引:{lineno} 题 {cells[0]} 已有题解 {hit} 但题名未带链接(已解=带链接)")
    for base, rows in sorted(index_rows.items()):
        if base not in actual:
            continue  # 链接目标不存在由 A 兜底
        meta = parse_solution_meta(actual[base][0])
        rel = os.path.relpath(actual[base][0], ROOT)
        if meta is None:
            errors.append(f"{rel} 缺少元数据行(题解是难度/频次/公司的权威源,必填)")
            continue
        stars, diff, comps = meta
        for idx_diff, idx_stars, idx_comps, lineno in rows:
            if diff and idx_diff != diff:
                errors.append(f"索引:{lineno} {base} 难度 {idx_diff} != 题解 {diff}(以题解为准)")
            if stars and idx_stars != stars:
                errors.append(f"索引:{lineno} {base} 频次 {idx_stars} != 题解 {stars}(以题解为准)")
            if comps and idx_comps != comps:
                errors.append(f"索引:{lineno} {base} 公司 {sorted(idx_comps)} != 题解 {sorted(comps)}(以题解为准)")
    return errors


RELATION_TYPES = ("同套路", "进阶", "基础", "易混", "知识点")
RELATION_RE = re.compile(r"^-\s*([^：:]{1,8})[：:]")


def check_relation_types():
    """O. 题解「## 关联题」条目必须带白名单类型前缀(关系是图的边,类型要可解析)。"""
    errors = []
    for path, _num, base in solution_files():
        lines = strip_code(read(path))
        try:
            start = lines.index("## 关联题")
        except ValueError:
            continue  # 缺小节由 L 项兜底
        for offset, line in enumerate(lines[start + 1 :], start + 2):
            if line.startswith("## "):
                break
            s = line.strip()
            if not s.startswith("- "):
                continue
            m = RELATION_RE.match(s)
            if not m:
                errors.append(f"{base}:{offset} 关联题条目缺类型前缀: {s[:40]}")
            elif m.group(1) not in RELATION_TYPES:
                errors.append(
                    f"{base}:{offset} 关系类型「{m.group(1)}」不在白名单 {'/'.join(RELATION_TYPES)}"
                )
    return errors


def _concept_refs():
    """概念页 -> {锚点: [(标题, 文件名, 域)]},复用 gen_concepts 的解析。"""
    concepts = list(gen_concepts.concept_files())
    if not concepts:
        return [], {}
    return concepts, gen_concepts.collect_refs({os.path.basename(p) for p in concepts})


def check_concept_maturity():
    """P. 概念页入链须覆盖 ≥2 个内容域(导航链接不算)。"""
    errors = []
    concepts, refs = _concept_refs()
    for path in concepts:
        base = os.path.basename(path)
        doms = {d for hits in refs[base].values() for _t, _b, d in hits}
        content_doms = {d for d in doms if gen_concepts.is_content_domain(d)}
        if len(content_doms) < 2:
            errors.append(
                f"概念/{base} 内容域入链只有 {sorted(content_doms) or '0 个'}"
                f"(需 ≥2 个),尚未成体系 -> 回收进正文或补足跨域引用"
            )
    return errors


def check_concept_view():
    """Q. 概念页「出现在哪里」须与全库真实入链一致(生成物不得手编/过期)。"""
    errors = []
    concepts, refs = _concept_refs()
    for path in concepts:
        base = os.path.basename(path)
        old = read(path)
        try:
            new = gen_concepts.splice(old, gen_concepts.render(path, refs[base]))
        except SystemExit as e:
            errors.append(f"概念/{base} {e}")
            continue
        if new != old:
            errors.append(f"概念/{base} 的「出现在哪里」已过期 -> 跑 python3 scripts/gen_concepts.py")
    return errors


def check_topic_view():
    """R. 套路页「已解题目」须与题解 topics: frontmatter 真实分组一致(镜像 Q 项)。"""
    errors = []
    patterns = list(gen_topics.pattern_files())
    if not patterns:
        return errors
    names = {os.path.splitext(os.path.basename(p))[0] for p in patterns}
    groups, _orphans = gen_topics.collect_groups(names)
    for path in patterns:
        name = os.path.splitext(os.path.basename(path))[0]
        old = read(path)
        try:
            new = gen_topics.splice(old, gen_topics.render(groups[name]))
        except SystemExit as e:
            errors.append(f"algorithms/{name}.md {e}")
            continue
        if new != old:
            errors.append(f"algorithms/{name}.md 的「已解题目」已过期 -> 跑 python3 scripts/gen_topics.py")
    return errors


def check_hot_meta_sync():
    """N. 题解元数据行(权威源) vs 高频题目索引 A 表(视图) 逐行比对。"""
    errors = []
    # 题号按整数归一:题解文件名允许零填充(01-two-sum.md)
    actual = {str(int(num)): (path, base) for path, num, base in solution_files()}
    in_section_a = False
    for lineno, line in enumerate(read(HOT_INDEX).splitlines(), 1):
        if line.startswith("## "):
            in_section_a = line.startswith("## A.")
            continue
        if not (in_section_a and line.startswith("|")):
            continue
        cells = [c.strip() for c in line.strip("|").split("|")]
        # 排名 | 题号 | 题名 | 专题 | 难度 | 热度 | 常考公司
        if len(cells) != 7 or not cells[1].isdigit():
            continue
        num = str(int(cells[1]))
        if num not in actual:
            continue  # 未收录题解,保持纯文本
        path, base = actual[num]
        m = LINK_RE.search(cells[2])
        if not m:
            errors.append(f"高频索引:{lineno} 题 {num} 已有题解 {base} 但题名未带链接(已解=带链接)")
            continue
        target = os.path.basename(m.group(1).split("#")[0])
        if target != base:
            errors.append(f"高频索引:{lineno} 题 {num} 链接指向 {target},应为 {base}")
            continue
        meta = parse_solution_meta(path)
        if meta is None:
            continue  # 缺元数据行由 K/L 兜底
        stars, diff, comps = meta
        if diff and cells[4] != diff:
            errors.append(f"高频索引:{lineno} {base} 难度 {cells[4]} != 题解 {diff}(以题解为准)")
        if stars and cells[5] != stars:
            errors.append(f"高频索引:{lineno} {base} 热度 {cells[5]} != 题解 {stars}(以题解为准)")
        if comps and set(cells[6].split()) != comps:
            errors.append(
                f"高频索引:{lineno} {base} 公司 {sorted(set(cells[6].split()))} "
                f"!= 题解 {sorted(comps)}(以题解为准,顺序不限)"
            )
    return errors


def check_solution_structure():
    """L. H1 格式 + 元数据行必填 + 固定小节顺序。"""
    errors = []
    for path, _num, _base in solution_files():
        rel = os.path.relpath(path, ROOT)
        lines = skip_frontmatter(strip_code(read(path)))
        if not lines or not ALGO_H1_RE.match(lines[0]):
            errors.append(f"{rel} H1 应为「# 题号. 中文题名（English Title）」,实际「{lines[0] if lines else ''}」")
        if parse_solution_meta(path) is None:
            errors.append(f"{rel} 缺少元数据行(频次/难度/高频,必填)")
        h2 = [ln[3:].strip() for ln in lines if ln.startswith("## ")]
        expect = list(ALGO_SECTIONS)
        if h2 != expect:
            errors.append(f"{rel} 小节应为 {'→'.join(expect)},实际 {'→'.join(h2)}")
    return errors


def check_map_on_top():
    """G. 专题第一个 H2 必须是无章号地图。"""
    errors = []
    for path in interview_files():
        if os.path.basename(path) in MAP_EXEMPT:
            continue
        rel = os.path.relpath(path, ROOT)
        lines = strip_code(read(path))
        first_h2 = next(((i, ln) for i, ln in enumerate(lines, 1) if H2_RE.match(ln)), None)
        if first_h2 is None or first_h2[1].strip() != MAP_HEADING:
            got = first_h2[1].strip() if first_h2 else "(无 H2)"
            errors.append(f"{rel} 第一个 H2 应为「{MAP_HEADING}」,实际是「{got}」")
    return errors


def check_section_naming():
    """H. H3-H6 小节标题禁止数字编号开头(小节标题是稳定语义 ID)。"""
    errors = []
    for path in interview_files():
        rel = os.path.relpath(path, ROOT)
        for lineno, line in enumerate(strip_code(read(path)), 1):
            if NUM_SECTION_RE.match(line):
                errors.append(f"{rel}:{lineno} 「{line.strip()}」(位置型编号,应改用问法式语义标题)")
    return errors


def check_meta_line():
    """I. 元数据行出现即校验格式: 频次 ★~★★★★★ · 难度 🟢🟡🔴 · 高频：公司/公司。"""
    errors = []
    for path in meta_files():
        rel = os.path.relpath(path, ROOT)
        for lineno, line in enumerate(strip_code(read(path)), 1):
            s = line.strip()
            if not META_TRIGGER_RE.match(s):
                continue
            parts = s.split(" · ")
            order = []
            bad = []
            for p in parts:
                if p.startswith("频次"):
                    order.append("频次")
                    if not META_PARTS[0][1].match(p):
                        bad.append(f"「{p}」应为「频次 ★」~「频次 ★★★★★」")
                elif p.startswith("难度"):
                    order.append("难度")
                    if not META_PARTS[1][1].match(p):
                        bad.append(f"「{p}」难度只能用 🟢/🟡/🔴")
                elif p.startswith("高频"):
                    order.append("高频")
                    names = re.sub(r"^高频[：:]", "", p)
                    unknown = [n for n in names.split("/") if n and n not in COMPANIES]
                    if names == p or unknown:
                        bad.append(f"「{p}」公司需在约定清单内(CLAUDE.md),未识别: {unknown or '缺冒号'}")
                else:
                    bad.append(f"「{p}」不是合法段(频次/难度/高频)")
            expected = [k for k, _ in META_PARTS if k in order]
            if order != expected:
                bad.append(f"段序应为 频次 → 难度 → 高频,实际 {' → '.join(order)}")
            for b in bad:
                errors.append(f"{rel}:{lineno} {b}")
    return errors


WIKILINK_ANCHOR_RE = re.compile(r"(?<!!)\[\[([^\]|#]+)#([^\]|]+)(?:\|[^\]]*)?\]\]")


def heading_slugs(path):
    """该文件所有 H1-H6 标题的 slug 集合(含重复后缀),与 Quartz 同源。"""
    texts = [t for _ln, _lvl, t in parse_headings(path)]
    return {s for _t, s in slugify_headings(texts)}


def check_anchor_links(by_name):
    """M. 锚点死链:#锚点 经 slugify 后须 ∈ 目标文件标题 slug 集合(归一化匹配)。

    归一化(双端 slugify)镜像 Quartz 宽松行为:链接里写 `#18-字典树Trie`(大写 T)
    经 slugify → `18-字典树trie`,与目标标题 slug 一致即放过,不误报大小写笔误。
    文件不存在/歧义由 A 项兜底,此处跳过。
    """
    errors = []
    slug_cache = {}

    def slugs_of(p):
        if p not in slug_cache:
            slug_cache[p] = heading_slugs(p)
        return slug_cache[p]

    for path in all_md_files():
        rel = os.path.relpath(path, ROOT)
        self_slugs = slugs_of(path)
        for lineno, line in enumerate(strip_code(read(path)), 1):
            # 标准链接 ](target) —— target 形如 file.md#锚点 / #锚点(页内)
            for m in LINK_RE.finditer(line):
                target = m.group(1)
                if target.startswith(("http://", "https://", "mailto:")) or "#" not in target:
                    continue
                file_part, anchor = target.split("#", 1)
                anchor = anchor.strip()
                if file_part == "":
                    if slugify(anchor) not in self_slugs and anchor not in self_slugs:
                        errors.append(f"{rel}:{lineno} -> #{anchor} (页内锚点非本文标题 slug)")
                    continue
                if "/" in file_part:
                    resolved = os.path.normpath(os.path.join(CONTENT, file_part.lstrip("/")))
                    if not os.path.isfile(resolved):
                        continue
                    tgt_slugs = slugs_of(resolved)
                else:
                    hits = by_name.get(os.path.basename(file_part), [])
                    if len(hits) != 1:
                        continue
                    tgt_slugs = slugs_of(os.path.join(CONTENT, hits[0]))
                if slugify(anchor) not in tgt_slugs and anchor not in tgt_slugs:
                    errors.append(f"{rel}:{lineno} -> {target} (锚点非目标文件任何标题 slug)")
            # 双链 [[file#锚点]](带 # 才校验,纯文件名由 A 项兜底)
            for m in WIKILINK_ANCHOR_RE.finditer(line):
                file_part, anchor = m.group(1), m.group(2).strip()
                hits = by_name.get(file_part + ".md", [])
                if len(hits) != 1:
                    continue
                tgt_slugs = slugs_of(os.path.join(CONTENT, hits[0]))
                if slugify(anchor) not in tgt_slugs and anchor not in tgt_slugs:
                    errors.append(f"{rel}:{lineno} -> [[{file_part}#{anchor}]] (锚点非目标文件任何标题 slug)")
    return errors


def main():
    by_name = build_name_map()
    actual = actual_categories()
    hub = parse_hub_table()
    checks = [
        ("A. 死链(Quartz shortest 语义)", check_dead_links(by_name)),
        ("B. 文件名全库唯一", check_unique_names(by_name)),
        ("C. 命名规范(无位置型数字前缀)", check_naming()),
        ("D. 文件集一致(实际==枢纽)", check_file_set(actual, hub)),
        ("E. 分类一致(目录==枢纽)", check_category(actual, hub)),
        ("F. 题解归属(topics: frontmatter 指向存在的套路页)", check_orphan_solutions()),
        ("G. 追问地图置顶(无章号)", check_map_on_top()),
        ("H. 小节标题无编号(标题是稳定语义 ID)", check_section_naming()),
        ("I. 元数据行格式(频次/难度/高频)", check_meta_line()),
        ("J. 关联题欠链(提到已收录题号必须链接)", check_related_links()),
        ("K. 元数据一致(题解权威源==索引视图)", check_algo_meta_sync()),
        ("L. 题解结构(H1/元数据行/固定小节)", check_solution_structure()),
        ("M. 锚点死链(归一化匹配)", check_anchor_links(by_name)),
        ("N. 高频表一致(题解权威源==高频索引 A 表)", check_hot_meta_sync()),
        ("O. 关系类型(关联题条目带白名单前缀)", check_relation_types()),
        ("P. 概念成色(入链覆盖 ≥2 个内容域)", check_concept_maturity()),
        ("Q. 概念视图(出现在哪里与真实入链一致)", check_concept_view()),
        ("R. 套路视图(已解题目与 topics: frontmatter 一致)", check_topic_view()),
    ]
    failed = False
    for name, errors in checks:
        if errors:
            failed = True
            print(f"✗ {name}: {len(errors)} 处问题")
            for e in errors:
                print(f"    - {e}")
        else:
            print(f"✓ {name}")
    if failed:
        print("\n索引校验未通过。")
        return 1
    print("\n索引校验通过 ✓")
    return 0


if __name__ == "__main__":
    sys.exit(main())

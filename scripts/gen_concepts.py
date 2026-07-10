#!/usr/bin/env python3
"""生成概念页的「出现在哪里」清单 —— 纯标准库。

用法:
    python3 scripts/gen_concepts.py            # 就地刷新所有概念页
    python3 scripts/gen_concepts.py --check    # 只检查是否需要刷新(CI 用,退出码 1 = 有漂移)

为什么需要本脚本(而不是直接用 Quartz 的 backlinks):
    Quartz 的 crawl-links 在存边时把锚点丢掉了(transformer.ts: `_destAnchor` 未使用),
    backlinks 组件按文件 slug 匹配。所以站点上只能看到「谁引用了树.md」,
    看不到「谁引用了树.md#b-树」。概念页一旦聚合(树 = 二叉树+平衡树+B/B+树),
    文件级反链就失去分辨率 —— 而「下溯到具体子概念」正是概念页存在的理由。

    本脚本自己解析 markdown,保留锚点,按子概念分组。权威源是各篇目正文里的链接,
    概念页的清单是它的视图 —— 与 gen_index.py / check_index.py K,N 项同一套路:
    视图永远生成,绝不手写(手写的成员列表最终都会变成漂移的 A 表)。
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import slugify, slugify_headings
from outline import parse_headings

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
CONCEPTS = os.path.join(CONTENT, "概念")

BEGIN = "<!-- gen:refs:begin 由 scripts/gen_concepts.py 生成，勿手编 -->"
END = "<!-- gen:refs:end -->"

LINK_RE = re.compile(r"\[[^\]]*\]\(([^)]+)\)")
WIKILINK_RE = re.compile(r"(?<!!)\[\[([^\]|]+)(?:\|[^\]]*)?\]\]")
FENCE_RE = re.compile(r"^(```|~~~)")
CODE_SPAN_RE = re.compile(r"`[^`]*`")
# 一级域 = interview 的分类目录(Java/数据库/分布式与架构/…) + 「算法」。
#
# 粒度必须细到 interview 的分类,不能把整个 interview 压成一个「八股」域:
# 否则判据变成「概念必须同时出现在算法和八股两侧」,而 CAP/Raft/幂等/一致性哈希
# 这类纯工程概念根本不会出现在算法题里,会被误判为「未成体系」——判据反过来
# 阻止了它想促成的抽取。
#
# 导航域(索引页/首页/概念页互链)不算跨域证据,否则自己链一下就凑够两个域。
NAV_DOMAINS = {"索引", "首页", "概念"}
# 「算法」不计入概念的入场券(校验 P)。算法侧有自己的概念层 —— algorithms/ 的 13 个
# 套路页与 概念/ 同构(原子 → 抽共性 → 反向视图),让 概念/ 去覆盖算法等于在已有概念
# 层上再盖一层。算法 → 概念 的单向链接仍然保留并展示在「出现在哪里」里,只是不计域:
# 「可以链」和「必须链」是两回事。见 CLAUDE.md 的「分域原则」。
UNCOUNTED_DOMAINS = NAV_DOMAINS | {"算法"}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def strip_code(text):
    """剥离围栏代码块与行内代码(与 check_index.py 同口径)。"""
    out, in_fence = [], False
    for line in text.splitlines():
        if FENCE_RE.match(line.strip()):
            in_fence = not in_fence
            out.append("")
            continue
        out.append("" if in_fence else CODE_SPAN_RE.sub("", line))
    return out


def all_md():
    for root, _dirs, files in os.walk(CONTENT):
        for f in sorted(files):
            if f.endswith(".md"):
                yield os.path.join(root, f)


def page_title(path):
    """页面显示名:优先 H1,回退文件名。"""
    for _lineno, level, text in parse_headings(path):
        if level == 1:
            return text
    return os.path.splitext(os.path.basename(path))[0]


def domain_of(path):
    parts = os.path.relpath(path, CONTENT).replace(os.sep, "/").split("/")
    head = parts[0]
    if head.endswith(".md"):
        return "首页"  # content 根下的散页(index.md)
    if head == "interview":
        return parts[1] if len(parts) > 2 else "面试"  # 分类目录即一级域
    if head == "algorithms":
        return "算法"
    if head == "indexes":
        return "索引"
    return head  # 概念/


def is_content_domain(dom):
    """是否计入概念入场券的「内容域」—— 即 interview/ 的 8 个分类。

    导航域(索引/首页/概念)不计,否则自己链一下就凑够两个域;算法域也不计,理由见
    UNCOUNTED_DOMAINS 上方的注释。
    """
    return dom not in UNCOUNTED_DOMAINS


def concept_files():
    if not os.path.isdir(CONCEPTS):
        return
    for f in sorted(os.listdir(CONCEPTS)):
        if f.endswith(".md"):
            yield os.path.join(CONCEPTS, f)


def collect_refs(concept_basenames):
    """反扫全库 -> {概念文件名: {锚点 or "": [(展示名, 源文件名, 一级域), ...]}}"""
    refs = {b: {} for b in concept_basenames}
    for path in all_md():
        base = os.path.basename(path)
        if base in concept_basenames:
            continue  # 概念页之间的互链不计入自己的清单
        targets = []
        for line in strip_code(read(path)):
            for m in LINK_RE.finditer(line):
                targets.append(m.group(1).strip())
            for m in WIKILINK_RE.finditer(line):
                t = m.group(1).strip()
                targets.append(t if "#" in t else t + ".md")
        for t in targets:
            if t.startswith(("http://", "https://", "mailto:")):
                continue
            file_part, _, anchor = t.partition("#")
            file_part = os.path.basename(file_part) or ""
            if not file_part.endswith(".md"):
                file_part += ".md"
            if file_part not in refs:
                continue
            entry = (page_title(path), base, domain_of(path))
            bucket = refs[file_part].setdefault(anchor, [])
            if entry not in bucket:
                bucket.append(entry)
    return refs


def render(concept_path, by_anchor):
    """按概念页自身的 H2 顺序渲染清单;未命中任何锚点的归入「整页引用」。"""
    headings = [(lvl, text) for _ln, lvl, text in parse_headings(concept_path)]
    h2_slugs = slugify_headings([t for lvl, t in headings if lvl == 2])
    order = [(text, slug) for text, slug in h2_slugs if slug not in ("出现在哪里",)]

    lines = []
    for text, slug in order:
        hits = by_anchor.get(slug, [])
        if not hits:
            continue
        lines.append(f"**[{text}](#{slug})**")
        lines.append("")
        for title, base, dom in sorted(hits, key=lambda e: (e[2], e[0])):
            lines.append(f"- [{title}]({base}) · {dom}")
        lines.append("")

    whole = by_anchor.get("", [])
    if whole:
        lines.append("**整页引用**")
        lines.append("")
        for title, base, dom in sorted(whole, key=lambda e: (e[2], e[0])):
            lines.append(f"- [{title}]({base}) · {dom}")
        lines.append("")

    if not lines:
        lines = ["_暂无引用 —— 概念页入链为 0，说明它尚未成体系。_", ""]
    return "\n".join(lines).rstrip("\n")


def splice(text, body):
    i, j = text.find(BEGIN), text.find(END)
    if i == -1 or j == -1:
        raise SystemExit(f"缺少 {BEGIN} / {END} 标记")
    return text[: i + len(BEGIN)] + "\n" + body + "\n" + text[j:]


def main():
    check_only = "--check" in sys.argv
    concepts = list(concept_files())
    if not concepts:
        print("content/概念/ 下没有概念页")
        return 0
    refs = collect_refs({os.path.basename(p) for p in concepts})

    drift = []
    for path in concepts:
        old = read(path)
        new = splice(old, render(path, refs[os.path.basename(path)]))
        if new == old:
            continue
        if check_only:
            drift.append(os.path.relpath(path, ROOT))
        else:
            with open(path, "w", encoding="utf-8") as f:
                f.write(new)
            print(f"刷新 {os.path.relpath(path, ROOT)}")

    if check_only and drift:
        for d in drift:
            print(f"✗ {d} 的「出现在哪里」已过期，请跑 python3 scripts/gen_concepts.py")
        return 1

    # 概念成色:内容域(interview 的 8 个分类)入链 < 2 个 -> 尚未成体系。
    # 导航域(索引/首页)与算法域都不计,但仍展示在「出现在哪里」里,只是不算入场券。
    for path in concepts:
        doms = {dom for hits in refs[os.path.basename(path)].values() for _t, _b, dom in hits}
        content_doms = {d for d in doms if is_content_domain(d)}
        name = os.path.basename(path)
        mark = "✓" if len(content_doms) >= 2 else "!"
        other = sorted(d for d in doms if not is_content_domain(d))
        tail = f"  (不计域的入链:{'/'.join(other)})" if other else ""
        print(f"  {mark} {name:<12} 内容域 {len(content_doms)} 个 {sorted(content_doms)}{tail}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

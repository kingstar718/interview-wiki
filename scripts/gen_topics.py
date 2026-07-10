#!/usr/bin/env python3
"""生成算法套路节点的「已解题目」清单 —— 纯标准库。

用法:
    python3 scripts/gen_topics.py            # 刷新所有套路页 + indexes/算法题索引.md
    python3 scripts/gen_topics.py --check    # 只检查是否需要刷新(CI 用,退出码 1 = 有漂移)

两份产物:
    algorithms/<套路>.md 的 `## 已解题目`   —— 标记块内生成
    indexes/算法题索引.md                   —— 整篇生成(套路 → 技术词 → 题目双链)

为什么需要本脚本:
    `algorithms/problems/` 是扁平题目池,题目归属哪个套路不再由目录体现,
    而是题解 frontmatter 的 `topics:` 字段(权威源,可多值 —— 一题可能同时属于
    「数组与字符串」和「双指针与滑动窗口」)。套路页的 `## 已解题目` 是这份
    frontmatter 的视图,同 gen_concepts.py 的原则:视图永远生成,绝不手写。

    与 gen_concepts.py 的反链扫描不同,这里是正向分组(frontmatter 直接声明归属,
    不需要从正文链接里反推),所以更简单 —— 不涉及锚点分组。

两级归属:
    topics:     粗套路(13 个),决定题目出现在哪个套路页
    techniques: 细技术词(44 个),决定题目在套路页里落到哪个分组

    白名单的权威源是**套路页自己的 frontmatter `techniques:`**,13 页的并集
    即全局词表。题解只能用所属套路声明过的词(严格模式,见 check_index 校验 S)。

    迁移期:某个套路页下还没有任何题解标注 techniques 时,退回平铺列表输出,
    与旧行为一致 —— 所以可以按套路逐个回填,不必一次改完 159 篇。
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from outline import parse_headings
from slug import slugify

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
ALGORITHMS = os.path.join(CONTENT, "algorithms")
PROBLEMS = os.path.join(ALGORITHMS, "problems")

ALGO_INDEX = os.path.join(CONTENT, "indexes", "算法题索引.md")

BEGIN = "<!-- gen:problems:begin 由 scripts/gen_topics.py 生成，勿手编 -->"
END = "<!-- gen:problems:end -->"
UNTAGGED = "未标注技术"

FRONTMATTER_RE = re.compile(r"^---\n(.*?\n)---\n", re.DOTALL)
LIST_ITEM_RE = re.compile(r"^\s+-\s*(.+?)\s*$")
NUM_RE = re.compile(r"^(\d+|offer\d+)-")
META_TRIGGER_RE = re.compile(r"^(频次 |难度 )")
FENCE_RE = re.compile(r"^(```|~~~)")


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def parse_list(text, key):
    """frontmatter 里 `key:` 下的 YAML 列表 -> [str, ...]。无 frontmatter 或无该键返回 []。"""
    m = FRONTMATTER_RE.match(text)
    if not m:
        return []
    items = []
    in_key = False
    for line in m.group(1).splitlines():
        if line.startswith(key + ":"):
            in_key = True
            continue
        if in_key:
            im = LIST_ITEM_RE.match(line)
            if im:
                items.append(im.group(1))
                continue
            break  # 缩进列表结束(遇到下一个顶层 key)
    return items


def parse_topics(text):
    return parse_list(text, "topics")


def parse_techniques(text):
    return parse_list(text, "techniques")


def pattern_files():
    """content/algorithms/ 下直属的套路页(不含 problems/ 与导航页 README.md)。"""
    for f in sorted(os.listdir(ALGORITHMS)):
        path = os.path.join(ALGORITHMS, f)
        if os.path.isfile(path) and f.endswith(".md") and f != "README.md":
            yield path


def problem_files():
    if not os.path.isdir(PROBLEMS):
        return
    for f in sorted(os.listdir(PROBLEMS)):
        if f.endswith(".md"):
            yield os.path.join(PROBLEMS, f)


def whitelist():
    """{套路名: [技术词, ...]},顺序即套路页 frontmatter 的声明顺序(= 分组展示顺序)。"""
    return {
        os.path.splitext(os.path.basename(p))[0]: parse_techniques(read(p))
        for p in pattern_files()
    }


def sort_key(base):
    m = NUM_RE.match(base)
    if not m:
        return (1, base)
    n = m.group(1)
    return (0, int(n[5:]) if n.startswith("offer") else int(n))


def h1_title(path):
    for _lineno, level, text in parse_headings(path):
        if level == 1:
            return text.split("（")[0].strip()
    return os.path.splitext(os.path.basename(path))[0]


def solution_meta(path):
    """题解元数据行 -> (频次, 难度, 公司列表) 或 None。公司保持书写顺序(主考公司在前)。

    题解的元数据行是难度/频次/公司的**权威源**,算法题索引是它的视图。
    check_index.py 复用本函数,避免两套解析漂移。
    """
    in_fence = False
    lines = []
    text = read(path)
    m = FRONTMATTER_RE.match(text)
    body = text[m.end():] if m else text
    for line in body.splitlines():
        if FENCE_RE.match(line.strip()):
            in_fence = not in_fence
            continue
        if not in_fence:
            lines.append(line)
        if len(lines) > 6:
            break
    for line in lines[:6]:
        s = line.strip()
        if META_TRIGGER_RE.match(s):
            stars = re.search(r"频次 (★{1,5})", s)
            diff = re.search(r"难度 ([🟢🟡🔴])", s)
            comp = re.search(r"高频[：:]([^ ·]+)", s)
            return (
                stars.group(1) if stars else None,
                diff.group(1) if diff else None,
                comp.group(1).split("/") if comp else [],
            )
    return None


ONE_LINER_RE = re.compile(r"^>\s*\*\*一句话\*\*[：:]\s*(.+?)\s*$", re.M)

# 套路的教学顺序(由易到难、由具体到抽象),索引与速查表都按它排。
# 文件名字典序(二分查找会排到最前)对读者毫无意义,所以顺序必须显式表达。
# 新增套路页要在这里登记,否则 render_index 会报错 —— 与 quartz.ts 的 ORDER 同理。
PATTERN_ORDER = [
    "数组与字符串", "链表", "栈与队列", "哈希表",
    "双指针与滑动窗口", "二分查找", "二叉树", "图论",
    "回溯", "动态规划与贪心", "排序与堆", "字典树", "位运算",
]


def ordered_patterns(wl):
    missing = set(wl) - set(PATTERN_ORDER)
    if missing:
        raise SystemExit(f"套路页 {sorted(missing)} 未在 gen_topics.PATTERN_ORDER 登记")
    return [n for n in PATTERN_ORDER if n in wl]


def pattern_one_liner(path):
    m = ONE_LINER_RE.search(read(path))
    return m.group(1) if m else ""


def render_index(groups, wl):
    """整篇生成 indexes/算法题索引.md —— 三级结构:套路 → 技术词 → 题目。

    这个文件里没有一个字节是原创信息:分组来自题解的 topics/techniques,
    难度/频次/公司来自题解元数据行。所以它必须是生成物,不是手写件。
    """
    total = len(list(problem_files()))
    order = ordered_patterns(wl)
    out = [
        "# 算法题索引",
        "",
        "> **本页由 `scripts/gen_topics.py` 生成，勿手编。** 改题解的 `topics:`/`techniques:`/元数据行后跑一次脚本即可。",
        ">",
        "> 三级结构:**套路**(为什么这些题是同一类) → **技术词**(具体用哪一招) → **题目**。",
        "> 一题可以横跨多个套路(如「接雨水」同时是数组、双指针、单调栈),各处指向的是同一份物理题解。",
        ">",
        "> 频次:★★★★★ 几乎必考 / ★★★★ 高频 / ★★★ 常见 / ★★ 偶考。难度:🟢 易 / 🟡 中 / 🔴 难。",
        "> 难度/频次/公司以**题解元数据行**为权威源,本页是它的视图。公司标注为大致倾向,以实际面试为准。",
        ">",
        "> 相关:[高频题目索引](高频题目索引.md)(按热度排序,跨套路)。",
        "",
        "## 怎么用这份索引",
        "",
        "1. **按套路刷,不按题号刷**。每个套路先做代表题建立模式,再横向扩展变式——套路页里「为什么这些题是同一个套路」才是主线。",
        "2. **技术词是套路内部的分工**。同一个套路下的技术词彼此不可替代(如双指针下的「对撞指针」与「滑动窗口」),搞混了就会硬套模板。",
        "3. **高频题(★★★★★)优先**,见 [高频算法题 Top 40](高频题目索引.md#a-高频算法题-top-40)。",
        "4. 每题留 Java 解 + 复杂度 + 一句变式;难题写关联题互链,`## 关联题` 是知识图谱的边。",
        "5. 推荐顺序:数组/链表/二叉树 → 双指针滑窗/二分/哈希表 → 回溯/动态规划 → 图论/栈与队列 → 排序与堆/字典树/位运算。",
        "",
        "## 套路速查",
        "",
        f"共 {len(wl)} 个套路、{len({t for v in wl.values() for t in v})} 个技术词、{total} 篇题解。",
        "",
        "| 套路 | 题数 | 技术词 |",
        "|---|---|---|",
    ]
    # 技术词在速查表里不做锚点链接:同一个词可被多页声明(`回溯框架` 同属二叉树与回溯),
    # 本页会出现两个同名 H3,锚点必然撞车。列纯文本,读者往下翻即可。
    for name in order:
        g = groups[name]
        cnt = len({e[1] for items in g.values() for e in items})
        techs = " / ".join(t for t in wl[name] if g.get(t))
        out.append(f"| [{name}]({name}.md) | {cnt} | {techs} |")
    out.append("")
    out.append("---")

    for name in order:
        g = groups[name]
        out += ["", f"## {name}", ""]
        one = pattern_one_liner(os.path.join(ALGORITHMS, name + ".md"))
        if one:
            out += [f"> {one}", "", f"详解见 [{name}]({name}.md)。", ""]
        for t in wl[name]:
            items = g.get(t)
            if not items:
                continue
            out += [f"### {t}", ""]
            for _k, base, title in items:
                stem = os.path.splitext(base)[0]
                meta = solution_meta(os.path.join(PROBLEMS, base))
                tail = ""
                if meta:
                    stars, diff, comps = meta
                    parts = [x for x in (diff, stars, "/".join(comps)) if x]
                    tail = " · " + " · ".join(parts)
                out.append(f"- [[{stem}|{title}]]{tail}")
            out.append("")
    return "\n".join(out).rstrip() + "\n"



def collect_groups(pattern_names):
    """{套路名: {技术词 | UNTAGGED: [(sort_key, base, title), ...]}},未标注的题落到 UNTAGGED。"""
    wl = whitelist()
    groups = {name: {} for name in pattern_names}
    orphans = []
    for path in problem_files():
        base = os.path.basename(path)
        text = read(path)
        topics = parse_topics(text)
        if not topics:
            orphans.append(base)
            continue
        techs = parse_techniques(text)
        title = h1_title(path)
        entry = (sort_key(base), base, title)
        for t in topics:
            if t not in groups:
                orphans.append(f"{base} (未知套路「{t}」)")
                continue
            # 该题在本套路下的技术词 = 交集(严格模式:词必须由本套路声明)
            mine = [x for x in wl.get(t, []) if x in techs]
            for x in mine or [UNTAGGED]:
                groups[t].setdefault(x, []).append(entry)
    for name in groups:
        for x in groups[name]:
            groups[name][x].sort(key=lambda e: e[0])
    return groups, orphans


def render(name, group, wl=None):
    """迁移期:本套路只要还有题没标注 techniques,就整页退回平铺列表(旧行为)。

    全标注完才切换成分组显示 —— 否则页面会变成「几个小组 + 一大坨未标注」,
    比平铺还难读。这条规则让回填可以按套路逐个推进,不影响其余套路页。
    """
    if wl is None:
        wl = whitelist()
    if not group:
        return "_暂无题解。_"

    def lines_of(items):
        return "\n".join(f"- [{title}]({base})" for _k, base, title in items)

    if group.get(UNTAGGED):
        flat = sorted({e for items in group.values() for e in items}, key=lambda e: e[0])
        return lines_of(flat)

    return "\n\n".join(
        f"### {t}\n\n{lines_of(group[t])}" for t in wl.get(name, []) if group.get(t)
    )


def audit_vocab(groups, wl):
    """词表成色审计(只提示,不拦)。

    历史:曾想做成硬校验「每词 ≥N 题」。实践证明阈值是错的约束 —— N=3 会逼出
    `快速选择`→`堆TopK`、`BST中序`→`递归返回值设计` 两个语义上就错的合并;
    降到 N=2 后,`快速选择` 自己又只剩 1 题。词表条数不是成本(回填才是),
    拿条数下限去卡语义精确是拿错了东西当约束。所以只打印,由人判断。
    """
    cov = {}
    for name, g in groups.items():
        for t, items in g.items():
            if t != UNTAGGED:
                cov.setdefault(t, set()).update(e[1] for e in items)
    thin = sorted(t for t, s in cov.items() if len(s) <= 1)
    if thin:
        print(f"\n  ⚠ 仅覆盖 1 题的技术词 {len(thin)} 个(不阻断,确认语义是否立得住):")
        for t in thin:
            print(f"      {t}")


def splice(text, body):
    i, j = text.find(BEGIN), text.find(END)
    if i == -1 or j == -1:
        raise SystemExit(f"缺少 {BEGIN} / {END} 标记")
    return text[: i + len(BEGIN)] + "\n" + body + "\n" + text[j:]


def main():
    check_only = "--check" in sys.argv
    patterns = list(pattern_files())
    if not patterns:
        print("content/algorithms/ 下没有套路页")
        return 0
    names = {os.path.splitext(os.path.basename(p))[0] for p in patterns}
    wl = whitelist()
    groups, orphans = collect_groups(names)

    drift = []
    for path in patterns:
        name = os.path.splitext(os.path.basename(path))[0]
        old = read(path)
        try:
            new = splice(old, render(name, groups[name], wl))
        except SystemExit as e:
            print(f"✗ {os.path.relpath(path, ROOT)}: {e}")
            return 1
        if new == old:
            continue
        if check_only:
            drift.append(os.path.relpath(path, ROOT))
        else:
            with open(path, "w", encoding="utf-8") as f:
                f.write(new)
            print(f"刷新 {os.path.relpath(path, ROOT)}")

    # 算法题索引:整篇生成(同 gen_index.py 的知识点索引),不留手写余地
    new_index = render_index(groups, wl)
    if read(ALGO_INDEX) != new_index:
        if check_only:
            drift.append(os.path.relpath(ALGO_INDEX, ROOT))
        else:
            with open(ALGO_INDEX, "w", encoding="utf-8") as f:
                f.write(new_index)
            print(f"刷新 {os.path.relpath(ALGO_INDEX, ROOT)}")

    if check_only and drift:
        for d in drift:
            print(f"✗ {d} 已过期，请跑 python3 scripts/gen_topics.py")
        return 1

    # 全局词表 = 13 页声明的并集(同一个词可被多页声明,如 `对撞指针` 同属数组与双指针)
    print(f"\n词表 {len({t for v in wl.values() for t in v})} 词 / {len(names)} 个套路")
    audit_vocab(groups, wl)
    for name in sorted(names):
        g = groups[name]
        total = len({e[1] for items in g.values() for e in items})
        untagged = len(g.get(UNTAGGED, []))
        mark = "分组显示" if untagged == 0 else f"平铺(已标注 {total - untagged}/{total})"
        print(f"  {name:<12} {total:>3} 题  {mark}")
    if orphans:
        print(f"\n未归入任何套路页的题解 {len(orphans)} 个:")
        for o in orphans:
            print("   ", o)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())

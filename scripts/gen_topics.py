#!/usr/bin/env python3
"""生成算法套路节点的「已解题目」清单 —— 纯标准库。

用法:
    python3 scripts/gen_topics.py            # 就地刷新所有套路页
    python3 scripts/gen_topics.py --check    # 只检查是否需要刷新(CI 用,退出码 1 = 有漂移)

为什么需要本脚本:
    `algorithms/problems/` 是扁平题目池,题目归属哪个套路不再由目录体现,
    而是题解 frontmatter 的 `topics:` 字段(权威源,可多值 —— 一题可能同时属于
    「数组与字符串」和「双指针与滑动窗口」)。套路页的 `## 已解题目` 是这份
    frontmatter 的视图,同 gen_concepts.py 的原则:视图永远生成,绝不手写。

    与 gen_concepts.py 的反链扫描不同,这里是正向分组(frontmatter 直接声明归属,
    不需要从正文链接里反推),所以更简单 —— 不涉及锚点分组。

两级归属:
    topics:     粗套路(13 个),决定题目出现在哪个套路页
    techniques: 细技术词(35 个),决定题目在套路页里落到哪个分组

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

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
ALGORITHMS = os.path.join(CONTENT, "algorithms")
PROBLEMS = os.path.join(ALGORITHMS, "problems")

BEGIN = "<!-- gen:problems:begin 由 scripts/gen_topics.py 生成，勿手编 -->"
END = "<!-- gen:problems:end -->"
UNTAGGED = "未标注技术"

FRONTMATTER_RE = re.compile(r"^---\n(.*?\n)---\n", re.DOTALL)
LIST_ITEM_RE = re.compile(r"^\s+-\s*(.+?)\s*$")
NUM_RE = re.compile(r"^(\d+|offer\d+)-")


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

    if check_only and drift:
        for d in drift:
            print(f"✗ {d} 的「已解题目」已过期，请跑 python3 scripts/gen_topics.py")
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

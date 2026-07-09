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

FRONTMATTER_RE = re.compile(r"^---\n(.*?\n)---\n", re.DOTALL)
TOPICS_ITEM_RE = re.compile(r"^\s*-\s*(.+?)\s*$")
NUM_RE = re.compile(r"^(\d+|offer\d+)-")


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def parse_topics(text):
    """题解 frontmatter 的 topics: 列表 -> [str, ...]。无 frontmatter 或无 topics 返回 []。"""
    m = FRONTMATTER_RE.match(text)
    if not m:
        return []
    lines = m.group(1).splitlines()
    topics = []
    in_topics = False
    for line in lines:
        if line.startswith("topics:"):
            in_topics = True
            continue
        if in_topics:
            im = TOPICS_ITEM_RE.match(line)
            if im:
                topics.append(im.group(1))
                continue
            break  # 缩进列表结束(遇到下一个顶层 key 或空行外的内容)
    return topics


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
    """{套路名: [(sort_key, base, title), ...]}"""
    groups = {name: [] for name in pattern_names}
    orphans = []
    for path in problem_files():
        base = os.path.basename(path)
        topics = parse_topics(read(path))
        if not topics:
            orphans.append(base)
            continue
        title = h1_title(path)
        for t in topics:
            if t not in groups:
                orphans.append(f"{base} (未知套路「{t}」)")
                continue
            groups[t].append((sort_key(base), base, title))
    for name in groups:
        groups[name].sort(key=lambda e: e[0])
    return groups, orphans


def render(items):
    if not items:
        return "_暂无题解。_"
    lines = [f"- [{title}]({base})" for _k, base, title in items]
    return "\n".join(lines)


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
    groups, orphans = collect_groups(names)

    drift = []
    for path in patterns:
        name = os.path.splitext(os.path.basename(path))[0]
        old = read(path)
        try:
            new = splice(old, render(groups[name]))
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

    for name in sorted(names):
        print(f"  {name:<12} {len(groups[name])} 题")
    if orphans:
        print(f"\n未归入任何套路页的题解 {len(orphans)} 个:")
        for o in orphans:
            print("   ", o)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())

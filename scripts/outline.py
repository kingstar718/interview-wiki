#!/usr/bin/env python3
"""定位 interview-wiki 内容 —— 纯标准库,改前先跑,不用通读千行文件。

用法:
    python3 scripts/outline.py Redis            # 打印文件名含 "Redis" 的专题标题树+行号
    python3 scripts/outline.py --grep 缓存预热   # 全库定位考点: 标题命中 + 正文命中(标注所在小节)
    python3 scripts/outline.py --grep -t 预热    # 只搜标题,不搜正文

设计前提: 小节标题是稳定语义 ID(规范见 CONTRIBUTING.md),定位到标题即定位到考点。
本脚本即时生成不落盘 —— 任何需要手工同步的清单最终都会烂掉。

典型工作流(对应 TODO.md 的「动手前先验证考点是否已覆盖」):
    1. outline.py --grep <考点词>  确认是否已覆盖、覆盖在哪
    2. outline.py <目标文件>       看章节结构,决定新小节插入位置
    3. 写完跑 check_index.py       校验
"""
import argparse
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")

HEADING_RE = re.compile(r"^(#{1,6})\s+(.*)")
FENCE_RE = re.compile(r"^(```|~~~)")


def all_md_files():
    for root, _dirs, files in os.walk(CONTENT):
        for f in sorted(files):
            if f.endswith(".md"):
                yield os.path.join(root, f)


def parse_headings(path):
    """[(lineno, level, text), ...] —— 跳过围栏代码块里的 # 注释行。"""
    headings = []
    in_fence = False
    with open(path, encoding="utf-8") as fh:
        for lineno, line in enumerate(fh, 1):
            if FENCE_RE.match(line.strip()):
                in_fence = not in_fence
                continue
            if in_fence:
                continue
            m = HEADING_RE.match(line)
            if m:
                headings.append((lineno, len(m.group(1)), m.group(2).strip()))
    return headings


def show_outline(keyword):
    """打印文件名(不含扩展名)包含 keyword 的所有文件的标题树。"""
    matched = [
        p for p in all_md_files()
        if keyword.lower() in os.path.splitext(os.path.basename(p))[0].lower()
    ]
    if not matched:
        print(f"没有文件名匹配「{keyword}」的 .md 文件")
        return 1
    for path in matched:
        rel = os.path.relpath(path, ROOT)
        print(f"\n{rel}")
        for lineno, level, text in parse_headings(path):
            indent = "  " * (level - 1)
            print(f"  L{lineno:<5}{indent}{text}")
    return 0


def grep(keyword, title_only):
    """全库搜标题与正文;正文命中标注其所在小节(最近的上级标题)。"""
    heading_hits = []  # (rel, lineno, text)
    body_hits = []     # (rel, lineno, section, line_text)
    for path in all_md_files():
        rel = os.path.relpath(path, ROOT)
        section = "(文件头)"
        in_fence = False
        with open(path, encoding="utf-8") as fh:
            for lineno, line in enumerate(fh, 1):
                stripped = line.strip()
                if FENCE_RE.match(stripped):
                    in_fence = not in_fence
                if not in_fence:
                    m = HEADING_RE.match(line)
                    if m:
                        section = m.group(2).strip()
                        if keyword in section:
                            heading_hits.append((rel, lineno, section))
                        continue
                if title_only or keyword not in line:
                    continue
                body_hits.append((rel, lineno, section, stripped))

    if heading_hits:
        print(f"标题命中 {len(heading_hits)} 处:")
        for rel, lineno, text in heading_hits:
            print(f"  {rel}:{lineno}  {text}")
    if body_hits and not title_only:
        print(f"\n正文命中 {len(body_hits)} 处:")
        for rel, lineno, section, text in body_hits:
            if len(text) > 60:
                text = text[:60] + "…"
            print(f"  {rel}:{lineno}  [{section}]  {text}")
    if not heading_hits and not body_hits:
        print(f"「{keyword}」全库无命中 —— 考点未覆盖,可登记 TODO.md 后动手")
        return 1
    return 0


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("keyword", help="文件名关键词;配合 --grep 时为考点关键词")
    ap.add_argument("--grep", action="store_true", help="全库搜索模式(标题+正文)")
    ap.add_argument("-t", "--title-only", action="store_true", help="--grep 时只搜标题")
    args = ap.parse_args()
    if args.grep:
        return grep(args.keyword, args.title_only)
    return show_outline(args.keyword)


if __name__ == "__main__":
    sys.exit(main())

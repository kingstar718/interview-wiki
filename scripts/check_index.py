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
    - 多段路径链接 `[x](algorithms/01-数组与字符串/README.md)`: 视为从
      content/ 根出发的路径(不是相对当前文件!)

检查项:
    A. 死链      —— 所有 .md 链接与 [[双链]] 都能按上述语义 resolve
    B. 文件名唯一 —— 纯文件名链接方案的前提: 除 README.md/index.md 外,
                    全库 .md 文件名不得重复(重复会导致链接歧义)
    C. 命名规范  —— interview/ 与 indexes/ 下禁止位置型数字前缀 (^数字-)
                    (algorithms/ 例外: 题号/固定专题序号是稳定 ID，允许)
    D. 文件集    —— interview/ 实际文件 == 枢纽「专题文件清单」收录
    E. 分类一致  —— interview/<分类>/ 目录名 == 枢纽清单中该文件的分类名
    F. 无孤儿题解 —— algorithms/<专题>/ 下每个题解文件都必须被本专题 README 链接
                    (防止「有文件却无本地导航入口」的断点)

任一检查失败 -> 退出码 1，可直接接入 CI / pre-commit / AI 改完自检。
"""
import os
import re
import sys
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
HUB = os.path.join(CONTENT, "indexes", "知识点索引.md")
INTERVIEW = os.path.join(CONTENT, "interview")
ALGORITHMS = os.path.join(CONTENT, "algorithms")

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
    """C. interview/ 与 indexes/ 下禁止位置型数字前缀(含分类子目录)。"""
    errors = []
    for folder in ("interview", "indexes"):
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
    """F. algorithms/<专题>/ 下的题解文件必须被本专题 README 链接。"""
    errors = []
    for topic in sorted(os.listdir(ALGORITHMS)):
        tdir = os.path.join(ALGORITHMS, topic)
        readme = os.path.join(tdir, "README.md")
        if not os.path.isdir(tdir) or not os.path.isfile(readme):
            continue
        solutions = {f for f in os.listdir(tdir) if f.endswith(".md") and f != "README.md"}
        if not solutions:
            continue
        linked = {
            os.path.basename(m.group(1).split("#")[0].strip())
            for m in LINK_RE.finditer(read(readme))
        }
        for f in sorted(solutions - linked):
            errors.append(f"algorithms/{topic}/{f} 未被本专题 README 链接(孤儿题解)")
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
        ("F. 无孤儿题解(题解都被专题 README 链接)", check_orphan_solutions()),
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

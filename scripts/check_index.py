#!/usr/bin/env python3
"""校验 interview-wiki 索引一致性 —— 纯标准库，无需构建。

用法:
    python3 scripts/check_index.py      # 在任意目录运行均可，脚本自定位仓库根

设计前提: `_sidebar.md`(docsify 实际读取的导航)是分类的唯一权威源，
其余索引/枢纽文件是它的「视图」。视图与权威源漂移(分类错位、少收录、
死链、位置型数字前缀)人工肉眼难查，故用本脚本一键校验。

检查项:
    A. 死链      —— content/ 下所有 .md 里的相对 .md 链接都能 resolve
    B. 命名规范  —— interview/ 与 indexes/ 下文件名禁止位置型数字前缀 (^数字-)
                    (algorithms/ 例外: 题号/固定专题序号是稳定 ID，允许)
    C. 文件集    —— interview/*.md 实际文件 == 侧栏收录 == 枢纽「专题文件清单」收录
    D. 分类一致  —— 同一 interview 文件在 _sidebar.md 与 社招问题知识点.md 的所属分类相同
    E. 无孤儿题解 —— algorithms/<专题>/ 下每个题解文件都必须被本专题 README 链接
                    (防止「有文件却无本地导航入口」的断点)

任一检查失败 -> 退出码 1，可直接接入 CI / pre-commit / AI 改完自检。
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
SIDEBAR = os.path.join(CONTENT, "_sidebar.md")
HUB = os.path.join(CONTENT, "社招问题知识点.md")
INTERVIEW = os.path.join(CONTENT, "interview")
ALGORITHMS = os.path.join(CONTENT, "algorithms")

LINK_RE = re.compile(r"\[[^\]]*\]\(([^)]+)\)")
NUM_PREFIX_RE = re.compile(r"^\d+[-_]")
SIDEBAR_CAT_RE = re.compile(r"^\s*-\s*\*\*(.+?)\*\*\s*$")
INTERVIEW_LINK_RE = re.compile(r"\]\(interview/([^)#]+\.md)")


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def all_md_files():
    for root, _dirs, files in os.walk(CONTENT):
        for f in files:
            if f.endswith(".md"):
                yield os.path.join(root, f)


def check_dead_links():
    """A. 相对 .md 链接必须指向真实存在的文件。"""
    errors = []
    for path in all_md_files():
        for lineno, line in enumerate(read(path).splitlines(), 1):
            for m in LINK_RE.finditer(line):
                target = m.group(1).split("#")[0].strip()
                if not target or target.startswith(("http://", "https://", "mailto:")):
                    continue
                if not target.endswith(".md"):
                    continue
                resolved = os.path.normpath(os.path.join(os.path.dirname(path), target))
                if not os.path.isfile(resolved):
                    rel = os.path.relpath(path, ROOT)
                    errors.append(f"{rel}:{lineno} -> {target} (目标不存在)")
    return errors


def check_naming():
    """B. interview/ 与 indexes/ 下禁止位置型数字前缀。"""
    errors = []
    for folder in ("interview", "indexes"):
        d = os.path.join(CONTENT, folder)
        if not os.path.isdir(d):
            continue
        for f in sorted(os.listdir(d)):
            if f.endswith(".md") and NUM_PREFIX_RE.match(f):
                errors.append(f"content/{folder}/{f} (位置型数字前缀，应改用语义文件名)")
    return errors


def parse_sidebar():
    """_sidebar.md -> {interview 文件名: 分类名}。粗体条目 **X** 起一个分类。"""
    mapping = {}
    cat = None
    for line in read(SIDEBAR).splitlines():
        h = SIDEBAR_CAT_RE.match(line)
        if h:
            cat = h.group(1).strip()
            continue
        m = INTERVIEW_LINK_RE.search(line)
        if m:
            mapping[m.group(1)] = cat
    return mapping


def parse_hub_table():
    """社招问题知识点.md 底部「专题文件清单」表 -> {interview 文件名: 分类名}。"""
    mapping = {}
    for line in read(HUB).splitlines():
        s = line.strip()
        if not (s.startswith("|") and "interview/" in s):
            continue
        cat = s.strip("|").split("|")[0].strip()
        for m in INTERVIEW_LINK_RE.finditer(s):
            mapping[m.group(1)] = cat
    return mapping


def check_file_set(side, hub):
    """C. 实际文件 / 侧栏 / 枢纽清单 三方文件集一致。"""
    actual = {f for f in os.listdir(INTERVIEW) if f.endswith(".md")}
    side_set, hub_set = set(side), set(hub)
    errors = []
    for name, s in (("侧栏 _sidebar.md", side_set), ("枢纽专题文件清单", hub_set)):
        for f in sorted(actual - s):
            errors.append(f"interview/{f} 存在但未被「{name}」收录")
        for f in sorted(s - actual):
            errors.append(f"「{name}」收录了 interview/{f}，但文件不存在")
    return errors


def check_category(side, hub):
    """D. 同一文件在侧栏与枢纽的分类名必须一致。"""
    errors = []
    for f in sorted(set(side) & set(hub)):
        if side[f] != hub[f]:
            errors.append(f"interview/{f} 分类漂移: 侧栏=「{side[f]}」 vs 枢纽=「{hub[f]}」")
    return errors


def check_orphan_solutions():
    """E. algorithms/<专题>/ 下的题解文件必须被本专题 README 链接。"""
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
            m.group(1).split("#")[0].strip()
            for m in LINK_RE.finditer(read(readme))
        }
        for f in sorted(solutions - linked):
            errors.append(f"algorithms/{topic}/{f} 未被本专题 README 链接(孤儿题解)")
    return errors


def main():
    side = parse_sidebar()
    hub = parse_hub_table()
    checks = [
        ("A. 死链", check_dead_links()),
        ("B. 命名规范(无位置型数字前缀)", check_naming()),
        ("C. 文件集一致(实际==侧栏==枢纽)", check_file_set(side, hub)),
        ("D. 分类一致(侧栏==枢纽)", check_category(side, hub)),
        ("E. 无孤儿题解(题解都被专题 README 链接)", check_orphan_solutions()),
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

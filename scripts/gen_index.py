#!/usr/bin/env python3
"""生成 content/indexes/知识点索引.md —— 扫描 interview/ 各篇目真实 H3 考点,
按 quartz.ts 的 Explorer 排序表排列,写出「真实标题 + github-slugger 锚点」的条目。

动机:旧索引是手维护的「人工考点关键词 + 人工描述别名」,链接不带锚点(点击只跳
文件顶),且别名早已与正文真实 H3 标题漂移。本脚本以正文 H3 为唯一源,锚点由
slug.py(与 Quartz 同源的 github-slugger 规则)生成,改完内容跑一次即可刷新,
永不漂移。设计前提同 outline.py:任何手工同步的清单最终都会烂掉。

用法:
    python3 scripts/gen_index.py          # 重写 content/indexes/知识点索引.md
    python3 scripts/gen_index.py --check  # 不写盘,仅比对现文件与生成结果是否一致
                                          # (不一致返回 1,可接入 CI 防漂移)

条目格式(平铺,真实 H3 原文):
    ### [集合框架](集合框架.md)
    - [ArrayList 和 LinkedList 的性能差异？](集合框架.md#arraylist-和-linkedlist-的性能差异)
    - [HashMap 的底层实现和扩容机制？](集合框架.md#hashmap-的底层实现和扩容机制)

注意:头部说明、分类顺序、尾表「专题文件清单」均由本脚本派生,勿手编该 md;
改说明改本脚本的 HEADER 模板,改篇目顺序改 quartz.ts 的 ORDER。
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from outline import parse_headings  # 跳过围栏代码块提取标题树
from slug import slugify_headings

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
INTERVIEW = os.path.join(CONTENT, "interview")
INDEX = os.path.join(CONTENT, "indexes", "知识点索引.md")
QUARTZ_TS = os.path.join(ROOT, "quartz.ts")

# 分类顺序(与 quartz.ts ORDER 的 interview 分类段一致,稳定,新增分类是大事)
CAT_ORDER = ["Java", "框架", "数据库", "中间件", "计算机基础", "分布式与架构", "工程实践", "面试"]

# 无追问地图的豁免篇目(其 H3 是指南结构而非考点),只在尾表出现,无 ### 块
EXEMPT = {"面试问题深挖指南.md"}
# 这两个 H2 段下不收 H3(段内本就无 H3,防御性跳过)
SKIP_H2 = {"面试追问地图", "参考资料"}

HEADER = """# 社招面试问题知识点

> Java 后端社招面试突击指南，按专题分类整理。点击专题名进入对应文件，点击小节标题跳转到对应问题位置。

答题深度与追问方法：[面试问题深挖指南](面试问题深挖指南.md)

> 相关：[高频题目索引](高频题目索引.md)（按热度）、[算法题索引](算法题索引.md)（按专题）、[社招算法训练计划](社招算法训练计划.md)（刷题周计划）。
>
> 分类与 `interview/` 目录结构保持一致：Java / 框架 / 数据库 / 中间件 / 计算机基础 / 分布式与架构 / 工程实践 / 面试。

---"""


def load_order():
    """解析 quartz.ts 的 ORDER 数组 → {分类: [篇目名]}(按 ORDER 顺序)。

    按 `// <分类>/` 注释行分段;ORDER 顶部的 indexes/分类名等因 cur=None 不收。
    """
    with open(QUARTZ_TS, encoding="utf-8") as fh:
        text = fh.read()
    m = re.search(r"const ORDER = \[(.*?)\]", text, re.S)
    if not m:
        raise RuntimeError("quartz.ts 未找到 ORDER 数组")
    body = m.group(1)
    cat_alt = "|".join(CAT_ORDER)
    seg_re = re.compile(rf"\s*//\s*({cat_alt})/")
    items = {c: [] for c in CAT_ORDER}
    cur = None
    for line in body.splitlines():
        sm = seg_re.match(line)
        if sm:
            cur = sm.group(1)
            continue
        if cur:
            items[cur].extend(re.findall(r'"([^"]+)"', line))
    return items


def sort_files(cat, files, order_items):
    """篇目排序:命中 ORDER 的按序,未命中回退码点序。"""
    order = order_items.get(cat, [])

    def key(f):
        stem = os.path.splitext(f)[0]
        try:
            return (0, order.index(stem))
        except ValueError:
            return (1, f)

    return sorted(files, key=key)


def collect_h3(path):
    """该篇目所有考点 H3 → [(text, slug)]。跳过追问地图/参考资料段,排除豁免。"""
    if os.path.basename(path) in EXEMPT:
        return []
    skipping = False
    h3_texts = []
    for _lineno, level, text in parse_headings(path):
        if level == 2:
            skipping = text in SKIP_H2
        elif level == 3 and not skipping:
            h3_texts.append(text)
    return slugify_headings(h3_texts)


def category_files(cat):
    """该分类目录下所有 .md 文件名(含扩展名)。"""
    d = os.path.join(INTERVIEW, cat)
    if not os.path.isdir(d):
        return []
    return [f for f in os.listdir(d) if f.endswith(".md")]


def build():
    """生成知识点索引全文。"""
    order_items = load_order()
    lines = [HEADER, ""]

    for ci, cat in enumerate(CAT_ORDER):
        files = sort_files(cat, category_files(cat), order_items)
        lines.append(f"## {cat}")
        lines.append("")
        for f in files:
            stem = os.path.splitext(f)[0]
            if f in EXEMPT:
                continue  # 豁免篇目只进尾表
            lines.append(f"### [{stem}]({f})")
            h3 = collect_h3(os.path.join(INTERVIEW, cat, f))
            if h3:
                for text, slug in h3:
                    lines.append(f"- [{text}]({f}#{slug})")
            else:
                lines.append("- （待补考点）")
            lines.append("")
        lines.append("---")
        lines.append("")

    # 尾表:从 interview/ 目录派生,与 check_index.py 的 D/E 项对齐
    lines.append("## 专题文件清单")
    lines.append("")
    lines.append("| 分类 | 专题文件 |")
    lines.append("|------|---------|")
    for cat in CAT_ORDER:
        files = sort_files(cat, category_files(cat), order_items)
        links = "、".join(f"[{os.path.splitext(f)[0]}]({f})" for f in files)
        lines.append(f"| {cat} | {links} |")
    lines.append("")  # 末尾换行
    return "\n".join(lines)


def main():
    check_only = "--check" in sys.argv
    new = build()
    if check_only:
        try:
            with open(INDEX, encoding="utf-8") as fh:
                old = fh.read()
        except FileNotFoundError:
            print("知识点索引不存在,需先跑一次生成")
            return 1
        if old == new:
            print("✓ 知识点索引与源文件一致,无漂移")
            return 0
        print("✗ 知识点索引与源文件漂移,请跑 `python3 scripts/gen_index.py` 刷新")
        return 1
    with open(INDEX, "w", encoding="utf-8") as fh:
        fh.write(new)
    print(f"已生成 {os.path.relpath(INDEX, ROOT)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

#!/usr/bin/env python3
"""从 content/英语学习/ 各篇的元数据行整篇生成 content/英语学习索引.md。

权威源是**篇内元数据行**(H1 下一行),同算法侧「题解元数据行 -> gen_topics.py ->
算法题索引」的链路。索引页是它的视图,勿手编。

元数据行格式:
    等级 L2 · 每天 15 分钟 · 场景:写邮件→一、英文邮件,写PR→二、PR 描述

    - 等级   必填, `L<n>` 或 `L<n>-L<m>`(跨级)
    - 每天   可选, 自由文本(资源/方案这类没有每日投入的篇目可省略)
    - 场景   必填, 逗号分隔的多项; 每项为 `场景名` 或 `场景名→本篇 H2 标题`
             带 → 的会在索引里生成直达该小节的锚点链接,不带的只链到篇

生成两个视图:
    按等级学 —— L1→L5 学习路径,每级列出该级篇目(跨级篇目在每一级都出现)
    按场景查 —— 场景 -> 涉及的篇+小节,跨篇聚合(写 PR 要同时看写作和词汇两篇)

用法:
    python3 scripts/gen_english.py            # 写入索引
    python3 scripts/gen_english.py --check    # 只检漂移,退出码 1 表示需要重跑
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import slugify  # 与 Quartz 同源的 github-slugger 规则
from outline import parse_headings  # 跳过围栏代码块提取标题

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
ENGLISH = os.path.join(CONTENT, "英语学习")
INDEX = os.path.join(CONTENT, "英语学习索引.md")

META_RE = re.compile(r"^等级\s+(L\d(?:-L\d)?)\s*(?:·\s*每天\s*([^·]+?)\s*)?·\s*场景:(.+)$")

# 等级释义(与 英语学习方案.md 的分级路线图一致,改那边要同步这里)
LEVELS = [
    ("L1", "入门", "看懂技术文档"),
    ("L2", "能干活", "写邮件 / PR / 设计文档"),
    ("L3", "能聊天", "和外籍同事日常交流"),
    ("L4", "能开会", "参与技术讨论与会议"),
    ("L5", "自如", "英语不再是「翻译」"),
]

# 场景展示顺序(未登记的场景排在最后,按首次出现序)
SCENE_ORDER = [
    "定级", "定方法", "选工具", "找材料", "找听力",
    "背词", "猜生词", "读文档", "查报错", "读论文",
    "写邮件", "写PR", "写设计文档", "写注释",
    "练发音", "听演讲", "技术讨论",
    "站会", "1对1", "给反馈", "提反对", "主持会议",
    "寒暄", "闲聊", "请假",
    "自我介绍", "行为面", "技术面", "谈薪",
]


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def parse_notes(strict=True):
    """-> [{name, title, level, daily, scenes: [(场景, H2 或 None)]}], 按文件名排序

    strict=True(生成时): 遇到格式错误直接退出。
    strict=False(check_index.py 校验时): 把错误收集到 parse_notes.errors,
    返回能解析的部分 —— 校验项要一次报全所有问题,不能第一条就中断。
    """
    parse_notes.errors = []

    def bad(msg):
        if strict:
            die(msg)
        parse_notes.errors.append(msg)

    notes = []
    for f in sorted(os.listdir(ENGLISH)):
        if not f.endswith(".md"):
            continue
        path = os.path.join(ENGLISH, f)
        lines = read(path).split("\n")
        title = lines[0].lstrip("# ").strip()
        meta = next((ln.strip() for ln in lines[1:6] if ln.strip().startswith("等级 ")), None)
        if not meta:
            bad(f"{f} 缺元数据行(H1 下一行应为「等级 L2 · 每天 … · 场景:…」)")
            continue
        m = META_RE.match(meta)
        if not m:
            bad(f"{f} 元数据行格式错误: {meta}")
            continue
        level, daily, scenes_raw = m.group(1), m.group(2), m.group(3)

        # 本篇真实 H2(跳过围栏代码块),用于校验「场景→小节」指向的小节确实存在
        h2s = [t for _ln, lv, t in parse_headings(path) if lv == 2]
        scenes = []
        for item in scenes_raw.split(","):
            item = item.strip()
            if not item:
                continue
            if "→" in item:
                scene, target = (s.strip() for s in item.split("→", 1))
                if target not in h2s:
                    bad(f"{f} 场景「{scene}」指向的小节「{target}」不存在(本篇 H2: {h2s})")
                    continue
                scenes.append((scene, target))
            else:
                scenes.append((item, None))
        notes.append(
            {"name": f, "title": title, "level": level, "daily": daily, "scenes": scenes}
        )
    return notes


def die(msg):
    print(f"✗ {msg}", file=sys.stderr)
    sys.exit(1)


def link(note, section=None):
    if section is None:
        return f"[{note['title']}]({note['name']})"
    return f"[{note['title']} · {section}]({note['name']}#{slugify(section)})"


def levels_of(note):
    """L1-L3 -> ['L1','L2','L3']"""
    if "-" in note["level"]:
        lo, hi = note["level"].split("-")
        return [f"L{i}" for i in range(int(lo[1]), int(hi[1]) + 1)]
    return [note["level"]]


def render(notes):
    out = [
        "# 英语学习索引",
        "",
        "> **本页由 `scripts/gen_english.py` 生成，勿手编。** 改篇目的元数据行(H1 下一行)后跑一次脚本即可。",
        ">",
        "> 两个视图:**按等级学**给学习路径(我在哪一级、接下来练什么),**按场景查**给任务速查",
        "> (我现在要写 PR / 开站会,该看哪几处)。同一篇会在多处出现——它们指向同一份内容。",
        ">",
        "> 等级定义与时间预期见 [英语学习方案](英语学习方案.md),工具与材料见 [英语学习资源](英语学习资源.md)。",
        "",
        "## 按等级学",
        "",
    ]
    for code, name, goal in LEVELS:
        members = [n for n in notes if code in levels_of(n)]
        if not members:
            continue
        out.append(f"### {code} {name} · {goal}")
        out.append("")
        out.append("| 篇目 | 每天投入 | 这一级用它做什么 |")
        out.append("|---|---|---|")
        for n in members:
            scenes = "、".join(s for s, _ in n["scenes"])
            out.append(f"| {link(n)} | {n['daily'] or '—'} | {scenes} |")
        out.append("")

    out.append("## 按场景查")
    out.append("")
    out.append("按「你现在要做什么」直达小节。一个场景横跨多篇时,按建议阅读顺序排列。")
    out.append("")
    out.append("| 你要做什么 | 直达 |")
    out.append("|---|---|")
    by_scene = {}
    for n in notes:
        for scene, section in n["scenes"]:
            by_scene.setdefault(scene, []).append((n, section))
    # 同一场景横跨多篇时按起始等级升序 —— 低等级的先看(先背词再上会,不是反过来)
    for items in by_scene.values():
        items.sort(key=lambda ns: int(levels_of(ns[0])[0][1]))
    seen = list(SCENE_ORDER) + [s for s in by_scene if s not in SCENE_ORDER]
    for scene in seen:
        if scene not in by_scene:
            continue
        targets = " · ".join(link(n, sec) for n, sec in by_scene[scene])
        out.append(f"| **{scene}** | {targets} |")
    out.append("")

    out.append("## 全部篇目")
    out.append("")
    out.append("| 篇目 | 等级 | 每天投入 | 覆盖场景 |")
    out.append("|---|---|---|---|")
    for n in notes:
        scenes = "、".join(s for s, _ in n["scenes"])
        out.append(f"| {link(n)} | {n['level']} | {n['daily'] or '—'} | {scenes} |")
    out.append("")
    return "\n".join(out)


def main():
    notes = parse_notes()
    text = render(notes)
    if "--check" in sys.argv:
        cur = read(INDEX) if os.path.isfile(INDEX) else ""
        if cur.rstrip("\n") != text.rstrip("\n"):
            print("✗ 英语学习索引与篇目元数据行漂移,请跑 python3 scripts/gen_english.py", file=sys.stderr)
            sys.exit(1)
        print("✓ 英语学习索引与元数据行一致,无漂移")
        return
    with open(INDEX, "w", encoding="utf-8") as f:
        f.write(text + "\n")
    scenes = {s for n in notes for s, _ in n["scenes"]}
    print(f"✓ 生成 {os.path.relpath(INDEX, ROOT)}: {len(notes)} 篇、{len(scenes)} 个场景")


if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""从 content/软考系统架构师/ 各篇的元数据行整篇生成 content/软考系统架构师索引.md。

与 gen_english.py / gen_ai.py 同构(权威源是**篇内元数据行**,索引页是它的视图,勿手编),
第五个域:软考高级**系统架构设计师**备考(考试信息/重点分布/知识域/案例分析/论文写作),
不是大厂面试八股,不进知识点索引。

元数据行格式(与英语/AI 域同构,便于共用校验直觉):
    科目 综合知识/案例分析/论文 · 考频 ★★★★★ · 场景:报名→一、考试基本信息,备考路线→二、八周备考路线

    - 科目   必填, 斜杠分隔, 限三科名:综合知识/案例分析/论文
    - 考频   必填, ★1~5 颗(与八股「频次」同语义:★ 越多越常考/备考优先级越高)
    - 场景   必填, 逗号分隔; 每项为 `场景名` 或 `场景名→本篇 H2 标题`
             带 → 的生成直达该小节的锚点链接,不带的只链到篇

生成两个视图:
    按科目学 —— 综合知识/案例分析/论文 三科分组,列出每科相关篇目
    按场景查 —— 场景 -> 涉及的篇+小节,跨篇聚合

用法:
    python3 scripts/gen_rk.py            # 写入索引
    python3 scripts/gen_rk.py --check    # 只检漂移,退出码 1 表示需要重跑
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from slug import slugify  # 与 Quartz 同源的 github-slugger 规则
from outline import parse_headings  # 跳过围栏代码块提取标题

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONTENT = os.path.join(ROOT, "content")
RK = os.path.join(CONTENT, "软考系统架构师")
INDEX = os.path.join(CONTENT, "软考系统架构师索引.md")

META_RE = re.compile(r"^科目\s+([^·]+?)\s*·\s*考频\s*(★{1,5})\s*·\s*场景:(.+)$")

# 考试三科(按科目学的分组顺序,与考试科目一致)
SUBJECTS = ["综合知识", "案例分析", "论文"]

# 场景展示顺序(未登记的场景排在最后,按首次出现序)
SCENE_ORDER = [
    "报名", "备考路线", "重点分布", "知识域",
    "刷真题", "案例答题", "论文写作", "资源",
]


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def die(msg):
    print(f"✗ {msg}", file=sys.stderr)
    sys.exit(1)


def parse_notes(strict=True):
    """-> [{name, title, subjects, stars, scenes: [(场景, H2 或 None)]}], 按文件名排序

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
    if not os.path.isdir(RK):
        return notes
    for f in sorted(os.listdir(RK)):
        if not f.endswith(".md"):
            continue
        path = os.path.join(RK, f)
        lines = read(path).split("\n")
        title = lines[0].lstrip("# ").strip()
        meta = next((ln.strip() for ln in lines[1:6] if ln.strip().startswith("科目 ")), None)
        if not meta:
            bad(f"{f} 缺元数据行(H1 下一行应为「科目 … · 考频 ★… · 场景:…」)")
            continue
        m = META_RE.match(meta)
        if not m:
            bad(f"{f} 元数据行格式错误: {meta}")
            continue
        subjects_raw, stars, scenes_raw = m.group(1), m.group(2), m.group(3)
        subjects = [s.strip() for s in subjects_raw.split("/")]
        unknown = [s for s in subjects if s not in SUBJECTS]
        if unknown:
            bad(f"{f} 科目只能是 {'/'.join(SUBJECTS)},出现未识别: {'、'.join(unknown)}")
            continue
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
            {"name": f, "title": title, "subjects": subjects, "stars": stars, "scenes": scenes}
        )
    return notes


def link(note, section=None):
    if section is None:
        return f"[{note['title']}]({note['name']})"
    return f"[{note['title']} · {section}]({note['name']}#{slugify(section)})"


def render(notes):
    out = [
        "# 软考系统架构师索引",
        "",
        "> **本页由 `scripts/gen_rk.py` 生成，勿手编。** 改篇目的元数据行(H1 下一行)后跑一次脚本即可。",
        ">",
        "> 两个视图:**按科目学**对应考试三科(综合知识/案例分析/论文),**按场景查**给备考任务速查",
        "> (我现在要报名 / 规划备考路线 / 查重点分布 / 刷真题,该看哪几处)。同一篇会在多处出现——它们指向同一份内容。",
        ">",
        "> 这是独立于八股/算法/英语/AI 的第五个域:软考高级**系统架构设计师**备考资料",
        "> (考试信息、重点分布、知识域、案例分析、论文写作),不是大厂面试八股。",
        "",
        "## 按科目学",
        "",
    ]
    for subject in SUBJECTS:
        members = [n for n in notes if subject in n["subjects"]]
        if not members:
            continue
        out.append(f"### {subject}")
        out.append("")
        out.append("| 篇目 | 考频 | 覆盖场景 |")
        out.append("|---|---|---|")
        for n in members:
            scenes = "、".join(s for s, _ in n["scenes"])
            out.append(f"| {link(n)} | {n['stars']} | {scenes} |")
        out.append("")

    out.append("## 按场景查")
    out.append("")
    out.append("按「你现在要做什么」直达小节。一个场景横跨多篇时,按科目优先级排列(先综合知识,再案例/论文)。")
    out.append("")
    out.append("| 你要做什么 | 直达 |")
    out.append("|---|---|")
    by_scene = {}
    for n in notes:
        for scene, section in n["scenes"]:
            by_scene.setdefault(scene, []).append((n, section))
    for items in by_scene.values():
        items.sort(key=lambda ns: min(SUBJECTS.index(s) for s in ns[0]["subjects"]))
    seen = list(SCENE_ORDER) + [s for s in by_scene if s not in SCENE_ORDER]
    for scene in seen:
        if scene not in by_scene:
            continue
        targets = " · ".join(link(n, sec) for n, sec in by_scene[scene])
        out.append(f"| **{scene}** | {targets} |")
    out.append("")

    out.append("## 全部篇目")
    out.append("")
    out.append("| 篇目 | 科目 | 考频 | 覆盖场景 |")
    out.append("|---|---|---|---|")
    for n in notes:
        scenes = "、".join(s for s, _ in n["scenes"])
        out.append(f"| {link(n)} | {'/'.join(n['subjects'])} | {n['stars']} | {scenes} |")
    out.append("")
    return "\n".join(out)


def main():
    notes = parse_notes()
    text = render(notes)
    if "--check" in sys.argv:
        cur = read(INDEX) if os.path.isfile(INDEX) else ""
        if cur.rstrip("\n") != text.rstrip("\n"):
            print("✗ 软考系统架构师索引与篇目元数据行漂移,请跑 python3 scripts/gen_rk.py", file=sys.stderr)
            sys.exit(1)
        print("✓ 软考系统架构师索引与元数据行一致,无漂移")
        return
    with open(INDEX, "w", encoding="utf-8") as f:
        f.write(text + "\n")
    scenes = {s for n in notes for s, _ in n["scenes"]}
    print(f"✓ 生成 {os.path.relpath(INDEX, ROOT)}: {len(notes)} 篇、{len(scenes)} 个场景")


if __name__ == "__main__":
    main()

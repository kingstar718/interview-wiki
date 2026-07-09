#!/usr/bin/env python3
"""github-slugger v2 规则的纯标准库复刻,供 gen_index.py / check_index.py 共用。

Quartz 用 github-slugger 给 H1-H6 生成 id(锚点)。生成索引时写出的 `文件.md#锚点`
必须与 Quartz 渲染出的 id 同规则,否则点击死链;check 校验锚点时也用同规则归一化。

算法(lowercase → 删非字母数字非空白非连字符 → 空白转 - → 合并连字符 → 去首尾 -):
    "HashMap 的底层实现和扩容机制？" → "hashmap-的底层实现和扩容机制"
    "ConcurrentHashMap JDK 1.7 vs 1.8 的区别" → "concurrenthashmap-jdk-17-vs-18-的区别"
    "## 18 字典树Trie"                   → "18-字典树trie"

正则说明:re 的 \\w 在 UNICODE 下含中文,但也含下划线;github-slugger 用
\\p{L}\\p{N}(不含下划线),故 `[^\\w\\s-]|_` 显式排除下划线,与 slugger 对齐。
"""
import re

_STRIP = re.compile(r"[^\w\s-]|_", re.UNICODE)
_WS = re.compile(r"\s+")
_DASH = re.compile(r"-{2,}")


def slugify(text):
    """单个标题 → slug,与 github-slugger v2 一致。"""
    s = text.lower()
    s = _STRIP.sub("", s)
    s = _WS.sub("-", s)
    s = _DASH.sub("-", s)
    return s.strip("-")


def slugify_headings(texts):
    """一组标题(按文件内出现顺序)→ [(text, slug)]。

    同文件内重复标题:第 n≥2 次出现追加 `-{n-1}`(第二次加 -1),与
    github-slugger 的 occurrence 机制一致,保证锚点指向唯一标题。
    """
    seen = {}
    out = []
    for t in texts:
        base = slugify(t)
        n = seen.get(base, 0)
        seen[base] = n + 1
        out.append((t, base if n == 0 else f"{base}-{n}"))
    return out

# Zensical 迁移规格

状态：待回归

## 目标

将站点从 Quartz 迁移到 Zensical，继续以 `content/` 为唯一内容源、以 `public/` 为构建输出，并收敛为可被常规 Markdown 工具识别的文档结构。

## 需求与验收

- R-01：使用锁定版本的 Zensical 构建静态站点，不保留双链、悬停预览、反向链接和关系图谱。
- R-02：站内链接统一为相对当前文件的标准 Markdown 链接，不依赖全库文件名解析。
- R-03：现有索引生成脚本不再依赖 `quartz.ts`，生成结果继续可重复校验。
- R-04：Cloudflare Pages 从仓库根目录构建，输出目录保持 `public/`。
- R-05：移除 Quartz、MkDocs Material、插件安装和 Node 构建依赖。
- R-06：保留 `mkdocs.yml` 作为 Zensical 的兼容配置，沿用现有中文锚点规则，避免已发布的页内 URL 变化。

验收标准：

1. `python -m zensical build` 成功且无构建问题。
2. `python scripts/check_index.py` 成功，且正文中没有可渲染的 `[[wikilink]]`。
3. 所有 `scripts/gen_*.py --check` 通过。
4. README、项目指南和部署说明均只描述 Zensical 工作流。
5. Cloudflare Pages 继续发布纯静态 `public/`，不引入 Worker 或服务端运行时。

## 验证记录

- `python -m zensical build`：通过，370 个页面，搜索索引与抽查的中文锚点均存在。
- 仅安装 `requirements.txt` 的全新虚拟环境构建：通过，未安装 MkDocs 或 Material。
- 所有生成索引的 `--check`：通过，无漂移。
- `scripts/check_index.py`：18 项检查全部通过。

尚待 Cloudflare Pages 使用新构建命令完成线上回归，因此状态保持“待回归”。

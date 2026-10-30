# 部署指引

站点由 Zensical 静态生成：`content/` 是内容源，`python -m zensical build` 输出 `public/`。

## Cloudflare Pages

仓库关联 `main` 分支后，将构建配置设为：

- 构建命令：`python -m pip install -r requirements.txt && python -m zensical build`
- 构建输出目录：`public`
- 根目录：仓库根目录
- Python：`.python-version` 指定的版本

自定义域名 `wiki.wujinxing.site` 继续在 Cloudflare 控制台管理。原来的 `npm run build` 必须改为上面的 Python 命令；迁移后不再安装 Node、Quartz 插件或 MkDocs Material。

必须通过 `python -m zensical` 启动，而不是直接执行 `zensical`。前者会将仓库根目录加入 Python 模块搜索路径，以便配置加载 `scripts.slug.slugify`，保持既有中文锚点不变。

## 本地预览

```bash
python -m venv .venv
.venv\Scripts\python -m pip install -r requirements.txt
.venv\Scripts\python -m zensical serve
```

## 其他静态托管

Vercel、Netlify 或自建 nginx 均执行相同构建命令，并将 `public/` 作为静态根目录。

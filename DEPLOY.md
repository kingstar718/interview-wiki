# 部署指引

Docsify 是纯静态站点(运行时 CDN 渲染,无构建)。`index.html` 在根目录,`content/` 是笔记。
直接把仓库根目录当静态站点托管即可。

## GitHub Pages(推荐)

1. 推到 `main` 分支。
2. 仓库 **Settings → Pages → Source = Deploy from a branch** → 分支 `main` / 文件夹 `/root`。
3. 保存,几分钟后站点上线:`https://<user>.github.io/<repo>/`。

**无需 GitHub Actions workflow**。`.nojekyll` 已在根目录(防止 Jekyll 忽略 `_sidebar.md` 等 `_` 开头文件)。

> 若之前用 Actions 部署(Source = GitHub Actions),改成 "Deploy from a branch" 即可。

## 本地预览

```bash
npx docsify-cli serve .     # http://localhost:3000
# 或
python -m http.server 8000  # http://localhost:8000
```

## Cloudflare Pages / Vercel / Netlify

连 Git 仓库 → 框架预设 `None` / `Other` → **无 build 命令** → 输出目录 `.`(根)→ 部署。

## 自建 nginx

把仓库根目录当静态根:

```nginx
location / {
    root /path/to/interview-wiki;
    try_files $uri $uri/ /index.html;
}
```

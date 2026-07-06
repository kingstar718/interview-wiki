# 部署指引

Quartz v5:本地 `npx quartz build` 产出 `public/` 静态目录,托管到任一平台即可。
官方支持 GitHub Pages / Cloudflare Pages / Vercel / Netlify / GitLab Pages / 自建 nginx。
完整文档:<https://quartz.jzhao.xyz/hosting>

## 前提

- 笔记在 `content/`,`quartz.config.yaml` 已配 `baseUrl`。
- Node 20+(本地),CI 用 24。

---

## GitHub Pages(推荐,已有 GitHub)

1. 推到 GitHub 仓库(如 `kingstar718/interview-wiki`)。
2. `quartz.config.yaml` 设 `baseUrl`(v5 配置为 YAML):
   ```yaml
   configuration:
     pageTitle: Interview Wiki
     baseUrl: "https://kingstar718.github.io/interview-wiki/"
   ```
3. 新建 `.github/workflows/deploy.yml`:
   ```yaml
   name: Deploy Quartz site
   on:
     push:
       branches: [ main ]          # 改成你的默认分支
   permissions:
     contents: read
     pages: write
     id-token: write
   concurrency:
     group: pages
     cancel-in-progress: false
   jobs:
     build-deploy:
       runs-on: ubuntu-latest
       environment:
         name: github-pages
         url: ${{ steps.deployment.outputs.page_url }}
       steps:
         - uses: actions/checkout@v4
           with: { fetch-depth: 0 }
         - uses: actions/setup-node@v4
           with: { node-version: 24 }
         - run: npm ci
         - run: npx quartz plugin install
         - run: npx quartz build
         - uses: actions/configure-pages@v4
         - uses: actions/upload-pages-artifact@v3
           with: { path: public }
         - id: deployment
           uses: actions/deploy-pages@v4
   ```
4. 仓库 **Settings → Pages → Source = GitHub Actions**。
5. `npx quartz sync` 推送 → 站点 `https://kingstar718.github.io/interview-wiki/`。

**坑**:
- `baseUrl` 必须带 `/<仓库名>/`(项目页);用户页 `<user>.github.io` 则不带。
- 报 environment protection rules 错 → Settings → Environments 删掉 `github-pages`,Action 会自建。
- GitHub Pages 不做尾斜杠重定向(从 Quartz 3 迁移才相关,本仓库新建无影响)。

---

## Cloudflare Pages(配置最少,官方亦推荐)

连 Git 仓库 → 框架预设 `None` →
Build `npx quartz plugin install && npx quartz build` →
Output `public` → 保存即部署,缓存自动。

Cloudflare 默认浅克隆,若依赖 git 时间戳,build 命令前加 `git fetch --unshallow &&`。

---

## Vercel

根目录 `vercel.json`:`{ "cleanUrls": true }`。
Build `npx quartz plugin install && npx quartz build`,框架预设 `Other`,root `./`。

---

## Netlify

Build `npx quartz plugin install && npx quartz build`,Publish 目录 `public`。

---

## 自建 nginx

把 `public/` 拷到服务器:

```nginx
location / {
    root /path/to/public;
    try_files $uri $uri.html $uri/ =404;
}
```

Quartz 的 CSS/JS 带内容哈希,可长期缓存;HTML 不要长缓存。

```nginx
location ~* -[0-9a-f]{8}\. {
    add_header Cache-Control "public, max-age=31536000, immutable";
}
```

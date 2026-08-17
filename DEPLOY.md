# 部署指引

站点由 [Quartz 4](https://quartz.jzhao.xyz/) 静态生成:`content/` 是笔记源码,
`npx quartz build` 产出 `public/`(已 gitignore),把 `public/` 当静态站点托管即可。

## Cloudflare Pages(当前方式)

1. 仓库已在 Cloudflare Pages 关联 `main` 分支。
2. 构建配置:build 命令 `npx quartz build`,输出目录 `public`,Node 22+。
3. 自定义域名 `wiki.wujinxing.site` 在 Cloudflare 控制台配置。

push `main` 后 CF Pages 自动拉取构建发布,无需本地操作。
注意:校验脚本 `scripts/check_index.py` 不在 CF 构建中运行,改完在本地跑一遍(见 CLAUDE.md)。

## 本地预览

```bash
npm ci                      # 首次
npx quartz build --serve    # http://localhost:8080,改 md 自动热重载
```

## Vercel / Netlify

连 Git 仓库 → build 命令 `npx quartz build` → 输出目录 `public` → Node 22+。

## 自建 nginx

先 `npx quartz build`,再把 `public/` 当静态根:

```nginx
location / {
    root /path/to/interview-wiki/public;
    try_files $uri $uri.html $uri/ =404;
}
```

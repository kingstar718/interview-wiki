# 部署指引

站点由 [Quartz 4](https://quartz.jzhao.xyz/) 静态生成:`content/` 是笔记源码,
`npx quartz build` 产出 `public/`(已 gitignore),把 `public/` 当静态站点托管即可。

## GitHub Pages(当前方式)

1. 推到 `main` 分支,[.github/workflows/deploy.yml](.github/workflows/deploy.yml) 自动构建并发布。
2. 仓库 **Settings → Pages → Source = GitHub Actions**(只需设置一次)。
3. 自定义域名 `wiki.wujinxing.site` 在 Pages 设置中配置,根目录 `CNAME` 文件仅作备份记录。

workflow 要点:`fetch-depth: 0`(Quartz 用 git 历史生成"最后修改时间")、
Node 22+、构建前先跑 `python3 scripts/check_index.py` 拦截死链/索引漂移。

## 本地预览

```bash
npm ci                      # 首次
npx quartz build --serve    # http://localhost:8080,改 md 自动热重载
```

## Cloudflare Pages / Vercel / Netlify

连 Git 仓库 → build 命令 `npx quartz build` → 输出目录 `public` → Node 22+。

## 自建 nginx

先 `npx quartz build`,再把 `public/` 当静态根:

```nginx
location / {
    root /path/to/interview-wiki/public;
    try_files $uri $uri.html $uri/ =404;
}
```

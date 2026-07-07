#!/usr/bin/env node
import fs from "fs"
import path from "path"
import YAML from "yaml"
import { installPlugins, parsePluginSource, regeneratePluginIndex } from "./gitLoader.js"

async function main() {
  // 1) Read plugin sources from quartz.config.yaml (Quartz v5 YAML format)
  const yamlPath = path.join(process.cwd(), "quartz.config.yaml")
  let sources: string[] = []

  if (fs.existsSync(yamlPath)) {
    const raw = fs.readFileSync(yamlPath, "utf-8")
    const yaml = YAML.parse(raw)
    const pluginEntries = yaml.plugins || []
    sources = pluginEntries
      .filter(
        (e: any) =>
          e.source &&
          typeof e.source === "string" &&
          (e.source.startsWith("github:") ||
            e.source.startsWith("git+") ||
            e.source.startsWith("https://")),
      )
      .map((e: any) => e.source)
  }

  // 2) Fallback: old-style config (externalPlugins in quartz.ts/js)
  if (sources.length === 0) {
    try {
      const config = await import("../../../quartz.js")
      const quartzConfig: any = config.default || config
      sources = quartzConfig.externalPlugins || []
    } catch {
      // quartz.ts doesn't exist or doesn't export externalPlugins — that's fine
    }
  }

  // 3) Install plugins or just regenerate the barrel file
  if (sources.length > 0) {
    console.log(`Installing ${sources.length} plugin(s) from Git...`)
    const specs = sources.map((source: string) => parsePluginSource(source))
    const installed = await installPlugins(specs, { verbose: true })

    if (installed.size === sources.length) {
      console.log("✓ All plugins installed successfully")
    } else {
      console.error(`✗ Only ${installed.size}/${sources.length} plugins installed`)
      process.exit(1)
    }
  } else {
    console.log("No external plugins to install.")
    // Still regenerate the barrel file in case plugins are already cached
    await regeneratePluginIndex({ verbose: true })
  }
}

main().catch((err) => {
  console.error("Failed to install plugins:", err)
  process.exit(1)
})

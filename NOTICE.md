# NOTICE

本目录是 TouhouTweaks 的 Minecraft NeoForge 1.21.1 移植工程。

## 上游与许可

- 原项目名称：TouhouTweaks
- 原作者：H2Sxxa
- 原开源地址：https://github.com/H2Sxxa/TouhouTweaks
- 许可证：GPL-3.0-only（保持不变）

## NeoForge 迁移记录

- 目标：Minecraft 1.21.1、NeoForge 21.1.256、Java 21。
- 保持 `touhoutweaks` modid、`io.github.h2sxxa.touhoutweaks` 包名、五个音效事件名与原资源文件名。
- `mods.toml` 改为 `neoforge.mods.toml`，音效使用 NeoForge 1.21.1 的 `DeferredRegister` 与 `DeferredHolder`。
- Forge 1.20.1 的 `SimpleChannel` 改为 NeoForge 1.21.1 的 `CustomPacketPayload`、`StreamCodec` 和 `PayloadRegistrar`；载荷标为 optional，保持旧版缺少通道时仍可连接的语义。
- 配置注册改用 `ModLoadingContext.get().getActiveContainer().registerConfig(...)`，适配 NeoForge 1.21.1 的 FML API。
- 保留暂停、死亡、漂浮/力量效果、截图音效、界面文字重定向和崩溃报告彩蛋功能。

移植工程不改变上游作者署名、开源地址或 GPL-3.0-only 许可证。

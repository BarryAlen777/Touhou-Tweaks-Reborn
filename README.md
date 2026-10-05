# TouhouTweaks（Forge 1.20.1 移植版）

一个让 MC 拥有更多车万要素的 mod。

本项目是 [H2Sxxa/TouhouTweaks](https://github.com/H2Sxxa/TouhouTweaks)（原 Forge 1.12.2 版）的
**Forge 1.20.1 移植版**：许可证保持 **GPL-3.0**，原作者署名 **H2Sxxa** 不变，
源码地址仍指向上面的原仓库，移植代码以同协议开源（移植变更记录见 `NOTICE.md`）。

## 功能

| 功能 | 触发时机 | 对应配置开关 |
| --- | --- | --- |
| 暂停游戏音效 | 游戏内打开暂停菜单（窗口激活时） | `sounds.pauseSound`（原 `PauseSoundEffect`） |
| 玩家死亡音效 | 玩家死亡事件（LOWEST 优先级、未取消时） | `sounds.deathSound` + 服务器 `server.deathSound` |
| 药水效果音效 | 玩家**新获得**漂浮 / 力量效果时 | `sounds.potionSound`（原 `PotionSoundEffect`）+ 服务器 `server.potionSound` |
| 截图音效 | F2 / PrintScreen 截图事件（未取消时） | `sounds.screenshotSound` |
| 世界加载文字 | 加载/加入界面文案替换为"少女祈祷中……" | `text.touhouTextRedirect` |
| 崩溃报告彩蛋 | 崩溃报告 System Details 随机一条车万 Witty Comment | 通用配置 `crash.wittyComment` |

五个音效（`thtplayerdead`、`thtpause`、`thtscreenshot`、`thtlevitation`、`thtstrength`）
的事件名与 ogg 文件与原仓库完全一致；崩溃彩蛋文案逐字保留（原版 34 条 + 车万 11 条，
同一个 `System.nanoTime()` 取模随机逻辑）。

## 与原版实现的对应关系（开发者向）

- 原 1.12.2 用 MixinBooter + Mixin；1.20.1 版**不再需要前置**：
  - `MixinMinecraft.displayInGameMenu` → `ClientTickEvent` 检测 `PauseScreen`；
  - `MixinEntityLivingBase.addPotionEffect` → `ClientTickEvent` 轮询效果新增边沿；
  - `MixinI18n` → 包装 `net.minecraft.locale.Language` 并 `Language.inject` 注入
    （`touhoutweaks.<key>` 优先的查找重定向，机制逐字等价）；
  - `MixinCrashReport` → Forge 官方 `CrashReportCallables` 扩展点；
  - `ScreenshotEvent` 新旧版本同名事件，逻辑逐字保留。
- `RegistryEvent` 注册 → `DeferredRegister`；`@Config` → `ForgeConfigSpec`；
  `ConfigChangedEvent` → `ModConfigEvent.Loading/Reloading`。
- 新增 `SimpleChannel`（协议 "1"）：死亡音效播放包（S2C，登录后可达）、
  服务器总开关同步包（登录 + 维度切换时发送），修复了原版联机无音效的问题，
  单人体验与原版一致。

## 构建与发布

```bash
./gradlew build          # 产物：build/libs/touhoutweaks-<版本>.jar
./gradlew runClient      # 开发运行
```

- 依赖：Forge 1.20.1（47.x，推荐 47.4.0）、Java 17、Gradle 8 + ForgeGradle 6。
- jar 元数据与 `META-INF/mods.toml` 已标注原作者 H2Sxxa、GPL-3.0 与原仓库地址；
  发布到 CurseForge / Modrinth 时请在描述页同样署名原作者并附原仓库链接。

## 许可

GPL-3.0-only。详见 `LICENSE` 与 `NOTICE.md`。

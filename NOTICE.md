# NOTICE

本项目是 TouhouTweaks 的 Minecraft Forge 1.20.1 移植版。

## 上游与许可

- 原项目名称：TouhouTweaks
- 原作者：H2Sxxa
- 原开源地址：https://github.com/H2Sxxa/TouhouTweaks
- 许可证：GPL-3.0-only（保持不变，本移植代码同样以 GPL-3.0 发布，见 LICENSE）

## 移植变更记录

在不改变协议、作者署名和上游仓库地址的前提下，记录如下变更：

1. **目标平台**：Forge 1.12.2（MixinBooter + Mixin）→ Forge 1.20.1（47.4.0，Java 17，
   Gradle 8 + ForgeGradle 6）。modid `touhoutweaks` 与包名 `io.github.h2sxxa.touhoutweaks`
   保持不变；不再需要 MixinBooter 前置。
2. **注册体系**：`RegistryEvent.Register<SoundEvent>` + `setRegistryName` 改为
   `DeferredRegister`；五个音效的事件名与 ogg 文件名不变。
3. **Mixin 移除，改走事件与官方扩展点**（触发时机与数值保持）：
   - `MixinMinecraft.displayInGameMenu`（窗口激活检查 `Display.isActive()` 同步保留，
     1.20.1 等价 API 为 `Minecraft.isWindowActive()`）→ ClientTickEvent 检测暂停界面
     （1.20.1 映射中该类名为 `PauseScreen`）；
   - `MixinEntityLivingBase.addPotionEffect`（"之前没有、现在有了才响"条件保留；
     1.20.1 映射中"力量"效果字段名为 `MobEffects.DAMAGE_BOOST`）→ 客户端轮询效果新增边沿；
   - `MixinI18n` 的 `touhoutweaks.<key>` 翻译重定向 → 包装 `net.minecraft.locale.Language`
     并经 `Language.inject` 注入，重载后自动重装，机制逐字等价；
   - `MixinCrashReport`（vanilla 34 条 + 车万 11 条合并、`System.nanoTime()` 取模随机、
     异常兜底文案均逐字保留）→ Forge 官方 `CrashReportCallables` 扩展点；
   - 截图音效：Forge 原版 `ScreenshotEvent` 在新旧版本都存在，逻辑逐字保留。
4. **sounds.json**：事件名、音频路径、subtitle 键全部不变；原 `"category": "sound"`
   在新版本不是合法的 `SoundCategory`（原 1.12.2 中它实际回退为主音量通道），
   改为等价的 `"master"`。
5. **网络**：原模组无网络包（死亡/药水音效仅客户端本地触发，联机不可闻）。移植版按方案
   引入 `SimpleChannel`（协议版本 "1"）：`PlaySoundPayload`（玩家死亡时 S2C 下发，字段为
   玩家实体 id 与音效 id）与 `ServerSwitchPayload`（登录与维度切换时同步服务器总开关），
   单人体验与原版一致，多人模式下音效不再丢失。
6. **配置**：原 `@Config` 注解（`PotionSoundEffect`、`PauseSoundEffect`）迁移为
   `ForgeConfigSpec`（客户端 `touhoutweaks-client.toml` + 通用 `touhoutweaks.toml`），
   并按功能逐项拆分开关（死亡、截图、加载文字、崩溃彩蛋为新增可禁用项），
   默认值全部保持原行为；原 `ConfigSync` 的 `ConfigChangedEvent → ConfigManager.sync`
   改为 `ModConfigEvent.Loading/Reloading → 缓存刷新`。
7. **语言文件**：`.lang` 格式更新为 1.20.1 的 JSON；`touhoutweaks.menu.loadingLevel`
   原文"少女祈祷中……"保留，并按 1.20.1 实际显示的世界加载/加入界面键
   （`progress.working`、`connect.connecting`、`connect.joining`）新增同名替换条目；
   为 sounds.json 的 subtitle 键补充了中英文字幕文案（原文件未提供条目，属新增显示文本，
   不影响听觉/视觉既有内容）。

移植参与者与更多细节见本仓库提交历史与 README.md。

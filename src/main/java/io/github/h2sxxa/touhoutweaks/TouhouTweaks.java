package io.github.h2sxxa.touhoutweaks;

import io.github.h2sxxa.touhoutweaks.crash.WittyComments;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.lang.TouhouLanguage;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;
import io.github.h2sxxa.touhoutweaks.regist.ModSounds;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * TouhouTweaks —— Forge 1.20.1 移植版入口。
 *
 * 原模组（1.12.2，作者 H2Sxxa，GPL-3.0-only）使用 Mixin + MixinBooter 注入原版；
 * 本移植版在保持功能、触发时机与数值不变的前提下，全部改用 Forge 事件总线、
 * DeferredRegister 注册体系、SimpleChannel 与官方崩溃报告扩展点实现，
 * 不再需要任何前置模组。各项功能可通过 config/touhoutweaks-client.toml 与
 * config/touhoutweaks.toml 单独开关，默认值与原模组行为一致。
 */
@Mod(Consts.MODID)
public class TouhouTweaks {
    public TouhouTweaks() {
        // 与原 TouhouTweaks.preInit 的 ModSounds.initModSounds() 对应：注册音效。
        // 1.20.1 走 DeferredRegister -> mod event bus。
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.SOUND_EVENTS.register(bus);

        // 与原 @Config 注解等价的配置注册：客户端开关 + 通用（服务器）开关。
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, THTConfigs.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, THTConfigs.COMMON_SPEC);
        THTConfigs.update();

        // SimpleChannel：死亡音效播放包 + 服务器配置状态同步包（登录 / 维度切换时发送）。
        THTNetwork.register();

        // 车万风崩溃报告彩蛋：原 MixinCrashReport 改注册到 Forge 官方的 CrashReportCallables。
        CrashReportCallables.registerCrashCallable(
                Consts.NAME + " Witty Comment",
                WittyComments::randomComment,
                () -> THTConfigs.crashWittyComment);

        // "少女祈祷中……"：原 MixinI18n 的 touhoutweaks.<key> 翻译重定向，
        // 在 1.20.1 通过包装 Language.getInstance() 等价实现。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            TouhouLanguage.ensureInstalled();
        }
    }
}

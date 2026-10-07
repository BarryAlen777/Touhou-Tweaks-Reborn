package io.github.h2sxxa.touhoutweaks;

import io.github.h2sxxa.touhoutweaks.config.ConfigSync;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.crash.WittyComments;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;
import io.github.h2sxxa.touhoutweaks.lang.TouhouLanguage;
import io.github.h2sxxa.touhoutweaks.regist.ModSounds;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.CrashReportCallables;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

/** TouhouTweaks NeoForge 1.21.1 入口。 */
@Mod(Consts.MODID)
public class TouhouTweaks {
    public TouhouTweaks(IEventBus modBus) {
        ModSounds.SOUND_EVENTS.register(modBus);
        modBus.addListener(ConfigSync::onConfigLoading);
        modBus.addListener(ConfigSync::onConfigReloading);
        modBus.addListener(THTNetwork::registerPayloads);

        // NeoForge 1.21.1：配置注册从 ModLoadingContext 移到了 ModContainer 上。
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, THTConfigs.CLIENT_SPEC);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, THTConfigs.COMMON_SPEC);
        THTConfigs.update();

        CrashReportCallables.registerCrashCallable(
                Consts.NAME + " Witty Comment",
                WittyComments::randomComment,
                () -> THTConfigs.crashWittyComment);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            TouhouLanguage.ensureInstalled();
        }
    }
}

package io.github.h2sxxa.touhoutweaks.config;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/** 配置加载、重载和服务器开关同步。 */
public class ConfigSync {
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        handle(event);
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        handle(event);
    }

    private static void handle(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (!Consts.MODID.equals(config.getModId())) {
            return;
        }
        THTConfigs.update();
        if (config.getType() == ModConfig.Type.COMMON
                && ServerLifecycleHooks.getCurrentServer() != null) {
            THTNetwork.broadcastServerSwitches();
        }
    }
}

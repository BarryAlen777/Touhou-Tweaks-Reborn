package io.github.h2sxxa.touhoutweaks.config;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * 配置同步（对应原版 config/ConfigSync.java）。
 * 原版监听 ConfigChangedEvent 后调用 ConfigManager.sync 把文件值刷回静态字段；
 * 1.20.1 的等价机制是 mod 总线上的 ModConfigEvent.Loading/Reloading，
 * 这里把文件值刷入 THTConfigs 的缓存字段。
 * 通用配置在服务器上重载时，还会通过 SimpleChannel 把服务器总开关重新同步给所有在线玩家。
 */
@Mod.EventBusSubscriber(modid = Consts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        if (config.getType() == ModConfig.Type.COMMON) {
            // 服务器（含单人游戏的内置服务器）上通用配置加载/重载后，重新广播总开关
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                THTNetwork.broadcastServerSwitches();
            }
        }
    }
}

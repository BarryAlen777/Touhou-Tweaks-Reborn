package io.github.h2sxxa.touhoutweaks.events;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 服务端事件：
 * - 玩家死亡（LOWEST 优先级 + 未取消检查与原版一致）时，经 SimpleChannel
 *   给死亡玩家发送播放包（原模组只在客户端本地播，联机听不到；移植后单人/联机一致）；
 * - 登录、维度切换时同步服务器总开关（移植方案的"登录同步/维度切换"处理）。
 */
@Mod.EventBusSubscriber(modid = Consts.MODID)
public class ServerEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.isCanceled()) {
            if (THTConfigs.serverDeathSound) {
                THTNetwork.sendToPlayer(player, new THTNetwork.PlaySoundPayload(
                        player.getId(), Consts.MODID + ":thtplayerdead"));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            THTNetwork.sendToPlayer(player, new THTNetwork.ServerSwitchPayload(
                    THTConfigs.serverDeathSound, THTConfigs.serverPotionSound));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            THTNetwork.sendToPlayer(player, new THTNetwork.ServerSwitchPayload(
                    THTConfigs.serverDeathSound, THTConfigs.serverPotionSound));
        }
    }
}

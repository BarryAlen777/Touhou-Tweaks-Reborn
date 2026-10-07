package io.github.h2sxxa.touhoutweaks.events;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** 服务端死亡事件与配置同步事件。 */
@EventBusSubscriber(modid = Consts.MODID)
public class ServerEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.isCanceled()
                && THTConfigs.serverDeathSound) {
            THTNetwork.sendToPlayer(player, new THTNetwork.PlaySoundPayload(
                    player.getId(), Consts.MODID + ":thtplayerdead"));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendServerSwitches(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendServerSwitches(player);
        }
    }

    private static void sendServerSwitches(ServerPlayer player) {
        THTNetwork.sendToPlayer(player, new THTNetwork.ServerSwitchPayload(
                THTConfigs.serverDeathSound, THTConfigs.serverPotionSound));
    }
}

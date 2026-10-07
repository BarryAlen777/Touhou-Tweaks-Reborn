package io.github.h2sxxa.touhoutweaks.events;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.lang.TouhouLanguage;
import io.github.h2sxxa.touhoutweaks.network.THTNetwork;
import io.github.h2sxxa.touhoutweaks.regist.ModSounds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenshotEvent;

/** 客户端暂停、药水、截图和网络音效事件。 */
@EventBusSubscriber(modid = Consts.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean pausedLastTick;
    private static ClientLevel lastLevel;
    private static final Map<UUID, Integer> POTION_STATE = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        TouhouLanguage.ensureInstalled();

        boolean pausedNow = mc.screen instanceof PauseScreen;
        if (pausedNow && !pausedLastTick && mc.isWindowActive() && THTConfigs.pauseSound) {
            ModSounds.playClientSound(ModSounds.SE_PLAYERPAUSE);
        }
        pausedLastTick = pausedNow;

        ClientLevel level = mc.level;
        if (level == null) {
            POTION_STATE.clear();
            lastLevel = null;
            return;
        }
        if (lastLevel != level) {
            POTION_STATE.clear();
            lastLevel = level;
        }
        if (!THTConfigs.potionSound || !THTNetwork.serverAllowsPotionSound) {
            POTION_STATE.clear();
            return;
        }

        Set<UUID> presentPlayers = new HashSet<>();
        for (Player player : level.players()) {
            int current = (player.hasEffect(MobEffects.LEVITATION) ? 1 : 0)
                    | (player.hasEffect(MobEffects.DAMAGE_BOOST) ? 2 : 0);
            UUID uuid = player.getUUID();
            int previous = POTION_STATE.getOrDefault(uuid, 0);
            if (current != previous) {
                if ((current & 1) != 0 && (previous & 1) == 0) {
                    ModSounds.playClientSound(ModSounds.SE_LEVITATION);
                }
                if ((current & 2) != 0 && (previous & 2) == 0) {
                    ModSounds.playClientSound(ModSounds.SE_STRENGTH);
                }
                POTION_STATE.put(uuid, current);
            }
            presentPlayers.add(uuid);
        }
        POTION_STATE.keySet().retainAll(presentPlayers);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerScreenshot(ScreenshotEvent event) {
        if (!event.isCanceled() && THTConfigs.screenshotSound) {
            ModSounds.playClientSound(ModSounds.SE_SCREENSHOT);
        }
    }

    public static void onRemoteSoundRequest(int playerId, String soundId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.getId() != playerId || !THTConfigs.deathSound) {
            return;
        }
        ResourceLocation location = ResourceLocation.tryParse(soundId);
        if (location == null || !Consts.MODID.equals(location.getNamespace())) {
            return;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(location);
        if (sound != null) {
            ModSounds.playClientSound(sound);
        }
    }
}

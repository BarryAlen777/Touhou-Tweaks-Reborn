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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 客户端事件（对应原版 events/ClientEvents.java 与两个客户端 Mixin 的行为）。
 *
 * 原模组实现方式 -> 1.20.1 等价实现：
 * - MixinMinecraft.displayInGameMenu（暂停放音效，且仅在窗口激活时）
 *     -> ClientTickEvent 中检测 PauseScreen 打开瞬间 + Minecraft.isWindowActive()；
 * - MixinEntityLivingBase.addPotionEffect（玩家新获得漂浮/力量时放音效）
 *     -> ClientTickEvent 轮询所有玩家身上这两种效果的"新增"边沿（原版也是客户端收到
 *        效果同步后触发，条件同样是"之前没有、现在有了才响"）；
 * - ScreenshotEvent（F2 截图音效）-> 原事件在 1.20.1 依然存在，逐字保留逻辑；
 * - 死亡音效 -> 由服务端事件经 SimpleChannel 下发（见 THTNetwork），本端只查本地开关。
 *
 * 注：1.20.1 official 映射里"力量"效果字段名为 MobEffects.DAMAGE_BOOST（即原 ModEffects.STRENGTH）。
 */
@Mod.EventBusSubscriber(modid = Consts.MODID, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ClientEvents {
    private static boolean pausedLastTick = false;
    private static ClientLevel lastLevel = null;
    /** UUID -> 位掩码：bit0 漂浮(LEVITATION)，bit1 力量(DAMAGE_BOOST)。 */
    private static final Map<UUID, Integer> POTION_STATE = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        // 语言重定向要在每次资源重载后重新挂上（对应原版 MixinI18n 的常驻效果）
        TouhouLanguage.ensureInstalled();

        // 1. 暂停音效：GameMenu 打开的瞬间播放，且窗口处于激活状态（原版 Display.isActive 检查）
        boolean pausedNow = mc.screen instanceof PauseScreen;
        if (pausedNow && !pausedLastTick && mc.isWindowActive() && THTConfigs.pauseSound) {
            ModSounds.playClientSound(ModSounds.SE_PLAYERPAUSE);
        }
        pausedLastTick = pausedNow;

        // 2. 药水音效：漂浮 / 力量 新获得时播放
        ClientLevel level = mc.level;
        if (level == null) {
            POTION_STATE.clear();
            lastLevel = null;
            return;
        }
        if (lastLevel != level) {          // 换世界/换维度：清掉上个存档的状态
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

    /** 截图音效：与原版 ClientEvents.onPlayerScreenshot 相同的事件、优先级与取消检查。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerScreenshot(ScreenshotEvent event) {
        if (!event.isCanceled() && THTConfigs.screenshotSound) {
            ModSounds.playClientSound(ModSounds.SE_SCREENSHOT);
        }
    }

    /** 收到服务端"该玩家死亡"播放包：本地开关开启时在耳边播放（音量 1.0、音调 1.0、master）。 */
    public static void onRemoteSoundRequest(int playerId, String soundId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.getId() != playerId) {
            return;
        }
        if (!THTConfigs.deathSound) {
            return;
        }
        ResourceLocation location = ResourceLocation.tryParse(soundId);
        if (location == null || !Consts.MODID.equals(location.getNamespace())) {
            return;
        }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(location);
        if (sound != null) {
            ModSounds.playClientSound(sound);
        }
    }
}

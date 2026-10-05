package io.github.h2sxxa.touhoutweaks.regist;

import io.github.h2sxxa.touhoutweaks.Consts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 音效注册（对应原版 regist/ModSounds.java）。
 * 原版用 RegistryEvent.Register&lt;SoundEvent&gt; + new SoundEvent(...).setRegistryName(...)，
 * 1.20.1 改用 DeferredRegister，注册时机由 mod 事件总线承担。
 * 五个音效的事件名与 ogg 文件名与原模组完全一致。
 *
 * playClientSound 对应原版 PositionedSoundRecord.getRecord(sound, 1.0F, 1.0F) 后
 * 直接丢给 SoundHandler 的"在耳边播放、不衰减"语义，
 * 1.20.1 的等价写法是 SimpleSoundInstance.forUI(sound, 1.0F)（master 声道、音量 1.0）。
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Consts.MODID);

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(Consts.MODID, name)));
    }

    public static final RegistryObject<SoundEvent> SE_PLAYERDEAD = registerSound("thtplayerdead");
    public static final RegistryObject<SoundEvent> SE_PLAYERPAUSE = registerSound("thtpause");
    public static final RegistryObject<SoundEvent> SE_SCREENSHOT = registerSound("thtscreenshot");
    public static final RegistryObject<SoundEvent> SE_LEVITATION = registerSound("thtlevitation");
    public static final RegistryObject<SoundEvent> SE_STRENGTH = registerSound("thtstrength");

    public static void playClientSound(SoundEvent sound) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && sound != null && mc.getSoundManager() != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    public static void playClientSound(RegistryObject<SoundEvent> sound) {
        if (sound.isPresent()) {
            playClientSound(sound.get());
        }
    }
}

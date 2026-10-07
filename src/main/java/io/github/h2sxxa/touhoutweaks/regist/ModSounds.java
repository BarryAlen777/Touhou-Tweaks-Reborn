package io.github.h2sxxa.touhoutweaks.regist;

import io.github.h2sxxa.touhoutweaks.Consts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** TouhouTweaks 五个音效注册。 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Consts.MODID);

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(Consts.MODID, name)));
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> SE_PLAYERDEAD = registerSound("thtplayerdead");
    public static final DeferredHolder<SoundEvent, SoundEvent> SE_PLAYERPAUSE = registerSound("thtpause");
    public static final DeferredHolder<SoundEvent, SoundEvent> SE_SCREENSHOT = registerSound("thtscreenshot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SE_LEVITATION = registerSound("thtlevitation");
    public static final DeferredHolder<SoundEvent, SoundEvent> SE_STRENGTH = registerSound("thtstrength");

    public static void playClientSound(SoundEvent sound) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() != null && sound != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    public static void playClientSound(DeferredHolder<SoundEvent, SoundEvent> sound) {
        if (sound.isBound()) {
            playClientSound(sound.value());
        }
    }
}

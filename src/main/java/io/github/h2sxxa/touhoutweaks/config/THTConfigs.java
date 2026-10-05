package io.github.h2sxxa.touhoutweaks.config;

import io.github.h2sxxa.touhoutweaks.Consts;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 配置（对应原版 config/THTConfigs.java 的 @Config 注解方案，1.20.1 改用 ForgeConfigSpec）。
 *
 * 原模组只有两个开关：PotionSoundEffect、PauseSoundEffect。
 * 按移植方案"配置化重做"：每项功能一个开关，默认值全部保持原模组行为（全部开启），
 * 玩家可以单独禁用任何一项。
 *
 * - 客户端配置 config/touhoutweaks-client.toml：本端播放/显示行为；
 * - 通用配置 config/touhoutweaks.toml：服务器侧总开关与崩溃报告彩蛋，
 *   多人游戏中服务器总开关会通过网络同步给客户端（见 network/THTNetwork.java）。
 */
public class THTConfigs {
    /** 缓存值：默认全部为 true，与原模组行为一致；配置加载/重载后刷新。 */
    public static volatile boolean pauseSound = true;           // 原 PauseSoundEffect
    public static volatile boolean potionSound = true;          // 原 PotionSoundEffect
    public static volatile boolean deathSound = true;
    public static volatile boolean screenshotSound = true;
    public static volatile boolean touhouTextRedirect = true;   // "少女祈祷中……"
    public static volatile boolean serverDeathSound = true;     // 服务器总开关（通用配置）
    public static volatile boolean serverPotionSound = true;    // 服务器总开关（通用配置）
    public static volatile boolean crashWittyComment = true;

    private static ForgeConfigSpec.BooleanValue cPause;
    private static ForgeConfigSpec.BooleanValue cPotion;
    private static ForgeConfigSpec.BooleanValue cDeath;
    private static ForgeConfigSpec.BooleanValue cScreenshot;
    private static ForgeConfigSpec.BooleanValue cRedirect;

    private static ForgeConfigSpec.BooleanValue gDeath;
    private static ForgeConfigSpec.BooleanValue gPotion;
    private static ForgeConfigSpec.BooleanValue gWitty;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("车万音效：各功能独立开关，默认行为与原 TouhouTweaks 一致").push("sounds");
        cPause = b.comment("暂停游戏时播放音效（对应原模组 PauseSoundEffect）").define("pauseSound", true);
        cPotion = b.comment("玩家获得漂浮/力量效果时播放音效（对应原模组 PotionSoundEffect）").define("potionSound", true);
        cDeath = b.comment("玩家死亡时播放音效（原功能，新增可单独禁用）").define("deathSound", true);
        cScreenshot = b.comment("截图时播放音效（原功能，新增可单独禁用）").define("screenshotSound", true);
        b.pop();
        b.comment("界面文字").push("text");
        cRedirect = b.comment("世界加载等界面显示车万文字（少女祈祷中……）。对应原模组的翻译重定向功能").define("touhouTextRedirect", true);
        b.pop();
        CLIENT_SPEC = b.build();
    }

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("服务器侧总开关。多人游戏时会同步到客户端，客户端本地开关仍然生效（两者都开才播放）").push("server");
        gDeath = b.comment("允许死亡音效").define("deathSound", true);
        gPotion = b.comment("允许药水效果音效").define("potionSound", true);
        b.pop();
        b.comment("崩溃报告").push("crash");
        gWitty = b.comment("在崩溃报告中随机加入一条车万风 Witty Comment（对应原模组 MixinCrashReport）").define("wittyComment", true);
        b.pop();
        COMMON_SPEC = b.build();
    }

    /** 把 spec 值刷入缓存字段；由 ConfigSync 在配置加载/重载时调用。 */
    public static void update() {
        if (CLIENT_SPEC.isLoaded()) {
            pauseSound = cPause.get();
            potionSound = cPotion.get();
            deathSound = cDeath.get();
            screenshotSound = cScreenshot.get();
            touhouTextRedirect = cRedirect.get();
        }
        if (COMMON_SPEC.isLoaded()) {
            serverDeathSound = gDeath.get();
            serverPotionSound = gPotion.get();
            crashWittyComment = gWitty.get();
        }
    }

    public static String modId() {
        return Consts.MODID;
    }
}

package io.github.h2sxxa.touhoutweaks.config;

import io.github.h2sxxa.touhoutweaks.Consts;

import net.neoforged.neoforge.common.ModConfigSpec;

/** TouhouTweaks 配置。 */
public class THTConfigs {
    public static volatile boolean pauseSound = true;
    public static volatile boolean potionSound = true;
    public static volatile boolean deathSound = true;
    public static volatile boolean screenshotSound = true;
    public static volatile boolean touhouTextRedirect = true;
    public static volatile boolean serverDeathSound = true;
    public static volatile boolean serverPotionSound = true;
    public static volatile boolean crashWittyComment = true;

    private static final ModConfigSpec.BooleanValue cPause;
    private static final ModConfigSpec.BooleanValue cPotion;
    private static final ModConfigSpec.BooleanValue cDeath;
    private static final ModConfigSpec.BooleanValue cScreenshot;
    private static final ModConfigSpec.BooleanValue cRedirect;
    private static final ModConfigSpec.BooleanValue gDeath;
    private static final ModConfigSpec.BooleanValue gPotion;
    private static final ModConfigSpec.BooleanValue gWitty;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("车万音效：各功能独立开关，默认行为与原 TouhouTweaks 一致").push("sounds");
        cPause = b.comment("暂停游戏时播放音效").define("pauseSound", true);
        cPotion = b.comment("获得漂浮或力量效果时播放音效").define("potionSound", true);
        cDeath = b.comment("玩家死亡时播放音效").define("deathSound", true);
        cScreenshot = b.comment("截图时播放音效").define("screenshotSound", true);
        b.pop();
        b.comment("界面文字").push("text");
        cRedirect = b.comment("加载界面显示车万文字").define("touhouTextRedirect", true);
        b.pop();
        CLIENT_SPEC = b.build();
    }

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.comment("服务器侧总开关，多人游戏时同步到客户端").push("server");
        gDeath = b.comment("允许死亡音效").define("deathSound", true);
        gPotion = b.comment("允许药水效果音效").define("potionSound", true);
        b.pop();
        b.comment("崩溃报告").push("crash");
        gWitty = b.comment("在崩溃报告中加入车万风彩蛋").define("wittyComment", true);
        b.pop();
        COMMON_SPEC = b.build();
    }

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

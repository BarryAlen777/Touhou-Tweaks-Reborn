package io.github.h2sxxa.touhoutweaks.crash;

import io.github.h2sxxa.touhoutweaks.Consts;

import java.util.Arrays;

/**
 * 车万风崩溃报告 Witty Comment（对应原版 MixinCrashReport）。
 *
 * 原版在 CrashReport.getWittyComment 的 RETURN 处注入，把原版彩蛋列表
 * 与模组彩蛋列表合并后按 System.nanoTime() 取模随机一条。
 * NeoForge 1.21.1 的该方法为 private static 且字符串硬编码在方法体内，无法同样注入；
 * 改走 NeoForge 官方扩展点 CrashReportCallables.registerCrashCallable
 * （由模组入口在构造时注册，本端崩溃报告的 System Details 中会多出一条随机彩蛋，
 * 与原版共用同一个取模随机逻辑与同样的"取不到就返回 Witty comment unavailable :("兜底）。
 */
public class WittyComments {
    /** 原版 1.12.2 MixinCrashReport 中复制的 vanilla 列表，逐字保留。 */
    private static final String[] VANILLA_COMMENTS = new String[] {
            "Who set us up the TNT?",
            "Everything's going to plan. No, really, that was supposed to happen.",
            "Uh... Did I do that?",
            "Oops.",
            "Why did you do that?",
            "I feel sad now :(",
            "My bad.",
            "I'm sorry, Dave.",
            "I let you down. Sorry :(",
            "On the bright side, I bought you a teddy bear!",
            "Daisy, daisy...",
            "Oh - I know what I did wrong!",
            "Hey, that tickles! Hehehe!",
            "I blame Dinnerbone.",
            "You should try our sister game, Minceraft!",
            "Don't be sad. I'll do better next time, I promise!",
            "Don't be sad, have a hug! <3",
            "I just don't know what went wrong :(",
            "Shall we play a game?",
            "Quite honestly, I wouldn't worry myself about that.",
            "I bet Cylons wouldn't have this problem.",
            "Sorry :(",
            "Surprise! Haha. Well, this is awkward.",
            "Would you like a cupcake?",
            "Hi. I'm Minecraft, and I'm a crashaholic.",
            "Ooh. Shiny.",
            "This doesn't make any sense!",
            "Why is it breaking :(",
            "Don't do that.",
            "Ouch. That hurt :(",
            "You're mean.",
            "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
            "There are four lights!",
            "But it works on my machine."
    };

    private static final String[] MERGED;

    static {
        MERGED = Arrays.copyOf(VANILLA_COMMENTS,
                VANILLA_COMMENTS.length + Consts.modWittyComment.length);
        System.arraycopy(Consts.modWittyComment, 0, MERGED,
                VANILLA_COMMENTS.length, Consts.modWittyComment.length);
    }

    /** 与原版相同的随机逻辑：System.nanoTime() % 列表长度。 */
    public static String randomComment() {
        try {
            return MERGED[(int) (System.nanoTime() % MERGED.length)];
        } catch (Throwable ignored) {
            return "Witty comment unavailable :(";
        }
    }
}

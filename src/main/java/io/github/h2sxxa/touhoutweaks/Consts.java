package io.github.h2sxxa.touhoutweaks;

/**
 * 全局常量。移植自 TouhouTweaks（原作者 H2Sxxa，GPL-3.0-only，
 * https://github.com/H2Sxxa/TouhouTweaks ），modid 与包名保持原样。
 *
 * 原 1.12.2 版本这里的 DEPENDENCIES 要求 mixinbooter 前置、REMAP/DONT_REMAP
 * 服务于 Mixin 注入；NeoForge 1.21.1 移植版不再使用 Mixin（全部改走事件与官方扩展点），
 * 因此相应字段移除，见 NOTICE.md 的移植变更记录。
 */
public class Consts {
    public final static String MODID = "touhoutweaks";
    public final static String NAME = "TouhouTweaks";

    /** 车万风崩溃报告 Witty Comment，逐字保留原仓库 Consts.modWittyComment 内容。 */
    public static String[] modWittyComment = new String[]{
        "Cirno freezed your minecraft \u15DC\u02F0\u15DC!!!",
        "Hey,it looks like Marisa stole something important!",
        "Reimu uses her purification rod defeating you.....",
        "Sakuya stops the world,including your game!",
        "Yukari is a BBA %#$@%^&!",
        "Yuyuko is hungry and eats up your game \u15DC\u02EC\u15DC!",
        "Game Crash is awwwwwwful,but Koishi is cute.",
        "Please vote for Reimu! Please vote for Reimu! Please vote for Reimu!!!",
        "Flandre is champion!",
        "Shanghai,Penglai,Langsy,Himalayan,Tibetan,Kyoto,London,Lucy,Aurelian!",
        "Sanae!Please cause miracles to occur and save my Game from crashing!"
    };
}

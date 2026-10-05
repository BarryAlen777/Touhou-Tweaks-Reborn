package io.github.h2sxxa.touhoutweaks.lang;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;

import java.util.Map;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/**
 * 翻译重定向（对应原版 MixinI18n）。
 *
 * 原版在 I18n.format 处重定向：任何翻译键 K，若存在 touhoutweaks.K 条目，就用它。
 * 1.20.1 不再需要 Mixin：net.minecraft.locale.Language 提供了公开的
 * getInstance()/inject()，这里注入一个包装实现，查找时先试 "touhoutweaks." + key，
 * 找不到再回落到原语言映射，语义与原版逐字一致。
 *
 * 资源重载（换语言等）时原版会 Language.inject 新的 ClientLanguage，
 * 覆盖掉我们的包装，因此 ClientTickEvent 里持续 ensureInstalled() 重新包装，
 * 效果等同原版 Mixin 的常驻重定向。
 *
 * 实际展示的替换由语言文件里的条目决定：
 * touhoutweaks.progress.working / connect.connecting / connect.joining
 * 均为"少女祈祷中……"（1.20.1 加载/加入界面显示的正是这些键；
 * 1.12.2 的 menu.loadingLevel 条目也保留，行为对齐）。
 */
public final class TouhouLanguage extends Language {
    private static final String PREFIX = Consts.MODID + ".";

    private volatile Language base;

    private TouhouLanguage(Language base) {
        this.base = base;
    }

    /** 若当前生效的语言不是本包装（首次加载或资源重载后），重新包装并注入。 */
    public static void ensureInstalled() {
        Language current = Language.getInstance();
        if (!(current instanceof TouhouLanguage)) {
            Language.inject(new TouhouLanguage(current));
        }
    }

    @Override
    public String getOrDefault(String key, String defaultString) {
        if (THTConfigs.touhouTextRedirect && !key.startsWith(PREFIX)) {
            String redirected = PREFIX + key;
            if (base.has(redirected)) {
                return base.getOrDefault(redirected, defaultString);
            }
        }
        return base.getOrDefault(key, defaultString);
    }

    @Override
    public boolean has(String key) {
        if (THTConfigs.touhouTextRedirect && !key.startsWith(PREFIX) && base.has(PREFIX + key)) {
            return true;
        }
        return base.has(key);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return base.isDefaultRightToLeft();
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText text) {
        return base.getVisualOrder(text);
    }

    @Override
    public Map<String, String> getLanguageData() {
        return base.getLanguageData();
    }
}

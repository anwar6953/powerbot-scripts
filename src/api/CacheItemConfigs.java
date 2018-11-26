package api;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.rt4.CacheItemConfig;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query-based  system for finding a CacheItemConfig based on its attributes.
 * Based off Component Query by Coma
 * @author Nom
 */
public class CacheItemConfigs extends AbstractQuery<CacheItemConfigs, CacheItemConfig, ClientContext> {
    final int HIGHEST_ITEM_ID = 30000;
    final HashMap<Integer, CacheItemConfig> allItems = new HashMap<>();

    public CacheItemConfigs(ClientContext ctx) {
        super(ctx);
    }

    @Override
    protected CacheItemConfigs getThis() {
        return this;
    }

    @Override
    protected List<CacheItemConfig> get() {
        if (!allItems.isEmpty()) return new ArrayList<>(allItems.values());
        for (int i = 0; i < HIGHEST_ITEM_ID; i++) {
            CacheItemConfig c = CacheItemConfig.load(i);
            if (!c.valid()) continue;
            allItems.put(i,c);
        }
        return new ArrayList<>(allItems.values());
    }

    @Override
    public CacheItemConfig nil() {
        return CacheItemConfig.load(-1);
    }

    public CacheItemConfigs id(final int index) {
        return select(CacheItemConfig -> CacheItemConfig.index == index);
    }

    public CacheItemConfigs members() {
        return select(CacheItemConfig -> CacheItemConfig.members);
    }

    public CacheItemConfigs stackable() {
        return select(CacheItemConfig -> CacheItemConfig.stackable);
    }
    public CacheItemConfigs cosmetic() {
        return select(CacheItemConfig -> CacheItemConfig.cosmetic);
    }

    public CacheItemConfigs noted() {
        return select(CacheItemConfig -> CacheItemConfig.noted);
    }

    public CacheItemConfigs tradeable() {
        return select(CacheItemConfig -> CacheItemConfig.tradeable);
    }

    public CacheItemConfigs name(final String... text) {
        return select(CacheItemConfig -> {
            for (String s : text) {
                if (CacheItemConfig.name.toLowerCase().contains(s.toLowerCase())) {
                    return true;
                }
            }
            return false;
        });
    }

    public CacheItemConfigs name(final Pattern pattern) {
        return select(CacheItemConfig -> pattern.matcher(CacheItemConfig.name).find());
    }

    public CacheItemConfigs actions(final String... text) {
        return select(CacheItemConfig -> {
            for (String s : text) {
                for (String t : CacheItemConfig.actions) {
                    if (t != null && t.toLowerCase().contains(s.toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public CacheItemConfigs actions(final Pattern pattern) {
        return select(CacheItemConfig -> {
            for (String t : CacheItemConfig.actions) {
                if (t != null && pattern.matcher(t).find()) {
                    return true;
                }
            }
            return false;
        });
    }

    public CacheItemConfigs groundActions(final String... text) {
        return select(CacheItemConfig -> {
            for (String s : text) {
                for (String t : CacheItemConfig.groundActions) {
                    if (t != null && t.toLowerCase().contains(s.toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public CacheItemConfigs groundActions(final Pattern pattern) {
        return select(CacheItemConfig -> {
            for (String t : CacheItemConfig.groundActions) {
                if (t != null && pattern.matcher(t).find()) {
                    return true;
                }
            }
            return false;
        });
    }

    public CacheItemConfigs equipActions(final String... text) {
        return select(CacheItemConfig -> {
            for (String s : text) {
                for (String t : CacheItemConfig.equipActions) {
                    if (t != null && t.toLowerCase().contains(s.toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public CacheItemConfigs equipActions(final Pattern pattern) {
        return select(CacheItemConfig -> {
            for (String t : CacheItemConfig.equipActions) {
                if (t != null && pattern.matcher(t).find()) {
                    return true;
                }
            }
            return false;
        });
    }
    public CacheItemConfigs value(final int... texture) {
        return select(CacheItemConfig -> {
            for (int i : texture) {
                if (CacheItemConfig.value == i) {
                    return true;
                }
            }
            return false;
        });
    }

    public CacheItemConfigs modelId(final int... modelId) {
        return select(CacheItemConfig -> {
            for (int i : modelId) {
                if (CacheItemConfig.modelId == i) {
                    return true;
                }
            }
            return false;
        });
    }

}
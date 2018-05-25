package scripts.enchant_jewellery;

import org.powerbot.script.rt4.Magic;
import scripts.ID;

public class Presets {
    public static final EnchantObj RECOIL =
            new EnchantObj(Magic.Spell.ENCHANT_LEVEL_1_JEWELLERY,
                    ID.STAFF_OF_WATER,
                    new int[] {ID.COSMIC_RUNE},
                    ID.SAPPHIRE_RING);
}

package scripts.enchant_jewellery;

import org.powerbot.script.rt4.Magic;

public class EnchantObj {
    private final Magic.MagicSpell spell;
    private final int STAFF_ID;
    private final int[] RUNES;
    private final int JEWELLERY;

    public EnchantObj(Magic.MagicSpell spell, int STAFF_ID, int[] RUNES, int JEWELLERY) {
        this.spell = spell;
        this.STAFF_ID = STAFF_ID;
        this.RUNES = RUNES;
        this.JEWELLERY = JEWELLERY;
    }

    /**
     * Gets spell
     *
     * @return value of spell
     */
    public Magic.MagicSpell getSpell() {
        return spell;
    }

    /**
     * Gets STAFF_ID
     *
     * @return value of STAFF_ID
     */
    public int getSTAFF_ID() {
        return STAFF_ID;
    }

    /**
     * Gets RUNES
     *
     * @return value of RUNES
     */
    public int[] getRUNES() {
        return RUNES;
    }

    /**
     * Gets JEWELLERY
     *
     * @return value of JEWELLERY
     */
    public int getJEWELLERY() {
        return JEWELLERY;
    }
}

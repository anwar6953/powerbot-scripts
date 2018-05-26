package scripts.edgeville_jewellery;

import scripts.ID;

import java.util.ArrayList;

public class Presets {
    public static final JewelObj
        JADE_AMULET = new JewelObj("Jade amulet(u)", ID.JADE, ID.SILVER_BAR, ID.AMULET_MOULD, ID.JADE_AMULET_U),
        OPAL_AMULET = new JewelObj("Opal amulet(u)", ID.OPAL, ID.SILVER_BAR, ID.AMULET_MOULD, ID.OPAL_AMULET_U);

    public static final ArrayList<JewelObj> SILVER = new ArrayList<>();

    static {
        SILVER.add(JADE_AMULET);
        SILVER.add(OPAL_AMULET);
    }
}

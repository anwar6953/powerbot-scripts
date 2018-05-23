package scripts.AIOHerblore;

import api.Bank;

import java.util.Arrays;
import java.util.HashMap;

class CombineObjj {
    final String name;
    final String input;
    final int[] ids;
    final int[] qs;
    final int size;


    private CombineObjj(String name, String input, ItemObj... itemObjs) {
        this.name = name;
        this.input = input;
        this.ids = Arrays.stream(itemObjs).mapToInt(ItemObj::getId).toArray();
        this.qs = Arrays.stream(itemObjs).mapToInt(ItemObj::getQ).toArray();
        this.size = ids.length;
    }


    public static final CombineObjj nil = new CombineObjj("","",new ItemObj(-1,0));

    static final CombineObjj[] allItems =
            {
                new CombineObjj("Guam","",new ItemObj(ID.GUAM_GRIMY,28)),
                new CombineObjj("Marrentill","",new ItemObj(ID.MARRENTILL_GRIMY,28)),
                new CombineObjj("Tarromin","",new ItemObj(ID.TARROMIN_GRIMY,28)),
                new CombineObjj("Harralander","",new ItemObj(ID.HARRALANDER_GRIMY,28)),
                new CombineObjj("Ranarr","",new ItemObj(ID.RANARR_GRIMY,28)),
                new CombineObjj("Irit","",new ItemObj(ID.IRIT_GRIMY,28)),
                new CombineObjj("Avantoe","",new ItemObj(ID.AVANTOE_GRIMY,28)),
                new CombineObjj("Kwuarm","",new ItemObj(ID.KWUARM_GRIMY,28)),
                new CombineObjj("Cadantine","",new ItemObj(ID.CADANTINE_GRIMY,28)),
                new CombineObjj("Dwarf","",new ItemObj(ID.DWARF_GRIMY,28)),
                new CombineObjj("Torstol","",new ItemObj(ID.TORSTOL_GRIMY,28)),
                new CombineObjj("Lantadyme","",new ItemObj(ID.LANTADYME_GRIMY,28)),
                new CombineObjj("Toadflax","",new ItemObj(ID.TOADFLAX_GRIMY,28)),
                new CombineObjj("Snapdragon","",new ItemObj(ID.SNAPDRAGON_GRIMY,28)),


                new CombineObjj("Guam unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.GUAM_CLEAN,14)),
                new CombineObjj("Marrentill unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.MARRENTILL_CLEAN,14)),
                new CombineObjj("Tarromin unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.TARROMIN_CLEAN,14)),
                new CombineObjj("Harralander unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.HARRALANDER_CLEAN,14)),
                new CombineObjj("Ranarr unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.RANARR_CLEAN,14)),
                new CombineObjj("Irit unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.IRIT_CLEAN,14)),
                new CombineObjj("Avantoe unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.AVANTOE_CLEAN,14)),
                new CombineObjj("Kwuarm unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.KWUARM_CLEAN,14)),
                new CombineObjj("Cadantine unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.CADANTINE_CLEAN,14)),
                new CombineObjj("Dwarf unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.DWARF_CLEAN,14)),
                new CombineObjj("Torstol unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.TORSTOL_CLEAN,14)),
                new CombineObjj("Lantadyme unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.LANTADYME_CLEAN,14)),
                new CombineObjj("Toadflax unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.TOADFLAX_CLEAN,14)),
                new CombineObjj("Snapdragon unf","1",new ItemObj(ID.VIAL_WATER,14), new ItemObj(ID.SNAPDRAGON_CLEAN,14)),

                new CombineObjj("Attack potion","1",new ItemObj(ID.GUAM_UNF,14), new ItemObj(ID.EYE_OF_NEWT,14)),
                new CombineObjj("Antipoison","1",new ItemObj(ID.MARRENTILL_UNF,14), new ItemObj(ID.UNICORN_DUST,14)),
                new CombineObjj("Strength potion","1",new ItemObj(ID.TARROMIN_UNF,14), new ItemObj(ID.LIMPWURT_ROOT,14)),
                new CombineObjj("Serum 207 ","1",new ItemObj(ID.TARROMIN_UNF,14), new ItemObj(ID.ASHES,14)),
                new CombineObjj("Compost potion","1",new ItemObj(ID.HARRALANDER_UNF,14), new ItemObj(ID.VOLCANIC_ASH,14)),
                new CombineObjj("Restore potion","1",new ItemObj(ID.HARRALANDER_UNF,14), new ItemObj(ID.RED_SPIDER_EGG,14)),
                new CombineObjj("Energy potion","1",new ItemObj(ID.HARRALANDER_UNF,14), new ItemObj(ID.CHOCOLATE_DUST,14)),
                new CombineObjj("Defence potion","1",new ItemObj(ID.RANARR_UNF,14), new ItemObj(ID.WHITEBERRY,14)),
                new CombineObjj("Agility potion","1",new ItemObj(ID.TOADFLAX_UNF,14), new ItemObj(ID.TOAD_LEG,14)),
                new CombineObjj("Combat potion","1",new ItemObj(ID.HARRALANDER_UNF,14), new ItemObj(ID.GOAT_DUST,14)),
                new CombineObjj("Prayer potion","1",new ItemObj(ID.RANARR_UNF,14), new ItemObj(ID.SNAPE_GRASS,14)),
                new CombineObjj("Super attack","1",new ItemObj(ID.IRIT_UNF,14), new ItemObj(ID.EYE_OF_NEWT,14)),
                new CombineObjj("Superantipoison","1",new ItemObj(ID.IRIT_UNF,14), new ItemObj(ID.UNICORN_DUST,14)),
                new CombineObjj("Fishing potion","1",new ItemObj(ID.AVANTOE_UNF,14), new ItemObj(ID.SNAPE_GRASS,14)),
                new CombineObjj("Super energy","1",new ItemObj(ID.AVANTOE_UNF,14), new ItemObj(ID.MORT_FUNGUS,14)),
                new CombineObjj("Hunter potion","1",new ItemObj(ID.AVANTOE_UNF,14), new ItemObj(ID.KEBBIT_DUST,14)),
                new CombineObjj("Super strength","1",new ItemObj(ID.KWUARM_UNF,14), new ItemObj(ID.LIMPWURT_ROOT,14)),
                new CombineObjj("Super restore","1",new ItemObj(ID.SNAPDRAGON_UNF,14), new ItemObj(ID.RED_SPIDER_EGG,14)),
                new CombineObjj("Antifire potion","1",new ItemObj(ID.LANTADYME_UNF,14), new ItemObj(ID.DRAGON_DUST,14)),
                new CombineObjj("Ranging potion","1",new ItemObj(ID.DWARF_UNF,14), new ItemObj(ID.WINE_OF_ZAM,14)),
                new CombineObjj("Magic potion","1",new ItemObj(ID.LANTADYME_UNF,14), new ItemObj(ID.POTATO_CACTUS,14)),
                new CombineObjj("Stamina potion","1",new ItemObj(ID.AMYLASE, Bank.Amount.ALL.getValue()), new ItemObj(ID.SUPER_ENERGY_4,27)),
                new CombineObjj("Zamorak brew","1",new ItemObj(ID.TORSTOL_UNF,14), new ItemObj(ID.JANGERBERRY,14)),
                new CombineObjj("Saradomin brew","1",new ItemObj(ID.TOADFLAX_UNF,14), new ItemObj(ID.CRUSHED_NEST,14)),
                new CombineObjj("Extended antifire ","1",new ItemObj(ID.LAVA_SCALE_SHARD, Bank.Amount.ALL.getValue()), new ItemObj(ID.ANTI_FIRE_4,27)),
                new CombineObjj("Anti-venom","1",new ItemObj(ID.ZULRAH_SCALE,Bank.Amount.ALL.getValue()), new ItemObj(ID.ANTIDOTE_PP_4,27)),
                new CombineObjj("Super combat potion","1",new ItemObj(ID.TORSTOL_CLEAN,7), new ItemObj(ID.SUPER_ATTACK_4,7), new ItemObj(ID.SUPER_STRENGTH_4,7), new ItemObj(ID.SUPER_DEFENCE_4,7)),
                new CombineObjj("Anti-venom+","1",new ItemObj(ID.TORSTOL_CLEAN,14), new ItemObj(ID.ANTI_VENOM_4,14)),

            };

}


package scripts.AIOFletcher;

import api.Bank;

import java.util.Arrays;

class CombineObj {
    final String name;
    final String input;
    final int[] ids;
    final int[] qs;
    final int size;

    private CombineObj(String name, String input, ItemObj... itemObjs) {
        this.name = name;
        this.input = input;
        this.ids = Arrays.stream(itemObjs).mapToInt(ItemObj::getId).toArray();
        this.qs = Arrays.stream(itemObjs).mapToInt(ItemObj::getQ).toArray();
        this.size = ids.length;
    }


    public static final CombineObj nil = new CombineObj("","",new ItemObj(-1,0));

    static final CombineObj[] allItems =
            {
                    //
                    //DARTS
                    //
                    new CombineObj("Bronze darts","",new ItemObj(ID.BRONZE_D_TIP, Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Iron darts","",new ItemObj(ID.IRON_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Steel darts","",new ItemObj(ID.STEEL_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Mithril darts","",new ItemObj(ID.MITH_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Adamant darts","",new ItemObj(ID.ADDY_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Rune darts","",new ItemObj(ID.RUNE_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragon darts","",new ItemObj(ID.DRAGON_D_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    //
                    //METAL BOLTS
                    //
                    new CombineObj("Bronze bolts","",new ItemObj(ID.BRONZE_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Iron bolts","",new ItemObj(ID.IRON_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Steel bolts","",new ItemObj(ID.STEEL_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Mithril bolts","",new ItemObj(ID.MITH_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Adamant bolts","",new ItemObj(ID.ADDY_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Rune bolts","",new ItemObj(ID.RUNE_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragon bolts","",new ItemObj(ID.DRAGON_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),
                    new CombineObj("Broad bolts","",new ItemObj(ID.BROAD_BOLT_U,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),

                    //
                    //GEM TIPPED BOLTS
                    //
                    new CombineObj("Opal bolts","1",new ItemObj(ID.OPAL_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.BRONZE_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Pearl bolts","1",new ItemObj(ID.PEARL_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.IRON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Topaz bolts","1",new ItemObj(ID.TOPAZ_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.STEEL_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Sapphire bolts","1",new ItemObj(ID.SAPPHIRE_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.MITH_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Emerald bolts","1",new ItemObj(ID.EMERALD_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.MITH_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Ruby bolts","1",new ItemObj(ID.RUBY_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.ADDY_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Diamond bolts","1",new ItemObj(ID.DIAMOND_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.ADDY_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragonstone bolts","1",new ItemObj(ID.DRAGONSTONE_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.RUNE_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Onyx bolts","1",new ItemObj(ID.ONYX_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.RUNE_BOLT,Bank.Amount.ALL.getValue())),

                    new CombineObj("Opal dragon bolts","1",new ItemObj(ID.OPAL_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Pearl dragon bolts","1",new ItemObj(ID.PEARL_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Jade dragon bolts","1",new ItemObj(ID.JADE_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Topaz dragon bolts","1",new ItemObj(ID.TOPAZ_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Sapphire dragon bolts","1",new ItemObj(ID.SAPPHIRE_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Emerald dragon bolts","1",new ItemObj(ID.EMERALD_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Ruby dragon bolts","1",new ItemObj(ID.RUBY_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Diamond dragon bolts","1",new ItemObj(ID.DIAMOND_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragonstone dragon bolts","1",new ItemObj(ID.DRAGONSTONE_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Onyx dragon bolts","1",new ItemObj(ID.ONYX_TIPS,Bank.Amount.ALL.getValue()),new ItemObj(ID.DRAGON_BOLT,Bank.Amount.ALL.getValue())),

                    //
                    //GEM TIPS
                    //
                    new CombineObj("Opal bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.OPAL_CUT,27)),
                    new CombineObj("Pearl bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.PEARL_CUT,27)),
                    new CombineObj("Topaz bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.TOPAZ_CUT,27)),
                    new CombineObj("Sapphire bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.SAPPHIRE_CUT,27)),
                    new CombineObj("Emerald bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.EMERALD_CUT,27)),
                    new CombineObj("Ruby bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.RUBY_CUT,27)),
                    new CombineObj("Diamond bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.DIAMOND_CUT,27)),
                    new CombineObj("Dragonstone bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.DRAGONSTONE_CUT,27)),
                    new CombineObj("Onyx bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.ONYX_CUT,27)),
                    new CombineObj("Amethyst arrow tips","2",new ItemObj(ID.CHISEL,1),new ItemObj(ID.AMETHYST_CUT,27)),
                    new CombineObj("Amethyst bolt tips","1",new ItemObj(ID.CHISEL,1),new ItemObj(ID.AMETHYST_CUT,27)),
                    new CombineObj("Amethyst javelin tips","3",new ItemObj(ID.CHISEL,1),new ItemObj(ID.AMETHYST_CUT,27)),

                    //
                    //ARROWS
                    //
                    new CombineObj("Headless arrows","1",new ItemObj(ID.ARROW_SHAFT,Bank.Amount.ALL.getValue()),new ItemObj(ID.FEATHER,Bank.Amount.ALL.getValue())),

                    new CombineObj("Bronze arrows","1",new ItemObj(ID.BRONZE_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Iron arrows","1",new ItemObj(ID.IRON_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Steel arrows","1",new ItemObj(ID.STEEL_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Mithril arrows","1",new ItemObj(ID.MITH_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Adamant arrows","1",new ItemObj(ID.ADDY_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Rune arrows","1",new ItemObj(ID.RUNE_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Amethyst arrows","1",new ItemObj(ID.AMETHYST_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragon arrows","1",new ItemObj(ID.DRAGON_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),
                    new CombineObj("Broad arrows","1",new ItemObj(ID.BROAD_ARROW_TIP,Bank.Amount.ALL.getValue()),new ItemObj(ID.HEADLESS_ARROW,Bank.Amount.ALL.getValue())),

                    //
                    //JAVELINS
                    //

                    new CombineObj("Javelin shafts","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.NORMAL_LOG,Bank.Amount.ALL.getValue())),

                    new CombineObj("Bronze javelins","1",new ItemObj(ID.BRONZE_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Iron javelins","1",new ItemObj(ID.IRON_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Steel javelins","1",new ItemObj(ID.STEEL_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Mithril javelins","1",new ItemObj(ID.MITH_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Adamant javelins","1",new ItemObj(ID.ADDY_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Rune javelins","1",new ItemObj(ID.RUNE_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Amethyst javelins","1",new ItemObj(ID.AMETHYST_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),
                    new CombineObj("Dragon javelins","1",new ItemObj(ID.DRAGON_JAVELIN_HEAD,Bank.Amount.ALL.getValue()),new ItemObj(ID.JAVELIN_SHAFT,Bank.Amount.ALL.getValue())),


                    //
                    //LOGS
                    //
                    new CombineObj("Magic longbow","1",new ItemObj(ID.MAGIC_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Magic longbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAGIC_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(magic)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAGIC_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Magic shortbow","1",new ItemObj(ID.MAGIC_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Magic shortbow u","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAGIC_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Magic xbow stock","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAGIC_LOG, Bank.Amount.ALL.getValue())),

                    new CombineObj("Yew longbow","1",new ItemObj(ID.YEW_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Yew longbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.YEW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(yew)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.YEW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Yew shortbow","1",new ItemObj(ID.YEW_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Yew shortbow u","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.YEW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Yew xbow stock","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.YEW_LOG, Bank.Amount.ALL.getValue())),

                    new CombineObj("Maple longbow","1",new ItemObj(ID.MAPLE_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Maple longbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAPLE_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(maple)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAPLE_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Maple shortbow","1",new ItemObj(ID.MAPLE_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Maple shortbow u","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAPLE_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Maple xbow stock","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAPLE_LOG, Bank.Amount.ALL.getValue())),

                    new CombineObj("Willow longbow","1",new ItemObj(ID.WILLOW_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Willow longbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.WILLOW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(willow)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.WILLOW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Willow shortbow","1",new ItemObj(ID.WILLOW_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Willow shortbow u","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.WILLOW_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Willow xbow stock","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.WILLOW_LOG, Bank.Amount.ALL.getValue())),

                    new CombineObj("Oak longbow","1",new ItemObj(ID.OAK_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Oak longbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.OAK_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(oak)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.OAK_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Oak shortbow","1",new ItemObj(ID.OAK_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Oak shortbow u","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.OAK_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Oak xbow stock","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.OAK_LOG, Bank.Amount.ALL.getValue())),

                    new CombineObj("Normal longbow","1",new ItemObj(ID.NORMAL_LB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Normal longbow u","4",new ItemObj(ID.KNIFE,1),new ItemObj(ID.NORMAL_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Shafts(Normal)","1",new ItemObj(ID.KNIFE,1),new ItemObj(ID.NORMAL_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Normal shortbow","1",new ItemObj(ID.NORMAL_SB_U,14),new ItemObj(ID.BOWSTRING,14)),
                    new CombineObj("Normal shortbow u","3",new ItemObj(ID.KNIFE,1),new ItemObj(ID.NORMAL_LOG, Bank.Amount.ALL.getValue())),
                    new CombineObj("Normal xbow stock","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.NORMAL_LOG, Bank.Amount.ALL.getValue())),

                    //SHIELDS
                    new CombineObj("Oak shield","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.OAK_LOG,26)),
                    new CombineObj("Willow shield","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.WILLOW_LOG, 26)),
                    new CombineObj("Maple shield","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAPLE_LOG, 26)),
                    new CombineObj("Yew shield","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.YEW_LOG,26)),
                    new CombineObj("Magic shield","5",new ItemObj(ID.KNIFE,1),new ItemObj(ID.MAGIC_LOG, 26)),
                    new CombineObj("Redwood shield","2",new ItemObj(ID.KNIFE,1),new ItemObj(ID.REDWOOD_LOG, 26)),

                    //MOLTEN GLASS
                    new CombineObj("Unpowered orb","6",new ItemObj(1785,1),new ItemObj(1775,27)),
                    new CombineObj("Lantern lens","7",new ItemObj(1785,1),new ItemObj(1775,27)),
                    new CombineObj("Light orb","8",new ItemObj(1785,1),new ItemObj(1775,27)),

            };
}



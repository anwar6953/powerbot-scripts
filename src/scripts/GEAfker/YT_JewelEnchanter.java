package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class YT_JewelEnchanter extends Task<ClientContext> {

    private Component spell = ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_DIAMOND_SPELL);
    private enchantObj sapphire = new enchantObj(ID.SAPPHIRE_RING, ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_SAPPHIRE_SPELL), ID.STAFF_WATER);
    private enchantObj ruby = new enchantObj(ID.RUBY_AMULET, ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_RUBY_SPELL), ID.STAFF_FIRE);
//    private enchantObj diamond = new enchantObj(ID.DIAMOND_AMULET, ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_DIAMOND_SPELL), ID.STAFF_EARTH);
    private enchantObj diamond = new enchantObj(ID.DIAMOND_BRACELET, ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_DIAMOND_SPELL), ID.STAFF_EARTH);
    private enchantObj curr;
    private List<enchantObj> enchantObjList = new ArrayList<>();

    private int staffID;

    public YT_JewelEnchanter(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.RUNE_COSMIC;
//        resourceIDARRAY2 = new int[] {ID.DIAMOND_AMULET};
//        resourceID2 = resourceIDARRAY2[0];
        actionName = "Jewellery enchanted";
        taskName = "YT_Enchant jewellery";

        enchantObjList.add(diamond);
        enchantObjList.add(ruby);
        enchantObjList.add(sapphire);
        skill = Constants.SKILLS_MAGIC;
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            int temp = 0;
            for (enchantObj e : enchantObjList) {
                temp += ctx.bank.select().id(e.jewellery).count(true);
            }
            resourceLeft2 = temp;
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = min(resourceLeft1, resourceLeft2);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        if (ctx.inventory.select().id(resourceID2).count() == 0 ||
                ctx.inventory.select().id(resourceID1).count() == 0 ||
                ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != curr.staffID) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 == 0) || (resourceLeft2 == 0)) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            boolean check = false;
            for (enchantObj e : enchantObjList) {
                if (!ctx.bank.select().id(e.jewellery).isEmpty()) {
                    curr = e;
                    resourceID2 = curr.jewellery;
                    spell = curr.spell;
                    staffID = curr.staffID;
                    check = true;
                    break;
                }
            }
            if (!check) resourceLeft2 = 0;
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID) {
                ctx.bank.depositEquipment();
                ctx.bank.withdraw(staffID, 1);
            }
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            ctx.bank.withdraw(resourceID2, Bank.Amount.ALL);
            Utils.closeBank();
            if (!ctx.inventory.select().id(staffID).isEmpty()) {
                ctx.game.tab(Game.Tab.INVENTORY);
                Item staff = ctx.inventory.select().id(staffID).poll();
                if (staff.valid())
                    staff.interact("Wield");
                Condition.sleep(1500);
            }
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        Item jewellery = ctx.inventory.select().id(resourceID2).poll();
        Condition.wait(() -> spell.visible(),500, 3);
        if (!spell.visible()) ctx.input.send("{VK_F6}");
        spell.click();
        Condition.wait(() -> ctx.game.tab() == Game.Tab.INVENTORY,100,20);
        if (ctx.game.tab() == Game.Tab.INVENTORY) {
            jewellery.interact("Cast");
            Condition.wait(() -> {
                if (ctx.players.local().animation() != -1) {
                    resourceLeft2 -= 1;
                    resourceLeft1 -= 1;
                    productDone++;
                    return true;
                } else {
                    return false;
                }
            }, 500, 5);
        }
    }

    private class enchantObj {
        int jewellery;
        Component spell;
        int staffID;

        private enchantObj(int j, Component s, int id) {
            jewellery = j;
            spell = s;
            staffID = id;
        }
    }
}
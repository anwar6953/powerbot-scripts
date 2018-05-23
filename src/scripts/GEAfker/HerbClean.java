package scripts.GEAfker;


import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import api.ClientContext;

import java.util.ArrayList;

import static java.lang.Math.min;


public class HerbClean extends Task<ClientContext> {
    private static int SKILL_LEVEL;
    private int
                GUAM = 199,
                MARRENTILL = 201,
                TARROMIN = 203,
                HARRALANDER = 205,
                RANARR = 207,
                IRIT = 209,
                AVANTOE = 211,
                KWUARM = 213,
                CADANTINE = 215,
                DWARF = 217,
                TORSTOL = 219,
                LANTADYME = 2485,
                TOADFLAX = 3049,
                SNAPDRAGON = 3051;

    ArrayList<herbObj> herbList = new ArrayList<>();

    public HerbClean(ClientContext ctx) {
        super(ctx);
        skill = Constants.SKILLS_HERBLORE;
//        resourceIDARRAY1 = new int[] {ID.HERB_CADANTINEGRIMY};
        actionName = "Herbs cleaned";
        gameMsg = "You clean the";
        taskName = "Clean herbs";
        herbList.add(new herbObj(199,3));
        herbList.add(new herbObj(201,5));
        herbList.add(new herbObj(203,11));
        herbList.add(new herbObj(205,20));
        herbList.add(new herbObj(207,25));
        herbList.add(new herbObj(209,40));
        herbList.add(new herbObj(211,48));
        herbList.add(new herbObj(213,54));
        herbList.add(new herbObj(215,65));
        herbList.add(new herbObj(217,70));
        herbList.add(new herbObj(219,75));
        herbList.add(new herbObj(2485,67));
        herbList.add(new herbObj(3049,30));
        herbList.add(new herbObj(3051,59));



        resourceID1 = herbList.get(0).ID;
    }

    @Override
    public void initialise() {
        SKILL_LEVEL = ctx.skills.level(Constants.SKILLS_HERBLORE);
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            int temp = 0;
            for (herbObj h : herbList) {
                if (SKILL_LEVEL >= h.level && ctx.bank.select().id(h.ID).count(true) > 0) {
                    temp += ctx.bank.select().id(h.ID).count(true);
                    resourceID1 = h.ID;
                }
            }
            resourceLeft1 = temp;
            if (limit > resourceLeft1 && limit == 0) limit = resourceLeft1;
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0;
    }

    @Override
    public void execute() {
        SKILL_LEVEL = ctx.skills.level(Constants.SKILLS_HERBLORE);
        if (ctx.inventory.select().id(resourceID1).count() == 0) {
            prologue();
        } else {
            action();
            if (resourceLeft1 == 0) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        String msg = me.text();
        if (msg.contains(gameMsg)) {
            resourceLeft1--;
            productDone++;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            int temp = 0;
            for (herbObj h : herbList) {
                if (SKILL_LEVEL >= h.level && ctx.bank.select().id(h.ID).count(true) > 0) {
                    temp += ctx.bank.select().id(h.ID).count(true);
                    resourceID1 = h.ID;
                }
            }
            resourceLeft1 = temp;
            if (resourceLeft1 + productDone >= limit) limit = resourceLeft1 + productDone;
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
        Item[] inven = ctx.inventory.items();
        for (int i : ctx.inventory.pattern()) {
            if (inven[i].id() == resourceID1) inven[i].interact("Clean");
        }
    }

}

class herbObj {
    int ID;
    int level;

    public herbObj (int i, int l) {
        ID = i;
        level = l;
    }
}
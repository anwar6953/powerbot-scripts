package scripts.GEAfker;


import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import api.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import static java.lang.Math.min;


public class Composter extends Task<ClientContext> {

    public Composter(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.COMPOST;
        resourceIDARRAY2 = new int[]{ID.COMPOST_POTION_1,ID.COMPOST_POTION_2,ID.COMPOST_POTION_3,ID.COMPOST_POTION_4};
        gameMsg = "The compost transforms";
        actionName = "Compost made";
        taskName = "Make compost";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            resourceLeft2 = ctx.bank.select().id(ID.COMPOST_POTION_4).count(true);
            resourceLeft1 = ctx.bank.select().id(ID.COMPOST).count(true);
            if (limit > min(resourceLeft1, resourceLeft2 *4) || limit == 0) limit = min(resourceLeft1, resourceLeft2 *4);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft2 > 0 &&
                resourceLeft1 > 0;
    }

    @Override
    public void execute() {
        if (ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(resourceIDARRAY2).count() == 0) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 == 0) || (resourceLeft2 == 0)) Utils.epilogue();
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
            resourceLeft2 = ctx.bank.select().id(ID.COMPOST_POTION_4).count(true);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            ctx.bank.withdraw(ID.COMPOST_POTION_4, 5);
            ctx.bank.withdraw(resourceID1, 20);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

        Item compost = ctx.inventory.firstLeft(resourceID1);
        Item potion = ctx.inventory.lastItem(resourceIDARRAY2);
        potion.interact("Use");
        compost.interact("Use");
        potion.hover();
        Condition.sleep(200);
    }
}
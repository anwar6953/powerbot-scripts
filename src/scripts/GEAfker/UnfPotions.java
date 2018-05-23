package scripts.GEAfker;


import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import api.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import static java.lang.Math.min;

public class UnfPotions extends Task<ClientContext> {
    public UnfPotions(ClientContext ctx) {
        super(ctx);
        int     GUAM = 249,
                MARRENTILL = 251,
                TARROMIN = 253,
                HARRALANDER = 255,
                RANARR = 257,
                IRIT = 259,
                AVANTOE = 261,
                KWUARM = 263,
                CADANTINE = 265,
                DWARF = 267,
                TORSTOL = 269,
                TOADFLAX = 2998,
                SNAPDRAGON = 3000,
                LANTADYME = 2481;
        resourceID1 = ID.VIAL_WATER;
        resourceIDARRAY2 = new int[] {
                GUAM, MARRENTILL,TARROMIN,HARRALANDER,RANARR,
                IRIT,AVANTOE,KWUARM, CADANTINE, DWARF,
//                TORSTOL,
                TOADFLAX, SNAPDRAGON, LANTADYME,
                };
        resourceID2 = resourceIDARRAY2[0];
        gameMsg = "You put the";
        actionName = "Unf potions made";
        taskName = "Unf potions";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
        }
    }

    @Override
    public boolean activate() {
//        if (productDone == 0 && ctx.bank.opened()) {
//            resourceLeft2 = 0;
//            for (int herb : resourceIDARRAY2) {
//                resourceLeft2 += ctx.bank.select().id(herb).count(true);
//            }
//        }
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        switch (getState()) {
            case ACTION:
                action();
                if ((resourceLeft1 == 0) || (resourceLeft2 == 0) || productDone >= limit) Utils.epilogue();
            case WITHDRAW:
                prologue();
            case WAIT:
                Condition.sleep(100);
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains(gameMsg)) {
            resourceLeft2 -= 1;
            resourceLeft1 -= 1;
            productDone += 1;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }


    private State getState() {
        if (ctx.inventory.select().id(resourceIDARRAY2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0) {
            return State.WITHDRAW;
        } else if (ctx.players.local().animation() == -1) {
            return State.ACTION;
        }

        return State.WAIT;
    }


    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = 0;
            for (int i : resourceIDARRAY2) {
                resourceLeft2 += ctx.bank.select().id(i).count(true);
            }
            if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = min(resourceLeft1, resourceLeft2);
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
            ctx.bank.withdrawUntil(resourceID1, 14);
            ctx.bank.withdrawUntil(resourceID2, 14);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);
    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
        final int temp = productDone;

        if(resource1.interact("Use") && resource2.interact("Use")) {
            Condition.wait(makeAll::visible,500,4);
        }
        if (makeAll.visible()) {
//                    makeAll.interact("Make all");
            ctx.input.send("1");
            Condition.wait(() -> temp != productDone, 500, 4);
            ctx.bank.hover(ctx);
            if (temp != productDone) {
                Condition.wait(() -> (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0), 500, 45);
            }
        }
    }

    private enum State {
        WITHDRAW,ACTION,WAIT
    }
}
package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

import static java.lang.Math.min;

public class Pineapple extends Task<ClientContext> {
    public Pineapple(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.KNIFE;
        resourceID2 = 2114;
        gameMsg = "You cut the";
        actionName = "Pineapples sliced";
        taskName = "Slice pineapples";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        switch (getState()) {
            case ACTION:
                action();
                if ((resourceLeft1 == 0) || (resourceLeft2 == 0) || productDone >= limit) Utils.epilogue();
                break;
            case WITHDRAW:
                prologue();
                break;
            case WAIT:
                Condition.sleep(100);
                break;
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains(gameMsg)) {
            resourceLeft2 -= 1;
            productDone += 1;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }


    private State getState() {
        if (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0) {
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
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = resourceLeft2;
            ctx.bank.withdraw(resourceID1, 1);
            ctx.bank.withdraw(resourceID2, 6);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
        Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);
        final int temp = productDone;

        if(resource1.interact("Use") && resource2.interact("Use")) {
            Condition.wait(makeAll::visible,500,4);
        }
        if (makeAll.visible()) {
            ctx.input.send("1");
            Condition.wait(() -> temp != productDone, 500, 4);
            ctx.bank.hover(ctx);
            if (temp != productDone) {
                Condition.wait(() -> (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0), 500, 25);
            }
        }
    }

    private enum State {
        WITHDRAW,ACTION,WAIT
    }
}
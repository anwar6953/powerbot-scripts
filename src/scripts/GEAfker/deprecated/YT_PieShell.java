package scripts.GEAfker.deprecated;



import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import api.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.GEAfker.Task;
import scripts.ID;

import static java.lang.Math.min;

public class YT_PieShell extends Task<ClientContext> {
    @Override
    public void inventory(InventoryEvent ie) {

    }

    public YT_PieShell(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.PIE_DISH;
        resourceIDARRAY2 = new int[] {ID.PASTRY_DOUGH};
        resourceID2 = resourceIDARRAY2[0];
        gameMsg = "pie shell";
        actionName = "Dishes shelled";
        taskName = "YT_PieSheller";
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
            resourceLeft1 -= 1;
            productDone += 1;
        }
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
//            resourceLeft2 = 0;
//            for (int i : resourceIDARRAY2) {
//                resourceLeft2 += ctx.bank.select().id(i).count(true);
//            }
            int temp = 0;
            for (int i : resourceIDARRAY2) {
                temp += ctx.bank.select().id(i).count(true);
            }
            resourceLeft2 = temp;
            if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = min(resourceLeft1, resourceLeft2);
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
            ctx.bank.withdraw(resourceID1, 14);
            ctx.bank.withdraw(resourceID2, 14);
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
        final int temp = productDone;

        if(resource1.interact("Use") && resource2.interact("Use")) {
            Condition.wait(() -> ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).visible(),500,4);
        }
        ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).interact("Make all");
        Condition.wait(() -> temp != productDone,500,4);
        if (temp != productDone) {
            Condition.wait(() -> (ctx.inventory.select().id(resourceIDARRAY2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0),500,35);
        }
    }

    private enum State {
        WITHDRAW,ACTION,WAIT
    }
}
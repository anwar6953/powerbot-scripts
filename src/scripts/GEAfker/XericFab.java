package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import static java.lang.Math.min;

public class XericFab extends Task<ClientContext> {
    private boolean isTool;
    private Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);

    private int toolID = 1734;
    public XericFab(ClientContext ctx) {
        super(ctx);
        isTool = true;
        resourceID1 = 1733;
        resourceID2 = 13383;
        gameMsg = "You make a";
        actionName = "Xeric fab";
        taskName = "Xeric Fab";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 3;
    }

    @Override
    public void execute() {
        switch (getState()) {
            case ACTION:
                Utils.closeBank();
                ctx.game.tab(Game.Tab.INVENTORY);
                if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

                final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
                final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
                final int temp = ctx.inventory.select().id(resourceID2).count();

                if(resource1.interact("Use") && resource2.interact("Use")) {
                    Condition.wait(makeAll::visible,500,4);
                }
                if (makeAll.visible()) {
//                    makeAll.interact("Make all");
                    ctx.input.send("2");
                    Condition.wait(() -> temp != productDone, 500, 4);
                    ctx.bank.hover(ctx);
                    if (temp != productDone) {
                        Condition.wait(() -> (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0), 500, 45);
                    }
                } else {
                    if (temp != ctx.inventory.select().id(resourceID2).count()) {
                        Condition.wait(() -> (ctx.inventory.select().id(resourceID1).count() == 0 ||
                                ctx.inventory.select().id(resourceID2).count() == 0) ||
                                ctx.chat.canContinue(), 500, 45);
                    }
                }
                if ((resourceLeft1 == 0) || (resourceLeft2 == 0) || productDone >= limit) Utils.epilogue();
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept(resourceID1,resourceID2,toolID);
                    resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
                    resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
                    if (!isTool) if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = min(resourceLeft1, resourceLeft2);
                    else if (limit < resourceLeft2 && limit == 0) limit = resourceLeft2/4;
                    if (isTool) {
                        if (limit < resourceLeft2 && limit == 0) limit = resourceLeft2/4;
                        ctx.bank.withdrawUntil(resourceID1, 1);
                        ctx.bank.withdrawUntil(toolID, Bank.Amount.ALL.getValue());
                        ctx.bank.withdrawUntil(resourceID2, 24);
                    }
                    Utils.closeBank();
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case WAIT:
                Condition.sleep(100);
                break;
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains(gameMsg)) {
            if(!isTool) resourceLeft1 -= 1;
            resourceLeft2 -= 1;
            productDone += 1;
        }
        if (me.text().contains("You'll need") || me.text().contains("You don't") || me.text().contains("You need")) {
            if(!isTool) resourceLeft1 = 0;
            resourceLeft2 = 0;
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

    private enum State {
        WITHDRAW,ACTION,WAIT
    }
}

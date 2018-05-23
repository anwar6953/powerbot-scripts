package scripts.GEAfker;

/**
 * Created by VincentT on 26/07/2017.
 */

import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.MessageEvent;
import api.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;

import org.powerbot.script.*;


public class YT_Eat1000 extends Task<ClientContext> {

    public YT_Eat1000(ClientContext ctx) {
        super(ctx);
        resourceID1 = 1963;
        actionName = "Bananas eaten";
        gameMsg = "You eat";
        taskName = "YT_Eat bananas";
        productDone = 1092;
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            limit = resourceLeft1;
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0;
    }

    @Override
    public void execute() {
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

    @Override
    public String getActionName(long runTime) {
        if (startTime == 0 && productDone > 0) startTime = runTime;
        if (activate()) endTime = runTime;
//        int perHr = (int)(productDone*3600000D/(endTime-startTime));
        return actionName + " " + productDone + "/" + "2000";
    }

    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            ctx.bank.withdraw(resourceID1, 28);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
        final int temp = productDone;
        Item i = ctx.inventory.select().id(resourceID1).poll();
        i.interact("Eat");
        Condition.wait(() -> temp != productDone, 100, 15);
        Condition.wait(() -> ctx.players.local().animation() == -1, 100, 15);
    }
}
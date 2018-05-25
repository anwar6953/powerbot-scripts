package scripts.GEAfker.deprecated;

/**
 * Created by VincentT on 26/07/2017.
 */

import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import api.ClientContext;
import org.powerbot.script.rt4.GeItem;
import scripts.GEAfker.Task;
import scripts.ID;

import static java.lang.Math.min;

import org.powerbot.script.rt4.Game;

public class GemCutter extends Task<ClientContext> {
    private static int resourceLeft1;
    private static int productDone;

    private static int resourceID1;
    private static int[] resourceIDARRAY1 = {ID.UNCUT_JADE, ID.UNCUT_RUBY};
    private static int toolID = ID.CHISEL;
    private static int productID = ID.UNCUT_JADE;

    private static GeItem resourceGE1;
    private static GeItem productGE;

    private static int limit = -1;

    private static long startTime = -1;

    public GemCutter(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceID1 =  Utils.selectResource(resourceIDARRAY1);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            if (limit > resourceLeft1 || limit < 0) limit = resourceLeft1;
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 &&
                productDone < limit;
    }

    @Override
    public String getName()
    {
        return "Cut gems";
    }

    @Override
    public String getActionName(long runTime) {
        if (productDone > 0 && startTime < 0) startTime = runTime;
        int perHr = (int)(productDone*3600000D/(runTime-startTime));
        return "Gems cut " + productDone + "/" + limit + " (" + perHr + ")";
    }


    @Override
    public void execute() {
        if (ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(toolID).count() == 0) {
            prologue();
        } else if (ctx.players.local().animation() == -1) {
            action();
            if (resourceLeft1 == 0 || productDone >= limit) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        String msg = me.text();
        if (msg.contains("Jade")) {
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
            resourceID1 =  Utils.selectResource(resourceIDARRAY1);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            if (ctx.bank.withdraw(toolID, 1) &&
                    ctx.bank.withdraw(resourceID1, 27) &&
                    ctx.bank.close()) {
                Condition.wait(() -> ctx.inventory.select().id(toolID).count() >= 1 &&
                        ctx.inventory.select().id(resourceID1).count() >= 1 &&
                        !ctx.bank.opened(), 250, 5);
            }
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item tool = ctx.inventory.select().id(toolID).poll();
        final int temp = resourceLeft1;
        if(resource1.interact("Use") && tool.interact("Use")) {
            Condition.wait(() -> ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).visible());
        }
        ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).interact("Make all");
        Condition.wait(() -> ctx.inventory.select().id(resourceID1).count() == 0,1000,30);
    }
}
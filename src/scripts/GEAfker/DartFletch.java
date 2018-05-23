package scripts.GEAfker;

import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Bank;
import api.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import java.util.Random;

import static java.lang.Math.min;

public class DartFletch extends Task<ClientContext> {
    public DartFletch(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.FEATHER;
        resourceIDARRAY2 = new int[] {ID.DARTTIP_MITHRIL};
        gameMsg = "You finish";
        actionName = "Darts fletched";
        taskName = "Fletch darts";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceID2 = Utils.selectResource(resourceIDARRAY2);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 &&
                resourceLeft2 > 0;
    }

    @Override
    public void execute() {
        if (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0) {
            setup();
        } else {
            action();
            if ((resourceLeft1 == 0) || (resourceLeft2 == 0) || productDone >= limit) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains(gameMsg)) {
            resourceLeft2 -= 10;
            resourceLeft1 -= 10;
            productDone += 10;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void setup() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceIDARRAY2).count(true);
            if (limit < min(resourceLeft1, resourceLeft2) && limit == 0) limit = min(resourceLeft1, resourceLeft2);
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            ctx.bank.withdraw(resourceID2, Bank.Amount.ALL);
            ctx.bank.close();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        Random rand = new Random();
        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
//        final int temp = productDone;
        resource1.interact("Use",resource1.name());
        resource2.interact("Use",resource2.name());
//        Condition.wait(() -> temp != productDone, 10, 4);
    }
}
package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

import static java.lang.Math.min;

public class BoltTipper extends Task<ClientContext> {
    public BoltTipper(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.ADAMANT_BOLTS;
        resourceIDARRAY2 = new int[] {ID.DIAMOND_BOLT_TIPS,ID.RUBY_BOLT_TIPS};
        gameMsg = "You fletch";
        actionName = "Bolts tipped";
        taskName = "Tip bolts";
        skill = Constants.SKILLS_FLETCHING;
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
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
    public String getName() {
        return "Tip bolts";
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
        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
        final int temp = productDone;
        resource2.interact("Use");
        resource1.interact("Use");
        Condition.wait(() -> ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).visible(), 500, 5);
        if (ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).visible()) {
//            ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE).interact("Make 10");
            ctx.input.send("1");
            Condition.wait(() -> temp != productDone, 500, 5);
            if (temp != productDone) {
                Condition.wait(() -> productDone - temp >= 100, 500, 17);
            }
        }
    }
}
package scripts.GEAfker;
import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

public class LongFletch extends Task<ClientContext> {
//    private static int FLETCHLEVEL;
    private Component fletchUI = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);

    public LongFletch(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.KNIFE;
        resourceIDARRAY2 = new int[] {ID.LOGS_MAGIC,ID.LOGS_YEW, ID.MAPLE_LOGS};
        resourceID2 = resourceIDARRAY2[0];
        gameMsg = "longbow";
        actionName = "Longbows";
        taskName = "Fletch longbows";
        skill = Constants.SKILLS_FLETCHING;
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            ctx.bank.depositInventory();
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft2 > 0 && ctx.skills.realLevel(skill) < 99;
    }

    @Override
    public void execute() {
        if (ctx.inventory.select().id(ID.KNIFE).count() == 0 || ctx.inventory.select().id(resourceID2).count() == 0) {
            prologue();
        } else if (ctx.players.local().animation() == -1) {
            action();
            if (resourceLeft2 == 0) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        String msg = me.text();
        if (msg.contains(gameMsg)) { //You carefully cut the wood into
            resourceLeft2--;
            productDone++;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceID2 =  Utils.selectResource(resourceIDARRAY2);
            resourceLeft2 = 0;
            for (int i : resourceIDARRAY2) {
                resourceLeft2 += ctx.bank.select().id(i).count(true);
            }
            if (limit == 0) limit = resourceLeft2;
            ctx.bank.withdraw(ID.KNIFE, 1);
            ctx.bank.withdraw(resourceID2, 27);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
        final Item knife = ctx.inventory.select().id(ID.KNIFE).poll();
        final Item logs = ctx.inventory.select().id(resourceID2).poll();
        final int numLogs = ctx.inventory.select().id(resourceID2).count();
        if (knife.interact("Use") && logs.interact("Use")) {
            Condition.wait(() -> fletchUI.visible(), 250, 5);
        }
        if (fletchUI.visible()) {
            ctx.input.send("3");
            Condition.wait(() -> ctx.players.local().animation() == -1 ||
                    ctx.chat.canContinue(), 1000, 40);
//            Condition.wait(() -> ctx.chat.pendingInput(), 500, 4);
//            if (ctx.chat.pendingInput()) {
//                makeXall();
//
//            }
        }
    }
}

package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

import static java.lang.Math.min;


public class YT_LeatherCraft extends Task<ClientContext> {
    private int level;
    private int toolID = ID.NEEDLE;

    public YT_LeatherCraft(ClientContext ctx) {
        super(ctx);
        level = ctx.skills.level(Constants.SKILLS_CRAFTING);
        resourceID1 = ID.THREAD;
        resourceID2 = ID.LEATHER_SOFT;
        gameMsg = "make";
        actionName = "Leather crafted";
        taskName = "YT_CraftLeather";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            ctx.bank.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            limit = min(resourceLeft1*5,resourceLeft2);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft2 > 0 && resourceLeft1 > 0;
    }

    @Override
    public void execute() {
        level = ctx.skills.level(Constants.SKILLS_CRAFTING);
        if (ctx.inventory.select().id(toolID).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(resourceID2).count() == 0) {
            prologue();
        } else if (ctx.players.local().animation() == -1) {
            action();
            if (resourceLeft1 == 0 || resourceLeft2 == 0) Utils.epilogue();
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
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            ctx.bank.withdraw(toolID, 1);
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            ctx.bank.withdraw(resourceID2, Bank.Amount.ALL);
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
        final Item tool = ctx.inventory.select().id(toolID).poll();
        final Item item = ctx.inventory.select().id(resourceID2).poll();
        if (tool.interact("Use") && item.interact("Use")) {
            Condition.wait(() -> ctx.widgets.component(ID.WIDGET_CRAFT_LEATHER,0).visible(), 250, 5);
        }
        ctx.widgets.component(ID.WIDGET_CRAFT_LEATHER, itemChoose(level)).interact("Make all");
        Condition.wait(() -> temp != productDone, 500, 5);
        Condition.wait(() -> ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(resourceID2).count() == 0 || ctx.chat.canContinue(), 500, 60);
    }

    private int itemChoose(int level) {
        int component = ID.WIDGET_LEATHER_1;
        if (level >= 7) component = ID.WIDGET_LEATHER_2;
        if (level >= 9) component = ID.WIDGET_LEATHER_3;
        if (level >= 11) component = ID.WIDGET_LEATHER_4;
        if (level >= 14) component = ID.WIDGET_LEATHER_5;
        if (level >= 18) component = ID.WIDGET_LEATHER_6;
        if (level >= 38) component = ID.WIDGET_LEATHER_7;
        return component;
    }

}

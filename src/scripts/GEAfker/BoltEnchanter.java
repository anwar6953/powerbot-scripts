package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;

import static java.lang.Math.min;

public class BoltEnchanter extends Task<ClientContext> {
    private int staffID = ID.STAFF_FIRE;
    public BoltEnchanter(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.RUBY_BOLTS;
        resourceIDARRAY2 = new int[] {ID.RUNE_BLOOD, ID.RUNE_COSMIC};
        gameMsg = "magic of the runes";
        actionName = "Bolts enchanted";
        taskName = "Enchant ruby bolts";
        skill = Constants.SKILLS_MAGIC;
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = min(ctx.bank.select().id(resourceIDARRAY2[0]).count(true), ctx.bank.select().id(resourceIDARRAY2[1]).count(true));
//            limit = min(resourceLeft1/10, resourceLeft2);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }

    @Override
    public String getName() {
        return "Enchant bolts";
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        if (ctx.inventory.select().id(resourceIDARRAY2[0]).count() == 0 || ctx.inventory.select().id(resourceIDARRAY2[1]).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 == 0) || (resourceLeft2 == 0)) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains(gameMsg)) {
            resourceLeft2 -= 1;
            resourceLeft1 -= 10;
            productDone += 1;
        }
        if (me.text().contains("You don't have enough")) {
            resourceLeft2 = 0;
            resourceLeft1 = 0;
            productDone += 1;
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void prologue() {
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = min(ctx.bank.select().id(resourceIDARRAY2[0]).count(true), ctx.bank.select().id(resourceIDARRAY2[1]).count(true));
            if (limit < min(resourceLeft1/10, resourceLeft2) && limit == 0) limit = min(resourceLeft1/10, resourceLeft2);
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID) {
                ctx.bank.depositEquipment();
                ctx.bank.withdraw(staffID, 1);
            }
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            ctx.bank.withdraw(resourceIDARRAY2[0], Bank.Amount.ALL);
            ctx.bank.withdraw(resourceIDARRAY2[1], Bank.Amount.ALL);
            ctx.bank.close();
            if (!ctx.inventory.select().id(staffID).isEmpty()) {
                ctx.game.tab(Game.Tab.INVENTORY);
                Item staff = ctx.inventory.select().id(staffID).poll();
                if (staff.valid())
                    staff.interact("Wield");
                Condition.sleep(1500);
            }
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        ctx.game.tab(Game.Tab.MAGIC);
        ctx.widgets.component(ID.MAGIC_WIDGET, ID.ENCHANT_BOLT_SPELL).click();
        Condition.wait(() -> ctx.widgets.component(ID.ENCHANT_BOLT_WIDGET, ID.ENCHANT_RUBY_BOLT).visible(), 500, 5);
        ctx.widgets.component(ID.ENCHANT_BOLT_WIDGET, ID.ENCHANT_RUBY_BOLT).click();
        Condition.wait(() -> !ctx.widgets.component(ID.ENCHANT_BOLT_WIDGET, ID.ENCHANT_RUBY_BOLT).visible(), 500, 5);
        if (ctx.widgets.component(ID.ENCHANT_BOLT_WIDGET, ID.ENCHANT_RUBY_BOLT).visible())
                ctx.widgets.component(ID.ENCHANT_BOLT_WIDGET, 1).component(11).interact("Close");
    }
}
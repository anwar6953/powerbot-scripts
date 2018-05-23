package scripts.GEAfker;

import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Bank;
import api.ClientContext;
import org.powerbot.script.rt4.Equipment;
import org.powerbot.script.rt4.Game;
import scripts.ID;

import static java.lang.Math.min;

public class FlaxSpin extends Task<ClientContext> {
    private int resourceID3 = ID.RUNE_NATURE;
    private int resourceLeft3 = 2;
    private int initial;

    public FlaxSpin(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.FLAX;
        resourceID2 = ID.RUNE_ASTRAL;
        actionName = "Flax spun";
        taskName = "Spin flax";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            resourceLeft3 = ctx.bank.select().id(resourceID3).count(true);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0 && resourceLeft3 > 1;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        if (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(resourceID3).count() == 0) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 <= 0) || (resourceLeft2 <= 0) || (resourceLeft3 <= 1)) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains("You do not")) {
            ctx.controller.stop();
        }
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    private void prologue() {
        if (ctx.bank.opened()) {
            ctx.bank.depositAllExcept(resourceID1, resourceID2, resourceID3);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            resourceLeft3 = ctx.bank.select().id(resourceID3).count(true);
            initial = resourceLeft2;
            if (limit < min(min(resourceLeft1/5, resourceLeft2),resourceLeft3/2) && limit == 0) limit = min(min(resourceLeft1/5, resourceLeft2),resourceLeft3/2);
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != 1381) {
                ctx.bank.depositEquipment();
                ctx.bank.withdraw(1381, 1);
            }
            ctx.bank.withdrawUntil(resourceID1, 25);
            ctx.bank.withdrawUntil(resourceID2, Bank.Amount.ALL.getValue());
            ctx.bank.withdrawUntil(resourceID3, Bank.Amount.ALL.getValue());
            Utils.closeBank();
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != 1381 &&
                    !ctx.inventory.select().id(1381).isEmpty()) {
                Condition.wait(() -> ctx.game.tab(Game.Tab.INVENTORY), 1000, 5);
                Condition.wait(() -> ctx.inventory.select().id(1381).poll().interact("Wield"), 1000, 5);
                Condition.wait(() -> ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == 1381, 1000, 5);
            }
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        if (ctx.chat.canContinue()) ctx.chat.clickContinue();
        if (!ctx.widgets.component(ID.MAGIC_WIDGET, ID.SPELL_SPIN_FLAX).visible()) ctx.input.send("{VK_F6}");
        if (ctx.widgets.component(ID.MAGIC_WIDGET, ID.SPELL_SPIN_FLAX).click()) {
            Condition.wait(() -> ctx.players.local().animation() != -1, 300, 8);
            Condition.wait(() -> ctx.players.local().animation() == -1, 300, 8);
        }

        resourceLeft2 = ctx.inventory.select().id(resourceID2).count(true);
        resourceLeft3 = ctx.inventory.select().id(resourceID3).count(true);
        productDone = initial - resourceLeft2;
    }
}
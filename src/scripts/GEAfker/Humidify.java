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

public class Humidify extends Task<ClientContext> {
    private int initial;
    private final int MAGIC_WIDGET = 218,
            SPELL_SPIN_FLAX = 142,
            RUNE_NATURE = 561,
            ASTRAL_RUNE = 9075,
            FLAX = 1779;
    int staffID = 11787;

    public Humidify(ClientContext ctx) {
        super(ctx);
        resourceID1 = 434;
        resourceID2 = ID.ASTRAL_RUNE;
        actionName = "Clay moistened";
        taskName = "Soften clay";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            initial = resourceLeft2;
            if ((limit < min(resourceLeft1/27, resourceLeft2)) || limit == 0) limit = min(resourceLeft1/27, resourceLeft2);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        if (ctx.inventory.select().id(resourceID2).count() == 0 || ctx.inventory.select().id(resourceID1).count() == 0) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 <= 0) || (resourceLeft2 <= 0)) Utils.epilogue();
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
            ctx.bank.depositAllExcept(resourceID2);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID) {
                ctx.bank.depositEquipment();
                ctx.bank.withdraw(staffID, 1);
            }
            ctx.bank.withdraw(resourceID2, Bank.Amount.ALL);
            ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
            Utils.closeBank();
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        equipStaff();
        if (ctx.chat.canContinue()) ctx.chat.clickContinue();
        if (!ctx.widgets.component(MAGIC_WIDGET, 101).visible()) ctx.input.send("{VK_F6}");
        if (ctx.widgets.component(MAGIC_WIDGET, 101).click()) {
            ctx.bank.hover(ctx);
            Condition.wait(() -> ctx.inventory.select().id(resourceID1).isEmpty(), 500, 8);
        }
        resourceLeft2 = ctx.inventory.select().id(resourceID2).count(true);
        productDone = initial - resourceLeft2;
    }

    private void equipStaff() {
        if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID &&
                !ctx.inventory.select().id(staffID).isEmpty()) {
            Condition.wait(() -> ctx.game.tab(Game.Tab.INVENTORY), 1000, 5);
            Condition.wait(() -> ctx.inventory.select().id(staffID).poll().interact("Wield"), 1000, 5);
            Condition.wait(() -> ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == staffID, 1000, 5);
        }
    }
}
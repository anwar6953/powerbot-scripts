package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.Equipment;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import static java.lang.Math.min;

public class PlankMake extends Task<ClientContext> {
    private int resourceID3 = ID.RUNE_NATURE;
    private int resourceID4 = 995;
    private int resourceLeft4;
    private int resourceLeft3 = 2;
    private int initial;
    private int staffID = 1385;

    public PlankMake(ClientContext ctx) {
        super(ctx);
        resourceID1 = 6332;
        resourceID2 = ID.RUNE_ASTRAL;
        actionName = "Planks";
        taskName = "Planks";
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            resourceLeft3 = ctx.bank.select().id(resourceID3).count(true);
            resourceLeft4 = ctx.bank.select().id(resourceID4).count(true);
            initial = resourceLeft3;
            if (limit == 0) limit = multiMin(resourceLeft1, resourceLeft2/2,resourceLeft3,resourceLeft4/1050);
        }
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 1 && resourceLeft3 > 0 && resourceLeft4 >= 1050;
    }

    @Override
    public void execute() {
//        if (limit < min(resourceLeft1/10, resourceLeft2) || limit < 0) limit = min(resourceLeft1/10, resourceLeft2);
        if (ctx.inventory.select().id(resourceID2).count() == 0 ||
                ctx.inventory.select().id(resourceID1).count() == 0 ||
                ctx.inventory.select().id(resourceID3).count() == 0 ||
                ctx.inventory.select().id(resourceID4).count() == 0) {
            prologue();
        } else {
            action();
            if ((resourceLeft1 <= 0) || (resourceLeft2 < 2) || (resourceLeft3 <= 0) || (resourceLeft4 < 1050)) Utils.epilogue();
        }
    }

    @Override
    public void message(MessageEvent me) {
        if (me.text().contains("You do not")) {
            ctx.controller.stop();
        }
    }

    private void prologue() {
        if (ctx.bank.opened()) {
            ctx.bank.depositAllExcept(resourceID1,resourceID2, resourceID3,resourceID4);
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID) {
                ctx.bank.depositEquipment();
                ctx.bank.withdraw(staffID, 1);
            }
            ctx.bank.withdrawUntil(resourceID2, Bank.Amount.ALL.getValue());
            ctx.bank.withdrawUntil(resourceID3, Bank.Amount.ALL.getValue());
            ctx.bank.withdrawUntil(resourceID4, Bank.Amount.ALL.getValue());
            ctx.bank.withdrawUntil(resourceID1, 25);
            Utils.closeBank();
            if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != staffID &&
                    !ctx.inventory.select().id(staffID).isEmpty()) {
                Condition.wait(() -> ctx.game.tab(Game.Tab.INVENTORY), 1000, 5);
                Condition.wait(() -> ctx.inventory.select().id(staffID).poll().interact("Wield"), 1000, 5);
                Condition.wait(() -> ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == staffID, 1000, 5);
            }
        } else {
            ctx.bank.openNearbyBank();
        }
    }

    private void action() {
        Utils.closeBank();
        if (ctx.chat.canContinue()) ctx.chat.clickContinue();
        if (!ctx.widgets.component(ID.MAGIC_WIDGET, 129).visible()) ctx.input.send("{VK_F6}");
        if (ctx.widgets.component(ID.MAGIC_WIDGET, 129).click()) {
            Condition.wait(() -> ctx.game.tab() == Game.Tab.INVENTORY, 300, 8);
            if (ctx.game.tab() == Game.Tab.INVENTORY) {
                Item log = ctx.inventory.select().id(resourceID1).poll();
                log.interact("Cast");
                Condition.sleep(600);
            }
        }

        resourceLeft2 = ctx.inventory.select().id(resourceID2).count(true);
        resourceLeft3 = ctx.inventory.select().id(resourceID3).count(true);
        resourceLeft4 = ctx.inventory.select().id(resourceID4).count(true);
        productDone = initial - resourceLeft3;
    }

    private int multiMin(int... nums) {
        int min = nums[0];
        for (int i : nums) {
            if (i < min) min = i;
        }
        return min;
    }
}
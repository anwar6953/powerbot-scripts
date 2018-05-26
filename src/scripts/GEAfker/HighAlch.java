package scripts.GEAfker;

import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.*;
import scripts.ID;
import org.powerbot.script.rt4.Bank;

import static java.lang.Math.min;
import static org.powerbot.script.rt4.Magic.Spell.HIGH_ALCHEMY;

public class HighAlch extends Task<ClientContext> {

    private Component highAlch = ctx.widgets.component(ID.MAGIC_WIDGET, ID.SPELL_HIGH_ALCH);
    private int skill = Constants.SKILLS_MAGIC;
    private int startExp;

    public HighAlch(ClientContext ctx) {
        super(ctx);
        resourceID1 = ID.RUNE_NATURE;
        resourceIDARRAY2 = new int[] {11095,1397,859};
        actionName = "High alch cast";
        taskName = "High alch";
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            int temp = 0;
            for (int id : resourceIDARRAY2) {
                temp += ctx.bank.select().id(id).count(true);
            }
            resourceLeft2 = temp;
        }
        startExp = ctx.skills.experience(skill);
    }

    @Override
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }


    @Override
    public void execute() {
        switch (getState()) {
            case ACTION:
                dragItem();
                ctx.inventory.deselectItem();
                if (ctx.magic.isSelected(HIGH_ALCHEMY)) {
                    ctx.input.send("{VK_ESCAPE}");
                } else if (!ctx.magic.isSelected(HIGH_ALCHEMY) &&
                        ctx.game.tab() == Game.Tab.MAGIC &&
                        ctx.magic.ready(HIGH_ALCHEMY)) {
                    highAlch.click();
                } else if (ctx.game.tab() == Game.Tab.MAGIC &&
                        !ctx.magic.ready(HIGH_ALCHEMY)) {
                    ctx.controller.stop();
                } else {
                    ctx.input.send("{VK_F6}");
                }
                Condition.wait(() -> ctx.game.tab() == Game.Tab.INVENTORY, 400, 3);
                if (ctx.game.tab() == Game.Tab.INVENTORY) {
                    highAlch.interact("Cast");
                    Condition.wait(() -> (ctx.players.local().animation() != -1), 300, 3);
                }
                productDone = (ctx.skills.experience(skill) - startExp)/65;
                resourceLeft1 = ctx.inventory.select().id(resourceID1).count(true);
                if (hasItem()) resourceLeft2 = 1;
                else resourceLeft2 = 0;

                if (!activate()) Utils.epilogue();
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    Utils.depositInventory();
                    resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
                    ctx.bank.withdraw(resourceID1, Bank.Amount.ALL);
                    if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != ID.STAFF_FIRE) {
                        ctx.bank.depositEquipment();
                        ctx.bank.withdraw(ID.STAFF_FIRE, 1);
                    }
                    ctx.bank.withdrawModeNoted(true);
                    int temp = 0;
                    for (int id : resourceIDARRAY2) {
                        temp += ctx.bank.select().id(id).count(true);
                        ctx.bank.withdraw(id, Bank.Amount.ALL);
                    }
                    resourceLeft2 = temp;
                    Utils.closeBank();

                    if (ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != ID.STAFF_FIRE &&
                            !ctx.inventory.select().id(ID.STAFF_FIRE).isEmpty()) {
                        Condition.wait(() -> ctx.game.tab(Game.Tab.INVENTORY), 1000, 5);
                        Condition.wait(() -> ctx.inventory.select().id(ID.STAFF_FIRE).poll().interact("Wield"), 1000, 5);
                        Condition.wait(() -> ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == ID.STAFF_FIRE, 1000, 5);
                    }

                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case WAIT:
                break;
        }
    }

    private enum State {
        ACTION,WAIT,WITHDRAW
    }

    private State getState() {
        int[] reqs = {ID.RUNE_NATURE};
        if (ctx.inventory.hasAll(reqs) && hasItem() && ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == ID.STAFF_FIRE) {
            return State.ACTION;
        }
        if (!ctx.inventory.hasAll(reqs) || !hasItem() || ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() != ID.STAFF_FIRE) {
            return State.WITHDRAW;
        }
        return State.WAIT;
    }

    @Override
    public void message(MessageEvent me) {
    }

    @Override
    public void inventory(InventoryEvent ie) {

    }

    // check if theres an alchable item in inventory
    private boolean hasItem() {
        if (!ctx.inventory.select().id(ID.RUNE_NATURE).isEmpty() &&
                !ctx.inventory.select().id(ID.GOLD).isEmpty() &&
                ctx.inventory.select().count() >= 3) {
            return true;
        }
        return !ctx.inventory.select().id(ID.RUNE_NATURE).isEmpty() &&
                ctx.inventory.select().id(ID.GOLD).isEmpty() &&
                ctx.inventory.select().count() >= 2;
    }

    //drag item to high alch spell location
    private void dragItem() {
        Item[] inven = ctx.inventory.items();
        if (!inven[15].valid() || inven[15].id() == ID.GOLD || inven[15].id() == ID.RUNE_NATURE) {
            Condition.wait(() -> ctx.game.tab(Game.Tab.INVENTORY));
            for (Item it : inven) {
                System.out.print(it.id());
                if (!(it.id() == ID.RUNE_NATURE || it.id() == ID.GOLD) && it.valid()) {
                    it.hover();
                    ctx.input.drag(highAlch.nextPoint(), true);
                    break;
                }
            }
        }
    }
}
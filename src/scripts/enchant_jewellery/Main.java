package scripts.enchant_jewellery;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.Equipment;
import org.powerbot.script.rt4.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

@Script.Manifest(name = "Enchant", properties = "author=nomivore;", description = "Enchant stuff")
public class Main extends PollingScript<ClientContext> implements PaintListener {
    private EnchantObj curr = Presets.RECOIL;
    private State state = State.WAIT;
    @Override
    public void poll() {
        ctx.inventory.deselectItem();
        state = getState();
        switch (state) {
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept(curr.getRUNES());
                    for (int i : curr.getRUNES()) {
                        ctx.bank.withdraw(i, Bank.Amount.ALL);
                    }
                    ctx.bank.withdraw(curr.getJEWELLERY(), Bank.Amount.ALL);
                    ctx.bank.close();
                } else {
                    if (ctx.menu.commands().length == 1) ctx.input.click(true);
                    ctx.bank.open();
                }
                break;
            case CAST:
                ctx.magic.cast(curr.getSpell());
                Condition.wait(()->ctx.game.tab() == Game.Tab.INVENTORY,200,5);
                ctx.inventory.select().id(curr.getJEWELLERY()).poll().click();
                Condition.wait(()->ctx.game.tab() == Game.Tab.MAGIC,200,5);
                break;
            case EQUIP:
                if (ctx.inventory.select().id(curr.getSTAFF_ID()).isEmpty()) {
                    if (ctx.bank.opened()) {
                        ctx.bank.depositEquipment();
                        ctx.bank.depositInventory();
                        ctx.bank.withdraw(curr.getSTAFF_ID(),1);
                        ctx.bank.close();
                    } else {
                        ctx.bank.open();
                    }
                } else {
                    ctx.inventory.select().id(curr.getSTAFF_ID()).poll().click();
                }
                break;
            case WAIT:
                break;
        }
    }

    private State getState() {
        if (!equippedStaff()) return State.EQUIP;

        if (hasItems()) {
            return State.CAST;
        } else {
            return State.WITHDRAW;
        }
    }

    private boolean equippedStaff() {
        return ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id() == curr.getSTAFF_ID();
    }

    private boolean hasItems() {
        return !ctx.inventory.select().id(curr.getJEWELLERY()).isEmpty()
                && ctx.inventory.hasAll(curr.getRUNES());
    }

    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        strings.clear();
        strings.add(state.toString());
        Utils.simplePaint(g,strings);
    }

    private enum State {
        WITHDRAW, CAST, EQUIP, WAIT
    }


}

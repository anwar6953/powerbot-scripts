package nomivore.Magic;

import CustomAPI.Bank;
import CustomAPI.ClientContext;
import CustomAPI.PollingScript;

import org.powerbot.script.*;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.Callable;

import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.*;

import static org.powerbot.script.rt4.Magic.Spell.HIGH_ALCHEMY;


@Script.Manifest(
        name = "ALLchemy", properties = "author=nomivore; topic=1338851; client=4;",
        description = "High alchs all items in inventory, supports equippables")
public class HighAlch extends PollingScript<ClientContext> implements PaintListener {
    private Component highAlch = ctx.widgets.component(ID.MAGIC_WIDGET,ID.SPELL_HIGH_ALCH);
    private int productDone;
    private int level;
    private int startExp;
    private long startTime;
    private int skill = Constants.SKILLS_MAGIC;
    private boolean expCheck = !ctx.skills.almostLevel(skill);
    private Item[] inven;

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(skill);
    }

    @Override
    public void poll() {
        Random rand = new Random();
        int nap = rand.nextInt(1000) + 2000;
        level = ctx.skills.realLevel(skill);
        if (ctx.skills.almostLevel(skill) && expCheck) ctx.controller.stop();
        switch (getState()) {
            case ACTION:
                dragItem();
                deselectItem();
//                equipCheck();
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
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.game.tab() == Game.Tab.INVENTORY;
                    }
                }, 400, 3);
                if (ctx.game.tab() == Game.Tab.INVENTORY) {
                    highAlch.interact("Cast");
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return (ctx.players.local().animation() != -1);
                        }
                    }, 300, 3);
                }
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.withdraw(ID.RUNE_NATURE, Bank.Amount.ALL);
                    closeBank();
                    if (!hasItem()) ctx.controller.stop();
                } else {
                    openNearbyBank();
                }
                break;
            case WAIT:
                Condition.sleep(nap);
                break;
        }
    }

    private void dragItem() {
        inven = ctx.inventory.items();
        if (!inven[15].valid() || inven[15].id() == ID.GOLD || inven[15].id() == ID.RUNE_NATURE) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.game.tab(Game.Tab.INVENTORY);
                }
            });
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

    private enum State {
        ACTION,WAIT,WITHDRAW
    }

    private State getState() {
        int[] reqs = {ID.RUNE_NATURE};
        if (ctx.inventory.hasAll(reqs) && hasItem()) {
            return State.ACTION;
        }
        if (!ctx.inventory.hasAll(reqs) || !hasItem()) {
            return State.WITHDRAW;
        }
        return State.WAIT;
    }

    private boolean hasItem() {
        if (!ctx.inventory.select().id(ID.RUNE_NATURE).isEmpty() &&
                !ctx.inventory.select().id(ID.GOLD).isEmpty() &&
                ctx.inventory.select().count() >= 3) {
            return true;
        }
        if (!ctx.inventory.select().id(ID.RUNE_NATURE).isEmpty() &&
                ctx.inventory.select().id(ID.GOLD).isEmpty() &&
                ctx.inventory.select().count() >= 2) {
            return true;
        }
        return false;
    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int exp = ctx.skills.experience(skill) - startExp;
        productDone = exp/65;
        int expHr = (int)(exp*3600000D/realRuntime(startTime));

        g.setColor(Color.WHITE);
        g.drawString(runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Alched %d", productDone) , 10, 140);
        g.drawString(String.format("Magic level %d", level) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }

}

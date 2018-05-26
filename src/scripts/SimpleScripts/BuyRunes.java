package scripts.SimpleScripts;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

//@Script.Manifest(
       //  name = "BuyRunes", properties = "author=nomivore; topic=1341420; client=4;",
      //description = "Sells all ITEMS in inventory to GE or Npc")
public class BuyRunes extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private Component npcStore = ctx.widgets.component(300,0);
    private Component[] runeStacks = {
            ctx.widgets.component(300,2).component(1),
            ctx.widgets.component(300,2).component(2),
            ctx.widgets.component(300,2).component(3),
            ctx.widgets.component(300,2).component(4),
//            ctx.widgets.component(300,2).component(5),
//            ctx.widgets.component(300,2).component(6),
    };
    private Component[] runePacks = {
            ctx.widgets.component(300,2).component(9),
            ctx.widgets.component(300,2).component(10),
            ctx.widgets.component(300,2).component(11),
            ctx.widgets.component(300,2).component(12),
//            ctx.widgets.component(300,2).component(13),
    };
    private Pattern runePattern = Pattern.compile(".*(?<!Mind|Body) rune");
    private Pattern packPattern = Pattern.compile(".*rune pack");
    private long startTime;
    private int initialRunes,
            boughtRunes,
            initialCash,
            currCash;
    private double costPer,
            profit;
    private State state = State.WAIT;

    @Override
    public void messaged(MessageEvent messageEvent) {
    }

    @Override
    public void start() {
        startTime = getRuntime();
        initialRunes = ctx.inventory.select().name(runePattern).count(true);
        initialCash = ctx.inventory.select().id(995).count(true);
    }

    @Override
    public void stop() {
        log.info(String.format("Cost per %f", costPer));
        log.info(String.format("Bought %d", boughtRunes));
    }

    @Override
    public void poll() {
        Random r = new Random();
        ctx.inventory.deselectItem();
        ctx.game.tab(Game.Tab.INVENTORY);
        state = getState();
        if (Utils.realRuntime(startTime) > 3600000*2) ctx.controller.stop();

        switch (state) {
            case WAIT:
                break;
            case TRADENPC:
                if (npcStore.visible()) break;
                Npc store = ctx.npcs.select().name("Betty").nearest().poll();
                if (Utils.stepInteract(store,"Trade"))
                Condition.wait(()->npcStore.visible());
                break;
            case OPENPACK:
                if (npcStore.visible()) ctx.input.send("{VK_ESCAPE}");
                Item[] inven = ctx.inventory.items();
                int c = 0;
                double rand = r.nextGaussian()*4+13;
                System.out.print((int)rand);
                for (int i : ctx.inventory.pattern()) {
                    if (inven[i].name().contains(" pack")) {
                        ctx.game.tab(Game.Tab.INVENTORY);
                        inven[i].click();
                        Condition.sleep((int)(r.nextGaussian()*10)+100);
                    }
                }
//                Utils.APmouseOffScreen();
//                if (ctx.inventory.select().count() < 28) {
                    boughtRunes = ctx.inventory.select().name(runePattern).count(true) - initialRunes;
                    costPer = (initialCash - currCash == 0) ? 0 : (double) (initialCash - currCash) / (double) boughtRunes;
                    profit = (5 - costPer) * boughtRunes;
//                }
                break;
            case BUYITEM:
                final int freeSlots = 28 - ctx.inventory.select().count();
                for (Component comp : runeStacks) {
                    if (comp.itemStackSize() > 4800) comp.interact("Buy 50");
                }
                int delay = 0;
                for (Component comp : runePacks) {
                    if (comp.itemStackSize() < 70) {
                        delay+=Math.min(500*(70-comp.itemStackSize()),4000);
                        continue;
                    }
                    if (comp.itemStackSize() == 80) {
                        delay-=2000;
                    }
                }
                log.info("Delay " + delay);
                Condition.sleep(delay);
                for (Component comp : runePacks) {
                    if (ctx.inventory.select().count() == 28) break;
                    if (comp.itemStackSize() > 70+r.nextGaussian()*3) {
                        comp.interact("Buy 5");
                        continue;
                    }
                    if (freeSlots < 10 && comp.itemStackSize() > 65) comp.interact("Buy 5");
                }

                Condition.sleep(2000);
                break;
        }
        currCash = ctx.inventory.select().id(995).count(true);
        if (currCash < 1000) ctx.controller.stop();
    }

    private enum State {
        WAIT,TRADENPC,OPENPACK,BUYITEM
    }

    private State getState() {
        if (ctx.inventory.select().count() < 28 && currCash > 1000) {
            if (npcStore.visible()) return State.BUYITEM;
            return State.TRADENPC;
        }
        if (!ctx.inventory.select().name(packPattern).isEmpty()) {
            return State.OPENPACK;
        }
        return State.WAIT;
    }


    private Font font = new Font("Trebuchet MS", Font.PLAIN, 14);
    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(font);
        strings.clear();
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add(String.format("Current cash: %d", currCash));
        strings.add("Bought runes " + boughtRunes + " : " + Utils.unitPerHour(boughtRunes,startTime));
        strings.add("Cost per " + costPer);
        strings.add("Est profit " + (int)profit + ":" + Utils.unitPerHour((int)profit,startTime));
        strings.add("State " + state.name());
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
    }

    class ShopItem {
        final int id;
        final int minStock;

        ShopItem(int id, int minStock) {
            this.id = id;
            this.minStock = minStock;
        }
    }
}

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

//@Script.Manifest(
       //  name = "BuyFeather", properties = "author=nomivore; topic=1341420; client=4;",
      //description = "Sells all ITEMS in inventory to GE or Npc")
public class BuyFeather extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private Component npcStore = ctx.widgets.component(300,0);
    private Component featherPack = ctx.widgets.component(300,16).component(9);
    private Component feathers = ctx.widgets.component(300,16).component(8);
    private long startTime;
    private int initialFeathers,
            boughtFeathers,
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
    }

    @Override
    public void stop() {
        log.info(String.format("Cost per %f", costPer));
        log.info(String.format("Bought %d", boughtFeathers));
    }

    @Override
    public void poll() {

        if (initialFeathers == 0) initialFeathers = ctx.inventory.select().id(314).count(true);
        if (initialCash == 0) initialCash = ctx.inventory.select().id(995).count(true);

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
                Npc store = ctx.npcs.select().name("Gerrant").nearest().poll();
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
                    if (inven[i].id() == 11881) {
                        ctx.game.tab(Game.Tab.INVENTORY);
                        inven[i].click();
                    }
                    if (c == (int)rand) break;
                    c++;
                }
                Utils.APmouseOffScreen();
                if (ctx.inventory.select().count() < 28) {
                    Condition.wait(() -> ctx.inventory.select().id(11881).isEmpty(), 10000, 5);
                    boughtFeathers = ctx.inventory.select().id(314).count(true) - initialFeathers;
                    costPer = (initialCash - currCash == 0) ? 0 : (double) (initialCash - currCash) / (double) boughtFeathers;
                    profit = (3 - costPer) * boughtFeathers;
                }
                break;
            case BUYITEM:
                final int freeSlots = 28 - ctx.inventory.select().count();
                if (featherPack.itemStackSize() < 60) {
                    Utils.APmouseOffScreen();
                    Condition.wait(()->featherPack.itemStackSize() > 60);
                }

                if (feathers.itemStackSize() >= 990) {
                    feathers.interact("Buy 50");
                    if (r.nextBoolean()) feathers.interact("Buy 10");
                }

                if (featherPack.itemStackSize() > 95) {
                    featherPack.interact("Buy 50");
                    Condition.sleep(1000);
                    break;
                }
                
                if ((featherPack.itemStackSize() > 60
                        && Utils.unitPerHour((int)profit,startTime) < 100000
                        && Utils.realRuntime(startTime) > 600000)
                        || freeSlots < 5) {
                    if (featherPack.itemStackSize() > 80) featherPack.interact("Buy 5");
                    featherPack.interact("Buy 1");
                    Condition.sleep(1000);
                    break;
                }

                if (featherPack.itemStackSize() > r.nextGaussian()*3+90) {
                    if (freeSlots > 9) featherPack.interact("Buy 10");
                    else featherPack.interact("Buy 5");
                    if (r.nextBoolean()) featherPack.interact("Buy 5");
                }

                Condition.sleep(500);
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
        if (ctx.inventory.select().count() == 28 && !ctx.inventory.select().id(11881).isEmpty()) {
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
        strings.add("Initial cash " + initialCash);
        strings.add("Initial feathers " + initialFeathers);
        strings.add("Bought feathers " + boughtFeathers + " : " + Utils.unitPerHour(boughtFeathers,startTime));
        strings.add("Cost per " + costPer);
        strings.add("Est profit " + (int)profit + ":" + Utils.unitPerHour((int)profit,startTime));
        strings.add("State " + state.name());
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));

        g.draw(npcStore.boundingRect());
        g.draw(featherPack.boundingRect());
        g.draw(feathers.boundingRect());
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

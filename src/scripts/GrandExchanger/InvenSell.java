package scripts.GrandExchanger;import api.ClientContext;
import api.PollingScript;
import api.GrandExchange;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Script.Manifest(
        name = "InventorySeller", properties = "author=nomivore; topic=1341420; client=4;",
        description = "Sells all ITEMS in inventory to GE or Npc")
public class InvenSell extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private Component npcStore = ctx.widgets.component(300,0);
    private long startTime;
    private GrandExchange ge;
    private int initalCash,
                earnedCash;
    private Item[] inven = ctx.inventory.items();
    private int ticks,
                trades;
    private String currItem = "";
    private HashMap<Integer,Integer> itemTickMap = new HashMap<>();
    private State state;
    private boolean sellGE = true;

    @Override
    public void messaged(MessageEvent messageEvent) {
        if (messageEvent.text().contains("You can't ")) {
            ctx.controller.stop();
        }
    }

    @Override
    public void start() {
        startTime = getRuntime();
        initalCash = ctx.inventory.select().id(995).count(true);

        if (JOptionPane.showConfirmDialog(null,"Yes for NPC, No for Grand Exchange","Settings",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            sellGE = false;
        } else {
            sellGE = true;
        }

        JOptionPane.showConfirmDialog(null,"If you still use this script, vote on the thread poll,\n otherwise it will be taken down and open sourced.","Hi",JOptionPane.OK_CANCEL_OPTION);

        for (Item i : inven) {
            itemTickMap.put(i.id(),0);
        }
    }

    @Override
    public void stop() {
        log.info(String.format("Earned cash %d",earnedCash));
    }

    @Override
    public void poll() {
        ge = new GrandExchange(ctx);
        inven = ctx.inventory.items();
        state = getState();
        switch (state) {
            case SELLGE:
                for (Item i : inven) {
                    if (i.valid() && i.id() != 995 && !ctx.inventory.select().id(i.id()).isEmpty() && itemTickMap.containsKey(i.id())) {
                        currItem = i.name();
                        ticks = itemTickMap.get(i.id());
                        if (ticks < -6) ctx.controller.stop();
                        if (itemTickMap.get(i.id()) < -4) {
                            ge.sellStack(i,1);
                        } else {
                            ge.sellStackTick(i, itemTickMap.get(i.id()));
                        }
                        break;
                    }
                }
                break;
            case OPENGE:
                if (ctx.bank.opened()) ctx.bank.close();
                ge.open();
                break;
            case ABORTCOLLECT:
                for (Item i : ctx.inventory.items()) {
                    if (itemTickMap.containsKey(i.id())) itemTickMap.replace(i.id(),itemTickMap.get(i.id())+1);
                }
                ge.abortCollect();
                Condition.wait(()->ctx.inventory.select().select(i->i.id()!=995).count() != 0,500,5);
                for (Item i : ctx.inventory.items()) {
                    if (itemTickMap.containsKey(i.id())) itemTickMap.replace(i.id(),itemTickMap.get(i.id())-1);
                    if (!itemTickMap.containsKey(i.id())) itemTickMap.put(i.id(),0);
                }
                if (ctx.inventory.select().select(i->i.id()!=995).count() == 0) ctx.controller.stop();
                break;
            case WAIT:
                break;
            case TRADENPC:
                Npc store = ctx.npcs.select().select(n-> Arrays.asList(n.actions()).contains("Trade")).nearest().poll();
                Utils.stepInteract(store,"Trade");
                Condition.wait(()->npcStore.visible());
                break;
            case SELLNPC:
                for (Item i : inven) {
                    if (i.valid() && i.id() != 995) {
                        currItem = i.name();
                        trades++;
                        i.interact("Sell 50");
                        break;
                    }
                }
                break;
        }
        earnedCash = ctx.inventory.select().id(995).count(true) - initalCash;
    }

    private enum State {
        WAIT,OPENGE,SELLGE,ABORTCOLLECT,TRADENPC,SELLNPC
    }

    private State getState() {
        if (sellGE) {
            if (!ge.opened()) {
                return State.OPENGE;
            }
            if (ge.opened()) {
                if (ge.getAvailableSlots() == 0 || ctx.inventory.select().select(i -> i.id() != 995).count() == 0)
                    return State.ABORTCOLLECT;
                return State.SELLGE;
            }
        } else {
            if (npcStore.visible()) {
                return State.SELLNPC;
            } else {
                return State.TRADENPC;
            }
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
        strings.add(String.format("Cash earned: %d", earnedCash));
        strings.add(String.format("Item: %s", currItem));
        if (sellGE) strings.add(String.format("Down ticks: %d", ticks));
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
    }
}

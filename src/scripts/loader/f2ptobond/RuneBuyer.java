package scripts.loader.f2ptobond;

import api.ClientContext;
import api.Components;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;
import osrs.ID;
import scripts.loader.Task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RuneBuyer extends Task<ClientContext> implements PaintListener {
    private Component closeButton = ctx.components.nil();
    private ArrayList<ShopItem> shoppingList = new ArrayList<>();
    public RuneBuyer(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private int stop = gausInt(300000)+600000;

    @Override
    public int stopTime() {
        return stop;
    }

    @Override
    public int getSkill() {
        return -1;
    }

    @Override
    public void initialise() {
        shoppingList.add(new ShopItem(ctx,4800, ID.FIRE_RUNE_554));
        shoppingList.add(new ShopItem(ctx,4800, ID.AIR_RUNE_556));
//        shoppingList.add(new ShopItem(ctx,4800, ID.MIND_RUNE_558));
        shoppingList.add(new ShopItem(ctx,70, ID.AIR_RUNE_PACK_12728));
        shoppingList.add(new ShopItem(ctx,20, ID.FIRE_RUNE_PACK_12734));
        shoppingList.add(new ShopItem(ctx,20, ID.DEATH_RUNE_560));
        shoppingList.add(new ShopItem(ctx,220, ID.CHAOS_RUNE_562));
    }

    @Override
    public boolean activate() {
        return ctx.inventory.select().id(ID.COINS_995).count(true) > 1000;
    }


    private boolean hop = false;
    private State state = State.WAIT;
    private int recentHops = 0;
    private int hops = 4+gausInt(2);
    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case WAIT:
                break;
            case TRADE:
                Npc shop = ctx.npcs.select().action("Trade").nearest().poll();
                if (Utils.stepInteract(shop,"Trade")) {
                    for (Component c : shoppingList.stream().map(ShopItem::getComponent).collect(Collectors.toList())) {
                        if (Condition.wait(c::visible,10,50)) break;
                    }
                }
                closeButton = (closeButton.valid()) ? closeButton : ctx.components.findCloseButton(shoppingList.get(0).getComponent().widget());
                break;
            case HOP:
                if (closeButton.visible()) {
                    ctx.input.send("{VK_ESCAPE}");
                    Condition.wait(()->!closeButton.visible(),500,5);
                    break;
                }

                if (!Condition.wait(ctx.worlds::open,300,5)) break;
                Component currWorldComp = ctx.components.select(Worlds.WORLD_WIDGET).text("Current world").poll();
                int currWorldNum = (currWorldComp.valid()) ?
                        ((!currWorldComp.text().isEmpty()) ? Integer.valueOf(currWorldComp.text().replaceAll("\\D",""))-300 : -1) : -1;
                log.info("Current world " + currWorldNum);
                ctx.worlds.select()
                        .select(w->w.id()>currWorldNum)
                        .joinable()
                        .types(World.Type.FREE)
                        .sort((Comparator.comparingInt(World::id)));
                if (ctx.worlds.isEmpty()) {
                    ctx.worlds.select()
                            .select(w->w.id()!=currWorldNum)
                            .joinable()
                            .types(World.Type.FREE)
                            .sort((Comparator.comparingInt(World::id)));
                }
                World newWorld = ctx.worlds.poll();
                if (Condition.wait(newWorld::hop,300,5)) {
                    log.info("Successfully hopped");
                    hop = false;
                    recentHops++;
                    if (recentHops > hops) {
                        log.info("Sleeping");
                        Utils.APmouseOffScreen(3);
                        recentHops = 0;
                        hops = gausInt(2)+3;
                        Condition.sleep(gausInt(10000)+30000);
                    }
                }
                break;
            case BUY:

                boolean bought = false;
                for (ShopItem s : shoppingList) {
                    if (s.getComponent().itemStackSize() >= s.getMinimumStock()) {
//                        s.getComponent().interact("Buy 50");
                        s.getComponent().interact((s.getInteract().isEmpty()) ? "Buy 50" : s.getInteract());
                        if (r.nextInt(10) == 1) s.getComponent().click();
                        bought = true;
                        Condition.sleep(gausInt(500)+300);
                        break;
                    }
                }
                if (!bought) hop = true;
                break;
            case OPEN:
                if (closeButton.visible()) {
                    ctx.input.send("{VK_ESCAPE}");
                    Condition.wait(()->!closeButton.visible(),500,5);
                    break;
                }

                int[] p = ctx.inventory.pattern();
                ArrayList<Integer> pattern = Arrays.stream(p).boxed().collect(Collectors.toCollection(ArrayList::new));
                Collections.reverse(pattern);
                for (Integer i : pattern) {
                    ctx.game.tab(Game.Tab.INVENTORY);
                    ctx.inventory.deselectItem();
                    ctx.inventory.select().select(it->it.inventoryIndex()==i).action("Open").poll().click();
                }
                break;
        }
    }

    @Override
    public void finish() {

    }

    @Override
    public String getStateName() {
        return state.name();
    }

    private State getState() {
        if (hop) return State.HOP;
        if (ctx.inventory.select().count() == 28) return State.OPEN;
        if (closeButton.visible()) {
            return State.BUY;
        } else {
            return State.TRADE;
        }
    }

    @Override
    public void repaint(Graphics g) {
//        g.drawPolygon(Components.RectangleToPolygon(closeButton.boundingRect()));
//        for (ShopItem s : shoppingList) {
//            if (!s.getComponent().equals(ctx.components.nil())) {
//                if (s.getComponent().itemStackSize() >= s.getMinimumStock()) {
//                    g.setColor(Color.GREEN);
//                } else {
//                    g.setColor(Color.RED);
//                }
//                g.drawPolygon(Components.RectangleToPolygon(s.getComponent().boundingRect()));
//                g.drawString(s.getMinimumStock()+"",s.getComponent().centerPoint().x,s.getComponent().centerPoint().y);
//            }
//        }
    }

    private enum State {
        WAIT, TRADE, HOP, BUY, OPEN
    }
}

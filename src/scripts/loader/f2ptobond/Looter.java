package scripts.loader.f2ptobond;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import scripts.ID;
import scripts.loader.Profitable;
import scripts.loader.Task;
import org.powerbot.script.rt4.Component;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;

import static java.lang.Thread.sleep;


public class Looter extends Task<ClientContext> implements PaintListener, Profitable {
    private GroundItem curr = ctx.groundItems.nil();
    private State state = State.WAIT;
    private Tile bankTile = new Tile(3221,3217,0);
    private Tile lootTile = new Tile(3238,3215,0);
    private Component safeZone = ctx.widgets.component(90,43);
    public Looter(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }
    private int profit = 0;
    private int inventoryWorth = 0;
    private int stop = gausInt(1000000) + 1200000;
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
        new Thread(new GroundCheck()).start();
        new Thread(new GroundSearch()).start();
    }

    @Override
    public void finish() {
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public int profit() {
        return profit;
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case BANK:
                if (bankTile.distanceTo(ctx.players.local()) > 1 && ctx.movement.destination().distanceTo(bankTile) > 1) {
                    ctx.movement.stepWait(bankTile);
                    break;
                }
                if (ctx.bank.opened()) {
                    for (Item i : ctx.inventory.items()) {
                        if (!PriceTable.containsKey(i.name())) continue;
                        profit += PriceTable.get(i.name())*i.stackSize();
                    }
                    ctx.bank.depositInventory();
                    ctx.input.send("{VK_ESCAPE}");
                    Condition.wait(()->ctx.inventory.select().count() == 0,500,4);
                } else {
                    Condition.wait(()->!ctx.players.local().inMotion(),500,7);
                    if (!ctx.players.local().inMotion()) ctx.bank.open();
                    Condition.wait(ctx.bank::opened,100,5);
                }
                break;
            case LOOT:
                Utils.stepInteract(curr,"Take");
                if (ctx.movement.destination().distanceTo(curr.tile()) == 0
                        && curr.tile().distanceTo(ctx.players.local()) != 0) {
                    Condition.wait(()->curr.tile().distanceTo(ctx.players.local()) == 0,gausInt(50)+50,20);
                    break;
                }
                break;
            case WAIT:
                break;
            case WALK:
                ctx.movement.stepWait(lootTile);
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.drawString(inventoryWorth + " gp",Utils.chatboxTopLeft().x,Utils.chatboxTopLeft().y);
        g.setColor(Color.GREEN);
        if (curr.valid()) {
            g.drawPolygon(curr.tile().matrix(ctx).bounds());
            if (PriceTable.containsKey(curr.name())) g.drawString(curr.name() + " (" + curr.stackSize() + ")-" + (curr.stackSize()*PriceTable.get(curr.name())), curr.centerPoint().x,curr.centerPoint().y);
        }
        g.setColor(Color.GRAY);
        if (lootTile.matrix(ctx).inViewport()) g.drawPolygon(lootTile.tile().matrix(ctx).bounds());
        if (bankTile.matrix(ctx).inViewport()) g.drawPolygon(bankTile.tile().matrix(ctx).bounds());
    }

    private enum State {
        BANK, LOOT,WAIT,WALK
    }

    private State getState() {
        if (safeZone.visible() && ctx.inventory.select().count(true) > 3) {
                return State.BANK;
        } else {
            if (ctx.players.local().inCombat() && (ctx.players.local().inCombat() && ctx.inventory.select().count(true) <= 3)) {
                return State.WAIT;
            }
            if (ctx.inventory.select().count() > 15
                    || (ctx.players.local().inCombat() && ctx.inventory.select().count(true) > 3)
                    || inventoryWorth > 5000) {
                return State.BANK;
            }

            if (lootTile.distanceTo(ctx.players.local()) > 20) return State.WALK;
            if (ctx.inventory.select().count() < 28) return State.LOOT;
        }
        return State.WAIT;
    }

    @Override
    public String getStateName() {
        return state.name();
    }


    private final HashMap<String,Integer> PriceTable = new HashMap<>();
    class GroundCheck implements Runnable {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended() && ctx.inventory.select().count() < 28) {
                    try {
                        sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final int minWorth = 55;
                    ctx.groundItems.select(15).within(lootTile,15).select(i->PriceTable.containsKey(i.name())
                                    && (PriceTable.get(i.name())*i.stackSize() > minWorth || PriceTable.get(i.name()) == 0))
                                    .sort((Comparator.comparingInt(o -> PriceTable.get(o.name()) * o.stackSize())))
                                    .reverse();
                    curr = (curr.valid()) ? curr : ctx.groundItems.limit(2).nearest().poll();
                }
            }
        }
    }

    class GroundSearch implements Runnable {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended()) {
                    try {
                        sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    inventoryWorth = 0;
                    for (Item i : ctx.inventory.items()) {
                        if (!PriceTable.containsKey(i.name())) continue;
                        inventoryWorth += PriceTable.get(i.name())*i.stackSize();
                    }
                    if (ctx.groundItems.select().within(lootTile,15).select(i->!PriceTable.containsKey(i.name())).isEmpty()) continue;
                    GroundItem drop = ctx.groundItems.poll();
                    if (drop.valid()) {
                        if (drop.name().equals("Coins")) {
                            PriceTable.put(drop.name(), 1);
                        } else {
                            PriceTable.put(drop.name(), new GeItem(drop.id()).price);
                            log.info(drop.name() + " " + PriceTable.get(drop.name()));
                        }
                    }
                }
            }
        }
    }
}

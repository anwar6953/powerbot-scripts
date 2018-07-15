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

import java.awt.*;


public class DraynorFish extends Task<ClientContext> implements PaintListener, Profitable {
    private Npc curr = ctx.npcs.nil();
    private String spotName = "Fishing spot";
    private String actionName = "Small Net";
    private State state = State.WAIT;
    private Tile bankTile = new Tile(3092,3245,0);
    private Tile fishTile = new Tile(3086,3230,0);
    private final int[] bounds = {-24, 48, 0, 0, -44, 12};
    private int looted = 0;
    private int itemPrice = -1;

    public DraynorFish(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private int stop = gausInt(1800000) + 3600000;
    @Override
    public int stopTime() {
        return stop;
    }

    @Override
    public int getSkill() {
        return Constants.SKILLS_FISHING;
    }

    @Override
    public void initialise() {
        itemPrice = new GeItem(ID.RAW_ANCHOVIES_321).price;
    }

    @Override
    public void finish() {
        log.info("Total banked "+looted);
        log.info("Profit "+(looted* itemPrice) + "-"+ timer.unitPerHour(looted* itemPrice));
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public int profit() {
        return looted* itemPrice;
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case BANK:
                if (bankTile.distanceTo(ctx.players.local()) > 1) {
                    ctx.movement.stepWait(bankTile);
                    break;
                }
                if (ctx.bank.opened()) {
                    final int count = ctx.inventory.select().id(ID.RAW_ANCHOVIES_321).count();
                    ctx.bank.depositAllExcept(ID.SMALL_FISHING_NET_303);
                    ctx.bank.withdrawUntil(ID.SMALL_FISHING_NET_303,1);
                    if (Condition.wait(() -> ctx.inventory.select().count() == 1,10,400)) {
                        looted+=count;
                        log.info("Total banked "+looted);
                        log.info("Profit "+(looted* itemPrice) + "-"+ timer.unitPerHour(looted* itemPrice));
                    }
                    ctx.input.send("{VK_ESCAPE}");
                } else {
                    ctx.bank.open();
                    Condition.wait(ctx.bank::opened,100,5);
                }
                break;
            case FISH:
                curr = (curr.valid()) ? curr : ctx.npcs.select().name(spotName).nearest().poll();
                curr.bounds(bounds);

                if (ctx.players.local().interacting().valid() && ctx.players.local().animation() != -1) {
                    Condition.wait(()->!ctx.players.local().interacting().valid()
                            || ctx.chat.canContinue(),gausInt(4000)+2000,50);
                } else {
                    Utils.stepInteract(curr,actionName);
                    if (!Condition.wait(()->ctx.players.local().interacting().valid()
                            && (ctx.players.local().animation() != -1 || ctx.players.local().inMotion()),gausInt(200)+300,5)) break;
                    Utils.APmouseOffScreen(3);
                }
                break;
            case WAIT:
                break;
            case WALK:
                ctx.movement.stepWait(fishTile);
                break;
            case DROP:
                ctx.inventory.shiftDrop(ID.RAW_SHRIMPS_317);
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.GREEN);
        if (curr.valid()) g.drawPolygon(curr.tile().matrix(ctx).bounds());
        g.setColor(Color.GRAY);
        if (fishTile.matrix(ctx).inViewport()) g.drawPolygon(fishTile.tile().matrix(ctx).bounds());
        if (bankTile.matrix(ctx).inViewport()) g.drawPolygon(bankTile.tile().matrix(ctx).bounds());
    }

    private enum State {
        BANK, FISH,WAIT,WALK,DROP
    }

    private int inventoryToDeposit = gausInt(5) + 23;
    private State getState() {
        if (ctx.players.local().animation() == -1) {
            if (ctx.inventory.select().id(ID.RAW_SHRIMPS_317).count() > 0) {
                return State.DROP;
            }

            if (ctx.inventory.select().count() > inventoryToDeposit || ctx.inventory.select().count() == 28
                            || !hasItems()) {
                return State.BANK;
            }
            inventoryToDeposit = Math.max(gausInt(5) + 23,26);


            if (fishTile.distanceTo(ctx.players.local()) > 10) return State.WALK;
            if (ctx.inventory.select().count() < 28 &&
                    hasItems()) return State.FISH;
        }
        return State.WAIT;
    }

    private boolean hasItems() {
        return !ctx.inventory.select().id(ID.SMALL_FISHING_NET_303).isEmpty();
    }

    @Override
    public String getStateName() {
        return state.name();
    }
}

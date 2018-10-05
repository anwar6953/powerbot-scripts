package scripts.loader.hcim;

import api.ClientContext;
import api.PollingScript;
import api.utils.Clicker;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GeItem;
import org.powerbot.script.rt4.Npc;
import scripts.ID;
import scripts.loader.Profitable;
import scripts.loader.Task;

import java.awt.*;


public class FlaxPick extends Task<ClientContext> implements PaintListener, Profitable {
    public static final Tile[] path = {new Tile(2455,3424,0),new Tile(2455,3427,0),new Tile(2454,3429,0),new Tile(2454,3432,0),new Tile(2454,3435,0),new Tile(2452,3436,0),new Tile(2451,3438,0),new Tile(2450,3440,0),new Tile(2449,3438,0),new Tile(2446,3438,0),new Tile(2444,3436,0),new Tile(2443,3434,0),new Tile(2445,3433,1),new Tile(2445,3430,1),new Tile(2446,3428,1),new Tile(2446,3427,1)};
    private GameObject curr = ctx.objects.nil();
    private State state = State.WAIT;
    private int looted,itemPrice;
    private Tile bankTile = new Tile(2446,3427,1);

    public FlaxPick(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private int stop = gausInt(400000) + 400000;
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
        itemPrice = new GeItem(ID.FLAX_1779).price;
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
                    walker.walkPath(path);
                    break;
                }
                if (ctx.movement.energyLevel() < 15) ctx.controller.stop();
                if (ctx.bank.opened()) {
                    final int count = ctx.inventory.select().id(ID.FLAX_1779).count();
                    ctx.bank.depositInventory();
                    if (Condition.wait(() -> ctx.inventory.select().count() == 0,10,400)) {
                        looted+=count;
                        log.info("Total banked "+looted);
                        log.info("Profit "+(looted* itemPrice) + "-"+ timer.unitPerHour(looted* itemPrice));
                        inventoryToDeposit = gausInt(5) + 23;
                    }
                    ctx.input.send("{VK_ESCAPE}");
                } else {
                    ctx.bank.open();
                    Condition.wait(ctx.bank::opened,100,5);
                }
                break;
            case PICK:
                if (ctx.players.local().tile().floor() == 1) {
                    walker.walkPathReverse(path);
                    break;
                }
                if (!curr.valid()) {
                    curr = ctx.objects.select(15).name("Flax").nearest().limit(3).shuffle().poll();
                    if (gausInt(10) == 4) {
                        Utils.APmouseOffScreen(3);
                        Condition.sleep(gausInt(10000) + 5000);
                    }
                }
                if (curr.inViewport()) {
                    Condition.wait(()->!ctx.players.local().inMotion());
                    Clicker.hoverClick(ctx,curr,"Pick");
                    Condition.sleep(gausInt(500)+200);
                } else {
                    ctx.movement.stepWait(curr);
                }
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.drawPolygon(curr.tile().matrix(ctx).bounds());
    }

    private enum State {
        BANK,PICK,WAIT
    }

    private int inventoryToDeposit = gausInt(5) + 23;
    private State getState() {
        if (ctx.inventory.select().count() == 28 || ctx.inventory.select().count() > inventoryToDeposit) {
            return State.BANK;
        }
        return State.PICK;
    }


    @Override
    public String getStateName() {
        return state.name();
    }
}

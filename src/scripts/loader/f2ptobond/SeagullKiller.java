package scripts.loader.f2ptobond;

import api.ClientContext;
import api.Components;
import api.PollingScript;
import api.utils.Timer;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;
import scripts.ID;
import scripts.loader.Task;

import java.awt.*;

import static java.lang.Thread.sleep;


public class SeagullKiller extends Task<ClientContext> implements PaintListener {
    private Npc curr = ctx.npcs.nil();
    private GroundItem loot = ctx.groundItems.nil();
    private String mobName = "Seagull";
    private State state = State.WAIT;
    private Tile depositTile = new Tile(3045,3235,0);
    private Tile seagullTile = new Tile(3028,3237,0);
    private final int[] bounds = {-24, 24, -64, 0, -24, 24};
    private int looted = 0;
    private int bonePrice = -1;

    public SeagullKiller(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private int stop = gausInt(1800000) + 3600000;
    @Override
    public int stopTime() {
        return stop;
    }

    @Override
    public int getSkill() {
        return Constants.SKILLS_STRENGTH;
    }

    @Override
    public void initialise() {
        ctx.groundItems.select(10).within(seagullTile,10).id(ID.BONES_526);
        bonePrice = new GeItem(ID.BONES_526).price;
    }

    @Override
    public void finish() {
        log.info("Total banked "+looted);
        log.info("Profit "+(looted*bonePrice) + "-"+ timer.unitPerHour(looted*bonePrice));
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case DEPOSIT:
                if (depositTile.distanceTo(ctx.players.local()) > 3) {
                    ctx.movement.stepWait(depositTile);
                    break;
                }
                if (ctx.depositBox.opened()) {
                    final int count = ctx.inventory.select().count();
                    ctx.depositBox.depositInventory();
                    if (Condition.wait(() -> ctx.inventory.select().count() == 0,500,4)) {
                        looted+=count;
                        log.info("Total banked "+looted);
                        log.info("Profit "+(looted*bonePrice) + "-"+ timer.unitPerHour(looted*bonePrice));
                    }
                    ctx.input.send("{VK_ESCAPE}");
                } else {
                    ctx.depositBox.open();
                    Condition.wait(ctx.depositBox::opened,100,5);
                }
                break;
            case ATTACK:
                ctx.npcs.select();
                for (Player p : ctx.players.select()) {
                    if (p.equals(ctx.players.local())) continue;
                    if (!p.interacting().valid()) continue;
                    ctx.npcs.select(m->p.interacting().equals(m));
                    log.info("Skipping npc being attacked");
                }
                curr = (curr.healthPercent() == 100) ? curr : ctx.npcs.within(10)
                        .select(m->m.healthPercent() > 10
                                && (m.interacting().equals(ctx.players.local()) || !m.interacting().valid()))
                        .name(mobName).nearest().limit(3).shuffle().poll();
                curr.bounds(bounds);
                Utils.stepInteract(curr);
                if (!Condition.wait(()->ctx.players.local().interacting().valid(),10+gausInt(5),50)) break;
                if (r.nextInt(100) > 90) {
                    Utils.APmouseOffScreen(3);
                    Condition.sleep(gausInt(5000)+5000);
                }
                Condition.wait(()->!ctx.players.local().interacting().valid(),30,200);
                break;
            case WAIT:
                break;
            case WALK:
                ctx.movement.stepWait(seagullTile);
                break;
            case LOOT:
                for (GroundItem i : ctx.groundItems) {
                    if (!i.valid() || !i.inViewport()) continue;
                    log.info("Looting " + i.name());
                    loot = i;
                    i.click();
                    Condition.wait(()->i.tile().distanceTo(ctx.players.local()) == 0,50+gausInt(10),100);
                }
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.RED);
        if (loot.valid()) g.drawPolygon(loot.tile().matrix(ctx).bounds());
        g.setColor(Color.GREEN);
        if (curr.valid()) g.drawPolygon(curr.tile().matrix(ctx).bounds());
        g.setColor(Color.GRAY);
        if (seagullTile.matrix(ctx).inViewport()) g.drawPolygon(seagullTile.tile().matrix(ctx).bounds());
        if (depositTile.matrix(ctx).inViewport()) g.drawPolygon(depositTile.tile().matrix(ctx).bounds());
    }


    private enum State {
        DEPOSIT,ATTACK,WAIT,WALK,LOOT
    }

    private int inventoryToDeposit = gausInt(5) + 20;
    private State getState() {
        if (ctx.inventory.select().count() > inventoryToDeposit) {
            return State.DEPOSIT;
        }
        inventoryToDeposit = gausInt(5) + 20;
        if (seagullTile.distanceTo(ctx.players.local()) > 10) return State.WALK;
        if (ctx.inventory.select().count() < 28 && ctx.groundItems.select().within(seagullTile,5).id(ID.BONES_526).select(i->i.tile().matrix(ctx).inViewport()).nearest().size() > 2 + gausInt(1)) {
            return State.LOOT;
        }
        if (!ctx.players.local().interacting().valid()) return State.ATTACK;
        return State.WAIT;
    }

    @Override
    public String getStateName() {
        return state.name();
    }
}

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

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class SafeSpotter extends Task<ClientContext> implements PaintListener {
    private Npc curr = ctx.npcs.nil();
    private String mobName = "Seagull";
    private State state = State.WAIT;
    private Tile safespot = Tile.NIL;

    public SafeSpotter(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }

    private int stop = gausInt(1800000) + 3600000;
    @Override
    public int stopTime() {
        return stop;
    }

    @Override
    public int getSkill() {
        return Constants.SKILLS_HITPOINTS;
    }

    @Override
    public void initialise() {
        safespot = ctx.players.local().tile();
        ArrayList<Npc> allMobs = new ArrayList<>();
        ArrayList<String> mobList = new ArrayList<>();
        ctx.npcs.select().within(10).action("Attack").addTo(allMobs);
        for (Npc m : allMobs) {
            if (!mobList.contains(m.name())) mobList.add(m.name());
        }
        String[] mobs = mobList.toArray(new String[mobList.size()]);
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                mobs, mobs[0]);

        if (selectedValue == null) ctx.controller.stop();
        else mobName = selectedValue.toString();
    }

    @Override
    public void finish() {
    }

    @Override
    public boolean activate() {
        return true;
    }


    @Override
    public void execute() {
        state = getState();
        switch (state) {
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
                Utils.stepInteract(curr);
                if (!Condition.wait(()->ctx.players.local().interacting().valid(),25+gausInt(25),10)) break;
                if (r.nextInt(100) > 90) {
                    Utils.APmouseOffScreen(3);
                    Condition.sleep(gausInt(7000)+2000);
                }
                Condition.wait(()->!ctx.players.local().interacting().valid(),30,200);
                break;
            case WAIT:
                break;
            case WALK:
                ctx.movement.stepWait(safespot);
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.setColor(Color.GREEN);
        if (curr.valid()) g.drawPolygon(curr.tile().matrix(ctx).bounds());
        g.setColor(Color.GRAY);
        if (safespot.matrix(ctx).inViewport()) g.drawPolygon(safespot.tile().matrix(ctx).bounds());
    }

    private enum State {
        ATTACK,WAIT,WALK
    }

    private State getState() {
        if (safespot.distanceTo(ctx.players.local()) > 0) return State.WALK;
        if (!ctx.players.local().interacting().valid()
                || ctx.chat.canContinue()) return State.ATTACK;
        return State.WAIT;
    }

    @Override
    public String getStateName() {
        return state.name();
    }
}

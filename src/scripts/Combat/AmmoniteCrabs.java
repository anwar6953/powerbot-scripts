package scripts.Combat;

import api.*;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.GeItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;


//@Script.Manifest(
       //  name = "AmmoniteCrabs", properties = "author=LOL; topic=1330081; client=4;",
      //description = "Kills AmmoniteCrabs")
public class AmmoniteCrabs extends PollingScript<ClientContext> implements PaintListener {

//    private static final Tile[] tiles = {new Tile(3185, 3436, 0), new Tile(3183, 3433, 0), new Tile(3185, 3430, 0), new Tile(3187, 3427, 0)};

    private boolean reset = false;
//    private static Tile destTile = new Tile(3733, 3846, 0);// 3 spot
//    private static final Tile resetTile = new Tile(3736, 3810, 0); //3 spot
    private static Tile destTile = new Tile(3717, 3846, 0);// 3 spot
    private static final Tile resetTile = new Tile(3714, 3875, 0); //3 spot
    private int level;
    private int startExp;
    private long startTime;
    private int skill = Constants.SKILLS_HITPOINTS;
    private Random rand = new Random();
    private boolean looting = false;
    private GroundItem loot;
    private State state = State.WAIT;
    private int boostSkill = Constants.SKILLS_RANGE;
    private String[] boostNames = new String[]{"Ranging potion.*","Combat potion.*",".*rength.*"};
    private Antipattern AP = new Antipattern(ctx,Utils) {

        @Override
        public void repaint(Graphics g) {
            Utils.simplePaint(g,buffer+String.valueOf(secondsAFK));
        }

        {
        profileHandler();
        }
    };

    @Override
    public void stop() {
    }

    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
        startTime = getRuntime();
        new Thread(new GroundCheck()).start();
        new Thread(new GroundSearch()).start();
        new Thread(new AnimationChecker(ctx));
        log.info("Start");
        ctx.inventory.select().name(Pattern.compile(".*potion.*")).poll();
    }

    @Override
    public void poll() {
        if (Utils.realRuntime(startTime) >= 3600000*3) ctx.controller.stop();
        level = ctx.skills.realLevel(skill);
        if (ctx.movement.energyLevel() < 20) ctx.controller.stop();
        state = getState();
        switch (state) {
            case WAIT:
                AP.incrementAFK();
                if (AP.getSecondsAFK()%240 == 0) Utils.APrandomTurn();
                Condition.sleep(1000);
                final List<Npc> rocks = new ArrayList<>();
                ctx.npcs.select().within(4).name("Fossil Rock").addTo(rocks);
                if (rocks.size() >= 2) {
                    log.info(rocks.size()+"");
                    Condition.wait(()->!ctx.players.local().interacting().valid());
                    reset = true;
                    return;
                }
                rocks.clear();

                if (!ctx.players.local().interacting().valid() && !ctx.players.local().inCombat()) {
                    if (Condition.wait(()->ctx.players.local().interacting().valid())) break;
                    AP.afkReturn();
                    log.info("Attacking crab");
                    Npc crab = ctx.npcs.select().within(5).name(Pattern.compile(".+Crab")).select(m->m.healthPercent() > 20).poll();
                    crab.interact("Attack",crab.name());
                }
                break;
            default:
                AP.afkReturn();
                break;
        }
        state = getState();
        switch (state) {
            case LOOT:
                if (ctx.inventory.select().count() == 28) {
                    ctx.inventory.select().name(Pattern.compile(".*potion.*")).poll().interact("Drop");
                }
                if (loot.inViewport()) {
                    loot.interact("Take", loot.name());
                } else {
                    ctx.movement.stepWait(loot);
                }
                Utils.APmouseOffScreen();
                break;
            case TODEST:
                log.info("Running to dest tile");
                log.info(Utils.runtimeFormatted(startTime));
                ctx.movement.stepWait(destTile);
                break;
            case TORESET:
                log.info("Running to reset tile");
                log.info(Utils.runtimeFormatted(startTime));
                if (resetTile.distanceTo(ctx.players.local()) < 10) reset = false;
                ctx.movement.stepWait(resetTile);
                break;
            case BOOST:
                Item boost = ctx.inventory.select().name(Pattern.compile(".*potion.*")).poll();
                boost.interact("Drink");
                Utils.APmouseOffScreen();
                Condition.wait(() -> ctx.skills.level(boostSkill) != ctx.skills.realLevel((boostSkill)),500, 3);
        }
    }

    private enum State {
        TODEST,TORESET,WAIT,BOOST,LOOT
    }

    private State getState() {
        if (destTile.distanceTo(ctx.players.local()) > 0 && !reset) {
            return State.TODEST;
        } else if (reset) {
            return State.TORESET;
        }
        if (looting) {
            return State.LOOT;
        }
        if (ctx.skills.level(boostSkill) == ctx.skills.realLevel((boostSkill)) &&
                !ctx.inventory.select().name(Pattern.compile(".*potion.*")).isEmpty()) {
            return State.BOOST;
        }
        return State.WAIT;
    }

    public static final Font TAHOMA = new Font("Trebuchet MS", Font.PLAIN, 18);
    private String[] strings = new String[4];
    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int exp = ctx.skills.experience(skill) - startExp;
        int expHr = (int)(exp*3600000D/Utils.realRuntime(startTime));

        strings[0]=Utils.runtimeFormatted(startTime);
        strings[1]=String.format("Level %d", level);
        strings[2]=String.format("Exp %d/hr", expHr);
        strings[3]=state.name();
        AP.repaint(g);
        Utils.simplePaint(g,strings);
    }

    private final HashMap<String,Integer> PriceTable = new HashMap<>();

    class GroundCheck implements Runnable {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended()) {
                    loot = ctx.groundItems.select(5).select(i->
                            (PriceTable.containsKey(i.name()) && PriceTable.get(i.name())*i.stackSize() > 1000) ||
                            i.name().contains("fossil")).poll();
                    looting = loot.valid();
                    if (looting) System.out.print(PriceTable + "\n");
                    try {
                        sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class GroundSearch implements Runnable {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended()) {
                    GroundItem drop = ctx.groundItems.select(5).select(i->!PriceTable.containsKey(i.name())).poll();
                    if (drop.valid()) {
                        if (drop.name().equals("Coins")) {
                            PriceTable.put(drop.name(), 1);
                        } else {
                            PriceTable.put(drop.name(), new GeItem(drop.id()).price);
                        }
                    }
                    try {
//                        System.out.print(PriceTable + "\n");
                        sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

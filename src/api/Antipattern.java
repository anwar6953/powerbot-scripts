package api;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.Game;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

public abstract class Antipattern implements PaintListener {
    protected ClientContext ctx;
    protected PollingScript.Utils Utils;
    protected int average,
                    std;
    protected Random r = new Random();
    protected Logger log = Logger.getLogger(getClass().getName());
    protected boolean fullAccess = false;
    protected String buffer = "                                     ";
    protected Integer[] allowedIDs = new Integer[] {};
    protected Integer[] bannedIDs = new Integer[] {};

    public Antipattern(ClientContext ctx, PollingScript.Utils utils) {
        this.ctx = ctx;
        Utils = utils;
    }

    /**
     *  Sets average and std for profile
     *  If not an allowed user, sets both to 0
     *  Must run in constructor
     */
    protected void profileHandler() {
        int userID = Integer.parseInt(ctx.properties.getProperty("user.id"));
        if (Arrays.asList(bannedIDs).contains(userID)) {
            log.info("Get lost");
            ctx.controller.stop();
        }
        if (Arrays.asList(allowedIDs).contains(userID)) {
            this.average = userID % 100;
            this.std = r.nextInt(20) + 10;
            fullAccess = true;
            log.info("User " + userID + " Full access");
        } else {
            if (!Boolean.valueOf(ctx.properties.get("user.vip").toString()))
                ctx.properties.setProperty("script.timeout","7200000");
            this.average = 0;
            this.std = 0;
            log.info("User " + userID + " Default access");
            //blank
        }
    }

    /**
     *
     * @return new normally distributed number based on average and std
     */
    protected int getState() {
        int num = (int)(r.nextGaussian()*std + average);
//        log.info("Num = " + num + "\n");
        if (Math.abs(num - average) > std*1.6449) log.info("Top/bottom 5% number");
        return num;
    }

    public void useTwo(int id1, int id2) {
        if (ctx.inventory.select().id(id1).count() == 1 &&
                ctx.inventory.select().id(id2).count() == 1) {
            ctx.inventory.select().id(id1).poll().interact("Use");
            ctx.inventory.select().id(id2).poll().interact("Use");
            return;
        }
        switch (getState()/25%4) {
            case 0:
                log.info("First-First\n");
                ctx.inventory.select().id(id1).poll().interact("Use");
                ctx.inventory.select().id(id2).poll().interact("Use");
                break;
            case 1:
                log.info("First-FirstLeft\n");
                ctx.inventory.select().id(id1).poll().interact("Use");
                ctx.inventory.firstLeft(id2).interact("Use");
                break;
            case 2:
                log.info("RandomLeft-FirstLeft\n");
                ctx.inventory.selectIndexes(id1,r.nextInt(6)*4).interact("Use");
                ctx.inventory.firstLeft(id2).interact("Use");
                break;
            case 3:
                log.info("Last-First\n");
                ctx.inventory.lastItem(id1).interact("Use");
                ctx.inventory.select().id(id2).poll().interact("Use");
                break;
        }
    }

    public void closeBank() {
        if (!ctx.bank.opened()) return;
        if (ctx.varpbits.varpbit(1224) != -1975088063) {
            ctx.bank.close();
        }
        switch ((average+((getState() > average+std*1.5)?1:0))%2) {
            case 0:
                log.info("Click close bank");
                ctx.bank.close();
                if (!Condition.wait(() -> !ctx.bank.opened(), 1000, 4))
                    log.info("Error: Failed to close bank");
                break;
            case 1:
                log.info("Esc close bank");
                if (!Condition.wait(() -> ctx.input.send("{VK_ESCAPE}") && !ctx.bank.opened(), 1000, 4))
                    log.info("Error: Failed to close bank");
                break;
        }
    }

    public int secondsAFK;
    public boolean afkReturn() {
        if (secondsAFK < 10) {
            secondsAFK = 0;
            return false;
        }
        int delay = Math.min(secondsAFK*(50+getState()), 30000+getState()*50);
        log.info("AFK return delay " + delay);
        Condition.sleep(delay);
        log.info("AFKed for " + secondsAFK);
        secondsAFK = 0;
        //
        return true;
    }

    public int getSecondsAFK() {
        return secondsAFK;
    }

    public void incrementAFK() {
        secondsAFK++;
    }

    public void setSecondsAFK(int secondsAFK) {
        this.secondsAFK = secondsAFK;
    }

    public long lastActionTime = System.currentTimeMillis();
    public void actionDelay() {
        int secondsSince = (int)((System.currentTimeMillis() - lastActionTime)/1000);
        if (secondsSince < 10) {
            lastActionTime = System.currentTimeMillis();
            return;
        }
        int delay = secondsSince*(50+getState());
        log.info("Action delay " + delay);
        Condition.sleep(delay);
        lastActionTime = System.currentTimeMillis();
    }

    public void antiAFK() {
        int i = r.nextInt(7);
        log.info("Anti afk number" + i);
        switch (i) {
            case 0:
                ctx.groundItems.select(7).poll().click(false);
                log.info("Anti afk click ground item");
                break;
            case 1:
                ctx.game.tab(Game.Tab.STATS);
                Condition.sleep(2500);
                ctx.game.tab(Game.Tab.INVENTORY);
                log.info("Anti afk change tabs");
                break;
            case 2:
                Utils.APrandomTurn();
                log.info("Anti afk random turn");
                break;
            case 3:
                ctx.players.local().tile().matrix(ctx).click();
                log.info("Anti afk click player tile");
                break;
            case 4:
                ctx.game.tab(Game.Tab.EQUIPMENT);
                Condition.sleep(2500);
                ctx.game.tab(Game.Tab.INVENTORY);
                log.info("Anti afk change tabs");
                break;
        }
    }

    public Random r() {
        return r;
    }
}

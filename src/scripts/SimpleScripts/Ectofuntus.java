package scripts.SimpleScripts;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.TilePath;

import java.awt.*;

////@Script.Manifest(
//       //  name = "Ectofuntus", properties = "author=LOL; topic=1330081; client=4;",
//      //description = "Makes fires at Varrock")
public class Ectofuntus extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int slimeID = 4286;
    private int potID = 1931;
    private int bucketID = 1925;
    private int boneID = 532; //526 bones, 532 big
    private int bonemealID = 4257; //4255 bones, 4257 big
    private int tokenID = 4278;
    private int barrierID = 16105; //Energy Barrier, Pass, Pay-toll(2-Ecto)
    private int staircaseID1 = 16646;// Climb-up
    private int staircaseID2 = 16647;// Climb-down
    private int loaderID = 16654;// Climb-up
    private int ectoID = 16648;//Worship Ectofuntus
    private int foodID = 7946;
    private int ghostID = 2988; //Ghost disciple
    private int[] grindItems = new int[] {boneID,potID};
    private int[] worshipItems = new int[] {bonemealID,slimeID};
    private final Tile[] tiles = {new Tile(3690, 3466, 0), new Tile(3690, 3471, 0), new Tile(3685, 3473, 0), new Tile(3681, 3476, 0), new Tile(3681, 3481, 0), new Tile(3678, 3485, 0), new Tile(3673, 3485, 0), new Tile(3670, 3489, 0), new Tile(3669, 3494, 0), new Tile(3666, 3498, 0), new Tile(3665, 3503, 0), new Tile(3660, 3506, 0)};
    private final Tile[] toEcto = {new Tile(3660,3509,0), new Tile(3659,3509,0)};
    private final Tile[] toBank = {new Tile(3660,3507,0), new Tile(3659,3507,0)};
    private final Area phasArea = new Area(new Tile(3705,3459,0), new Tile(3655,3507,0));
    private final Area ectoArea = new Area(new Tile(3669,3508,0), new Tile(3649,3528,0));
    private final Area ectoUpperArea = new Area(new Tile(3669,3508,1), new Tile(3649,3528,1));
    private TilePath pToBank;
    private TilePath pToEcto;

    private long startTime;

    @Override
    public void messaged(MessageEvent messageEvent) {

    }

    @Override
    public void repaint(Graphics graphics) {

    }

    @Override
    public void start() {
        startTime = getRuntime();
        pToEcto = ctx.movement.newTilePath(tiles);
        pToBank = ctx.movement.newTilePath(tiles).reverse();
    }

    @Override
    public void poll() {
        switch (getState()) {
            case TOBANK:
                //if at ecto, go barrier and pass, else go to bank
                if (inArea(ectoUpperArea,ectoArea)) {
                    if (ctx.inventory.select().id(tokenID).isEmpty()) {
                        Npc ghost = ctx.npcs.select().id(ghostID).poll();
                        if (ghost.valid() && ghost.tile().distanceTo(ctx.players.local()) > 5) {
                            ctx.movement.step(ghost);
                            Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                            Condition.wait(() -> !ctx.players.local().inMotion());
                        } else if (ghost.valid()) {
                            if (ghost.interact("Talk-to")) {
                                Condition.wait(ctx.chat::chatting, 500,10);
                                while (ctx.chat.chatting()) {
                                    ctx.chat.clickContinue(true);
                                    Condition.sleep(700);
                                }
                            }
                        }
                    } else {
                        final int currToken = ctx.inventory.select().id(tokenID).count(true);
                        final int[] bounds = {-96, 104, -168, 0, -60, -52};
                        GameObject barrier = ctx.objects.select(15).name("Energy Barrier").poll();
                        barrier.bounds(bounds);
                        if (barrier.valid() && barrier.tile().distanceTo(ctx.players.local()) > 5) {
                            System.out.print(barrier.tile().distanceTo(ctx.players.local()));
                            ctx.movement.step(barrier);
                            Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                            Condition.wait(() -> !ctx.players.local().inMotion());
                        } else if (barrier.valid()) {
                            if (barrier.interact("Pay-toll(2-Ecto)", "Barrier")) {
                                Condition.wait(()->currToken != ctx.inventory.select().id(tokenID).count(true) &&
                                        inArea(phasArea),500,8);
                            }
                        }
                    }
                } else {
                    if (ctx.bank.nearest().tile().distanceTo(ctx.players.local()) > 5) {
                        pToBank.traverse();
                        Condition.wait(()->ctx.players.local().inMotion(),500,5);
                        Condition.sleep(800);
                    } else {
                        if (ctx.bank.opened()) {
                            Utils.depositInventory();
                            ctx.bank.withdraw(slimeID, 9);
                            ctx.bank.withdraw(boneID, 9);
                            ctx.bank.withdraw(potID,9);
                            ctx.bank.close();
                        } else {
                            ctx.bank.openNearbyBank();
                        }
                    }
                }
                break;
            case TOECTO:
                //not at ecto, go to barrier and pass
                final int[] bounds = {-96, 104, -168, 0, -60, -52};
                GameObject barrier = ctx.objects.select(5).id(barrierID).poll();
                barrier.bounds(bounds);
                if (!barrier.valid()) {
                    pToEcto.traverse();
                    Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                    Condition.sleep(800);
                } else {
                    if (barrier.interact("Pass", "Barrier")) {
                        Condition.wait(()->inArea(ectoArea),500,8);
                    }
                }
                break;
            case GRINDING:
                //at ecto, go up staircase, use bone on loader
                if (inArea(ectoArea)) {
                    GameObject stairUp = ctx.objects.select().id(staircaseID1).poll();
                    if (stairUp.valid() && stairUp.tile().distanceTo(ctx.players.local()) > 5) {
                        ctx.movement.step(new Tile(3666,3517,0));
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (stairUp.valid()) {
                        if (stairUp.interact("Climb-up", "Staircase")) {
                            Condition.wait(()->inArea(ectoUpperArea),500,8);
                        }
                    }
                } else {
                    GameObject loader = ctx.objects.select().id(loaderID).poll();
                    if (loader.valid() && loader.tile().distanceTo(ctx.players.local()) > 5) {
                        ctx.movement.step(new Tile(3660,3524,1));
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else {
                        Item bone = ctx.inventory.select().id(boneID).poll();
                        if (bone.interact("Use") && loader.interact("Use", "Loader")) {
                            Condition.wait(() -> ctx.players.local().animation() != -1, 500, 10);
                            Condition.wait(() -> !ctx.inventory.hasAll(grindItems), 1000,100);
                            Condition.wait(() -> ctx.inventory.select().id(potID).isEmpty(), 500,20);
                        }
                    }
                }
                break;
            case WORSHIPPING:
                //at ecto, if upstairs, go down, then click ectofuntus
                if (inArea(ectoUpperArea)) {
                    GameObject stairDown = ctx.objects.select().id(staircaseID2).poll();
                    if (stairDown.valid() && stairDown.tile().distanceTo(ctx.players.local()) > 5) {
                        ctx.movement.step(stairDown);
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (stairDown.valid()) {
                        if (stairDown.interact("Climb-down", "Staircase")) {
                            Condition.wait(()->inArea(ectoArea),500,8);
                        }
                    }
                } else {
                    final int[] ectoBounds = {-116, 92, 4, 76, 12, 132};
                    GameObject ectofuntus = ctx.objects.select().id(ectoID).poll();
                    ectofuntus.bounds(ectoBounds);
                    if (ectofuntus.valid() && ectofuntus.tile().distanceTo(ctx.players.local()) > 5) {
                        ctx.movement.step(ectofuntus);
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else {
                        ectofuntus.interact("Worship");
                    }
                }
                break;
            case WAIT:
//                waiterBoy(startTime);
                break;
        }
    }

    private enum State {
        TOBANK,TOECTO,GRINDING,WORSHIPPING,WAIT
    }

    private State getState() {
        //if no req ITEMS, go bank
        if (!ctx.inventory.hasAll(worshipItems) && !ctx.inventory.hasAll(grindItems)) {
            return State.TOBANK;
        }
        //if have worship or grind, go to ecto
        if ((ctx.inventory.hasAll(worshipItems) || ctx.inventory.hasAll(grindItems)) && inArea(phasArea)) {
            return State.TOECTO;
        }
        //if have grinding
        if (ctx.inventory.hasAll(grindItems) && inArea(ectoArea,ectoUpperArea)) {
            return State.GRINDING;
        }
        //if have worshipping
        if (ctx.inventory.hasAll(worshipItems) && !ctx.inventory.hasAll(grindItems) && inArea(ectoArea,ectoUpperArea)) {
            return State.WORSHIPPING;
        }

        return State.WAIT;
    }

    private boolean inArea(Area... arr) {
        for (Area a : arr) {
            if (a.contains(ctx.players.local())) return true;
        }
        return false;
    }
}

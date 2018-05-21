package nomivore.WoodFletch;

import nomivore.ID;

import api.ClientContext;
import api.PollingScript;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Random;

@Script.Manifest(
        name = "SeerCutFletch", properties = "author=nomivore; topic=1338232; client=4;",
        description = "Cut wood fletch headless arrows at Seer's Village")
public class YT_Woodcutter extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private Tile destTile;
    private Component makeUI = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);
    private treeObject normal = new treeObject("Tree", ID.LOGS_NORMAL,new Tile(2758, 3460), 1);
    private treeObject oak = new treeObject("Oak", ID.LOGS_OAK,new Tile(2769, 3463), 15);
    private treeObject willow = new treeObject("Willow", ID.LOGS_WILLOW,new Tile(2711, 3510), 30);
    private treeObject maple = new treeObject("Maple tree", ID.LOGS_MAPLE,new Tile(2731, 3500), 45);

    private List<treeObject> treeList = new ArrayList();

    private int[] logIDs = {ID.LOGS_NORMAL, ID.LOGS_OAK, ID.LOGS_WILLOW, ID.LOGS_MAPLE};
    private int logID;
    private String treeName;
    private int treesCut;
    private int skill1 = Constants.SKILLS_WOODCUTTING;
    private int skill2 = Constants.SKILLS_FLETCHING;
    private int level1;
    private int level2;
    private int startExp1;
    private int startExp2;
    private long startTime;

    @Override
    public void start() {
        startTime = getRuntime();
        startExp1 = ctx.skills.experience(skill1);
        startExp2 = ctx.skills.experience(skill2);
        treeList.add(normal);
        treeList.add(oak);
        treeList.add(willow);
        treeList.add(maple);
    }

    @Override
    public void poll() {
        Random rand = new Random();
//        int camAngle = rand.nextInt(100) + 30;
        int nap = rand.nextInt(100) + 200;
        level1 = ctx.skills.level(skill1);
        level2 = ctx.skills.level(skill2);
        if (ctx.chat.canContinue()) {
            ctx.input.send("{VK_SPACE}");
            Condition.sleep(2000);
            ctx.input.send("{VK_SPACE}");
        }
        switch (getState()) {
            case CHOP:
                GameObject tree = ctx.objects.select(7).name(treeName).nearest().poll();
//                ctx.camera.pitch(camAngle);
//                ctx.camera.angle(camAngle);
                if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
                if (tree != null) {
                    if (tree.inViewport()) {
                        tree.interact("Chop", treeName);
                    } else {
                        ctx.movement.step(tree);
                        ctx.camera.turnTo(tree);
                    }
                }
                Condition.sleep(nap);
                break;
            case FLETCHLOG:
                if (!ctx.inventory.select().id(ID.KNIFE).isEmpty()) {
                    final Item logs = ctx.inventory.select().id(logID).poll();
                    final Item knife = ctx.inventory.select().id(ID.KNIFE).poll();
                    if (knife.interact("Use") && logs.interact("Use")) {
                        Condition.wait(() -> makeUI.visible(), 250, 5);
                    }
                    if (makeUI.visible()) {
                        ctx.input.send("1");
                        Condition.wait(() -> ctx.players.local().animation() == -1 ||
                                ctx.inventory.select().id(logID).isEmpty() ||
                                ctx.chat.canContinue(), 1000, 40);
//                        makeXall();
//                        Condition.wait(ctx.chat::pendingInput, 500, 4);
//                        if (ctx.chat.pendingInput()) {
//                            ctx.chat.sendInput(33);
//
//                        }
                    }
                }
                Item[] inven = ctx.inventory.items();
                for (int index : ctx.inventory.pattern()) {
                    if (ctx.inventory.selectedItem().valid()) {
                        ctx.inventory.selectedItem().interact("Cancel");
                    }
                    for (int ID : logIDs) {
                        if (inven[index].id() == ID) {
                            ctx.input.send("{VK_SHIFT down}");
                            inven[index].click(true);
                            ctx.input.send("{VK_SHIFT up}");
                        }
                    }
                }
                ctx.input.send("{VK_SHIFT up}");
                break;
            case FLETCHARROW:
                final Item feather = ctx.inventory.select().id(ID.FEATHER).poll();
                final Item shaft = ctx.inventory.select().id(ID.ARROW_SHAFT).poll();
                if (feather.interact("Use") && shaft.interact("Use")) {
                    Condition.wait(() -> makeUI.visible(), 250, 5);
                }
                if (makeUI.visible()) {
                    if (ctx.input.send("1"))
                        Condition.wait(ctx.chat::canContinue,1000,10);
                }
                break;
            case TODEST:
                if (!ctx.players.local().inMotion()) ctx.movement.step(destTile);
                break;
//            case DROP:
//                Item[] inven = ctx.inventory.items();
//                for (Item i : inven) {
//                    if (ctx.inventory.selectedItem().valid()) {
//                        ctx.inventory.selectedItem().interact("Cancel");
//                    }
//                    for (int ID : logIDs) {
//                        if (i.id() == ID && i.id() != logID) {
//                            ctx.input.send("{VK_SHIFT down}");
//                            i.click(true);
//                            ctx.input.send("{VK_SHIFT up}");
//                        }
//                    }
//                }
//                ctx.input.send("{VK_SHIFT up}");
//                break;
            case WAIT:
                Condition.sleep(nap);
                break;
        }
    }

    private enum State {
        CHOP, WAIT, TODEST, DROP, FLETCHLOG, FLETCHARROW
    }

    private State getState() {
        for (treeObject t : treeList) {
            if (level1 >= t.level && level2 >= t.level) {
                treeName = t.treeName;
                logID = t.logID;
                destTile = t.dest;
            }
        }
        if (destTile.distanceTo(ctx.players.local()) > 9) {
            return State.TODEST;
        }
        if (ctx.inventory.select().id(ID.FEATHER).count(true) >= 100 &&
                ctx.inventory.select().id(ID.ARROW_SHAFT).count(true) >= 100 &&
                ctx.players.local().animation() == -1) {
            return State.FLETCHARROW;
        }
        if (ctx.inventory.select().count() == 28 &&
                !ctx.inventory.select().id(logID).isEmpty() &&
                ctx.players.local().animation() == -1) {
            return State.FLETCHLOG;
        }
        if (ctx.inventory.select().count() < 28 &&
                ctx.players.local().animation() == -1 &&
                !ctx.players.local().inMotion()) {
            return State.CHOP;
        }
        return State.WAIT;
    }

    public void openNearbyBank() {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
            if (ctx.bank.open()) {
                Condition.wait(() -> ctx.bank.opened(), 250, 10);
            }
        } else {
            ctx.camera.turnTo(ctx.bank.nearest());
        }
    }

    public void depositInventory() {
        if (ctx.bank.depositInventory()) {
            Condition.wait(() -> ctx.inventory.select().count() == 0);
        }
    }

    public void closeBank() {
        if (ctx.bank.opened()) {
            Condition.wait(() -> {
                ctx.bank.close();
                return !ctx.bank.opened();
            });
        }
    }

    @Override
    public void messaged(MessageEvent me) {
        if (me.text().contains("You get")) {
            treesCut++;
        }
    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);


        int exp1 = ctx.skills.experience(skill1) - startExp1;
        int expHr1 = (int)(exp1*3600000D/realRuntime(startTime));
        int exp2 = ctx.skills.experience(skill2) - startExp2;
        int expHr2 = (int)(exp2*3600000D/realRuntime(startTime));

        g.setColor(Color.WHITE);
        g.drawString(runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Trees cut %d", treesCut) , 10, 140);
        g.drawString(String.format("Woodcutting level %d", level1) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr1) , 10, 180);
        g.drawString(String.format("Fletching level %d", level2) , 10, 200);
        g.drawString(String.format("Exp %d/hr", expHr2) , 10, 220);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 140);
    }
}
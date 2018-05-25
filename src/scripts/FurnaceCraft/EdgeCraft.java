package scripts.FurnaceCraft;

import api.ClientContext;
import api.PollingScript;
import scripts.ID;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Random;

//import scripts.ID;

@Script.Manifest(
        name = "YT_EdgeCraft", properties = "author=LOL; topic=1330081; client=4;",
        description = "Craft at Edgeville Furnace")
public class EdgeCraft extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private static final Tile destTile = new Tile(3109, 3499);
    private static final Tile bankTile = new Tile(3096, 3494);

    public static List<FurnaceCraftGUI.craftObj> craftList = new ArrayList();
    private FurnaceCraftGUI.craftObj currObj;
    private int furnaceID = 16469;
    private int toolID;
    private int resourceID1 = ID.GOLD_BAR;
    private int resourceID2;
    private Component ui;
    private int productDone;
    private int level;
    private int startExp;
    private int skill = Constants.SKILLS_CRAFTING;

    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
        final FurnaceCraftGUI gui = new FurnaceCraftGUI();

        while(!gui.done()) {
            Condition.sleep();
        }
    }

    @Override
    public void poll() {
        boolean stop = true;
        Random rand = new Random();
        int nap = rand.nextInt(1000) + 2000;
        level = ctx.skills.level(skill);
        if (ctx.chat.canContinue()) {
            ctx.input.send("{VK_SPACE}");
            Condition.sleep(2000);
            ctx.input.send("{VK_SPACE}");
        }
        for (FurnaceCraftGUI.craftObj c : craftList) {
            if (!c.name.equals("")) {
                currObj = c;
                toolID = c.toolID;
                resourceID2 = c.gemID;
                ui = ctx.widgets.component(123,c.component);
                stop = false;
                break;
            }
        }
        if (stop) ctx.controller.stop();
        switch (getState()) {
            case ACTION:
                ctx.game.tab(Game.Tab.INVENTORY);
                final int temp = ctx.inventory.select().id(resourceID1).count();
                GameObject furnace = ctx.objects.select().id(furnaceID).nearest().poll();
                Item bar = ctx.inventory.select().id(resourceID1).poll();
                bar.interact("Use");
                furnace.interact("Use", "Furnace");
                Condition.wait(() -> ui.visible(),500, 7);
                if (ui.visible()) {
                    ui.interact("Make-all");
                    Condition.wait(() -> temp != ctx.inventory.select().id(resourceID1).count(), 500, 5);
                    if (temp != ctx.inventory.select().id(resourceID1).count()) {
                        Condition.wait(() -> ctx.inventory.select().id(resourceID1).isEmpty() ||
                                ctx.inventory.select().id(resourceID2).isEmpty() ||
                                ctx.chat.canContinue(), 1000, 50);
                    }
//                    Condition.wait(() -> ctx.chat.pendingInput(), 500, 4);
//                    if (ctx.chat.pendingInput()) {
//                        makeXall();
//                    }
                }
                break;
            case TODEST:
//                if (!ctx.inventory.select().id(resourceID1).isEmpty() && isRunning()) {
//                    ctx.widgets.component(160,24).interact("Toggle Run");
//                    Condition.afkReturn(1000);
//                }
                if (!ctx.players.local().inMotion()) ctx.movement.step(destTile);
                break;
            case TOBANK:
                if (bankTile.distanceTo(ctx.players.local()) > 3) {
                    if (ctx.movement.energyLevel() > 30 && ctx.inventory.select().id(resourceID1).isEmpty()) ctx.movement.running(true);
                    if (!ctx.players.local().inMotion()) ctx.movement.step(bankTile);
                } else {
                    if (ctx.bank.opened()) {
                        if (!ctx.inventory.select().isEmpty()) productDone += ctx.inventory.select().count() - 1;
                        Utils.depositInventory();
                        if (ctx.bank.select().id(resourceID1).count(true) == 0 ||
                                ctx.bank.select().id(resourceID2).count(true) == 0) {
                            currObj.name = "";
                            break;
                        }
                        ctx.bank.withdraw(toolID, 1);
                        ctx.bank.withdraw(resourceID1, 13);
                        ctx.bank.withdraw(resourceID2, 13);
                        ctx.bank.close();
                    } else {
                        ctx.bank.openNearbyBank();
                    }
                }
                break;
            case WAIT:
                Condition.sleep(nap);
                break;
        }
    }

    private enum State {
        TODEST,ACTION,WAIT,TOBANK
    }

    private State getState() {
        if (ctx.inventory.select().id(resourceID1).count() > 0 &&
                ctx.inventory.select().id(resourceID2).count() > 0 &&
                ctx.players.local().animation() == -1 &&
                ctx.inventory.select().id(toolID).count() > 0 &&
                destTile.distanceTo(ctx.players.local()) < 3) {
            return State.ACTION;
        }
        if (ctx.inventory.select().id(resourceID1).count() > 0 &&
                ctx.inventory.select().id(resourceID2).count() > 0 &&
                ctx.inventory.select().id(toolID).count() > 0 &&
                destTile.distanceTo(ctx.players.local()) > 2) {
            return State.TODEST;
        }
        if ((ctx.inventory.select().id(resourceID1).count() == 0 ||
                ctx.inventory.select().id(resourceID2).count() == 0 ||
                ctx.inventory.select().id(toolID).count() == 0)) {
            return State.TOBANK;
        }
        return State.WAIT;
    }

    @Override
    public void messaged(MessageEvent me) {
//        if (me.text().contains("You ")) {
//            productDone++;
//        }
    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);
        g.setColor(Color.WHITE);

        int s = (int)Math.floor(getRuntime()/1000 % 60);
        int m = (int)Math.floor(getRuntime()/60000 % 60);
        int h = (int)Math.floor(getRuntime()/3600000);

        int exp = ctx.skills.experience(skill) - startExp;
        int expHr = (int)(exp*3600000D/getRuntime());

        g.drawString(String.format("Runtime %02d:%02d:%02d", h, m, s), 10, 120);
        g.drawString(String.format("Crafted %d", productDone) , 10, 140);
        g.drawString(String.format("Crafting level %d", level) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }
}

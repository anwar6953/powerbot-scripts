package scripts.RuneCraft;
import api.Bank;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.Random;

//import scripts.ID;

////@Script.Manifest(
//       //  name = "YT_Earthcraft", properties = "author=LOL; client=4;",
//      //description = "Crafts runes")
public class Earthcraft extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    private final Tile[] tilesBank = {new Tile(3212, 3425, 0), new Tile(3218, 3426, 0), new Tile(3223, 3427, 0), new Tile(3228, 3427, 0), new Tile(3233, 3428, 0), new Tile(3238, 3428, 0), new Tile(3243, 3428, 0), new Tile(3248, 3428, 0), new Tile(3253, 3426, 0), new Tile(3253, 3421, 0)};
    private TilePath bankPath;
    private final Tile[] tilesAltar = {new Tile(3254, 3420, 0), new Tile(3254, 3426, 0), new Tile(3259, 3428, 0), new Tile(3264, 3428, 0), new Tile(3269, 3428, 0), new Tile(3274, 3431, 0), new Tile(3275, 3436, 0), new Tile(3279, 3440, 0), new Tile(3280, 3445, 0), new Tile(3281, 3450, 0), new Tile(3284, 3455, 0), new Tile(3287, 3460, 0), new Tile(3291, 3464, 0), new Tile(3296, 3467, 0), new Tile(3301, 3469, 0), new Tile(3305, 3472, 0)};
    private TilePath ruinPath;
    private static final Tile ruinTile = new Tile(3304, 3472);
    private static final Tile bankTile = new Tile(3254,3420);
    private static final Tile altarTile = new Tile(2658, 4839);
    private static final Tile portalTile = new Tile(2655, 4830);

    private int tiaraID = ID.TIARA_EARTH;
    private int essenceID = ID.ESSENCE_PURE;
    private int teletabID = ID.TELETAB_VARROCK;
    private int altarID = ID.ALTAR_EARTH;
    private Component ui;
    private int productDone;
    private int level;
    private int startExp;
    private int skill = Constants.SKILLS_RUNECRAFTING;

    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
        bankPath = ctx.movement.newTilePath(tilesBank);
        ruinPath = ctx.movement.newTilePath(tilesAltar);
//        final RuneCraftGUI gui = new RuneCraftGUI();
//
//        while(!gui.done()) {
//            Condition.afkReturn();
//        }
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
        switch (getState()) {
            case ACTION:
                final int temp = productDone;
                GameObject altar = ctx.objects.select().id(altarID).nearest().poll();
                altar.interact("Craft");
                Condition.wait(() -> temp != productDone);
                Condition.wait(() -> ctx.players.local().animation() == -1);
                break;
            case TODEST:
                GameObject ruins = ctx.objects.select(7).id(ID.RUINS_EARTH).poll();
                if (ruins.valid()) {
                    ruins.interact("Enter");
                    Condition.wait(() -> altarTile.distanceTo(ctx.players.local()) < 50,1000,4);
                }
                if (!ctx.players.local().inMotion() &&
                        altarTile.distanceTo(ctx.players.local()) < 50) ctx.movement.step(altarTile);
                else if (!ctx.players.local().inMotion() &&
                        ruinTile.distanceTo(ctx.players.local()) > 2) ruinPath.traverse();
                break;
            case TOBANK:
                if (bankTile.distanceTo(ctx.players.local()) > 3) {
                    if (altarTile.distanceTo(ctx.players.local()) < 10) {
                        ctx.inventory.select().id(teletabID).poll().interact("Break");
                        Condition.wait(() -> altarTile.distanceTo(ctx.players.local()) > 10,1000,5);
                    }
//                    else if (!ctx.players.local().inMotion()) ctx.movement.step(bankTile);
                    else if (!ctx.players.local().inMotion()) bankPath.traverse();
                } else {
                    if (ctx.bank.opened()) {
                        ctx.bank.depositAllExcept(teletabID);
//                        ctx.bank.withdraw(tiaraID, 1);
                        if (ctx.bank.select().id(essenceID).count(true) <= 26) ctx.controller.stop();
                        ctx.bank.withdraw(teletabID, Bank.Amount.ALL);
                        ctx.bank.withdraw(essenceID, 27);
                        if (ctx.inventory.select().id(teletabID).count(true) == 0) ctx.controller.stop();
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
        if (ctx.inventory.select().id(essenceID).count() > 0 &&
                ctx.players.local().animation() == -1 &&
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() == tiaraID &&
                altarTile.distanceTo(ctx.players.local()) < 3) {
            return State.ACTION;
        }
        if (ctx.inventory.select().id(essenceID).count() > 0 &&
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() == tiaraID) {
            return State.TODEST;
        }
        if ((ctx.inventory.select().id(essenceID).count() == 0 ||
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() != tiaraID)) {
            return State.TOBANK;
        }
        return State.WAIT;
    }

    @Override
    public void messaged(MessageEvent me) {
        if (me.text().contains(ID.gameMsg)) {
            productDone++;
        }
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
        g.drawString(String.format("Exp remaining %d", ctx.skills.remainingXP(skill)) , 10, 200);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 120);
    }
}

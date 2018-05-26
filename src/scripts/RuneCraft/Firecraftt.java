package scripts.RuneCraft;

import api.Bank;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.Random;
import java.util.regex.Pattern;

//import scripts.ID;

//@Script.Manifest(
       //  name = "YT_Firecraft", properties = "author=LOL; topic=1330081; client=4;",
      //description = "Crafts fire runes")
public class Firecraftt extends PollingScript<ClientContext> implements PaintListener, MessageListener {

    private static final Tile ruinTile = new Tile(3312, 3253);
    private static final Tile bankTile = new Tile(2443, 3083);
    private static final Tile altarTile = new Tile(2584, 4840);
    private static final Tile portalTile = new Tile(2575, 4849);
    private int bankID = ID.BANK_CHEST_CWAR;
    private int tiaraID = ID.TIARA_FIRE;
    private int essenceID = ID.ESSENCE_PURE;
    private int ringID = ID.RING_DUELLING_8;
    private int altarID = ID.ALTAR_FIRE;
    private int ruinsID = ID.RUINS_FIRE;
    private int productDone;
    private int level;
    private int startExp;
    private int skill = Constants.SKILLS_RUNECRAFTING;

    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
    }

    @Override
    public void poll() {
        Random rand = new Random();
        level = ctx.skills.level(skill);
        if (ctx.chat.canContinue()) {
            ctx.input.send("{VK_SPACE}");
            Condition.sleep(2000);
            ctx.input.send("{VK_SPACE}");
        }
        if (ctx.movement.energyLevel() > 40) ctx.movement.running(true);
        switch (getState()) {
            case ACTION:
                final int temp = productDone;
                GameObject altar = ctx.objects.select(7).id(altarID).nearest().poll();
                if (altar.valid()) {
                    altar.interact("Craft");
                    Condition.wait(() -> temp != productDone, 500, 6);
                    Condition.wait(() -> ctx.players.local().animation() == -1, 500, 6);
                }
                break;
            case TOALTAR:
                break;
            case TORUIN:
                GameObject ruins = ctx.objects.select(7).id(ruinsID).poll();
                if (ruins.valid()) {
                    ruins.interact("Enter");
                    Condition.wait(() -> altarTile.distanceTo(ctx.players.local()) < 15,1000,4);
                }
                else if (!ctx.players.local().inMotion() &&
                        altarTile.distanceTo(ctx.players.local()) < 15) {
                    ctx.movement.step(altarTile);
                    Condition.wait(() -> ctx.players.local().inMotion(), 500, 6);
                    Condition.wait(() -> !ctx.players.local().inMotion());
                }
                else if (bankTile.distanceTo(ctx.players.local()) < 10) {
                    rubRing(1);
                }
                else if (!ctx.players.local().inMotion() &&
                        ruinTile.distanceTo(ctx.players.local()) > 2) ctx.movement.step(ruinTile);
                Condition.wait(()->ctx.players.local().inMotion(),500,6);
                Condition.wait(()->!ctx.players.local().inMotion());
                break;
            case TOBANK:
                if (bankTile.distanceTo(ctx.players.local()) > 3) {
                    if (altarTile.distanceTo(ctx.players.local()) < 5) {
                        rubRing(2);
                    }
                    else if (!ctx.players.local().inMotion()) ctx.movement.step(bankTile);
                } else {
                    if (ctx.bank.opened()) {
                        ctx.bank.deposit(ID.RUNE_FIRE, Bank.Amount.ALL);
//                        ctx.bank.withdraw(tiaraID, 1);
                        if (ctx.bank.select().id(essenceID).count(true) <= 26) ctx.controller.stop();
                        if (ctx.inventory.select().name(Pattern.compile("(.*dueling.*)")).isEmpty()) ctx.bank.withdraw(ringID, 1);
                        ctx.bank.withdraw(essenceID, 27);
                        if (ctx.inventory.select().name(Pattern.compile("(.*dueling.*)")).isEmpty() &&
                                ctx.bank.select().id(ringID).isEmpty()) ctx.controller.stop();
                        ctx.bank.close();
                    } else {
                        Utils.openNearbyBank(bankID,"Use");
                    }
                }
                break;
            case WAIT:
                break;
        }
    }

    private enum State {
        TORUIN,ACTION,WAIT,TOBANK,TOALTAR
    }

    private State getState() {
        if (ctx.inventory.select().id(essenceID).count() > 0 &&
                ctx.players.local().animation() == -1 &&
                !ctx.inventory.select().name(Pattern.compile("(.*dueling.*)")).isEmpty() &&
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() == tiaraID &&
                altarTile.distanceTo(ctx.players.local()) < 7) {
            return State.ACTION;
        }
        if (ctx.inventory.select().id(essenceID).count() > 0 &&
                !ctx.inventory.select().name(Pattern.compile("(.*dueling.*)")).isEmpty() &&
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() == tiaraID) {
            return State.TORUIN;
        }
        if ((ctx.inventory.select().id(essenceID).count() == 0 ||
                ctx.equipment.itemAt(Equipment.Slot.HEAD).id() != tiaraID)) {
            return State.TOBANK;
        }
        return State.WAIT;
    }

    private void rubRing(int dest) {
        Item ring = ctx.inventory.select().name(Pattern.compile("(.*dueling.*)")).poll();
        String key;
        if (dest == 1) key = "Al Kharid Duel Arena.";
        else if (dest == 2) key = "Castle Wars Arena.";
        else key = "Nowhere.";
        if (ring.interact("Rub")) {
//            ctx.input.send(key);
            Condition.sleep(1000);
            ctx.chat.continueChat(true,key);
            Condition.wait(() -> !ctx.chat.chatting() && ctx.players.local().animation() == -1,1000, 4);
        }
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

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }
}

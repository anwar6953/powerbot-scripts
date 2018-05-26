package scripts.SimpleScripts;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Item;

import java.awt.*;

////@Script.Manifest(
//       //  name = "SpecClean", properties = "author=LOL; topic=1330081; client=4;",
//      //description = "Cleans specimens")
public class SpecimenClean extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int pileID = 24559;
    private int tableID = 24556;
    private int rockID = 11175;
    private int[] reqItems = new int[] {670,675,676};
    private int[] junkItems = new int[] {11176,11177,11183,11178,1203,1469,526};
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
    }

    @Override
    public void poll() {
        if (ctx.chat.canContinue()) {
            ctx.chat.clickContinue(true);
        }
        switch (getState()) {
            case DROPPING:
                ctx.input.send("{VK_SHIFT down}");
                for (int id : junkItems) {
                    ctx.inventory.deselectItem();
                    ctx.inventory.select().id(id).poll().click(true);
                }
                ctx.input.send("{VK_SHIFT up}");
                break;
            case LOOTING:
                //take from pile
                final int[] bounds = {4, 80, -48, 0, -52, 44};
                GameObject pile = ctx.objects.select().id(pileID).poll();
                pile.bounds(bounds);
                if (!pile.inViewport()) {
                    ctx.movement.step(pile);
                    Condition.wait(()->!ctx.players.local().inMotion());
                }
                if (pile.interact("Take")) {
                    Condition.wait(()-> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion());
                }
                break;
            case CLEANING:
                //use rock on table
                GameObject table = ctx.objects.select().id(tableID).poll();
                Item rock = ctx.inventory.select().id(rockID).poll();
                if (!table.inViewport()) {
                    ctx.movement.step(table);
                    Condition.wait(()->!ctx.players.local().inMotion());
                }
                if (rock.interact("Use") && table.interact("Use")) {
                    Condition.wait(()-> ctx.players.local().animation() != -1, 500, 7);
                    Condition.wait(()-> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion());
                }
                break;
            case WAIT:
//                waiterBoy(startTime);
                break;
        }
    }

    private enum State {
        LOOTING, CLEANING, WAIT, DROPPING
    }

    private State getState() {
        //if have junk, drop
        if (!ctx.inventory.select().id(junkItems).isEmpty()) {
            return State.DROPPING;
        }
        //if have req items and have unidentified rock
        if (ctx.inventory.hasAll(reqItems) && !ctx.inventory.select().id(rockID).isEmpty()) {
            return State.CLEANING;
        }
        //if have req ITEMS and dont have unidentified rock
        if (ctx.inventory.hasAll(reqItems) && ctx.inventory.select().id(rockID).isEmpty()) {
            return State.LOOTING;
        }
        return State.WAIT;
    }
}

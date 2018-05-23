package scripts.AIOFletcher;

import api.Antipattern;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

public class APFletcher extends Antipattern {

    APFletcher(ClientContext ctx, PollingScript.Utils utils) {
        super(ctx, utils);
        profileHandler();
    }

    void tickTwo(int id1, int id2) {
        Item[] inven = ctx.inventory.items();
        Item item1 = ctx.inventory.select().id(id1).poll();
        Item item2 = ctx.inventory.select().id(id2).poll();
        Item temp;
        if (!(inven[0].id() == id1 || inven[0].id() == id2)) {
            temp = ctx.inventory.select().id(id1,id2).poll();
            ctx.inventory.dragItemTo(temp,0);
        }
        //Choose to prefer which item to select first, with ~7% prob does the other way
        switch ((average+((getState() > average+std*1.5)?1:0))%2) {
            case 0:
                log.info("First-Second\n");
                useTwo(id1,id2);
                break;
            case 1:
                log.info("Second-First\n");
                useTwo(id2,id1);
                break;
        }
        switch (getState()/20%3) {
            case 0:
                break;
            case 1:
            case 2:
                if (getState() > average+std*2) {
                    if (!(inven[1].id() == id1 || inven[1].id() == id2)) {
                        temp = (inven[1].id() != id1) ? item1 : item2;
                        log.info("Dragging to 2nd slot!!\n");
                        ctx.inventory.dragItemTo(temp, 1);
                    } else {
                        temp = (inven[4].id() != id1) ? item1 : item2;
                        log.info("Dragging to 5nd slot!!\n");
                        ctx.inventory.dragItemTo(temp, 4);
                    }
                    break;
                }
                if (getState() > average+std) {
                    log.info("Dragging over each other\n");
                    if (r.nextBoolean()) ctx.inventory.dragItemTo(item1,item2);
                    else ctx.inventory.dragItemTo(item2,item1);
                }
                break;
                //blank
        }
    }

    void moveMouse() {
        switch (getState() / 25 % 4) {
            case 0:
                break;
            case 1:
                if (getState() > average + std * .5) {
                    log.info("Hover random\n");
                    Utils.APmouseRandom();
                }
                break;
            case 2:
                if (getState() > average + std) {
                    log.info("Hover offscreen\n");
                    Utils.APmouseOffScreen();
                }
                break;
            case 3:
                if (getState() > average + std * 2) {
                    log.info("Hover bank\n");
                    ctx.bank.hover(ctx);
                }
                break;
        }
    }

    @Override
    public void repaint(Graphics g) {
        Utils.simplePaint(g, "                                     At least 1 2+ hour progress report must be posted per week", "                                     or I assume script isn't working and script goes VIP");
    }
}

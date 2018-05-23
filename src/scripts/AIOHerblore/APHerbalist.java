package scripts.AIOHerblore;

import api.Antipattern;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

public class APHerbalist extends Antipattern {

    APHerbalist(ClientContext ctx, PollingScript.Utils utils) {
        super(ctx, utils);
        profileHandler();
    }

    private int[] pattern0 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};//default
    private int[] pattern1 = {0,1,2,3,7,6,5,4,8,9,10,11,15,14,13,12,16,17,18,19,23,22,21,20,24,25,26,27};//right left alt
    private int[] pattern2 = {0,1,4,5,8,9,12,13,16,17,20,21,24,25,26,27,22,23,18,19,14,15,10,11,6,7,2,3};//2 col zig zag
    private int[] pattern3 = {0,4,8,12,16,20,24,25,21,17,13,9,5,1,2,6,10,14,18,22,26,27,23,19,15,11,7,3};//down up alt
    private int[] pattern4 = {0,4,1,5,2,6,3,7,11,15,10,14,9,13,8,12,16,20,17,21,18,22,19,23,27,26,25,24};//2 row zig zag
    private int[] pattern5 = {0,4,8,12,16,20,24,1,5,9,13,17,21,25,2,6,10,14,18,22,26,3,7,11,15,19,23,27};//columns
    private int[][] patterns = {pattern0,pattern1,pattern2,pattern3,pattern4,pattern5};
    public void cleanHerbs(int id) {
        Item[] inven = ctx.inventory.items();

        int[] pattern = patterns[(int)(average+((getState() > average+std*0.5)?r.nextGaussian()*5:0))%6];
        for (int i : pattern) {
            if (inven[i].id() == id) inven[i].click();
//            Condition.sleep(Math.abs(getState()*10)%1000);
        }
    }

    public void moveMouse() {
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
//        if (!fullAccess) Utils.simplePaint(g,"                                     Post a progress pic for full access to profiling and anti-ban");
    }
}

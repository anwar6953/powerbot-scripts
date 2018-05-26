package scripts.Agility;


import api.Bank;
import api.ClientContext;
import api.ClientContext.*;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

////@Script.Manifest(
//       //  name = "VarrockRoof", properties = "author=LOL; client=4;",
//      //description = "Varrock")
public class VarrockRoof extends PollingScript<ClientContext> implements PaintListener {
    private static final Area groundFloor = new Area(new Tile(3000, 3300, 0), new Tile(3300, 3500, 0));
    private static final Tile destTile = new Tile(3221, 3414, 0);

    private int markID = 11849;
    private int level;
    private int startExp;
    private int currOb;
    private int skill = Constants.SKILLS_AGILITY;
    private long startTime;
    private Random rand = new Random();
    private final List<Obstacle> obList = new ArrayList<Obstacle>();


    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
        startTime = getRuntime();
        obList.add(new Obstacle("Climb", 10586, 3216, 3409,3226,3419,0));
        obList.add(new Obstacle("Cross", 10587,3214, 3418, 3222, 3409, 3));
        obList.add(new Obstacle("Leap", 10642,3200, 3419,3209, 3412, 3));
        obList.add(new Obstacle("Balance", 10777,3192, 3417,3200, 3415, 1));
        obList.add(new Obstacle("Leap", 10778,3190, 3408,3200, 3400, 3));
        obList.add(new Obstacle("Leap", 10779,3180, 3380,3210, 3405, 3));
        obList.add(new Obstacle("Leap", 10780,3215,3390, 3234,3405, 3));
        obList.add(new Obstacle("Hurdle", 10781,3234, 3400,3242, 3410, 3));
        obList.add(new Obstacle("Jump-off", 10817,3234, 3409,3242, 3416, 3));
    }

    @Override
    public void poll() {
        int nap = rand.nextInt(1000) + 2000;
        level = ctx.skills.level(skill);
        if (ctx.movement.energyLevel() > 40) ctx.movement.running(true);
        switch (getState()) {
            case ACTION:
                for (Obstacle o : obList) {
                    if (o.bound.contains(ctx.players.local())) {
                        currOb = obList.indexOf(o);
                        break;
                    }
                }
                GroundItem mark = ctx.groundItems.select(10).id(markID).poll();
                if (mark.valid()) {
                    if (!mark.inViewport()) {
                        ctx.movement.step(mark);
                        Condition.wait(() -> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion(), 500, 6);
                        mark.interact("Take");
                    }
                    else {
                        mark.interact("Take");
                        Condition.wait(() -> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion(), 500, 6);
                    }
                }
                final int currExp = ctx.skills.experience(skill);
                GameObject obstacle = ctx.objects.select(25).id(obList.get(currOb).ID).poll();
                if (!obstacle.inViewport() || obstacle.tile().distanceTo(ctx.players.local()) > 10) ctx.movement.step(obstacle);
                else {
                    obstacle.interact(obList.get(currOb).action);
                    Condition.wait(() -> currExp != ctx.skills.experience(skill), 500, 10);
                }

                for (Obstacle o : obList) {
                    if (o.bound.contains(ctx.players.local())) {
                        currOb = obList.indexOf(o);
                        break;
                    }
                }
                break;
            case TODEST:
                currOb = 0;
                if (!ctx.players.local().inMotion()) ctx.movement.step(destTile);
                break;
            case WAIT:
//                Condition.sleep(nap);
                break;
        }
    }

    private enum State {
        TODEST,ACTION,WAIT,INTERRUPT
    }

    private State getState() {
        if (groundFloor.contains(ctx.players.local()) &&
                destTile.distanceTo(ctx.players.local()) > 5) {
//            System.out.print("TORUIN\n");
            return State.TODEST;
        }

        if (ctx.players.local().animation() == -1 &&
                !ctx.players.local().inMotion()) {
//            System.out.print("CHOP\n");
            return State.ACTION;
        }
        return State.WAIT;
    }


    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        long time = getRuntime() - startTime;

        int s = (int)Math.floor(time/1000 % 60);
        int m = (int)Math.floor(time/60000 % 60);
        int h = (int)Math.floor(time/3600000);

        int exp = ctx.skills.experience(skill) - startExp;
        int expHr = (int)(exp*3600000D/time);

        g.setColor(Color.WHITE);
        g.drawString(String.format("Runtime %02d:%02d:%02d", h, m, s), 10, 120);

        g.drawString(String.format("Current obstacle %d", currOb) , 10, 140);
        g.drawString(String.format("Level %d", level) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }

    public class Obstacle {
        String action;
        int ID;
        Area bound;

        public Obstacle(String newAction, int newID, int x1, int y1, int x2, int y2, int z) {
            action = newAction;
            ID = newID;
            bound = new Area(new Tile(x1, y1, z), new Tile(x2, y2, z));
        }
    }

}

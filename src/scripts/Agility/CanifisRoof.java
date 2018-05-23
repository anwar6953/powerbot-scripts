package scripts.Agility;


import api.ClientContext;
import api.PollingScript;
import com.sun.deploy.util.ArrayUtil;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

@Script.Manifest(
        name = "CanifisRoof", properties = "author=LOL; client=4;",
        description = "Varrock")
public class CanifisRoof extends PollingScript<ClientContext> implements PaintListener {
    private static final Area groundFloor = new Area(new Tile(3466, 3471, 0), new Tile(3514, 3512, 0));
    private static final Tile destTile = new Tile(3507, 3488, 0);

    private int markID = 11849;
    private int level;
    private int startExp;
    private Obstacle currOb;
    private int skill = Constants.SKILLS_AGILITY;
    private long startTime;
    private Random rand = new Random();
    private final List<Obstacle> obList = new ArrayList<Obstacle>();
    private Area lol = new Area(new Tile(3503, 3507, 2), new Tile(3497, 3503, 2));


    @Override
    public void start() {
        startExp = ctx.skills.experience(skill);
        startTime = getRuntime();
        obList.add(new Obstacle("Climb", 10819, 3501, 3492,3512,3485,0, new int[] {108, 240, -320, 32, -32, 32}));
        obList.add(new Obstacle("Jump", 10820,3511, 3488, 3504, 3496, 2));
        obList.add(new Obstacle("Jump", 10821,3504, 3507,3497, 3503, 2));
        obList.add(new Obstacle("Jump", 10828,3493, 3505,3486, 3498, 2));
        obList.add(new Obstacle("Jump", 10822,3480, 3500,3475, 3492, 3));
        obList.add(new Obstacle("Vault", 10831,3484, 3487,3477, 3481, 2));
        obList.add(new Obstacle("Jump", 10823,3487, 3469,3503, 3478, 3));
        obList.add(new Obstacle("Jump", 10832,3515, 3482,3509, 3475, 2));
    }

    @Override
    public void poll() {
        level = ctx.skills.level(skill);
        if (ctx.movement.energyLevel() > 40) ctx.movement.running(true);
        if (Utils.realRuntime(startTime) > 5400000) ctx.controller.stop();
        switch (getState()) {
            case ACTION:
                for (Obstacle o : obList) {
                    if (o.area.contains(ctx.players.local())) {
                        currOb = o;
                        break;
                    }
                }
                GroundItem mark = ctx.groundItems.select(10).id(markID).poll();
                if (mark.valid()) {
                    if (!mark.inViewport()) {
                        ctx.movement.stepWait(mark);
                        Condition.wait(() -> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion(), 500, 6);
                        mark.interact("Take");
                    }
                    else {
                        mark.interact("Take");
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 6);
                        Condition.wait(() -> !ctx.players.local().inMotion(), 500, 6);
                    }
                }
                final int currExp = ctx.skills.experience(skill);
                GameObject obstacle = ctx.objects.select(25).id(currOb.ID).poll();
                if (currOb.bound != null) obstacle.bounds(currOb.bound);
                if (!obstacle.inViewport() || obstacle.tile().distanceTo(ctx.players.local()) > 10) ctx.movement.stepWait(obstacle);
                else {
                    obstacle.interact(currOb.action);
                    Condition.wait(() -> currExp != ctx.skills.experience(skill), 500, 10);
                }

                for (Obstacle o : obList) {
                    if (o.area.contains(ctx.players.local())) {
                        currOb = o;
                        break;
                    }
                }
                break;
            case TODEST:
                currOb = obList.get(0);
                if (!ctx.players.local().inMotion()) ctx.movement.stepWait(destTile);
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

        g.drawString(String.format("Current obstacle %d", obList.indexOf(currOb)) , 10, 140);
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
        Area area;
        int[] bound;

        public Obstacle(String newAction, int newID, int x1, int y1, int x2, int y2, int z) {
            action = newAction;
            ID = newID;
            area = new Area(new Tile(x1, y1, z), new Tile(x2, y2, z));
        }
        public Obstacle(String newAction, int newID, int x1, int y1, int x2, int y2, int z, int[] bound) {
            action = newAction;
            ID = newID;
            area = new Area(new Tile(x1, y1, z), new Tile(x2, y2, z));
            this.bound = bound;
        }
    }
}

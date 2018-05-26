package scripts.Agility;


import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.TilePath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Script.Manifest(
       //  name = "SeersRoof", properties = "author=LOL; client=4;",
      //description = "SeersRoof")
public class SeersRoof extends PollingScript<ClientContext> implements PaintListener {
    private static final Tile destTile = new Tile(2728,3486, 0);
    public static final Tile[] path = {new Tile(2704, 3464, 0), new Tile(2709, 3463, 0), new Tile(2714, 3463, 0), new Tile(2718, 3466, 0), new Tile(2720, 3471, 0), new Tile(2724, 3476, 0), new Tile(2727, 3481, 0), new Tile(2728, 3486, 0)};
    private TilePath endToStart = ctx.movement.newTilePath(path);
    private int markID = 11849;
    private int level;
    private int startExp;
    private Obstacle currOb;
    private int skill = Constants.SKILLS_AGILITY;
    private long startTime;
    private Random rand = new Random();
    private final List<Obstacle> obList = new ArrayList<Obstacle>();
    private State state = State.WAIT;

    @Override
    public void start() {
        startTime = getRuntime();
        obList.add(new Obstacle("Climb-up", 11373,2690, 3450, 100, 100,0, new int[] {-9, 97, -151, 0, 21, 91})); //, new int[] {19, 103, -152, 0, 110, 130}
        obList.add(new Obstacle("Jump", 11374,2732, 3488, -12, 10,3));
        obList.add(new Obstacle("Cross", 11378,2705, 3495, 9, -10,2, new int[] {-17, 118, -64, 0, -1, 79}));
        obList.add(new Obstacle("Jump", 11375,2715, 3477, -8, 5,2));
        obList.add(new Obstacle("Jump", 11376,2700, 3475, 16, -5,3));
        obList.add(new Obstacle("Jump", 11377,2695, 3467, 9, -10,2));
        currOb = obList.get(0);
        }

    @Override
    public void poll() {
        level = ctx.skills.level(skill);
        if (ctx.movement.energyLevel() > 40) ctx.movement.running(true);
        if (Utils.realRuntime(startTime) > 5400000) ctx.controller.stop();
        state = getState();
        switch (state) {
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
                Utils.APturnTo(obstacle);
                if (!obstacle.inViewport()) ctx.camera.turnTo(obstacle);
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
                boolean path = false;
                for (Tile t : endToStart.toArray()) {
                    if (t.distanceTo(ctx.players.local()) < 6) {
                        ctx.movement.stepWait(endToStart.next());
                        path = true;
                        break;
                    }
                }
                if (!path) ctx.movement.stepWait(destTile);
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
        if (ctx.players.local().tile().floor() == 0 &&
                destTile.distanceTo(ctx.players.local()) > 5) {
            return State.TODEST;
        }

        if (ctx.players.local().animation() == -1 &&
                !ctx.players.local().inMotion()) {
            return State.ACTION;
        }
        return State.WAIT;
    }


    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
    private ArrayList<String> strings = new ArrayList<>();

    public void repaint(Graphics g)
    {
//        g.drawString(String.format("Runtime %02d:%02d:%02d", h, m, s), 10, 120);
//
//        g.drawString(String.format("Current obstacle %d", obList.indexOf(currOb)) , 10, 140);
//        g.drawString(String.format("Level %d", level) , 10, 160);
//        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);
        strings.clear();
    }


    public class Obstacle {
        String action;
        int ID;
        Area area;
        int[] bound;

        public Obstacle(String newAction, int newID, int x1, int y1, int east, int north, int z) {
            action = newAction;
            ID = newID;
            area = new Area(new Tile(x1, y1, z), new Tile(x1+east, y1+north, z));
        }
        public Obstacle(String newAction, int newID, int x1, int y1, int east, int north, int z, int[] bound) {
            action = newAction;
            ID = newID;
            area = new Area(new Tile(x1, y1, z), new Tile(x1+east, y1+north, z));
            this.bound = bound;
        }
    }
}

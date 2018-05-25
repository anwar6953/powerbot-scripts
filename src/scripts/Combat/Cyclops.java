package scripts.Combat;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.GeItem;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.regex.Pattern;

@Script.Manifest(
        name = "Cyclops", properties = "author=LOL; topic=1330081; client=4;",
        description = "Makes fires at Varrock")
public class Cyclops extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private boolean looting = false;
    GroundSearch itemS = new GroundSearch();
    GroundCheck groundC = new GroundCheck();
    private int platformID = 23955;
    private int foodID = 7946;
    private int[] reqItems = new int[] {8851,foodID};
    private long startTime;
    private Tile exitTile = new Tile(2911,9968,0);//basement
    private Area exitArea = new Area(new Tile(2911,9966,0), new Tile(2905,9973,0)); //basement
    private int skill = Constants.SKILLS_HITPOINTS;
    private int startExp;
    GroundItem loot;
    private int profit;

    @Override
    public void messaged(MessageEvent messageEvent) {

    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int exp = ctx.skills.experience(skill) - startExp;
        int expHr = (int)((exp*3600000D/Utils.realRuntime(startTime)+99)/100)*100;
        int level = ctx.skills.realLevel(skill);

        g.setColor(Color.WHITE);
        g.drawString(Utils.runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Level %d", level) , 10, 160);
        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);
        g.drawString(String.format("Profit %d", profit) , 10, 200);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 110);
    }

    @Override
    public void start() {
        itemS.start();
        groundC.start();
        startExp = ctx.skills.experience(skill);
        startTime = getRuntime();
    }

    @Override
    public void stop() {
        try {
            String path = getStorageDirectory() + "\\file.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            writer.append(Utils.runtimeFormatted(startTime));
            writer.append(' ');
            writer.append("Profit:");
            writer.append(String.valueOf(profit));
            writer.append("\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void poll() {
        switch (getState()) {
            case LOOTING:
                //check ground for items and loot
                if (loot.valid()) {
                    if (ctx.inventory.select().count() == 28 && !ctx.inventory.select().id(foodID).isEmpty()) {
                        Item food = ctx.inventory.select().id(foodID).poll();
                        food.interact("Eat");
                        Condition.wait(()->ctx.players.local().animation() == -1);
                    } else if (ctx.inventory.select().count() < 28) {
                        if (loot.inViewport()) {
                            loot.interact("Take", loot.name());
                            if (PriceTable.containsKey(loot.name())) profit += PriceTable.get(loot.name())*loot.stackSize();
                            Condition.wait(() -> !loot.valid(), 500, 10);
                        } else {
                            ctx.movement.step(loot);
                            Condition.sleep(2000);
                        }
                    }
                }
                break;

            case ATTACKING:
                //check HP and boosted stats to drink potion and eat food
                if (ctx.skills.level(Constants.SKILLS_STRENGTH) == ctx.skills.realLevel((Constants.SKILLS_STRENGTH))) {
                    Item boost = ctx.inventory.select().name(Pattern.compile("(.*potion.*)")).poll();
                    boost.interact("Drink");
                    Condition.wait(()->ctx.players.local().animation() == -1);
                    Condition.wait(()->ctx.players.local().interacting().valid());
                }
                if (ctx.skills.level(Constants.SKILLS_HITPOINTS) <= ctx.skills.realLevel((Constants.SKILLS_HITPOINTS)) - 20) {
                    Item food = ctx.inventory.select().id(foodID).poll();
                    food.interact("Eat");
                    Condition.wait(()->ctx.players.local().animation() == -1);
                    Condition.wait(()->ctx.players.local().interacting().valid());
                }
                break;
            case TARGETING:
                //click animate platform
                Npc cyclops = ctx.npcs.select().nearest().select(npc -> !npc.inCombat()).name("Cyclops").poll();
                if (cyclops.valid() && cyclops.healthPercent() > 50) {
                    if (cyclops.inViewport()) {
                        if (cyclops.interact("Attack", "Cyclops")) {
                            Condition.wait(() -> ctx.players.local().interacting().valid(), 500, 10);
                        }
                    } else {
                        ctx.movement.step(cyclops);
                        Condition.wait(()->ctx.players.local().inMotion(),500,5);
                        Condition.wait(()->!ctx.players.local().inMotion());
                    }
                }
                break;
            case EXITING:
                final int[] bounds = {112, 132, -228, -32, 20, 116};
                GameObject door = ctx.objects.select().name("Door").poll();
                door.bounds(bounds);
                if (exitTile.distanceTo(ctx.players.local()) != 0) {
                    if (door.valid() && door.tile().distanceTo(ctx.players.local()) > 5) {
                        ctx.movement.step(door);
                        Condition.wait(() -> ctx.players.local().inMotion(), 500, 5);
                        Condition.wait(() -> !ctx.players.local().inMotion());
                    } else if (door.valid()) {
                        if (door.interact("Open", "Door")) {
                            Condition.wait(()->exitTile.distanceTo(ctx.players.local()) == 0,500,8);
                        }
                    }
                } else if (exitTile.distanceTo(ctx.players.local()) == 0 || exitArea.contains(ctx.players.local())) {
                    ctx.controller.stop();
                }
                break;
            case WAIT:
//                waiterBoy(startTime);
                break;
        }
    }

    private enum State {
        ATTACKING, LOOTING, TARGETING, WAIT, EXITING
    }

    private State getState() {
        if (ctx.inventory.hasAll(reqItems)) {
            //if defender on ground loot
            if (looting) {
                return State.LOOTING;
            }
            //if attacking npc
            if (ctx.players.local().interacting().valid()) {
                return State.ATTACKING;
            }
            //if not attacking, attack
            if (!ctx.players.local().interacting().valid()) {
                return State.TARGETING;
            }
        } else {
            return State.EXITING;
        }
        return State.WAIT;
    }

    private final HashMap<String,Integer> PriceTable = new HashMap<>();

    class GroundSearch extends Thread {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended()) {
                    GroundItem drop = ctx.groundItems.select(10).select(i->!PriceTable.containsKey(i.name())).poll();
                    if (drop.valid()) {
                        if (drop.name().equals("Coins")) {
                            PriceTable.put(drop.name(), 1);
                        } else {
                            PriceTable.put(drop.name(), new GeItem(drop.id()).price);
                        }
                    }
                    try {
                        System.out.print(PriceTable + "\n");
                        sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class GroundCheck extends Thread {
        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (!ctx.controller.isSuspended()) {
                    loot = ctx.groundItems.select(10).select(i->
                            (PriceTable.containsKey(i.name()) && PriceTable.get(i.name())*i.stackSize() > 500) ||
                                i.name().contains("defender") ||
                                i.name().contains("Long bone") ||
                                i.name().contains("Curved bone")).poll();
                    looting = loot.valid();
                    System.out.print(looting + "\n");
                    try {
                        sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

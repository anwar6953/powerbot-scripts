package scripts.Combat;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Item;

import java.awt.*;
import java.util.regex.Pattern;

//@Script.Manifest(
       //  name = "AniArmour", properties = "author=LOL; topic=1330081; client=4;",
      //description = "Makes fires at Varrock")
public class AniArmour extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int platformID = 23955;
    private int foodID = 379;
//    private int[] reqItems = new int[] {1125, 1165,1077,8851,foodID};
    private int[] reqItems = new int[] {1125, 1165,1077,8851,foodID};
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
        switch (getState()) {
            case LOOTING:
                //check ground for ITEMS and loot
                for (int id : reqItems) {
                    GroundItem item = ctx.groundItems.select(5).id(id).poll();
                    if (item.valid()) {
                        item.interact("Take");
                    }
                }
                break;
            case ATTACKING:
                //check HP and boosted stats to drink potion and eat food
                if (ctx.skills.level(Constants.SKILLS_STRENGTH) == ctx.skills.realLevel((Constants.SKILLS_STRENGTH))) {
                    Item boost = ctx.inventory.select().name(Pattern.compile("(.*potion.*)")).poll();
                    boost.interact("Drink");
                    Condition.wait(()->ctx.players.local().animation() == -1);
                }
                if (ctx.skills.level(Constants.SKILLS_HITPOINTS) <= ctx.skills.realLevel((Constants.SKILLS_HITPOINTS)) - 20) {
                    Item food = ctx.inventory.select().id(foodID).poll();
                    food.interact("Eat");
                    Condition.wait(()->ctx.players.local().animation() == -1);
                }
                break;
            case SUMMONING:
                //click animate platform
                GameObject platform = ctx.objects.select().id(platformID).nearest().poll();
                if (Utils.stepInteract(platform,"Animate")) {
                    Condition.wait(()-> ctx.players.local().animation() == -1 && !ctx.players.local().inMotion());
                }
                break;
            case WAIT:
//                waiterBoy(startTime);
                break;
        }
    }

    private enum State {
        ATTACKING, LOOTING, SUMMONING, WAIT
    }

    private State getState() {
        //if attacking npc
        if (ctx.players.local().interacting().valid()) {
            return State.ATTACKING;
        }
        //if have ITEMS and no ITEMS on ground, summon
        if (ctx.inventory.hasAll(reqItems) && ctx.groundItems.select(5).id(reqItems).isEmpty()) {
//            System.out.print("summoning");
            return State.SUMMONING;
        }
        //if dont have ITEMS or ITEMS on ground, attempt to loot
        if (!ctx.inventory.hasAll(reqItems) || !ctx.groundItems.select(5).id(reqItems).isEmpty()) {
//            System.out.print("looting");
            return State.LOOTING;
        }
//        System.out.print("waiting");
        return State.WAIT;
    }
}

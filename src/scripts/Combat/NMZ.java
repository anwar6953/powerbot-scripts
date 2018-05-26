package scripts.Combat;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static org.powerbot.script.Random.nextGaussian;

//@Script.Manifest(
       //  name = "NMZ", properties = "author=LOL; topic=1330081; client=4;",
      //description = "NMZ")
public class NMZ extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int level,
            startExp;
    private long startTime;
    private int skill = Constants.SKILLS_HITPOINTS;
    private State state = State.WAIT;

    Component nmzPoints,
            absorptionPoints,
            coffer,
            acceptDream,
            rapidHealActive;
    private int absorptionPointsLeft;
    private String[] powerups = new String[] {"Recurrent damage","Zapper"};
    @Override
    public void start() {
        initialiseWidgets();
        startExp = ctx.skills.experience(skill);
        startTime = getRuntime();
        new Thread(new ComponentChecker()).start();
    }
    @Override
    public void poll() {
        state = getState();
        switch (state) {
            case COLLECTPOTION:
                GameObject tank = ctx.objects.nil();
                String inputString = "";
                final int absorbCount = ctx.inventory.select().name(Pattern.compile("Absorption.*")).count();
                final int numAbsorbs = 16;
                final int overloadCount = ctx.inventory.select().name(Pattern.compile("Overload.*")).count();
                final int numOverloads = 9;
                final int freeSlots = 28 - ctx.inventory.select().count();
                if (overloadCount < numOverloads) {
                    tank = ctx.objects.select().name("Overload potion").poll();
                    inputString = String.valueOf(Math.min(freeSlots*4,(numOverloads-overloadCount)*4));
                } else {
                    tank = ctx.objects.select().name("Absorption potion").poll();
                    inputString = String.valueOf(Math.max(freeSlots*4,(numAbsorbs-absorbCount)*4));
                }
                if (!tank.valid()) break;
                Utils.stepInteract(tank,"Take");
                if (!Condition.wait(ctx.chat::pendingInput)) break;
                ctx.input.send(inputString);
                ctx.input.send("{VK_ENTER}");

                Condition.sleep(1000);
                break;
            case PAYFORDREAM:
                Npc dom = ctx.npcs.select().name("Dominic Onion").poll();
                Utils.stepInteract(dom,"Dream");
                if (!Condition.wait(ctx.chat::chatting)) break;
                ctx.input.send("4");
                if (!Condition.wait(ctx.chat::canContinue)) break;
                ctx.chat.clickContinue(true);
                Condition.wait(ctx.chat::chatting);
                ctx.input.send("1");
                Condition.wait(this::dreamPaid);
                break;
            case ENTERDREAM:
                GameObject potion = ctx.objects.select().name("Potion").poll();
                Utils.stepInteract(potion,"Drink");
                Condition.sleep(2000);
                if (ctx.chat.canContinue()) ctx.controller.stop();
                if (!Condition.wait(()->acceptDream.visible())) break;
                acceptDream.interact("Continue");
                break;
            case ABSORPTION:
                if (!Condition.wait(()->ctx.game.tab(Game.Tab.INVENTORY))) break;
                Item absorption = ctx.inventory.select().name(Pattern.compile("Absorption.*")).poll();
                absorption.interact("Drink");
                break;
            case OVERLOAD:
                if (!Condition.wait(()->ctx.game.tab(Game.Tab.INVENTORY))) break;
                if (ctx.players.local().inCombat()) Utils.toggleQuickPrayer(true);
                final int currHitpoints = HP();
                Item overload = ctx.inventory.select().name(Pattern.compile("Overload.*")).poll();
                overload.interact("Drink");
                Condition.wait(this::boosted);
                Condition.wait(()->currHitpoints - HP() >= 41);
                Utils.toggleQuickPrayer(false);
                break;
            case ROCKCAKE:
                if (!Condition.wait(()->ctx.game.tab(Game.Tab.INVENTORY))) break;
                Item rockCake = ctx.inventory.select().name(Pattern.compile("Dwarven rock cake")).poll();
                if (!nmzPoints.visible()) {
                    if (HP() > 60) rockCake.interact("Guzzle");
                    else rockCake.interact("Eat");
                    Condition.wait(()->HP() > 50);
                    break;
                }
                if (HP() == 2) {
                    rockCake.interact("Guzzle");
                    Condition.wait(()->HP() == 1);
                } else if (HP() > 10) {
                    rockCake.interact("Guzzle");
                } else {
                    rockCake.interact("Eat");
                }
                break;
            case HEALFLICK:
                if (!Condition.wait(()->ctx.game.tab(Game.Tab.PRAYER))) break;
                log.info("Heal flick " + Utils.runtimeFormatted(startTime));
                rapidHealActive.click();
                if (Condition.wait(()->!rapidHealActive.visible() ||
                    rapidHealActive.click(),1000,3)) break;
                break;
            case POWERUP:
                GameObject powerup = ctx.objects.select().within(20).name(powerups).nearest().poll();
                Condition.wait(()->Utils.stepInteract(powerup,"Activate"));
                break;
            case WAIT:
                Condition.sleep(500);
                break;
        }

    }

    private State getState() {
        if (nmzPoints.visible()) { // inside minigame
            if (boosted()) {
                if (absorptionPointsLeft < 250 && !ctx.inventory.select().name(Pattern.compile("Absorption.*")).isEmpty()) {
                    return State.ABSORPTION;
                }
                if (HP() < 51 && HP() > 1 && !ctx.inventory.select().name("Dwarven rock cake").isEmpty()) {
                    return State.ROCKCAKE;
                }
                if (Utils.realRuntime(startTime)%(nextGaussian()*2000+25000) < 500
                        && HP() == 1 && ctx.skills.level(Constants.SKILLS_PRAYER) > 0 && ctx.skills.realLevel(Constants.SKILLS_PRAYER) >= 22) {
                    return State.HEALFLICK;
                }
            }
            if (!ctx.objects.select().within(20).name(powerups).isEmpty()) {
                return State.POWERUP;
            }
            if (ctx.inventory.select().name(Pattern.compile("Overload.*")).isEmpty() || ctx.inventory.select().name(Pattern.compile("Absorption.*")).isEmpty()) {
                if (HP() > 1) return State.ROCKCAKE;
                return State.WAIT;
            }
            if (!boosted() && HP() >= 51 && !ctx.inventory.select().name(Pattern.compile("Overload.*")).isEmpty()) {
                return State.OVERLOAD;
            }
        } else {
            if (dreamPaid()) { //paid for dream
                if (ctx.inventory.select().count() != 28)
                    return State.COLLECTPOTION;
                if (HP() > 55 && !ctx.inventory.select().name("Dwarven rock cake").isEmpty()) {
                    return State.ROCKCAKE;
                }
                if (HP() > 50) return State.ENTERDREAM;
            } else {
                return State.PAYFORDREAM;
            }
        }
        return State.WAIT;
    }


    private enum State {
        WAIT,COLLECTPOTION,PAYFORDREAM,ENTERDREAM,OVERLOAD,ROCKCAKE,HEALFLICK,ABSORPTION,POWERUP
    }

    public static final Font FONT = new Font("Trebuchet MS", Font.PLAIN, 18);
    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        g.setFont(FONT);
        int exp = ctx.skills.experience(skill) - startExp;
        strings.clear();
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add("Exp/hr " + Utils.unitPerHour(exp,startTime));
        strings.add("Absorption left " + absorptionPointsLeft);
        strings.add("State " + state.name());
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
    }


    @Override
    public void messaged(MessageEvent me) {
        if (me.text().contains("This barrel is empty.")) {
            ctx.controller.stop();
        }
    }

    private void initialiseWidgets() {
        nmzPoints = ctx.widgets.component(202,1).component(3);
        absorptionPoints = ctx.widgets.component(202,1).component(9);
        coffer = ctx.widgets.component(207,1).component(21);
        acceptDream = ctx.widgets.component(129,6).component(9);
        rapidHealActive = ctx.widgets.component(541,11).component(0);
    }

    private boolean boosted() {
        return ctx.skills.level(Constants.SKILLS_STRENGTH) - ctx.skills.realLevel(Constants.SKILLS_STRENGTH) >= 5;
    }

    private int HP() {
        return ctx.skills.level(Constants.SKILLS_HITPOINTS);
    }

    private boolean dreamPaid() {
//        return (ctx.varpbits.varpbit(1058) << 14) != 0;
        return ctx.objects.select().name("Empty vial").isEmpty();
    }

    private class ComponentChecker implements Runnable {

        @Override
        public void run() {
            while (!ctx.controller.isStopping()) {
                if (ctx.controller.isSuspended()) continue;
                nmzPoints = ctx.widgets.component(202,1).component(3);
                absorptionPoints = ctx.widgets.component(202,1).component(9);
                coffer = ctx.widgets.component(207,1).component(21);
                acceptDream = ctx.widgets.component(129,6).component(9);
                rapidHealActive = ctx.widgets.component(541,11).component(0);
                absorptionPointsLeft = (absorptionPoints.visible()) ? Integer.parseInt(absorptionPoints.text()) : 0;
                try {
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

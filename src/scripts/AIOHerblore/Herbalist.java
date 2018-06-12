package scripts.AIOHerblore;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;

import java.awt.*;
import java.util.ArrayList;

//@Script.Manifest(name = "MrHerblore", properties = "author=nomivore; topic=1341867; client=4;", description = "Supports cleaning, unf, most potions, amulet of chemistry")
public class Herbalist extends PollingScript<ClientContext> implements PaintListener {
    public static long startTime;
    public static int startExp;
    private static boolean useAmulet = false;
    //blank
    private HerbloreGUI gui = new HerbloreGUI();
    private static ArrayList<CombineObjj> taskList = new ArrayList<>();
    private CombineObjj curr = CombineObjj.nil;
    private State state = State.WAIT;
    private Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);
    private Component geClose = ctx.widgets.component(ID.GE_WINDOW,ID.GE_TOP_BAR).component(ID.GE_CLOSE);


    private APHerbalist AP = new APHerbalist(ctx,Utils);;

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(Constants.SKILLS_HERBLORE);
        gui.setVisible();
        while (!gui.isDone()) {
            Condition.sleep(500);
        }
        if (taskList.isEmpty()) ctx.controller.stop();
        curr = taskList.get(0);


        log.info("Start");
    }

    @Override
    public void stop() {
        log.info(Utils.runtimeFormatted(startTime));
        log.info("Finished");
    }

    @Override
    public void poll() {
        if (geClose.visible()) {
            geClose.click();
            return;
        }
        state = getState();
        ctx.game.tab(Game.Tab.INVENTORY);
        ctx.inventory.deselectItem();
        switch (state) {
            case WAIT:
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.depositInventory();
                    if (useAmulet) {
                        if (ctx.bank.noneLeft(ID.AMULET_OF_CHEMISTRY)) {
                            ctx.controller.stop();
                            break;
                        }
                        if (!hasAmulet() && !amuletEquipped())
                        ctx.bank.withdrawUntil(ID.AMULET_OF_CHEMISTRY,1);
                    }
                    boolean nextTask = false;
                    for (int i = 0; i < curr.size; i++) {
                        ctx.inventory.select();
                        int id = curr.ids[i]; int q = curr.qs[i];
                        if (ctx.bank.withdrawUntil(id, q)) continue;
                        if (ctx.bank.noneLeft(id)) {
                            ctx.bank.depositInventory();
                            taskList.remove(curr);
                            if (taskList.isEmpty()) {
                                ctx.controller.stop();
                                break;
                            }
                            curr = taskList.get(0);
                            nextTask = true;
                            break;
                        }
                    }
                    if (nextTask) break;
                    AP.closeBank();

                    if (useAmulet && hasAmulet()) {
                        ctx.game.tab(Game.Tab.INVENTORY);
                        ctx.inventory.select().id(ID.AMULET_OF_CHEMISTRY).poll().interact("Wear");
                        Condition.sleep(2000);
                    }
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case MAKE:
                AP.useTwo(curr.ids[0],curr.ids[1]);
                if (!Condition.wait(()->makeAll.visible())) break;
                ctx.input.send(curr.input);
                AP.moveMouse();
                Condition.wait(()->!hasItems() || ctx.chat.canContinue() || !amuletEquipped(),1000,60);
                AP.actionDelay();
                break;
            case CLEAN:
                ctx.bank.close();
                AP.cleanHerbs(curr.ids[0]);
                break;
        }
    }

    private State getState() {
        if (!hasItems() || !amuletEquipped()) {
            return State.WITHDRAW;
        }
        AP.closeBank();
        if (curr.input.isEmpty()) {
            return State.CLEAN;
        }
        if (hasItems() && amuletEquipped()) {
            return State.MAKE;
        }
        return State.WAIT;
    }

    private boolean hasItems() {
        for (int i = 0; i < curr.ids.length; i++) {
            if (ctx.inventory.select().id(curr.ids[i]).isEmpty()) return false;
        }
        return true;
    }

    private boolean hasAmulet() {
        if (!useAmulet) return true;
        return !ctx.inventory.select().id(ID.AMULET_OF_CHEMISTRY).isEmpty();
    }
    private boolean amuletEquipped() {
        if (!useAmulet) return true;
        return ctx.equipment.itemAt(Equipment.Slot.NECK).id() == ID.AMULET_OF_CHEMISTRY;
    }

    private Font font = new Font("Trebuchet MS",Font.PLAIN,18);
    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        g.setFont(font);
        AP.repaint(g);
        int xp = ctx.skills.experience(Constants.SKILLS_HERBLORE) - startExp;
        strings.clear();
        strings.add("Nomivore's Herbalist");
        strings.add("Version 1.2.2");
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add("Exp " + xp + "("+Utils.unitPerHour(xp,startTime)+")");
        strings.add((gui.isDone()) ? curr.name:"");
        strings.add("Remaining tasks " + (taskList.size()-1));
        Utils.simplePaint(g,strings.toArray(new String[strings.size()]));
    }

    public static void setUseAmulet(boolean use) {
        useAmulet = use;
    }

    private enum State {
        WAIT,WITHDRAW,MAKE,CLEAN
    }

    public static void addTaskList(CombineObjj obj) {
        taskList.add(obj);
    }
}

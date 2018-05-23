package scripts.AIOFletcher;

import api.Bank;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Constants;
import org.powerbot.script.rt4.Game;

import java.awt.*;
import java.util.*;
import java.util.List;

@Script.Manifest(name = "MrFletcher", properties = "author=nomivore; topic=1341646; client=4;", description = "Supports darts,bolts,gem tips,logs,bows etc.")
public class Fletcher extends PollingScript<ClientContext> implements PaintListener {
    public static long startTime;
    public static int startExp;
//
    private FletcherGUI gui = new FletcherGUI(ctx);
    private static ArrayList<CombineObj> taskList = new ArrayList<>();
    private CombineObj curr = CombineObj.nil;
    private State state = State.WAIT;
    private Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE,ID.COMPONENT_MAKE);
    private Component geClose = ctx.widgets.component(ID.GE_WINDOW,ID.GE_TOP_BAR).component(ID.GE_CLOSE);

    private APFletcher AP = new APFletcher(ctx,Utils);
    private List tools = new ArrayList<Integer>() {{
        add(ID.KNIFE);add(ID.CHISEL);add(1785);
    }};

    @Override
    public void start() {
        startTime = getRuntime();
        startExp = ctx.skills.experience(Constants.SKILLS_FLETCHING);
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
        if (Utils.realRuntime(startTime) > 5400000) ctx.controller.stop();
        if (geClose.visible()) {
            geClose.click();
            return;
        }
        state = getState();
        final int resCount = (curr.size >= 2) ? ctx.inventory.select().id(curr.ids[1]).count(true) : 0;
        ctx.game.tab(Game.Tab.INVENTORY);
        ctx.inventory.deselectItem();
        switch (state) {
            case WAIT:
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept((tools.contains(curr.ids[0])?curr.ids[0]:-1));
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
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case MAKE:
                AP.useTwo(curr.ids[0],curr.ids[1]);
                if (!Condition.wait(()->makeAll.visible())) break;
                ctx.input.send(curr.input);
                AP.moveMouse();
                if (Condition.wait(()->resCount == ctx.inventory.select().id(curr.ids[1]).count(true),5000,1)) break;
                Condition.wait(()->!hasItems() || ctx.chat.canContinue(),3500,20);
                break;
            case TICK:
                AP.tickTwo(curr.ids[0],curr.ids[1]);
                break;
            case MAKESHORT:
                for (int i = 0; i < 2; i++) {
                    ctx.inventory.select().id(curr.ids[i]).poll().interact("Use");
                }
                if (!Condition.wait(()->makeAll.visible())) break;
                ctx.input.send(curr.input);
                Condition.wait(()->!hasItems(),1000,10);
                break;
        }
        AP.actionDelay();
    }

    private State getState() {
        if (!hasItems()) {
            return State.WITHDRAW;
        }
        AP.closeBank();
        if (curr.input.isEmpty()) {
            return State.TICK;
        }

        if (!curr.input.isEmpty() && curr.qs[0] == Bank.Amount.ALL.getValue()) {
            return State.MAKESHORT;
        }
        return State.MAKE;
    }

    private boolean hasItems() {
        for (int i = 0; i < curr.ids.length; i++) {
            if (ctx.inventory.select().id(curr.ids[i]).isEmpty()) return false;
        }
        return true;
    }

    private Font font = new Font("Trebuchet MS",Font.PLAIN,18);
    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        g.setFont(font);
        AP.repaint(g);
        int xp = ctx.skills.experience(Constants.SKILLS_FLETCHING) - startExp;
        strings.clear();
        strings.add("Nomivore's Fletcher");
        strings.add("Version 1.2.4");
        strings.add(Utils.runtimeFormatted(startTime));
        strings.add("Exp " + xp + "("+Utils.unitPerHour(xp,startTime)+")");
        strings.add((gui.isDone())? curr.name:"");
        strings.add("Remaining tasks " + (taskList.size()-1));
        Utils.simplePaint(g,strings);
    }

    private enum State {
        WAIT,WITHDRAW,MAKE,TICK,MAKESHORT
    }

    public static void addTaskList(CombineObj obj) {
        taskList.add(obj);
    }
}

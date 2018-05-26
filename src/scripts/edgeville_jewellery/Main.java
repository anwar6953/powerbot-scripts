package scripts.edgeville_jewellery;

import api.Components;
import api.gui.StringQueueGUI;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Script.Manifest(name = "Edge jewels", properties = "author=nomivore;", description = "Make jewellery")
public class Main extends PollingScript<ClientContext> implements PaintListener {

    private JewelObj curr;
    private LinkedList<JewelObj> tasks;
    private StringQueueGUI gui;
    private State state = State.WAIT;
    private Tile furnaceTile = Tile.NIL;
    private Tile bankTile = new Tile(3098,3494);
    private Component craftWidget = ctx.components.nil();
    private final CopyOnWriteArraySet<Component> components = new CopyOnWriteArraySet<>();

    @Override
    public void start() {
        super.start();
        HashSet<String> strings = new HashSet<>();
        strings.addAll(Presets.SILVER.stream().map(JewelObj::getName).collect(Collectors.toList()));
        gui = new StringQueueGUI(strings);
        while (!gui.isDone()) {
            Condition.sleep();
        }
        if (gui.getReturnList().isEmpty()) ctx.controller.stop();
        tasks = new LinkedList<>(Presets.SILVER.stream()
                .filter(obj->gui.getReturnList().contains(obj.getName()))
                .collect(Collectors.toList()));
        if (tasks.isEmpty()) ctx.controller.stop();
        curr = tasks.poll();

        GameObject furnace = ctx.objects.select(20).name("Furnace").poll();
        Tile der = furnace.tile().derive(-1,0);
        furnaceTile = new Tile(der.x(),der.y());
    }

    @Override
    public void poll() {

        ctx.inventory.deselectItem();
        state = getState();
        switch (state) {
            case WITHDRAW:
                if (bankTile.distanceTo(ctx.players.local()) > 5) {
                    ctx.movement.stepWait(bankTile);
                    break;
                }
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept(curr.getMOULD());
                    if (ctx.bank.noneLeft(curr.getBAR())
                            || ctx.bank.noneLeft(curr.getJEWEL())) {
                        if (tasks.isEmpty()) {
                            log.info("Nothing left");
                            ctx.controller.stop();
                        } else {
                            components.clear();
                            curr = tasks.poll();
                        }
                        return;
                    }
                    components.add(ctx.bank.select().id(curr.getMOULD()).poll().component());
                    components.add(ctx.bank.select().id(curr.getBAR()).poll().component());
                    components.add(ctx.bank.select().id(curr.getJEWEL()).poll().component());
                    ctx.bank.withdrawUntil(curr.getMOULD(), 1);
                    ctx.bank.withdrawUntil(curr.getBAR(), 13);
                    ctx.bank.withdrawUntil(curr.getJEWEL(), 13);
                    ctx.bank.close();
                } else {
                    if (ctx.players.local().inMotion()) break;
                    ctx.bank.open();
                }
                break;
            case SMITH:
                GameObject furnace = ctx.objects.select(20).name("Furnace").poll();
                if (furnaceTile.distanceTo(ctx.players.local()) > 5) {
                    ctx.movement.stepWait(furnaceTile);
                    break;
                }
                craftWidget = ctx.components.select().itemId(curr.getRESULT()).poll();
                if (craftWidget.visible()) {
                    craftWidget.interact("Craft All");
                    if (!Condition.wait(()->ctx.players.local().animation() != -1,500,5)) {
                        log.info("Did not start crafting");
                        break;
                    }
                    Condition.wait(()->!hasItems(),2000,15);
                } else {
                    if (ctx.players.local().inMotion()) break;
                    furnace.interact(false,"Smelt");
                    Condition.wait(()->ctx.components.select().itemId(curr.getRESULT()).poll().visible(),500,5);
                }
                break;
            case WAIT:
                break;
        }
    }

    private State getState() {
        if (hasItems()) {
            return State.SMITH;
        } else {
            return State.WITHDRAW;
        }
    }

    private boolean hasItems() {
        return !ctx.inventory.select().id(curr.getBAR()).isEmpty() &&
                !ctx.inventory.select().id(curr.getJEWEL()).isEmpty() &&
                !ctx.inventory.select().id(curr.getMOULD()).isEmpty();
    }

    private enum State {
        WITHDRAW, SMITH, WAIT
    }

    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        strings.clear();
        strings.add(state.toString());
        Utils.simplePaint(g,strings);
        g.drawPolygon(furnaceTile.matrix(ctx).bounds());
        g.drawPolygon(Components.RectangleToPolygon(craftWidget));
        g.drawPolygon(bankTile.matrix(ctx).bounds());
        for (Component c : components) {
            if (!c.visible()) continue;
            g.drawPolygon(Components.RectangleToPolygon(c));
        }
    }
}

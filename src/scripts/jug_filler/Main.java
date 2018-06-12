package scripts.jug_filler;

import api.ClientContext;
import api.Listeners.EventDispatcher;
import api.Listeners.Inventory.InventoryEvent;
import api.Listeners.Inventory.InventoryListener;
import api.PollingScript;
import api.utils.Timer;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.GameObject;
import osrs.ID;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

@Script.Manifest(name = "timer tests", properties = "author=nomivore;", description = "")
public class Main extends PollingScript<ClientContext> implements PaintListener,InventoryListener {
    protected EventDispatcher eventDispatcher;
    private Timer inventoryTimer = new Timer();
    private Timer movementTimer = new Timer();
    private Timer animationTimer = new Timer();
    private Timer mouseTimer = new Timer();

    @Override
    public void start() {
        super.start();
        eventDispatcher = new EventDispatcher(ctx);
        eventDispatcher.addListener(this);
        new Thread(()->{
            while (!ctx.controller.isStopping()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ctx.players.local().inMotion()) movementTimer.resetTimer();
            }
        }).start();
        new Thread(()->{
            while (!ctx.controller.isStopping()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ctx.players.local().animation() != -1) animationTimer.resetTimer();
            }
        }).start();

        new Thread(()->{
            while (!ctx.controller.isStopping()) {
                Point p = ctx.input.getLocation();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (p.x != ctx.input.getLocation().x
                        || p.y != ctx.input.getLocation().y) mouseTimer.resetTimer();
            }
        }).start();

        new Thread(()->{
            while (!ctx.controller.isStopping()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (animationTimer.getRuntime() > 30000) {
                    log.info("Haven't animated in 1 minute, terminating");
                    ctx.controller.stop();
                    return;
                }
                if (movementTimer.getRuntime() > 30000) {
                    log.info("Haven't moved in 1 minute, terminating");
                    ctx.controller.stop();
                    return;
                }
                if (inventoryTimer.getRuntime() > 30000) {
                    log.info("Haven't changed inventory in 1 minute, terminating");
                    ctx.controller.stop();
                    return;
                }
                if (mouseTimer.getRuntime() > 30000) {
                    log.info("Haven't moved mouse in 1 minute, terminating");
                    ctx.controller.stop();
                    return;
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        super.stop();
        eventDispatcher.setRunning(false);
    }

    @Override
    public void onInventoryChange(InventoryEvent ie) {
        if (!ctx.bank.opened()) inventoryTimer.resetTimer();
    }

    private State state = State.FILL;
    private ArrayList<String> strings = new ArrayList<>();
    private final Font font = new Font("Trebuchet MS", Font.PLAIN, 30);
    @Override
    public void repaint(Graphics g) {
        g.setFont(font);
        strings.clear();
        strings.add("Movement timer " + Timer.formatTime(30000-movementTimer.getRuntime()));
        strings.add("Animation timer " + Timer.formatTime(30000-animationTimer.getRuntime()));
        strings.add("Inventory timer " + Timer.formatTime(30000-inventoryTimer.getRuntime()));
        strings.add("Mouse timer " + Timer.formatTime(30000-mouseTimer.getRuntime()));
        Utils.simplePaint(g,strings);
    }

    @Override
    public void poll() {
        state = getState();
        switch (state) {
            case BANK:
                if (ctx.bank.opened()) {
                    ctx.bank.depositInventory();
                    ctx.bank.withdraw(ID.JUG_1935,28);
                    ctx.bank.close();
                } else if (!ctx.bank.nearest().tile().matrix(ctx).inViewport()) {
                    ctx.movement.inchTowards(ctx.bank.nearest());
                } else {
                    ctx.bank.open();
                }
                break;
            case FILL:
                GameObject fountain = ctx.objects.select(10).name("Fountain").nearest().poll();
                if (fountain.inViewport()) {
                    ctx.inventory.select().id(ID.JUG_1935).poll().click();
                    fountain.click();
                    if (!Condition.wait(()->ctx.players.local().animation() != -1,500,5)) break;
                    else Condition.wait(()->ctx.inventory.select().id(ID.JUG_1935).isEmpty(),2000,20);
                } else {
                    ctx.movement.inchTowards(fountain);
                }
                break;
            case WAIT:
                break;
        }
    }
    private enum State {
        BANK,FILL,WAIT
    }

    private State getState() {
        if (ctx.inventory.select().id(ID.JUG_1935).isEmpty()) {
            return State.BANK;
        } else {
            return State.FILL;
        }
    }

}

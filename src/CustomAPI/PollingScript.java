package CustomAPI;

import nomivore.ID;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;

import java.util.Random;
import java.util.concurrent.Callable;

public abstract class PollingScript<C extends ClientContext> extends org.powerbot.script.PollingScript<ClientContext> {
    private Random r = new Random();
    private int i;

    public String runtimeFormatted(long startTime) {
        int s = (int)Math.floor(realRuntime(startTime)/1000 % 60);
        int m = (int)Math.floor(realRuntime(startTime)/60000 % 60);
        int h = (int)Math.floor(realRuntime(startTime)/3600000);
        return String.format("Runtime %02d:%02d:%02d", h, m, s);
    }

    public long realRuntime(long startTime) {
        return getRuntime() - startTime;
    }



    public void depositInventory() {
        if (ctx.bank.depositInventory()) {
            Condition.wait(() -> ctx.inventory.select().count() == 0,500,6);
            if (ctx.inventory.select().count() != 0) depositInventory();
        }
    }

    public boolean isRunning() {
        Condition.sleep(500);
        return ctx.widgets.component(160,24).textureId() == 1065;
    }

    public boolean checkAllSelected() {
        if (ctx.widgets.component(ID.WIDGET_MAKE,ID.COMPONENT_MAKE).visible()) {
            return ctx.widgets.component(ID.WIDGET_MAKE,12).textureId() == -1;
        }
        return false;
    }


    public void waiterBoy(long startTime) {
        Condition.sleep(1000);
        long runTime = realRuntime(startTime);
        int s = (int)Math.floor(realRuntime(startTime)/1000 % 60);
        int m = (int)Math.floor(realRuntime(startTime)/60000 % 60);
        int h = (int)Math.floor(realRuntime(startTime)/3600000);
        if (m < 30) {
            Condition.sleep(5000);
        } else if (h >= 1) {
            Condition.sleep(10000);
        }
    }

    // ANTI PATTERN
    public void closeBank() {
        i = r.nextInt(100);
        if (ctx.bank.opened()) {
            if (i%9 == 0) ctx.bank.close();
            else ctx.input.send("{VK_ESCAPE}");
            Condition.wait(() -> !ctx.bank.opened(), 500, 4);
        }
    }

    public void openNearbyBank() {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

            if (ctx.bank.open()) {
                Condition.wait(() -> ctx.bank.opened(), 250, 5);
            }
            if (!ctx.bank.opened()) {
                ctx.input.click(true);
            }
        } else {
            ctx.camera.turnTo(ctx.bank.nearest());
        }
    }

    public void openNearbyBank(int objID, String action) {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
            if (ctx.objects.select(10).id(objID).nearest().poll().interact(action)) {
                Condition.wait(() -> ctx.bank.opened(), 250, 10);
            }
        } else {
            ctx.camera.turnTo(ctx.objects.select(10).id(objID).nearest().poll());
        }
    }

    public void APturnTo(GameObject obj) {
        if (new Random().nextBoolean()) {
            ctx.camera.turnTo(obj);
            ctx.camera.pitch(r.nextInt(50)+50);
        }
    }
    public void APturnTo(Npc obj) {
        if (new Random().nextBoolean()) {
            ctx.camera.turnTo(obj);
            ctx.camera.pitch(r.nextInt(50)+50);
        }
    }
    public void APmoveOffScreen() {
        Condition.sleep();
        switch (random(0, 3)) {
            case 0: // To Top
                ctx.input.move(random(0, ctx.game.dimensions().getWidth()-1),0);
                break;
            case 1: // To Bottom
                ctx.input.move(random(0, ctx.game.dimensions().getWidth()-1),
                        (int) (ctx.game.dimensions().getHeight()-1));
                break;
            case 2: // To Left
                ctx.input.move(0,
                        random(0, ctx.game.dimensions().getHeight()-1));
                break;
            case 3: // To Right
                ctx.input.move((int) ctx.game.dimensions().getWidth()-1,
                        random(0, ctx.game.dimensions().getHeight()-1));
                break;
        }
    }

    public void APmoveRandom() {
        Condition.sleep();
        switch (random(0, 5)) {
            case 0: // To Top right
                ctx.input.move(
                        random(ctx.game.dimensions().getWidth()/2, ctx.game.dimensions().getWidth()-1),
                        random(0, ctx.game.dimensions().getHeight()/2));
                break;
            case 1: // To Bottom right
                ctx.input.move(
                        random(ctx.game.dimensions().getWidth()/2, ctx.game.dimensions().getWidth()-1),
                        random(ctx.game.dimensions().getHeight()/2, ctx.game.dimensions().getHeight()-1));
                break;
            case 2: // To Top left
                ctx.input.move(
                        random(0, ctx.game.dimensions().getWidth()/2),
                        random(0, ctx.game.dimensions().getHeight()/2));
                break;
            case 3: // To Bottom left
                ctx.input.move(
                        random(0, ctx.game.dimensions().getWidth()/2),
                        random(ctx.game.dimensions().getHeight()/2, ctx.game.dimensions().getHeight()-1));
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    private int random(double a, double b){
        int min = (int)a;
        int max = (int)b;
        return r.nextInt((max-min)+1)+min;
    }
}

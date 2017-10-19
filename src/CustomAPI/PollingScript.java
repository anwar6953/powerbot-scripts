package CustomAPI;

import nomivore.ID;
import org.powerbot.script.Condition;

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



    public void closeBank() {
        i = r.nextInt(100);
        if (ctx.bank.opened()) {
            if (i%9 == 0) ctx.bank.close();
            else ctx.input.send("{VK_ESCAPE}");
            Condition.wait(() -> !ctx.bank.opened(), 500, 4);
        }
    }

    public boolean makeXall() {
        i = r.nextInt(100);
        int s = 0;
        if (i%9 == 0) s = 66;
        else if (i%7 == 0) s = 55;
        else if (i%6 == 0) s = 55;
        else s = 33;
        return ctx.chat.sendInput(s);
    }

    public boolean checkAllSelected() {
        if (ctx.widgets.component(ID.WIDGET_MAKE,ID.COMPONENT_MAKE).visible()) {
            return ctx.widgets.component(ID.WIDGET_MAKE,12).textureId() == -1;
        }
        return false;
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
}

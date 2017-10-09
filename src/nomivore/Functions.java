package nomivore;

import org.powerbot.script.*;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.Random;
import java.util.concurrent.Callable;
import java.awt.*;

public class Functions<C extends ClientContext> extends ClientAccessor<C> {

    private Random r = new Random();
    private int i;

    public Functions(C ctx) {
        super(ctx);
    }

    public int[] pattern() {
        i = r.nextInt(100);
        int[] pattern0 = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27};
        int[] pattern1 = {0,1,2,3,7,6,5,4,8,9,10,11,15,14,13,12,16,17,18,19,23,22,21,20,24,25,26,27};
        int[] pattern2 = {0,1,4,5,8,9,12,13,16,17,20,21,24,25,26,27,22,23,18,19,14,15,10,11,6,7,2,3};

        if (i%5 == 0) return pattern2;
        else if (i%4 == 0) return pattern0;
        else return pattern1;
    }

    public boolean almostLevel(int skill) {
        int level = ctx.skills.level(skill);
        if (ctx.skills.experienceAt(level+1) - ctx.skills.experience(skill) < Math.pow(level,2.1) &&
                (level%10 == 9 || level == 98) &&
                level > 25) {
            return true;
        } else {
            return false;
        }
    }

    public int remainingXP(int skill) {
        int level = ctx.skills.level(skill);
        return ctx.skills.experienceAt(level+1) - ctx.skills.experience(skill);
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

    public void closeBank() {
        i = r.nextInt(100);
        if (ctx.bank.opened()) {
            if (i%9 == 0) ctx.bank.close();
            else ctx.input.send("{VK_ESCAPE}");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.bank.opened();
                }
            }, 500, 4);
        }
    }

    public void openNearbyBank() {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
            if (ctx.bank.open()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.opened();
                    }
                }, 250, 10);
            }
        } else {
            ctx.camera.turnTo(ctx.bank.nearest());
        }
    }

    public void openNearbyBank(int objID, String action) {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");
            if (ctx.objects.select(10).id(objID).nearest().poll().interact(action)) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.opened();
                    }
                }, 250, 10);
            }
        } else {
            ctx.camera.turnTo(ctx.objects.select(10).id(objID).nearest().poll());
        }
    }

    public void depositInventory() {
        if (ctx.bank.depositInventory()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() == 0;
                }
            },500,6);
            if (ctx.inventory.select().count() != 0) depositInventory();
        }
    }

    public boolean isRunning() {
        Condition.sleep(500);
        return ctx.widgets.component(160,24).textureId() == 1065;
    }

    public void screenshot(File sd) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        final File screenshot = new File(sd, String.valueOf(System.currentTimeMillis()).concat(".png"));
        try {
            System.out.print("Screenshot taken at " + String.valueOf(System.currentTimeMillis()));
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "bmp", screenshot);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}

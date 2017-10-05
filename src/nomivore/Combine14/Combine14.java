package nomivore.Combine14;



import org.powerbot.script.*;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Item;
import nomivore.ID;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.Math.min;
@Script.Manifest(
        name = "Comcubiner", properties = "author=nomivore; topic=1338568; client=4;",
        description = "Combines items")
public class Combine14 extends PollingScript<ClientContext> implements PaintListener, MessageListener {


    private int resourceID1;
    private int resourceID2;
    private int resourceLeft1;
    private int resourceLeft2;
    private int productDone;
    private boolean hasResource;
    private boolean stop;


    public static List<Combine14GUI.IDPair> itemList = new ArrayList();

    @Override
    public void start() {
        final Combine14GUI gui = new Combine14GUI();

        while(!gui.done()) {
            Condition.sleep();
        }
    }

    @Override
    public void poll() {
        stop = true;
        for (Combine14GUI.IDPair i : itemList) {
            if (i.a != 0 && i.b != 0) {
                hasResource = true;
                resourceLeft1 = 1;
                resourceLeft2 = 1;
                resourceID1 = i.a;
                resourceID2 = i.b;
                if (activate()) execute();
                if (!hasResource) {
                    i.a = 0;
                    i.b = 0;
                }
                stop = false;
                break;
            }
        }
        if (stop) ctx.controller.stop();
    }
//
//
//    public void initialise() {
//        openNearbyBank();
//        if (ctx.bank.opened()) {
//            depositInventory();
//        }
//    }
//
    public boolean activate() {
        return resourceLeft1 > 0 && resourceLeft2 > 0;
    }
//
    public void execute() {
        if (ctx.chat.canContinue()) {
            ctx.input.send("{VK_SPACE}");
            Condition.sleep(2000);
            ctx.input.send("{VK_SPACE}");
        }
        switch (getState()) {
            case ACTION:
                action();
                break;
            case WITHDRAW:
                prologue();
                if ((resourceLeft1 == 0) || (resourceLeft2 == 0)) {
                    epilogue();
                    hasResource = false;
                }
                break;
            case WAIT:
                Condition.sleep(100);
                break;
        }
    }

    @Override
    public void messaged(MessageEvent me) {
//        if (me.text().contains(gameMsg)) {
//            resourceLeft2 -= 1;
//            resourceLeft1 -= 1;
//            productDone += 1;
//        }
    }

    private State getState() {
        if (ctx.inventory.select().id(resourceID1).count() == 0 || ctx.inventory.select().id(resourceID2).count() == 0) {
            return State.WITHDRAW;
        } else if (ctx.players.local().animation() == -1) {
            return State.ACTION;
        }

        return State.WAIT;
    }


    private void prologue() {
        if (ctx.bank.opened()) {
            productDone += ctx.inventory.select().count();
            depositInventory();
            resourceLeft1 = ctx.bank.select().id(resourceID1).count(true);
            resourceLeft2 = ctx.bank.select().id(resourceID2).count(true);
            ctx.bank.withdraw(resourceID1, 14);
            ctx.bank.withdraw(resourceID2, Bank.Amount.ALL);
            closeBank();
        } else {
            openNearbyBank();
        }
    }

    private void action() {
        closeBank();
        ctx.game.tab(Game.Tab.INVENTORY);
        if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

        final Item resource1 = ctx.inventory.select().id(resourceID1).poll();
        final Item resource2 = ctx.inventory.select().id(resourceID2).poll();
        final int temp = ctx.inventory.select().id(resourceID2).count();

        if(resource1.interact("Use") && resource2.interact("Use")) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.widgets.component(ID.WIDGET_CHATBOX, ID.WIDGET_MAKE).visible();
                }
            },500,4);
        }
        if (ctx.widgets.component(ID.WIDGET_CHATBOX, ID.WIDGET_MAKE).visible()) {
            ctx.widgets.component(ID.WIDGET_CHATBOX, ID.WIDGET_MAKE).interact("Make all");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return temp != ctx.inventory.select().id(resourceID2).count();
                }
            }, 500, 4);
            if (temp != ctx.inventory.select().id(resourceID2).count()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return (ctx.inventory.select().id(resourceID1).count() == 0 ||
                                ctx.inventory.select().id(resourceID2).count() == 0) ||
                                ctx.chat.canContinue();
                    }
                }, 500, 45);
            }
        } else {
            if (temp != ctx.inventory.select().id(resourceID2).count()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return (ctx.inventory.select().id(resourceID1).count() == 0 ||
                                ctx.inventory.select().id(resourceID2).count() == 0) ||
                                ctx.chat.canContinue();
                    }
                }, 500, 45);
            }
        }
    }

    private enum State {
        WITHDRAW,ACTION,WAIT
    }

    public static final Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);

    public void repaint(Graphics graphics)
    {
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        int s = (int)Math.floor(getRuntime()/1000 % 60);
        int m = (int)Math.floor(getRuntime()/60000 % 60);
        int h = (int)Math.floor(getRuntime()/3600000);

        g.setColor(Color.WHITE);
        g.drawString(String.format("Runtime %02d:%02d:%02d", h, m, s), 10, 120);


        g.drawString(String.format("Current made %d", productDone) , 10, 140);
//        g.drawString(String.format("Crafting level %d", level) , 10, 160);
//        g.drawString(String.format("Exp %d/hr", expHr) , 10, 180);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }

    public void epilogue() {
        if (ctx.bank.opened()) {
            if (ctx.bank.depositInventory()) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.inventory.select().count() == 0;
                    }
                });
            }
        } else {
            openNearbyBank();
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

    public void depositInventory() {
        if (ctx.bank.depositInventory()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().count() == 0;
                }
            });
        }
    }

    public void closeBank() {
        if (ctx.bank.opened()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
//          ctx.bank.close();
                    ctx.input.send("{VK_ESCAPE}");
                    return !ctx.bank.opened();
                }
            });
        }
    }

    public int selectResource(int[] itemArray) {
        for (int selectResource : itemArray) {
            if (ctx.bank.select().id(selectResource).count(true) > 0) {
                return selectResource;
            }
        }
        for (int selectResource : itemArray) {
            if (ctx.inventory.select().id(selectResource).count(true) > 0) {
                return selectResource;
            }
        }
        return 0;
    }

    public void logout() {
        ctx.game.tab(Game.Tab.LOGOUT);
        ctx.widgets.component(182, 10).interact("Logout");
    }
}
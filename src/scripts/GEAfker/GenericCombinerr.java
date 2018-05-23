package scripts.GEAfker;

import api.Bank;
import api.ClientContext;
import api.Listeners.Inventory.InventoryEvent;
import scripts.ID;
import org.powerbot.script.Condition;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;

import static java.lang.Math.min;

public class GenericCombinerr extends Task<ClientContext> {
    private boolean isTool;
    private Component makeAll = ctx.widgets.component(ID.WIDGET_MAKE, ID.COMPONENT_MAKE);
    private ResItem[] resArray;
    private int[] idArray;
    private String inputString = "1";

    public GenericCombinerr(ClientContext ctx, String name, String inputString, ResItem... resArray) {
        super(ctx);
        limit = -1;
        this.resArray = resArray;
        actionName = name;
        if (!inputString.isEmpty()) this.inputString = inputString;
        idArray = new int[resArray.length];
        for (int i = 0; i < resArray.length;i++) {
            idArray[i] = resArray[i].id;
        }
    }

    @Override
    public void initialise() {
        ctx.bank.openNearbyBank();
        if (ctx.bank.opened()) {
            Utils.depositInventory();
            for (ResItem r : resArray) {
                setStock(r);
                if ((r.consume != 0) && (limit == -1 || limit > r.stock/r.consume)) limit = r.stock/r.consume;
            }
        }
    }

    @Override
    public boolean activate() {
        for (ResItem r : resArray) {
            if (r.stock < r.consume) return false;
        }
        return true;
    }

    @Override
    public void execute() {
        switch (getState()) {
            case ACTION:
                Utils.closeBank();
                ctx.game.tab(Game.Tab.INVENTORY);
                ctx.inventory.deselectItem();

                final int temp = productDone;

                for (ResItem r : resArray) {
                    if (r.interact.isEmpty()) continue;
                    ctx.inventory.select().id(r.id).poll().interact(r.interact);
                }
                Condition.sleep(800);
                if (makeAll.visible()) {
//                    makeAll.interact("Make all");
                    ctx.input.send(inputString);
                    Condition.wait(() -> temp != productDone, 500, 4);
//                    ctx.bank.hover(ctx);
                    ctx.npcs.select().name("Banker").nearest().poll().hover();
                    if (temp != productDone) {
                        Condition.wait(() -> !hasRes(resArray) ||
                                ctx.chat.canContinue(), 500, 100);
                    }
                } else {
                    if (temp != productDone) {
                        Condition.wait(() -> (!hasRes(resArray) ||
                                ctx.chat.canContinue()), 500, 100);
                    }
                }
                if (!hasRes(resArray)) Utils.epilogue();
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.inventory.select();
//                    ctx.bank.depositAllExcept(idArray);
                    Utils.depositInventory();
                    for (ResItem r :resArray) {
                        setStock(r);
                        ctx.bank.withdrawUntil(r.id,r.withdraw);
                    }
                    Utils.closeBank();
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case WAIT:
                Condition.sleep(100);
                break;
        }
    }

    @Override
    public void message(MessageEvent me) {
    }

    @Override
    public void inventory(InventoryEvent ie) {
        int newID = ie.getNewItem().id();
        if (newID == 21350)
            System.out.print("Old " + ie.getOldItem().stackSize() + "New " + ie.getNewItem().stackSize() + "\n");
        if (newID <= 0) return;

        for (ResItem r : resArray) {
            if (ie.getNewItem().id() == r.id) return;
        }

        for (ResItem r : resArray) {
            r.stock -= r.consume;
        }
        productDone += 1;
    }


    private State getState() {
        if (!hasRes(resArray)) {
            return State.WITHDRAW;
        } else if (ctx.players.local().animation() == -1) {
            return State.ACTION;
        }

        return State.WAIT;
    }

    private enum State {
        WITHDRAW,ACTION,WAIT
    }

    private void setStock(ResItem r) {
        if (ctx.bank.open())
            r.stock = ctx.bank.select().id(r.id).count(true);
        else r.stock = ctx.inventory.select().id(r.id).count(true);
    }

    private boolean hasRes(ResItem... resArray) {
        for (ResItem r : resArray) {
            if (ctx.inventory.select().id(r.id).count(true) < r.consume) return false;
        }
        return true;
    }
}

class ResItem {
    int id;
    int stock;
    int withdraw;
    int consume;
    String interact;

    public ResItem(int id, int withdraw, int consume) {
        this.id = id;
//        this.stock = stock;
        this.withdraw = withdraw;
        this.consume = consume;
        this.interact = "Use";
    }

    public ResItem(int id, int withdraw, int consume, String interact) {
        this.id = id;
//        this.stock = stock;
        this.withdraw = withdraw;
        this.consume = consume;
        this.interact = interact;
    }

    private static final int
        bowstring = 1777,
        magiclong = 70,
        yewlong = 66
        ;

    static ResItem[] StringMagic = {
            new ResItem(magiclong,14,1),
            new ResItem(bowstring,14,1)
    };
    static ResItem[] StringYew = {
            new ResItem(yewlong,14,1),
            new ResItem(bowstring,14,1)
    };

    private static final int
            amethyst = 21347,
            chisel = 1755
                    ;
    static ResItem[] AmethystTips = {
            new ResItem(chisel,1,0),
            new ResItem(amethyst, Bank.Amount.ALL.getValue(),1)
    };

    static ResItem[] Sapphires = {
            new ResItem(chisel,1,0),
            new ResItem(1623, Bank.Amount.ALL.getValue(),1)
    };

    private static final int
            xericfab = 13383,
            needle = 1733,
            thread = 1734
                    ;
    static ResItem[] XericFab = {
            new ResItem(needle,1,0),
            new ResItem(xericfab, 24,4),
            new ResItem(thread, Bank.Amount.ALL.getValue(),1,"")};

    private static final int
            volcanicash = 21622,
            supercompost = 6034
                    ;
    static ResItem[] UltraCompost = {
            new ResItem(volcanicash,Bank.Amount.ALL.getValue(),2),
            new ResItem(supercompost, Bank.Amount.ALL.getValue(),1)};
}
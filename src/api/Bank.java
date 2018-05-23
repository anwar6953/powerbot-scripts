package api;

import api.ClientContext.*;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GameObject;

import java.util.List;
import java.util.Random;

import static api.ClientContext.invalidItemID;

public class Bank extends org.powerbot.script.rt4.Bank {

    private Random r = new Random();
    private int i;

    public Bank(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public void openNearbyBank() {
        if (ctx.bank.inViewport()) {
            if (ctx.inventory.selectedItem().valid()) ctx.inventory.selectedItem().interact("Cancel");

            if (ctx.bank.open()) {
                Condition.wait(ctx.bank::opened, 250, 5);
            }
            if (!ctx.bank.opened()) {
                ctx.input.click(true);
            }
        } else {
            ctx.camera.turnTo(ctx.bank.nearest());
        }
    }
    final int[] bounds = {4, 124, -128, 0, 128, 108};
    @Override
    public boolean open() {
//        GameObject geBooth = ctx.objects.select(10).name("Grand Exchange booth").action("Withdraw").nearest().poll();
//        if (geBooth.valid()) {
//            geBooth.bounds(bounds);
//            ctx.camera.turnTo(geBooth);
//            geBooth.click();
//            return Condition.wait(ctx.bank::opened,100,20);
//        }
        return super.open();
    }

    public boolean closeBank() {
        if (!ctx.bank.opened()) return false;
        if (ctx.varpbits.varpbit(1224) != -1975088063) {
            ctx.bank.close();
        } else {
            ctx.input.send("{VK_ESCAPE}");
        }
        return (Condition.wait(() -> !ctx.bank.opened(), 500, 4));
    }

    public void hover(ClientContext ctx) {
        i = r.nextInt(100)+1;
        ctx.input.move(ctx.bank.nearest().tile().matrix(ctx).point(i/100,i/100,i%7));
    }

    public int selectID(int[] ids) {
        for (int selectResource : ids) {
            if (ctx.bank.select().id(selectResource).count(true) > 0) {
                return selectResource;
            }
        }
        return invalidItemID;
    }


    public boolean noneLeft(int... ID) {
        for (int i : ID) {
            if (!ctx.inventory.select().id(i).isEmpty()
            || !ctx.bank.select().id(i).isEmpty()) continue;
            return true;
        }
        return false;
    }

    //withdraws only if inven count is less than withdraw amount
    public boolean withdrawUntil(int id, int amount) {
        if (amount == Amount.ALL.getValue() || amount == Amount.ALL_BUT_ONE.getValue()) {
            return withdraw(id, amount);
        }
        int count = ctx.inventory.select().id(id).count(true);
        return count < amount && withdraw(id, amount-count);
    }
}

package api;



import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GameObject;

import static api.ClientContext.invalidItemID;

public class DepositBox extends org.powerbot.script.rt4.DepositBox {

    public DepositBox(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public void open() {
        if (opened()) return;
        String name = "Bank deposit box";
        GameObject box = ctx.objects.select().name(name).nearest().poll();
        if (!box.valid()) return;
        box.interact("Deposit", name);
        Condition.wait(this::opened);
    }
}

package scripts.GrandExchanger;
import api.ClientContext;
import api.PollingScript;
import api.GrandExchange;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Item;

import java.awt.*;

////@Script.Manifest(
//       //  name = "InvenDump", properties = "author=LOL; topic=1330081; client=4;",
//      //description = "Sells inventory")
public class InvenDump extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int productDone;
    private long startTime;
    GrandExchange ge;

    @Override
    public void messaged(MessageEvent messageEvent) {
        if (messageEvent.text().contains("You can't trade that")) {
            ctx.controller.stop();
        }
    }

    @Override
    public void start() {
        startTime = getRuntime();
    }

    @Override
    public void repaint(Graphics graphics) {
        Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        g.setColor(Color.WHITE);
        g.drawString(Utils.runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Done %d", productDone) , 10, 140);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(5, 100, 200, 100);
    }

    @Override
    public void poll() {
//        switch (getStateName()) {
//            case MOVE:
//                break;
//        }
        ge = new GrandExchange(ctx);
        if (ge.opened()) {
            Item item = ctx.inventory.select(it -> it.id() != 995 && it.valid() && it.id() != -1).poll();
            if (item.valid()) {
                ge.sellStack(item,1);
                Condition.sleep(1000);
            }
        } else {
            ge.open();
        }
        if (ge.getAvailableSlots() <= 1) {
            ge.abortCollect();
        }
        if (ge.getAvailableSlots() == 8 && ctx.inventory.select().count() == 1) {
            ctx.controller.stop();
        }
        if (ctx.inventory.select().count() == 1) {
            ge.abortCollect();
            ctx.controller.stop();
        }
        Condition.sleep(1000);
    }

    private enum State {
        WAIT
    }

    private State getState() {
        return State.WAIT;
    }
}

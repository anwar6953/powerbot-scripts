package scripts.GrandExchanger;
import api.ClientContext;
import api.PollingScript;
import api.GrandExchange;
import org.powerbot.script.*;

import java.awt.*;

////@Script.Manifest(
//       //  name = "BuyStuff", properties = "author=LOL; topic=1330081; client=4;",
//      //description = "Sells inventory")
public class BuyStuff extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private int productDone;
    private long startTime;
    GrandExchange ge;
    int cash;

    private int bstaffID = 1391;
    private int orbID = 573;

    @Override
    public void messaged(MessageEvent messageEvent) {
        if (messageEvent.text().contains("You can't trade that")) {
            ctx.controller.stop();
        }
    }

    @Override
    public void start() {
        startTime = getRuntime();
        cash = ctx.inventory.select().id(995).count(true);
    }

    @Override
    public void repaint(Graphics graphics) {
        Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        g.setColor(Color.WHITE);
        g.drawString(Utils.runtimeFormatted(startTime), 10, 120);

        g.drawString(String.format("Done %d", productDone) , 10, 140);
        g.drawString(String.format("Cash %d", cash) , 10, 160);

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

        if (cash > 1000000) {
            if (ge.opened() && ge.getAvailableSlots() > 0) {
                ge.buyDefault("battl",bstaffID,cash/10000);
                Condition.sleep(1000);
                ge.buyDefault("air o",orbID,cash/10000);
                Condition.sleep(1000);
                ge.buyDefault("nat",561,cash/10000);


                ctx.controller.stop();
            } else {
                ge.open();
            }
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

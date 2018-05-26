package scripts.GrandExchanger;
import api.ClientContext;
import api.PollingScript;
import api.GrandExchange;
import org.powerbot.script.*;
import org.powerbot.script.rt4.Item;

import java.awt.*;

//@Script.Manifest(
       //  name = "BuyMin", properties = "author=LOL; topic=1330081; client=4;",
      //description = "Buys ITEMS")
public class BuyMin extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private long startTime;
    private GrandExchange ge;
    private int cash;
    private final Item[] inven = ctx.inventory.items();;
    private int divisor;
    private final int x = (int)ctx.game.dimensions().getWidth() - 220;
    private final int y = 175;

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
        for (Item i : inven) {
            if (i.id() != 995 && i.valid()) {
                divisor += i.stackSize();
            }
        }
        System.out.print(divisor);
    }

    @Override
    public void repaint(Graphics graphics) {
        Font TAHOMA = new Font("Tahoma", Font.PLAIN, 12);
        final Graphics2D g = (Graphics2D) graphics;
        g.setFont(TAHOMA);

        g.setColor(Color.WHITE);
        g.drawString(Utils.runtimeFormatted(startTime), x, y);

        g.drawString(String.format("Cash %d", cash) , x, y+20);
        g.drawString(String.format("Divisor %d", divisor) , x, y+40);
        if (divisor!=0) g.drawString(String.format("Cash/item %d", cash/divisor) , x, y+60);

        g.setColor(Color.BLACK);
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g.setComposite(alphaComposite);
        g.fillRect(x-10, y-20, 200, 100);
    }

    @Override
    public void poll() {
        ge = new GrandExchange(ctx);

        switch (getState()) {
            case BUY:
                if (divisor == 0) divisor++;
                for (Item i : inven) {
                    if (i.valid() && i.id() != 995) {
                        ge.buyMarketMin(i, cash * i.stackSize() / divisor);
                    }
                }
                ctx.controller.stop();
                break;
            case OPENGE:
                ge.open();
                break;
        }
    }

    private enum State {
        WAIT,OPENGE,BUY
    }

    private State getState() {
        if (!ge.opened()) {
            return State.OPENGE;
        }
        if (ge.opened()) {
            return State.BUY;
        }
        return State.WAIT;
    }
}

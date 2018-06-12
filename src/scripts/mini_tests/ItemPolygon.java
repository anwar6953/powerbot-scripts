package scripts.mini_tests;

import api.ClientContext;
import api.Inventory;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import scripts.ID;

import java.awt.*;
import java.util.ArrayList;

//@Script.Manifest(name = "widget item", properties = "author=nomivore;", description = "Make jewellery")
public class ItemPolygon extends PollingScript<ClientContext> implements PaintListener {
    private Component component = ctx.components.nil();
    private Polygon p = new Polygon();
    private Item i = ctx.inventory.nil();
    @Override
    public void start() {
        super.start();
        i = ctx.inventory.select().poll();
    }

    @Override
    public void poll() {
        for (int j = 0; j < 28; j++) {
            p = ctx.inventory.polygon(j);
            Condition.sleep(300);
        }
    }

    @Override
    public void repaint(Graphics g) {
        g.drawPolygon(Inventory.polygon(i));
        i = ctx.inventory.select().poll();
        g.drawPolygon(p);
    }
}

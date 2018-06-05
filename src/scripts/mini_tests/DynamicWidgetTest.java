package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;
import scripts.ID;

import java.awt.*;
import java.util.ArrayList;

//@Script.Manifest(name = "widget item", properties = "author=nomivore;", description = "Make jewellery")
public class DynamicWidgetTest extends PollingScript<ClientContext> implements PaintListener {
    private Component component = ctx.components.nil();
    private ArrayList<Component> components = new ArrayList<>();

    @Override
    public void start() {
        super.start();
        component = ctx.components.select().itemId(ID.JADE_AMULET_U).poll();
    }

    @Override
    public void poll() {
        components.clear();
        components.add(ctx.components.select().itemId(ID.JADE_AMULET_U).poll());
    }

    private ArrayList<String> strings = new ArrayList<>();

    @Override
    public void repaint(Graphics g) {
        strings.clear();
        strings.add(component.itemId()+"");
        Utils.simplePaint(g,strings);
        g.drawRect((int) component.boundingRect().getX(), (int) component.boundingRect().getY(), (int) component.boundingRect().getWidth(), (int) component.boundingRect().getHeight());
    }
}

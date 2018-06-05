package scripts.mini_tests.graphics_test;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import java.awt.*;
import java.util.ArrayList;

//@Script.Manifest(name = "pass graphgics", properties = "author=nomivore;", description = "Make jewellery")
public class PassGraphics extends PollingScript<ClientContext> implements PaintListener {
    private PaintInterface drawingStuff = new PaintInterface();
    private DrawableProperty drawableProperty = new DrawableProperty();
    private MethodPass methodPass = new MethodPass();
    @Override
    public void start() {
        super.start();
    }

    @Override
    public void poll() {
    }

    private ArrayList<String> strings = new ArrayList<>();
    int x=0,y=0;
    private Color c = Color.BLACK;
    @Override
    public void repaint(Graphics g) {
        g.setColor(new Color((x*5)%255,(x*3)%255,(x*6)%255));
        g.translate(x,y);
        x = (x > 255) ? 0 : ++x;
        drawingStuff.repaint(g);
        g.setColor(new Color((x*7)%255,(x*8)%255,(x*9)%255));
        g.drawPolygon(drawableProperty.getP());
        g.setColor(new Color((x)%255,(x*6)%255,(x*2)%255));
        methodPass.paintAll(g);
    }
}

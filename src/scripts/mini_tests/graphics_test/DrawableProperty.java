package scripts.mini_tests.graphics_test;

import org.powerbot.script.PaintListener;

import java.awt.*;

public class DrawableProperty {
    private Polygon p = new Polygon(new int[]{0,100,100,0}, new int[]{0,0,100,100},4);
    int counter = 0;
    boolean negative = false;
    public Polygon getP() {
        if (counter%10 == 0) {
            negative = !negative;
        }
        if (!negative) {
            p.translate(20, 20);
        } else {
            p.translate(-20,-20);
        }
        counter++;
        return p;
    }
}

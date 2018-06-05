package scripts.mini_tests.graphics_test;

import org.powerbot.script.PaintListener;

import java.awt.*;

public class PaintInterface implements PaintListener {
    private int x= 0, y=0;

    @Override
    public void repaint(Graphics g) {
        g.fillRect(x,y,250,250);
        g.drawString("REEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE",x*3,y);
        x = (x > 100) ? 0 : ++x;
        y = (y > 100) ? 0 : ++y;
    }
}

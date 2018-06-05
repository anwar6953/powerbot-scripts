package scripts.mini_tests.graphics_test;

import java.awt.*;

public class MethodPass {
    public void paintAnOval(Graphics g) {
        g.fillOval(50,50,200,200);
    }

    public void paintRectTangle(Graphics g) {
        g.fillRect(200,200,200,200);
    }

    public void paintStrings(Graphics g) {
        g.drawString("RRRRRRREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE", 400,500);
    }

    public void paintAll(Graphics g) {
        paintRectTangle(g);
        paintAnOval(g);
        paintStrings(g);
    }
}

package scripts.SimpleScripts;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;
import org.powerbot.script.rt4.Widgets;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Script.Manifest(name = "FidgetWidget", properties = "author=nomivore; topic=1342073; client=4;", description = "Helps you find widgets (Developer Tool)")
public class WidgetExplorer extends PollingScript<ClientContext> implements PaintListener, MouseListener {
    private Font font = new Font("Trebuchet MS", Font.PLAIN, 20);
    private List<Component> compList = new ArrayList<>();
    private List subList = new ArrayList<>();
    private Point clickPoint = ctx.input.getLocation();
    private ArrayList<Color> colors = new ArrayList<Color>() {{
        add(Color.RED); add(Color.BLUE); add(Color.CYAN); add(Color.MAGENTA);
        add(Color.GREEN); add(Color.YELLOW);
    }};
    private Widgets widgets = ctx.widgets;

    @Override
    public synchronized void repaint(Graphics g) {
        g.setFont(font);
        int x = (clickPoint.getX() < ctx.game.dimensions().getWidth()/2) ? (int)ctx.game.dimensions().getWidth()-250 : 15;
        int y = 0;
        int i = 0;

        subList = compList.subList(0,Math.min(5,compList.size()));
        for (Object aSubList : subList) {
            Component c = (Component) aSubList;
            g.setColor(colors.get(i % colors.size()));
            i++;
            y += font.getSize();
            Component temp = c;
            StringBuilder s = new StringBuilder(String.valueOf(temp.index()) + ",");
            for (int depth = 0; depth < 3; depth++) {
                if (temp.parent().valid()) {
                    s.append(String.valueOf(temp.parent().index())).append(",");
                } else {
                    s.append(temp.widget().id());
                    break;
                }
                temp = temp.parent();
            }
//            Utils.drawStringOutline(g, x, y, "Widget: " + c.widget().id() + " Component: " + c.index());
            Utils.drawStringOutline(g, x, y, s.toString());
            g.drawRect((int) c.boundingRect().getX(), (int) c.boundingRect().getY(), (int) c.boundingRect().getWidth(), (int) c.boundingRect().getHeight());
        }
    }

    @Override
    public synchronized void mouseClicked(MouseEvent e) {
        clickPoint = e.getPoint();
        compList.clear();
        for (Widget w : widgets) {
            if (!w.valid()) continue;
            for (Component c : w.components()) {
                if (!c.valid() || !c.visible()) continue;
                if (c.components().length > 0) {
                    for (Component cc : c.components()) {
                        if (!cc.valid() || !cc.visible()) continue;
                        if (cc.boundingRect().contains(clickPoint)) {
                            compList.add(cc);
                        }
                    }
                } else {
                    if (c.boundingRect().contains(clickPoint)) {
                        compList.add(c);
                    }
                }
            }
        }
        compList.sort(new CustomComparator());
    }

    public class CustomComparator implements Comparator<Component> {
        @Override
        public int compare(Component c1, Component c2) {
            return (int) (c1.boundingRect().getWidth() * c1.boundingRect().getWidth() -
                    c2.boundingRect().getWidth() * c2.boundingRect().getWidth());
        }
    }

    @Override
    public void poll() {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

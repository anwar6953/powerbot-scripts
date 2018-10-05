package scripts.dev_tools;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.TileMatrix;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArraySet;

@Script.Manifest(
        name = "PathGenerator", properties = "author=nomivore; topic=1341420; client=4;",
        description = "Walks places")
public class PathGenerator extends PollingScript<ClientContext> implements PaintListener, MouseListener {
    private CopyOnWriteArraySet<Tile> tiles = new CopyOnWriteArraySet<>();
    private Tile lastTile = Tile.NIL;
    @Override
    public void start() {
        ctx.input.blocking(false);
    }

    @Override
    public void stop() {
        System.out.print("public static final Tile[] path = {");
        StringJoiner sb = new StringJoiner(",");
        for (Tile t : tiles) {
            String s = "new Tile(" + t.x() + "," + t.y() + "," + t.floor() + ")";
            sb.add(s);
        }
        System.out.println(sb.toString() + "};");
    }

    @Override
    public synchronized void poll() {
        if (tiles.isEmpty()) {
            tiles.add(ctx.players.local().tile());
            lastTile = ctx.players.local().tile();
        } else if (lastTile.distanceTo(ctx.players.local()) > 2) {
            tiles.add(ctx.players.local().tile());
            lastTile = ctx.players.local().tile();
        }
    }

    @Override
    public synchronized void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2) {
            tiles.add(ctx.players.local().tile());
            lastTile = ctx.players.local().tile();
        }
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

    private enum State {
        MOVE,DRINK
    }

    private ArrayList<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics g) {
//        strings.clear();
//        Utils.simplePaint(g,strings);
        int counter = 0;
        for (Tile t : tiles) {
            counter++;
            if (t.distanceTo(ctx.players.local()) > 8) continue;
            TileMatrix tm = t.matrix(ctx);
            g.drawPolygon(tm.bounds());
            g.drawString(counter+"",tm.centerPoint().x,tm.centerPoint().y);
        }
    }

}

package api;

import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

import java.util.Random;

public class Movement extends org.powerbot.script.rt4.Movement {

    public Movement(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

//    public boolean stepScreen(Locatable obj) {
//        Tile tile = obj.tile();
//        if (tile.distanceTo(ctx.players.local()) == 0 ||
//                tile.distanceTo(ctx.players.local()) > 200 ||
//                tile == org.powerbot.script.Tile.NIL) return false;
//        if (tile.matrix(ctx).inViewport() && tile.distanceTo(ctx.players.local()) < 10) {
//            tile.matrix(ctx).interact("Walk here");
//        } else if (tile.distanceTo(ctx.players.local()) < 5) {
//            new Thread(()->ctx.camera.turnTo(obj)).start();
//            tile.matrix(ctx).interact("Walk here");
//        } else {
//            step(obj);
//        }
//        return true;
//    }

    public boolean stepWait(Locatable obj, boolean minimap) {
        if (energyLevel() > 40) running(true);
        Tile tile = obj.tile();
        double distToLocal = tile.distanceTo(ctx.players.local());
        if (distToLocal == 0 ||
                distToLocal > 200 ||
                tile == org.powerbot.script.Tile.NIL) return false;

        if (ctx.movement.destination().distanceTo(obj) == 0) {
            return Condition.wait(()->
                            ctx.movement.destination().distanceTo(ctx.players.local()) < 3 ||
                                    ctx.movement.destination().equals(Tile.NIL)
                    , 100, 15);
        }

        if (distToLocal < 8) {
            if (minimap) step(obj);
            else inchTowards(obj);
        } else if (distToLocal < 15) {
            step(obj);
        } else if (distToLocal < 75) {
            step(randomTile(2,tile));
        }
        if (!Condition.wait(()->ctx.players.local().inMotion()
                || tile.distanceTo(ctx.players.local()) == 0,500,4)) return false;

        Tile dest = ctx.movement.destination();
        return Condition.wait(()->
                        !ctx.players.local().inMotion() ||
                                dest.distanceTo(ctx.players.local()) < 5 ||
                                dest.distanceTo(ctx.players.local()) > 35
                , 100, 15);
    }

    public boolean stepWait(Locatable obj) {
        return stepWait(obj,false);
    }

    public boolean inchTowards(Locatable obj) {
        final Tile curr = ctx.players.local().tile();
        final Tile dest = obj.tile();
        if (dest.matrix(ctx).inViewport()) {
            dest.matrix(ctx).interact("Walk here");
            return true;
        }
        final int times = 20;
        for (int i = 0; i < times; i++) {
            int x = curr.x()*i/times + dest.x()*(times-i)/times;
            int y = curr.y()*i/times + dest.y()*(times-i)/times;
            Tile next = randomTile(1,new Tile(x,y,curr.floor()));
            if (next.matrix(ctx).inViewport()) {
                if (next.matrix(ctx).click()) return true;
            }
        }
        return false;
    }

    public Tile randomTile(int der, Tile tile) {
        Random r = new Random();
        int x = r.nextInt(1+der*2)-der;
        int y = r.nextInt(1+der*2)-der;
        Tile dest = tile.derive(x,y);
        return dest;
    }
}

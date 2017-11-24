package CustomAPI;

import org.powerbot.bot.rt4.client.Tile;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GameObject;

public class Movement extends org.powerbot.script.rt4.Movement {

    public Movement(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }


    public void stepWait(GameObject obj) {
        step(obj);
        Condition.wait(()->ctx.players.local().inMotion(),500,6);
        Condition.wait(()->!ctx.players.local().inMotion());
    }
}

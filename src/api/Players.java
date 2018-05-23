package api;

import org.powerbot.script.Condition;
import java.util.Random;
public class Players extends org.powerbot.script.rt4.Players {
    private Random r = new Random();
    public Players(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    /**
     * Use after attempting action that changes animation
     * @return true if animation started, waits a short period for animation to finish
     */
    public boolean aniWait() {
        if (!Condition.wait(()->ctx.players.local().animation() != -1, 100,21)) return false;
        return Condition.wait(()->local().animation() == -1, 300,15);
    }
}

package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.World;
import org.powerbot.script.rt4.Worlds;
@Script.Manifest(name = "Worldtest", properties = "client=4;", description = "")
public class WorldsTest extends PollingScript<ClientContext> {

    @Override
    public void start() {
        for (World w : ctx.worlds.select()) {
            log.info(w.id()+"");
        }
    }

    @Override
    public void poll() {
    }
}
